import os
import pandas as pd

from dotenv import load_dotenv
from sqlalchemy import create_engine, text


def create_db_engine():
    """
    Create MySQL database connection using SQLAlchemy.
    DB credentials are loaded from ml/.env file.
    """

    current_dir = os.path.dirname(os.path.abspath(__file__))
    env_path = os.path.join(current_dir, "..", ".env")

    load_dotenv(env_path)

    db_host = os.getenv("DB_HOST")
    db_port = os.getenv("DB_PORT")
    db_name = os.getenv("DB_NAME")
    db_user = os.getenv("DB_USER")
    db_password = os.getenv("DB_PASSWORD")

    if not all([db_host, db_port, db_name, db_user, db_password]):
        raise Exception("Database configuration is missing. Please check ml/.env file.")

    connection_url = (
        f"mysql+pymysql://{db_user}:{db_password}@{db_host}:{db_port}/{db_name}"
    )

    return create_engine(connection_url)


def get_latest_performance_month(engine):
    """
    Get latest performance year and month from agent_performance.
    """

    query = text("""
        SELECT performance_year, performance_month
        FROM agent_performance
        ORDER BY performance_year DESC, performance_month DESC
        LIMIT 1
    """)

    with engine.connect() as connection:
        result = connection.execute(query).mappings().first()

    if result is None:
        raise Exception("No records found in agent_performance table.")

    return int(result["performance_year"]), int(result["performance_month"])


def get_latest_prediction_month(engine):
    """
    Get latest prediction year and month from ai_performance_predictions.
    """

    query = text("""
        SELECT prediction_year, prediction_month
        FROM ai_performance_predictions
        ORDER BY prediction_year DESC, prediction_month DESC
        LIMIT 1
    """)

    with engine.connect() as connection:
        result = connection.execute(query).mappings().first()

    if result is None:
        raise Exception("No records found in ai_performance_predictions table.")

    return int(result["prediction_year"]), int(result["prediction_month"])


def read_latest_performance(engine, year, month):
    """
    Read latest monthly performance records.
    """

    query = text("""
        SELECT
            ap.agent_id,
            u.username,
            u.nickname,
            u.supervisor_id,

            ap.performance_year,
            ap.performance_month,
            ap.total_leads,
            ap.converted_leads,
            ap.cancelled_leads,
            ap.on_hold_leads,
            ap.total_premium,
            ap.target_premium,
            ap.target_achievement_percentage,
            ap.conversion_rate,
            ap.average_premium,
            ap.performance_score
        FROM agent_performance ap
        INNER JOIN users u ON u.id = ap.agent_id
        WHERE ap.performance_year = :year
        AND ap.performance_month = :month
        ORDER BY ap.agent_id
    """)

    return pd.read_sql(query, engine, params={
        "year": year,
        "month": month
    })


def read_latest_predictions(engine, year, month):
    """
    Read latest AI prediction records.
    """

    query = text("""
        SELECT
            p.agent_id,
            p.prediction_year,
            p.prediction_month,
            p.predicted_total_premium,
            p.predicted_conversion_rate,
            p.predicted_performance_score,
            p.risk_level,
            p.prediction_label,
            p.confidence_score
        FROM ai_performance_predictions p
        WHERE p.prediction_year = :year
        AND p.prediction_month = :month
        ORDER BY p.agent_id
    """)

    return pd.read_sql(query, engine, params={
        "year": year,
        "month": month
    })


def read_open_fraud_alert_summary(engine):
    """
    Read open fraud alert count and highest severity by agent.
    """

    query = """
        SELECT
            agent_id,
            COUNT(*) AS open_alert_count,
            SUM(CASE WHEN severity = 'CRITICAL' THEN 1 ELSE 0 END) AS critical_alert_count,
            SUM(CASE WHEN severity = 'HIGH' THEN 1 ELSE 0 END) AS high_alert_count,
            SUM(CASE WHEN severity = 'MEDIUM' THEN 1 ELSE 0 END) AS medium_alert_count
        FROM ai_fraud_alerts
        WHERE status = 'OPEN'
        GROUP BY agent_id
    """

    return pd.read_sql(query, engine)


