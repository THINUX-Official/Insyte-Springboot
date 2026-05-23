import os
from dotenv import load_dotenv
from sqlalchemy import create_engine, text


def get_engine():
    load_dotenv()

    db_host = os.getenv("DB_HOST", "localhost")
    db_port = os.getenv("DB_PORT", "3306")
    db_name = os.getenv("DB_NAME", "insyte")
    db_user = os.getenv("DB_USER", "root")
    db_password = os.getenv("DB_PASSWORD", "1234")

    db_url = f"mysql+pymysql://{db_user}:{db_password}@{db_host}:{db_port}/{db_name}"
    return create_engine(db_url)


def cleanup_pipeline_outputs():
    """
    Prevent duplicate records when AI pipeline is run multiple times.

    Safe cleanup strategy:
    1. Delete predictions for next prediction month.
    2. Delete only auto-generated OPEN fraud alerts.
    3. Delete active/new/open AI recommendations if status column supports it.
    """

    engine = get_engine()

    with engine.begin() as conn:
        latest_row = conn.execute(text("""
            SELECT performance_year, performance_month
            FROM agent_performance
            ORDER BY performance_year DESC, performance_month DESC
            LIMIT 1
        """)).fetchone()

        if latest_row is None:
            print("No agent performance records found. Cleanup skipped.")
            return

        latest_year = int(latest_row.performance_year)
        latest_month = int(latest_row.performance_month)

        if latest_month == 12:
            prediction_year = latest_year + 1
            prediction_month = 1
        else:
            prediction_year = latest_year
            prediction_month = latest_month + 1

        print("\n==============================")
        print("PIPELINE DUPLICATE CLEANUP")
        print("==============================")
        print(f"Latest performance month : {latest_year}-{latest_month:02d}")
        print(f"Prediction month cleanup : {prediction_year}-{prediction_month:02d}")

        prediction_result = conn.execute(text("""
            DELETE FROM ai_performance_predictions
            WHERE prediction_year = :prediction_year
              AND prediction_month = :prediction_month
        """), {
            "prediction_year": prediction_year,
            "prediction_month": prediction_month
        })

        print(f"Deleted old prediction rows: {prediction_result.rowcount}")

        fraud_result = conn.execute(text("""
            DELETE FROM ai_fraud_alerts
            WHERE status = 'OPEN'
              AND description LIKE 'Anomaly detected for agent%'
        """))

        print(f"Deleted old auto-generated open fraud alerts: {fraud_result.rowcount}")

        try:
            recommendation_result = conn.execute(text("""
                DELETE FROM ai_recommendations
                WHERE status IN ('NEW', 'PENDING', 'UNREAD', 'OPEN')
            """))

            print(f"Deleted old active recommendation rows: {recommendation_result.rowcount}")

        except Exception as ex:
            print(f"Recommendation status cleanup skipped. Reason: {ex}")

            try:
                recommendation_result = conn.execute(text("""
                    DELETE FROM ai_recommendations
                    WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
                """))

                print(f"Deleted recent recommendation rows using fallback cleanup: {recommendation_result.rowcount}")

            except Exception as fallback_ex:
                print(f"Recommendation fallback cleanup skipped. Reason: {fallback_ex}")

        print("Pipeline duplicate cleanup completed.\n")