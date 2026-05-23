import os
import joblib
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


def load_model_and_features():
    """
    Load trained model and feature list from models folder.
    """

    current_dir = os.path.dirname(os.path.abspath(__file__))
    model_dir = os.path.join(current_dir, "models")

    model_path = os.path.join(model_dir, "performance_prediction_model.pkl")
    features_path = os.path.join(model_dir, "performance_prediction_features.pkl")

    if not os.path.exists(model_path):
        raise Exception(
            "Trained model not found. Please run train_performance_model.py first."
        )

    if not os.path.exists(features_path):
        raise Exception(
            "Feature list not found. Please run train_performance_model.py first."
        )

    model = joblib.load(model_path)
    feature_columns = joblib.load(features_path)

    return model, feature_columns


def get_latest_performance_month(engine):
    """
    Get latest available performance year and month from agent_performance.
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


def get_next_month(year, month):
    """
    Calculate next year and month.
    """

    if month == 12:
        return year + 1, 1

    return year, month + 1


def read_latest_agent_performance(engine, year, month):
    """
    Read latest month performance records.
    These records are used as input to predict next month performance.
    """

    query = text("""
        SELECT
            ap.id,
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

    df = pd.read_sql(query, engine, params={
        "year": year,
        "month": month
    })

    if df.empty:
        raise Exception(
            f"No agent performance records found for {year}-{month}."
        )

    return df


def prepare_prediction_features(df, feature_columns, prediction_year, prediction_month):
    """
    Prepare input features for model prediction.

    Important:
    We use latest performance values, but change the performance_year/month
    to the next prediction year/month before prediction.
    """

    prediction_df = df.copy()

    prediction_df["performance_year"] = prediction_year
    prediction_df["performance_month"] = prediction_month

    for column in feature_columns:
        prediction_df[column] = pd.to_numeric(prediction_df[column], errors="coerce")

    prediction_df = prediction_df.fillna(0)

    X_prediction = prediction_df[feature_columns]

    return prediction_df, X_prediction


def calculate_predicted_total_premium(row, predicted_score):
    """
    Estimate next month premium using current total premium and predicted score.

    This is a simple practical formula for prototype/demo:
    - If predicted score increases, premium increases slightly.
    - If predicted score decreases, premium decreases slightly.
    """

    current_score = float(row.get("performance_score", 0))
    current_premium = float(row.get("total_premium", 0))

    if current_score <= 0:
        return round(current_premium, 2)

    score_change_ratio = (predicted_score - current_score) / 100

    predicted_premium = current_premium * (1 + score_change_ratio)

    if predicted_premium < 0:
        predicted_premium = 0

    return round(predicted_premium, 2)


def calculate_predicted_conversion_rate(row, predicted_score):
    """
    Estimate next month conversion rate using current conversion rate and predicted score.
    """

    current_score = float(row.get("performance_score", 0))
    current_conversion_rate = float(row.get("conversion_rate", 0))

    if current_score <= 0:
        return round(current_conversion_rate, 2)

    score_change_ratio = (predicted_score - current_score) / 100

    predicted_conversion_rate = current_conversion_rate * (1 + score_change_ratio)

    if predicted_conversion_rate < 0:
        predicted_conversion_rate = 0

    if predicted_conversion_rate > 100:
        predicted_conversion_rate = 100

    return round(predicted_conversion_rate, 2)


def get_risk_level(predicted_score):
    """
    Convert predicted score into risk level.
    """

    if predicted_score >= 75:
        return "LOW"

    if predicted_score >= 50:
        return "MEDIUM"

    return "HIGH"


def get_prediction_label(predicted_score):
    """
    Convert predicted score into performer category.
    """

    if predicted_score >= 75:
        return "HIGH_PERFORMER"

    if predicted_score >= 50:
        return "AVERAGE_PERFORMER"

    return "LOW_PERFORMER"