def merge_recommendation_data(performance_df, prediction_df, fraud_df):
    """
    Merge performance, prediction, and fraud alert data into one dataframe.
    """

    df = performance_df.merge(
        prediction_df,
        on="agent_id",
        how="left"
    )

    df = df.merge(
        fraud_df,
        on="agent_id",
        how="left"
    )

    numeric_columns = [
        "total_leads",
        "converted_leads",
        "cancelled_leads",
        "on_hold_leads",
        "total_premium",
        "target_premium",
        "target_achievement_percentage",
        "conversion_rate",
        "average_premium",
        "performance_score",
        "predicted_total_premium",
        "predicted_conversion_rate",
        "predicted_performance_score",
        "confidence_score",
        "open_alert_count",
        "critical_alert_count",
        "high_alert_count",
        "medium_alert_count",
    ]

    for column in numeric_columns:
        if column in df.columns:
            df[column] = pd.to_numeric(df[column], errors="coerce")

    df = df.fillna({
        "open_alert_count": 0,
        "critical_alert_count": 0,
        "high_alert_count": 0,
        "medium_alert_count": 0,
        "risk_level": "UNKNOWN",
        "prediction_label": "UNKNOWN"
    })

    return df


def create_recommendation(
        agent_id,
        supervisor_id,
        recommendation_type,
        title,
        recommendation_text,
        priority
):
    """
    Build one recommendation record.
    """

    return {
        "agent_id": int(agent_id),
        "supervisor_id": int(supervisor_id) if supervisor_id and not pd.isna(supervisor_id) else None,
        "recommendation_type": recommendation_type,
        "title": title,
        "recommendation_text": recommendation_text,
        "priority": priority,
        "status": "NEW",
    }


