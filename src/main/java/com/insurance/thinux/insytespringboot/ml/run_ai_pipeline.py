import os
import subprocess
import sys
from datetime import datetime

from pipeline_cleanup import cleanup_pipeline_outputs


def run_script(script_name, script_path):
    """
    Run a Python script and stop the pipeline if it fails.
    """

    print("\n" + "=" * 70)
    print(f"STARTING: {script_name}")
    print("=" * 70)

    start_time = datetime.now()

    if not os.path.exists(script_path):
        raise FileNotFoundError(f"Script not found: {script_path}")

    script_dir = os.path.dirname(script_path)

    result = subprocess.run(
        [sys.executable, script_path],
        cwd=script_dir,
        text=True
    )

    end_time = datetime.now()
    duration = end_time - start_time

    if result.returncode != 0:
        print("\n" + "=" * 70)
        print(f"FAILED: {script_name}")
        print(f"Duration: {duration}")
        print("=" * 70)
        raise Exception(f"{script_name} failed. Pipeline stopped.")

    print("\n" + "=" * 70)
    print(f"COMPLETED: {script_name}")
    print(f"Duration: {duration}")
    print("=" * 70)


def run_ai_pipeline():
    """
    Run full AI pipeline:
    0. Cleanup previous auto-generated pipeline outputs
    1. Train performance model
    2. Predict next month performance
    3. Detect fraud/anomaly patterns
    4. Generate AI recommendations
    """

    base_dir = os.path.dirname(os.path.abspath(__file__))

    scripts = [
        {
            "name": "Train Performance Prediction Model",
            "path": os.path.join(
                base_dir,
                "performance_prediction",
                "train_performance_model.py"
            )
        },
        {
            "name": "Predict Next Month Performance",
            "path": os.path.join(
                base_dir,
                "performance_prediction",
                "predict_next_month_performance.py"
            )
        },
        {
            "name": "Detect Fraud / Anomaly Patterns",
            "path": os.path.join(
                base_dir,
                "fraud_detection",
                "detect_fraud_anomalies.py"
            )
        },
        {
            "name": "Generate AI Recommendations",
            "path": os.path.join(
                base_dir,
                "recommendation_engine",
                "generate_ai_recommendations.py"
            )
        }
    ]

    print("\n" + "#" * 70)
    print("INSYTE AI PIPELINE STARTED")
    print(f"Started at: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("#" * 70)

    pipeline_start = datetime.now()

    print("\n" + "=" * 70)
    print("STARTING: Cleanup Previous Pipeline Outputs")
    print("=" * 70)

    cleanup_start = datetime.now()

    cleanup_pipeline_outputs()

    cleanup_end = datetime.now()
    cleanup_duration = cleanup_end - cleanup_start

    print("\n" + "=" * 70)
    print("COMPLETED: Cleanup Previous Pipeline Outputs")
    print(f"Duration: {cleanup_duration}")
    print("=" * 70)

    for script in scripts:
        run_script(
            script_name=script["name"],
            script_path=script["path"]
        )

    pipeline_end = datetime.now()
    total_duration = pipeline_end - pipeline_start

    print("\n" + "#" * 70)
    print("INSYTE AI PIPELINE COMPLETED SUCCESSFULLY")
    print(f"Completed at: {pipeline_end.strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"Total duration: {total_duration}")
    print("#" * 70)


if __name__ == "__main__":
    try:
        run_ai_pipeline()
    except Exception as e:
        print("\n" + "!" * 70)
        print("INSYTE AI PIPELINE FAILED")
        print(str(e))
        print("!" * 70)
        sys.exit(1)