def delete_existing_predictions(engine, prediction_year, prediction_month):
    """
    Delete existing predictions for the same prediction month.
    This prevents duplicate rows when running the script multiple times.
    """

    delete_query = text("""
        DELETE FROM ai_performance_predictions
        WHERE prediction_year = :prediction_year
        AND prediction_month = :prediction_month
        AND model_name = :model_name
        AND model_version = :model_version
    """)

    with engine.begin() as connection:
        connection.execute(delete_query, {
            "prediction_year": prediction_year,
            "prediction_month": prediction_month,
            "model_name": "Agent Performance Prediction Model",
            "model_version": "v1.0"
        })


def insert_predictions(engine, predictions):
    """
    Insert predicted results into ai_performance_predictions table.
    """

    insert_query = text("""
        INSERT INTO ai_performance_predictions
        (
            agent_id,
            prediction_year,
            prediction_month,
            predicted_total_premium,
            predicted_conversion_rate,
            predicted_performance_score,
            risk_level,
            prediction_label,
            model_name,
            model_version,
            confidence_score,
            generated_at
        )
        VALUES
        (
            :agent_id,
            :prediction_year,
            :prediction_month,
            :predicted_total_premium,
            :predicted_conversion_rate,
            :predicted_performance_score,
            :risk_level,
            :prediction_label,
            :model_name,
            :model_version,
            :confidence_score,
            NOW(6)
        )
    """)

    with engine.begin() as connection:
        connection.execute(insert_query, predictions)


def run_prediction():
    engine = create_db_engine()

    model, feature_columns = load_model_and_features()

    latest_year, latest_month = get_latest_performance_month(engine)
    prediction_year, prediction_month = get_next_month(latest_year, latest_month)

    print("\n==============================")
    print("NEXT MONTH PERFORMANCE PREDICTION")
    print("==============================")
    print(f"Latest performance month : {latest_year}-{latest_month:02d}")
    print(f"Prediction month         : {prediction_year}-{prediction_month:02d}")

    latest_df = read_latest_agent_performance(
        engine=engine,
        year=latest_year,
        month=latest_month
    )

    prediction_df, X_prediction = prepare_prediction_features(
        df=latest_df,
        feature_columns=feature_columns,
        prediction_year=prediction_year,
        prediction_month=prediction_month
    )

    predicted_scores = model.predict(X_prediction)

    predictions_to_insert = []

    print("\nPrediction results:")

    for index, row in prediction_df.iterrows():
        predicted_score = round(float(predicted_scores[index]), 2)

        if predicted_score < 0:
            predicted_score = 0

        if predicted_score > 100:
            predicted_score = 100

        predicted_total_premium = calculate_predicted_total_premium(
            row=row,
            predicted_score=predicted_score
        )

        predicted_conversion_rate = calculate_predicted_conversion_rate(
            row=row,
            predicted_score=predicted_score
        )

        risk_level = get_risk_level(predicted_score)
        prediction_label = get_prediction_label(predicted_score)

        # Prototype confidence calculation.
        # Higher score gives slightly higher confidence for dashboard display.
        confidence_score = min(95.00, max(60.00, 60.00 + (predicted_score * 0.35)))

        prediction_record = {
            "agent_id": int(row["agent_id"]),
            "prediction_year": prediction_year,
            "prediction_month": prediction_month,
            "predicted_total_premium": predicted_total_premium,
            "predicted_conversion_rate": predicted_conversion_rate,
            "predicted_performance_score": predicted_score,
            "risk_level": risk_level,
            "prediction_label": prediction_label,
            "model_name": "Agent Performance Prediction Model",
            "model_version": "v1.0",
            "confidence_score": round(confidence_score, 2),
        }

        predictions_to_insert.append(prediction_record)

        print(
            f"Agent {int(row['agent_id'])} | "
            f"Score: {predicted_score} | "
            f"Premium: {predicted_total_premium} | "
            f"Conversion: {predicted_conversion_rate}% | "
            f"Risk: {risk_level} | "
            f"Label: {prediction_label}"
        )

    delete_existing_predictions(
        engine=engine,
        prediction_year=prediction_year,
        prediction_month=prediction_month
    )

    insert_predictions(engine, predictions_to_insert)

    print("\nPredictions inserted successfully into ai_performance_predictions table.")


if __name__ == "__main__":
    try:
        run_prediction()
    except Exception as e:
        print("\nError while predicting next month performance:")
        print(str(e))