def generate_rules_for_agent(row):
    """
    Generate recommendation records using practical rule-based logic.
    """

    recommendations = []

    agent_id = row["agent_id"]
    supervisor_id = row.get("supervisor_id")

    username = row.get("username", "agent")
    nickname = row.get("nickname") if row.get("nickname") else username

    performance_score = float(row.get("performance_score", 0))
    conversion_rate = float(row.get("conversion_rate", 0))
    target_achievement = float(row.get("target_achievement_percentage", 0))
    cancelled_leads = int(row.get("cancelled_leads", 0))
    total_leads = int(row.get("total_leads", 0))

    predicted_score = float(row.get("predicted_performance_score", 0))
    predicted_conversion_rate = float(row.get("predicted_conversion_rate", 0))
    risk_level = row.get("risk_level", "UNKNOWN")
    prediction_label = row.get("prediction_label", "UNKNOWN")

    open_alert_count = int(row.get("open_alert_count", 0))
    critical_alert_count = int(row.get("critical_alert_count", 0))
    high_alert_count = int(row.get("high_alert_count", 0))

    cancelled_rate = 0
    if total_leads > 0:
        cancelled_rate = cancelled_leads / total_leads * 100

    # Rule 1: Fraud/suspicious activity review
    if open_alert_count > 0:
        priority = "HIGH"

        if critical_alert_count > 0:
            priority = "HIGH"
        elif high_alert_count > 0:
            priority = "HIGH"

        recommendations.append(
            create_recommendation(
                agent_id=agent_id,
                supervisor_id=supervisor_id,
                recommendation_type="TRAINING_REQUIRED",
                title="Review suspicious activity alerts",
                recommendation_text=(
                    f"{nickname} has {open_alert_count} open fraud/anomaly alert(s). "
                    f"The supervisor should review the agent's recent performance pattern, "
                    f"especially premium concentration, conversion behaviour, and cancellation trends."
                ),
                priority=priority
            )
        )

    # Rule 2: Low predicted performance
    if predicted_score < 50 or risk_level == "HIGH" or prediction_label == "LOW_PERFORMER":
        recommendations.append(
            create_recommendation(
                agent_id=agent_id,
                supervisor_id=supervisor_id,
                recommendation_type="TRAINING_REQUIRED",
                title="Supervisor coaching recommended",
                recommendation_text=(
                    f"{nickname} is predicted to have a low performance score of "
                    f"{predicted_score:.2f}. Supervisor coaching is recommended to improve "
                    f"lead handling, customer follow-up, and conversion quality."
                ),
                priority="HIGH"
            )
        )

    # Rule 3: Medium predicted performance
    elif 50 <= predicted_score < 75 or risk_level == "MEDIUM":
        recommendations.append(
            create_recommendation(
                agent_id=agent_id,
                supervisor_id=supervisor_id,
                recommendation_type="FOLLOW_UP_IMPROVEMENT",
                title="Improve follow-up conversion",
                recommendation_text=(
                    f"{nickname} is predicted to achieve a moderate performance score of "
                    f"{predicted_score:.2f}. The agent should prioritize MEDIUM and HIGH "
                    f"probability leads and complete follow-ups within 48 hours."
                ),
                priority="MEDIUM"
            )
        )

    # Rule 4: High performer
    elif predicted_score >= 75:
        recommendations.append(
            create_recommendation(
                agent_id=agent_id,
                supervisor_id=supervisor_id,
                recommendation_type="PRODUCT_FOCUS",
                title="Maintain high-performing strategy",
                recommendation_text=(
                    f"{nickname} is predicted to be a high performer with a score of "
                    f"{predicted_score:.2f}. Continue focusing on high probability leads "
                    f"and repeat the sales approach used in the latest successful month."
                ),
                priority="LOW"
            )
        )

    # Rule 5: Low target achievement
    if target_achievement < 70:
        recommendations.append(
            create_recommendation(
                agent_id=agent_id,
                supervisor_id=supervisor_id,
                recommendation_type="TARGET_IMPROVEMENT",
                title="Improve monthly target achievement",
                recommendation_text=(
                    f"{nickname}'s latest target achievement is only "
                    f"{target_achievement:.2f}%. The agent should focus on high-value "
                    f"leads and reduce time spent on low-probability opportunities."
                ),
                priority="HIGH"
            )
        )

    # Rule 6: Low conversion rate
    if conversion_rate < 30:
        recommendations.append(
            create_recommendation(
                agent_id=agent_id,
                supervisor_id=supervisor_id,
                recommendation_type="FOLLOW_UP_IMPROVEMENT",
                title="Increase lead conversion rate",
                recommendation_text=(
                    f"{nickname}'s latest conversion rate is {conversion_rate:.2f}%. "
                    f"The agent should improve follow-up timing, use structured call scripts, "
                    f"and prioritize leads with higher probability."
                ),
                priority="HIGH"
            )
        )

    # Rule 7: High cancellation rate
    if cancelled_rate >= 30:
        recommendations.append(
            create_recommendation(
                agent_id=agent_id,
                supervisor_id=supervisor_id,
                recommendation_type="LEAD_PRIORITY",
                title="Reduce cancelled lead count",
                recommendation_text=(
                    f"{nickname} has a cancellation rate of {cancelled_rate:.2f}%. "
                    f"The agent should validate customer interest earlier and focus on better-qualified leads."
                ),
                priority="MEDIUM"
            )
        )

    # Rule 8: Predicted conversion rate decreasing
    if predicted_conversion_rate > 0 and predicted_conversion_rate < conversion_rate:
        recommendations.append(
            create_recommendation(
                agent_id=agent_id,
                supervisor_id=supervisor_id,
                recommendation_type="FOLLOW_UP_IMPROVEMENT",
                title="Prevent conversion rate decline",
                recommendation_text=(
                    f"{nickname}'s predicted conversion rate is {predicted_conversion_rate:.2f}%, "
                    f"which is lower than the latest actual conversion rate of {conversion_rate:.2f}%. "
                    f"Immediate follow-up improvement is recommended."
                ),
                priority="MEDIUM"
            )
        )

    # Avoid too many recommendations per agent.
    # Keep top 3 by priority.
    priority_order = {
        "HIGH": 1,
        "MEDIUM": 2,
        "LOW": 3
    }

    recommendations = sorted(
        recommendations,
        key=lambda item: priority_order.get(item["priority"], 9)
    )

    return recommendations[:3]


def delete_existing_ml_generated_recommendations(engine):
    """
    Delete previous ML-generated NEW recommendations to avoid duplicates.

    Already viewed/applied/dismissed recommendations are kept.
    """

    delete_query = text("""
        DELETE FROM ai_recommendations
        WHERE status = 'NEW'
        AND (
            title IN (
                'Review suspicious activity alerts',
                'Supervisor coaching recommended',
                'Improve follow-up conversion',
                'Maintain high-performing strategy',
                'Improve monthly target achievement',
                'Increase lead conversion rate',
                'Reduce cancelled lead count',
                'Prevent conversion rate decline'
            )
        )
    """)

    with engine.begin() as connection:
        connection.execute(delete_query)


