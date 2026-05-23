import os
import pandas as pd
from dotenv import load_dotenv
from sqlalchemy import create_engine


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


def read_agent_performance_data():
    """
    Read agent performance data from agent_performance table.
    This dataset will later be used for ML model training.
    """

    engine = create_db_engine()

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

    return df


def check_dataset(df):
    """
    Basic dataset checking before ML.
    """

    print("\n==============================")
    print("AGENT PERFORMANCE DATASET")
    print("==============================")

    print("\nFirst 5 records:")
    print(df.head())

    print("\nDataset shape:")
    print(df.shape)

    print("\nColumn names:")
    print(df.columns.tolist())

    print("\nMissing values:")
    print(df.isnull().sum())

    print("\nDataset information:")
    print(df.info())

    print("\nBasic statistics:")
    print(df.describe())


def save_dataset_to_csv(df):
    """
    Save loaded dataset into CSV file for checking and future ML use.
    """

    current_dir = os.path.dirname(os.path.abspath(__file__))
    output_path = os.path.join(current_dir, "agent_performance_dataset.csv")

    df.to_csv(output_path, index=False)

    print(f"\nDataset saved successfully to: {output_path}")


if __name__ == "__main__":
    try:
        performance_df = read_agent_performance_data()

        if performance_df.empty:
            print("No data found in agent_performance table.")
            print("Please generate performance first using:")
            print("POST /api/agent-performance/generate?year=2026&month=5")
        else:
            check_dataset(performance_df)
            save_dataset_to_csv(performance_df)

    except Exception as e:
        print("Error while reading agent performance data:")
        print(str(e))
