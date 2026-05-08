import os
import joblib
import pandas as pd

from dotenv import load_dotenv
from sqlalchemy import create_engine, text

from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_absolute_error, mean_squared_error, r2_score
from sklearn.linear_model import LinearRegression
from sklearn.tree import DecisionTreeRegressor
from sklearn.ensemble import RandomForestRegressor


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
    Read agent performance records from MySQL.
    """

    query = """
        SELECT
            ap.id,
            ap.agent_id,
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

    return pd.read_sql(query, engine)


def prepare_dataset(df):
    """
    Prepare features and target for ML model training.
    """

    if df.empty:
        raise Exception("No data found in agent_performance table.")

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

    feature_columns = [
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
    ]

    target_column = "performance_score"

    X = df[feature_columns]
    y = df[target_column]

    return X, y, feature_columns


def train_and_compare_models(X, y):
    """
    Train multiple regression models and return the best one based on RMSE.
    """

    if len(X) < 5:
        raise Exception(
            "Dataset is too small for model training. "
            "Please add more agent_performance records first. "
            "Recommended minimum: 30+ records."
        )

    test_size = 0.25

    if len(X) < 20:
        test_size = 0.30

    X_train, X_test, y_train, y_test = train_test_split(
        X,
        y,
        test_size=test_size,
        random_state=42
    )

    models = {
        "Linear Regression": LinearRegression(),
        "Decision Tree Regressor": DecisionTreeRegressor(random_state=42),
        "Random Forest Regressor": RandomForestRegressor(
            n_estimators=100,
            random_state=42
        ),
    }

    results = []

    for model_name, model in models.items():
        model.fit(X_train, y_train)

        y_pred = model.predict(X_test)

        mae = mean_absolute_error(y_test, y_pred)
        mse = mean_squared_error(y_test, y_pred)
        rmse = mse ** 0.5
        r2 = r2_score(y_test, y_pred)

        results.append({
            "model_name": "Agent Performance Prediction Model",
            "algorithm": model_name,
            "model": model,
            "mae": mae,
            "rmse": rmse,
            "r2_score": r2,
        })

    results = sorted(results, key=lambda item: item["rmse"])

    return results[0], results


def save_best_model(best_result, feature_columns):
    """
    Save trained model and feature list.
    """

    current_dir = os.path.dirname(os.path.abspath(__file__))
    model_dir = os.path.join(current_dir, "models")

    os.makedirs(model_dir, exist_ok=True)

    model_path = os.path.join(model_dir, "performance_prediction_model.pkl")
    features_path = os.path.join(model_dir, "performance_prediction_features.pkl")

    joblib.dump(best_result["model"], model_path)
    joblib.dump(feature_columns, features_path)

    print(f"\nBest model saved to: {model_path}")
    print(f"Feature list saved to: {features_path}")

    return model_path


def save_experiment_result(engine, best_result, dataset_size):
    """
    Save best model evaluation result into ml_model_experiments table.
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
            :accuracy_score,
            :mae,
            :rmse,
            :r2_score,
            :precision_score,
            :recall_score,
            :f1_score,
            :model_version,
            :remarks,
            NOW(6)
        )
    """)

    params = {
        "model_name": best_result["model_name"],
        "model_type": "PERFORMANCE_PREDICTION",
        "algorithm": best_result["algorithm"],
        "dataset_size": dataset_size,
        "accuracy_score": None,
        "mae": round(float(best_result["mae"]), 4),
        "rmse": round(float(best_result["rmse"]), 4),
        "r2_score": round(float(best_result["r2_score"]), 4),
        "precision_score": None,
        "recall_score": None,
        "f1_score": None,
        "model_version": "v1.0",
        "remarks": "Performance prediction model trained using agent_performance table data.",
    }

    with engine.begin() as connection:
        connection.execute(insert_query, params)

    print("\nExperiment result saved into ml_model_experiments table.")


def print_model_results(results):
    """
    Print model comparison result.
    """

    print("\n==============================")
    print("MODEL TRAINING RESULTS")
    print("==============================")

    for index, result in enumerate(results, start=1):
        print(f"\n{index}. {result['algorithm']}")
        print(f"MAE      : {result['mae']:.4f}")
        print(f"RMSE     : {result['rmse']:.4f}")
        print(f"R2 Score : {result['r2_score']:.4f}")

    best = results[0]

    print("\n==============================")
    print("BEST MODEL")
    print("==============================")
    print(f"Algorithm: {best['algorithm']}")
    print(f"MAE      : {best['mae']:.4f}")
    print(f"RMSE     : {best['rmse']:.4f}")
    print(f"R2 Score : {best['r2_score']:.4f}")


if __name__ == "__main__":
    try:
        engine = create_db_engine()

        df = read_agent_performance_data(engine)

        print("\nLoaded dataset:")
        print(df.head())
        print(f"\nDataset size: {len(df)} records")

        X, y, feature_columns = prepare_dataset(df)

        best_result, all_results = train_and_compare_models(X, y)

        print_model_results(all_results)

        save_best_model(best_result, feature_columns)

        save_experiment_result(
            engine=engine,
            best_result=best_result,
            dataset_size=len(df)
        )

        print("\nPerformance model training completed successfully.")

    except Exception as e:
        print("\nError while training performance prediction model:")
        print(str(e))
