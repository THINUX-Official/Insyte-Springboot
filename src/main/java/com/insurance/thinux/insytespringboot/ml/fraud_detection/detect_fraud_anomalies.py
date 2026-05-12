import os
import joblib
import pandas as pd

from dotenv import load_dotenv
from sqlalchemy import create_engine, text

from sklearn.ensemble import IsolationForest
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import precision_score, recall_score, f1_score, accuracy_score


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


def read_agent_performance_data(engine):
    """
    Read agent performance data for anomaly detection.
    """

    query = """
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
        ORDER BY ap.performance_year, ap.performance_month, ap.agent_id
    """

    df = pd.read_sql(query, engine)

    if df.empty:
        raise Exception("No data found in agent_performance table.")

    return df


def prepare_anomaly_features(df):
    """
    Prepare features for Isolation Forest anomaly detection.
    """

    df = df.copy()

    numeric_columns = [
        "agent_id",
        "supervisor_id",
        "performance_year",
        "performance_month",
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
    ]

    for column in numeric_columns:
        df[column] = pd.to_numeric(df[column], errors="coerce")

    df = df.fillna(0)

    # Extra calculated features useful for anomaly detection
    df["cancelled_rate"] = df.apply(
        lambda row: (row["cancelled_leads"] / row["total_leads"] * 100)
        if row["total_leads"] > 0 else 0,
        axis=1
    )

    df["on_hold_rate"] = df.apply(
        lambda row: (row["on_hold_leads"] / row["total_leads"] * 100)
        if row["total_leads"] > 0 else 0,
        axis=1
    )

    df["premium_gap"] = df["total_premium"] - df["target_premium"]

    feature_columns = [
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
        "cancelled_rate",
        "on_hold_rate",
        "premium_gap",
    ]

    X = df[feature_columns]

    return df, X, feature_columns


def train_isolation_forest(X):
    """
    Train Isolation Forest model.
    contamination controls expected anomaly percentage.
    For demo/research, 0.12 means around 12% of records may be marked anomalous.
    """

    if len(X) < 10:
        raise Exception(
            "Dataset is too small for anomaly detection. "
            "Please add more agent_performance records first."
        )

    scaler = StandardScaler()
    X_scaled = scaler.fit_transform(X)

    model = IsolationForest(
        n_estimators=150,
        contamination=0.12,
        random_state=42
    )

    model.fit(X_scaled)

    return model, scaler, X_scaled


def classify_alert_type(row):
    """
    Decide fraud/anomaly alert type based on record pattern.
    """

    conversion_rate = float(row["conversion_rate"])
    cancelled_rate = float(row["cancelled_rate"])
    total_premium = float(row["total_premium"])
    target_achievement = float(row["target_achievement_percentage"])
    performance_score = float(row["performance_score"])

    if conversion_rate >= 70 and total_premium >= 300000:
        return "HIGH_CONVERSION_ANOMALY"

    if total_premium >= 350000 or target_achievement >= 150:
        return "UNUSUAL_PREMIUM_PATTERN"

    if cancelled_rate >= 35:
        return "FAST_STATUS_CHANGE"

    if performance_score <= 35 and cancelled_rate >= 30:
        return "LOCATION_ANOMALY"

    return "OTHER"


def classify_severity(anomaly_score, row):
    """
    Decide severity based on anomaly score and business values.
    Isolation Forest lower score means more abnormal.
    """

    cancelled_rate = float(row["cancelled_rate"])
    target_achievement = float(row["target_achievement_percentage"])
    performance_score = float(row["performance_score"])

    if anomaly_score <= -0.20 or cancelled_rate >= 40 or target_achievement >= 160:
        return "CRITICAL"

    if anomaly_score <= -0.10 or cancelled_rate >= 30 or performance_score <= 35:
        return "HIGH"

    if anomaly_score <= -0.03:
        return "MEDIUM"

    return "LOW"


def build_alert_description(row, alert_type, severity, anomaly_score):
    """
    Build readable fraud/anomaly alert description.
    """

    username = row.get("username", "Unknown")
    year = int(row["performance_year"])
    month = int(row["performance_month"])

    total_leads = int(row["total_leads"])
    converted_leads = int(row["converted_leads"])
    cancelled_leads = int(row["cancelled_leads"])

    total_premium = float(row["total_premium"])
    target_premium = float(row["target_premium"])
    conversion_rate = float(row["conversion_rate"])
    cancelled_rate = float(row["cancelled_rate"])
    performance_score = float(row["performance_score"])

    return (
        f"Anomaly detected for agent {username} in {year}-{month:02d}. "
        f"Alert type: {alert_type}. Severity: {severity}. "
        f"Anomaly score: {anomaly_score:.4f}. "
        f"Total leads: {total_leads}, converted leads: {converted_leads}, "
        f"cancelled leads: {cancelled_leads}, conversion rate: {conversion_rate:.2f}%, "
        f"cancelled rate: {cancelled_rate:.2f}%, total premium: {total_premium:.2f}, "
        f"target premium: {target_premium:.2f}, performance score: {performance_score:.2f}. "
        f"This pattern should be reviewed by a supervisor."
    )


def delete_existing_ml_generated_alerts(engine):
    """
    Delete previous ML-generated open alerts to avoid duplicates when script is re-run.

    This deletes only alerts whose description starts with 'Anomaly detected for agent'.
    Manually created or reviewed alerts are not touched.
    """

    delete_query = text("""
        DELETE FROM ai_fraud_alerts
        WHERE description LIKE 'Anomaly detected for agent%'
        AND status = 'OPEN'
    """)

    with engine.begin() as connection:
        connection.execute(delete_query)


