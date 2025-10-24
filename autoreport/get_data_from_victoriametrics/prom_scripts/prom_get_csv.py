import json
import os
import sys
import argparse
from datetime import datetime

basedir = os.environ.get('basedir', '..')

parser = argparse.ArgumentParser(description='Generate csv data', add_help=True, usage=True)
parser.add_argument(
    '--start_time',
    type=str,
    help='START_TIME'
)

parsed_args = parser.parse_args()

start_time = parsed_args.start_time

# Преобразуем json в csv файл

# 1 # RPS_by_label
file_response = os.path.join(basedir, f'json_{start_time}/RPS_by_label.json')
file_output = os.path.join(basedir, f'csv_{start_time}/RPS_by_label.csv')

# Чтение JSON файла
with open(file_response, 'r') as f:
    data = json.load(f)
# Обработка и запись данных в CSV
with open(file_output, 'w') as f:
    # Записываем заголовок
    f.write('date;value\n')
    for entry in data['data']['result']:
        for timestamp, value in entry['values']:
            # Преобразование Unix времени в читаемый формат
            dt = datetime.fromtimestamp(timestamp)
            formatted_date = dt.strftime('%Y-%m-%d %H:%M:%S')
            # Запись в CSV с разделителем ';'
            f.write(f'{formatted_date};{value}\n')

# 2 # Pct_response_times
file_response = os.path.join(basedir, f'json_{start_time}/Pct_response_times.json')
file_output = os.path.join(basedir, f'csv_{start_time}/Pct_response_times.csv')
with open(file_response, 'r') as f:
    data = json.load(f)
with open(file_output, 'w') as f:
    f.write('date;value\n')
    for entry in data['data']['result']:
        for timestamp, value in entry['values']:
            dt = datetime.fromtimestamp(timestamp)
            f.write(f'{formatted_date};{value}\n')

# 3 # Virtual_Users
file_response = os.path.join(basedir, f'json_{start_time}/Virtual_Users.json')
file_output = os.path.join(basedir, f'csv_{start_time}/Virtual_Users.csv')
with open(file_response, 'r') as f:
    data = json.load(f)
with open(file_output, 'w') as f:
    f.write('date;value\n')
    for entry in data['data']['result']:
        for timestamp, value in entry['values']:
            dt = datetime.fromtimestamp(timestamp)
            formatted_date = dt.strftime('%Y-%m-%d %H:%M:%S')
            f.write(f'{formatted_date};{value}\n')

# 4 # RPS_error
file_response = os.path.join(basedir, f'json_{start_time}/RPS_error.json')
file_output = os.path.join(basedir, f'csv_{start_time}/RPS_error.csv')
with open(file_response, 'r') as f:
    data = json.load(f)
with open(file_output, 'w') as f:
    f.write('date;value\n')
    for entry in data['data']['result']:
        for timestamp, value in entry['values']:
            dt = datetime.fromtimestamp(timestamp)
            formatted_date = dt.strftime('%Y-%m-%d %H:%M:%S')
            f.write(f'{formatted_date};{value}\n')

# 5 # RPS_Error_by_message
file_response = os.path.join(basedir, f'json_{start_time}/RPS_Error_by_message.json')
file_output = os.path.join(basedir, f'csv_{start_time}/RPS_Error_by_message.csv')
with open(file_response, 'r') as f:
    data = json.load(f)
with open(file_output, 'w') as f:
    f.write('date;value\n')
    for entry in data['data']['result']:
        for timestamp, value in entry['values']:
            dt = datetime.fromtimestamp(timestamp)
            formatted_date = dt.strftime('%Y-%m-%d %H:%M:%S')
            f.write(f'{formatted_date};{value}\n')

