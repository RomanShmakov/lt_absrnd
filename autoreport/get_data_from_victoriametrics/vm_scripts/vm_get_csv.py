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

# 1 # Throughput
file_response = os.path.join(basedir, f'json_{start_time}/Throughput.json')
file_output = os.path.join(basedir, f'csv_{start_time}/Throughput.csv')
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

# 2 # Response Time - Pass (95th pct)
file_response = os.path.join(basedir, f'json_{start_time}/Pct_95_response_time.json')
file_output = os.path.join(basedir, f'csv_{start_time}/Pct_95_response_time.csv')
with open(file_response, 'r') as f:
    data = json.load(f)
with open(file_output, 'w') as f:
    f.write('date;value\n')
    for entry in data['data']['result']:
        for timestamp, value in entry['values']:
            dt = datetime.fromtimestamp(timestamp)
            f.write(f'{formatted_date};{value}\n')

# 3 # Virtual Users
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

# 4 # Total Error
file_response = os.path.join(basedir, f'json_{start_time}/Total_Error.json')
file_output = os.path.join(basedir, f'csv_{start_time}/Total_Error.csv')
with open(file_response, 'r') as f:
    data = json.load(f)
with open(file_output, 'w') as f:
    f.write('date;value\n')
    for entry in data['data']['result']:
        for timestamp, value in entry['values']:
            dt = datetime.fromtimestamp(timestamp)
            formatted_date = dt.strftime('%Y-%m-%d %H:%M:%S')
            f.write(f'{formatted_date};{value}\n')

# 5 # % Error
file_response = os.path.join(basedir, f'json_{start_time}/Percent_Error.json')
file_output = os.path.join(basedir, f'csv_{start_time}/Percent_Error.csv')
with open(file_response, 'r') as f:
    data = json.load(f)
with open(file_output, 'w') as f:
    f.write('date;value\n')
    for entry in data['data']['result']:
        for timestamp, value in entry['values']:
            dt = datetime.fromtimestamp(timestamp)
            formatted_date = dt.strftime('%Y-%m-%d %H:%M:%S')
            f.write(f'{formatted_date};{value}\n')