def insert_fraud_alerts(engine, alerts):
    """
    Insert anomaly alerts into ai_fraud_alerts.
    """

    if not alerts:
        print("\nNo anomaly alerts to insert.")
        return

    insert_query = text("""
        INSERT INTO ai_fraud_alerts
        (
            agent_id,
            lead_id,
            alert_type,
            severity,
            anomaly_score,
            description,
            status,
            detected_at
        )
        VALUES
        (
            :agent_id,
            NULL,
            :alert_type,
            :severity,
            :anomaly_score,
            :description,
            'OPEN',
            NOW(6)
        )
    """)

    with engine.begin() as connection:
        connection.execute(insert_query, alerts)

    print(f"\nInserted {len(alerts)} anomaly alert(s) into ai_fraud_alerts table.")


def save_model_and_scaler(model, scaler, feature_columns):
    """
    Save Isolation Forest model, scaler, and feature list.
    """

    current_dir = os.path.dirname(os.path.abspath(__file__))
    model_dir = os.path.join(current_dir, "models")

    os.makedirs(model_dir, exist_ok=True)

    model_path = os.path.join(model_dir, "fraud_anomaly_model.pkl")
    scaler_path = os.path.join(model_dir, "fraud_anomaly_scaler.pkl")
    features_path = os.path.join(model_dir, "fraud_anomaly_features.pkl")

    joblib.dump(model, model_path)
    joblib.dump(scaler, scaler_path)
    joblib.dump(feature_columns, features_path)

    print(f"\nFraud anomaly model saved to: {model_path}")
    print(f"Scaler saved to: {scaler_path}")
    print(f"Feature list saved to: {features_path}")


def save_experiment_result(engine, dataset_size, total_anomalies):
    """
    Save fraud detection experiment result into ml_model_experiments.

    Since this is unsupervised learning, true labels may not exist.
    So precision/recall/F1 here are optional prototype values.
    """

    anomaly_rate = round((total_anomalies / dataset_size), 4) if dataset_size > 0 else 0

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
            :accuracy_score,
            NULL,
            NULL,
            NULL,
            :precision_score,
            :recall_score,
            :f1_score,
            :model_version,
            :remarks,
            NOW(6)
        )
    """)

    params = {
        "model_name": "Fraud Anomaly Detection Model",
        "model_type": "FRAUD_DETECTION",
        "algorithm": "Isolation Forest",
        "dataset_size": dataset_size,
        "accuracy_score": None,
        "precision_score": None,
        "recall_score": None,
        "f1_score": None,
        "model_version": "v1.0",
        "remarks": (
            f"Isolation Forest anomaly detection executed using agent_performance data. "
            f"Detected {total_anomalies} anomalies from {dataset_size} records. "
            f"Anomaly rate: {anomaly_rate}. This is an unsupervised model, so labelled "
            f"accuracy metrics were not calculated."
        ),
    }

    with engine.begin() as connection:
        connection.execute(insert_query, params)

    print("\nFraud detection experiment result saved into ml_model_experiments table.")


def detect_anomalies():
    engine = create_db_engine()

    df = read_agent_performance_data(engine)
    df, X, feature_columns = prepare_anomaly_features(df)

    model, scaler, X_scaled = train_isolation_forest(X)

    # Isolation Forest prediction:
    #  1 = normal
    # -1 = anomaly
    predictions = model.predict(X_scaled)

    # decision_function:
    # lower score = more abnormal
    anomaly_scores = model.decision_function(X_scaled)

    df["anomaly_prediction"] = predictions
    df["anomaly_score_raw"] = anomaly_scores

    anomalies = df[df["anomaly_prediction"] == -1].copy()

    alerts = []

    print("\n==============================")
    print("FRAUD / ANOMALY DETECTION")
    print("==============================")
    print(f"Total records checked : {len(df)}")
    print(f"Anomalies detected    : {len(anomalies)}")

    if anomalies.empty:
        print("\nNo suspicious performance patterns detected.")
    else:
        print("\nDetected anomalies:")

        for _, row in anomalies.iterrows():
            anomaly_score = round(float(row["anomaly_score_raw"]), 4)
            alert_type = classify_alert_type(row)
            severity = classify_severity(anomaly_score, row)
            description = build_alert_description(
                row=row,
                alert_type=alert_type,
                severity=severity,
                anomaly_score=anomaly_score
            )

            alert = {
                "agent_id": int(row["agent_id"]),
                "alert_type": alert_type,
                "severity": severity,
                "anomaly_score": abs(anomaly_score),
                "description": description,
            }

            alerts.append(alert)

            print(
                f"Agent {int(row['agent_id'])} | "
                f"{int(row['performance_year'])}-{int(row['performance_month']):02d} | "
                f"Type: {alert_type} | "
                f"Severity: {severity} | "
                f"Score: {anomaly_score}"
            )

    delete_existing_ml_generated_alerts(engine)
    insert_fraud_alerts(engine, alerts)

    save_model_and_scaler(model, scaler, feature_columns)

    save_experiment_result(
        engine=engine,
        dataset_size=len(df),
        total_anomalies=len(anomalies)
    )

    print("\nFraud anomaly detection completed successfully.")


if __name__ == "__main__":
    try:
        detect_anomalies()
    except Exception as e:
        print("\nError while detecting fraud/anomaly patterns:")
        print(str(e))
