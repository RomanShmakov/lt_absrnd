import os
import sys
from pathlib import Path
import requests

# Получаем абсолютный путь к директории скрипта и поднимаемся на уровень выше
basedir = Path(__file__).parent.resolve()
basedir = basedir.parent.resolve()

# Константы
url = "http://monitor.dev.moscow.alfaintra.net:8428"
step = "5s"


def get_metrics_from_graf(start_time, end_time, app_jmeter, transaction_jmeter):
    # Создаем директорию для результатов
    output_dir = basedir / f"json_{start_time.replace(':', '-')}"
    output_dir.mkdir(parents=True, exist_ok=True)

    # 1. Throughput
    query = f'jmeter_count{{application="{app_jmeter}",transaction="all"}}/5'
    params = {
        'query': query,
        'start': start_time,
        'end': end_time,
        'step': step
    }
    out_json = output_dir / "Throughput.json"
    get_json_with_values_from_victoria(params, out_json)

    # 2 # Response Time - Pass (95th pct)
    query = f'jmeter_pct95.0{{application=\"{app_jmeter}\",transaction=\"{transaction_jmeter}\",statut=\"ok\"}}'
    params = {
        'query': query,
        'start': start_time,
        'end': end_time,
        'step': step
    }
    out_json = output_dir / "Pct_95_response_time.json"
    get_json_with_values_from_victoria(params, out_json)

    # 3 # % Error
    query = f'sum(jmeter_count{{application=\"{app_jmeter}\",transaction=~\"{transaction_jmeter}\",statut=\"ko\"}})*100/sum(jmeter_count{{APP_JMETER=\"{app_jmeter}\",transaction=~\"{transaction_jmeter}\",statut=\"all\"}})'
    params = {
        'query': query,
        'start': start_time,
        'end': end_time,
        'step': step
    }
    out_json = output_dir / "Percent_Error.json"
    get_json_with_values_from_victoria(params, out_json)

    # 4 # Virtual Users
    query = f'jmeter_maxAT{{application=\"{app_jmeter}\"}}'
    params = {
        'query': query,
        'start': start_time,
        'end': end_time,
        'step': step
    }
    out_json = output_dir / "Virtual_Users.json"
    get_json_with_values_from_victoria(params, out_json)


def get_json_with_values_from_victoria(params, out_json):
    try:
        response = requests.get(
            f"{url}/api/v1/query_range",
            params=params,
            timeout=120
        )
        response.raise_for_status()  # Проверка на ошибки HTTP
        with open(out_json, 'w') as f:
            f.write(response.text)
    except requests.exceptions.RequestException as e:
        print(f"Ошибка при выполнении запроса: {e}")
        sys.exit(1)