def insert_recommendations(engine, recommendations):
    """
    Insert generated recommendations into ai_recommendations table.
    """

    if not recommendations:
        print("\nNo recommendations to insert.")
        return

    insert_query = text("""
        INSERT INTO ai_recommendations
        (
            agent_id,
            supervisor_id,
            recommendation_type,
            title,
            recommendation_text,
            priority,
            status,
            generated_at
        )
        VALUES
        (
            :agent_id,
            :supervisor_id,
            :recommendation_type,
            :title,
            :recommendation_text,
            :priority,
            :status,
            NOW(6)
        )
    """)

    with engine.begin() as connection:
        connection.execute(insert_query, recommendations)

    print(f"\nInserted {len(recommendations)} recommendation(s) into ai_recommendations table.")


def save_experiment_result(engine, dataset_size, recommendation_count):
    """
    Save recommendation engine execution result into ml_model_experiments.
    """

    insert_query = text("""
        INSERT INTO ml_model_experiments
        (
            model_name,
            model_type,
            algorithm,
            dataset_size,
            accuracy_score,
            mae,
            rmse,
            r2_score,
            precision_score,
            recall_score,
            f1_score,
            model_version,
            remarks,
            created_at
        )
        VALUES
        (
            :model_name,
            :model_type,
            :algorithm,
            :dataset_size,
            NULL,
            NULL,
            NULL,
            NULL,
            NULL,
            NULL,
            NULL,
            :model_version,
            :remarks,
            NOW(6)
        )
    """)

    params = {
        "model_name": "AI Recommendation Engine",
        "model_type": "RECOMMENDATION",
        "algorithm": "Rule-Based Recommendation Engine",
        "dataset_size": dataset_size,
        "model_version": "v1.0",
        "remarks": (
            f"Generated {recommendation_count} recommendations using latest "
            f"agent performance, AI prediction, and open fraud alert data."
        ),
    }

    with engine.begin() as connection:
        connection.execute(insert_query, params)

    print("\nRecommendation experiment result saved into ml_model_experiments table.")


def generate_ai_recommendations():
    engine = create_db_engine()

    performance_year, performance_month = get_latest_performance_month(engine)
    prediction_year, prediction_month = get_latest_prediction_month(engine)

    print("\n==============================")
    print("AI RECOMMENDATION GENERATION")
    print("==============================")
    print(f"Performance month used : {performance_year}-{performance_month:02d}")
    print(f"Prediction month used  : {prediction_year}-{prediction_month:02d}")

    performance_df = read_latest_performance(
        engine=engine,
        year=performance_year,
        month=performance_month
    )

    prediction_df = read_latest_predictions(
        engine=engine,
        year=prediction_year,
        month=prediction_month
    )

    fraud_df = read_open_fraud_alert_summary(engine)

    if performance_df.empty:
        raise Exception("Latest performance data is empty.")

    if prediction_df.empty:
        raise Exception("Latest prediction data is empty.")

    merged_df = merge_recommendation_data(
        performance_df=performance_df,
        prediction_df=prediction_df,
        fraud_df=fraud_df
    )

    all_recommendations = []

    print("\nGenerated recommendations:")

    for _, row in merged_df.iterrows():
        agent_recommendations = generate_rules_for_agent(row)
        all_recommendations.extend(agent_recommendations)

        print(
            f"Agent {int(row['agent_id'])} | "
            f"{row.get('username')} | "
            f"{len(agent_recommendations)} recommendation(s)"
        )

        for recommendation in agent_recommendations:
            print(f"  - [{recommendation['priority']}] {recommendation['title']}")

    delete_existing_ml_generated_recommendations(engine)
    insert_recommendations(engine, all_recommendations)

    save_experiment_result(
        engine=engine,
        dataset_size=len(merged_df),
        recommendation_count=len(all_recommendations)
    )

    print("\nAI recommendation generation completed successfully.")


if __name__ == "__main__":
    try:
        generate_ai_recommendations()
    except Exception as e:
        print("\nError while generating AI recommendations:")
        print(str(e))
