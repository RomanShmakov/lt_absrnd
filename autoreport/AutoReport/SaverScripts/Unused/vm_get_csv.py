import json
import os


def calculate_average_value(start_time):
    # 1 # Throughput
    os.makedirs(f"./json_{start_time.replace(':', '-')}", exist_ok=True)
    file_response = os.path.join(f"./json_{start_time.replace(':', '-')}/", "Throughput.json")
    # file_output = os.path.join(basedir, f'csv_{start_time}/Throughput.csv')
    # Чтение JSON файла
    with open(file_response, 'r') as f:
        data = json.load(f)
    # Обработка и запись данных в CSV
    values = []
    # Извлечение данных по пути: $.data.result[0].values[*][1]
    try:
        for entry in data['data']['result'][0]['values']:
            values.append(float(entry[1]))
    except (KeyError, IndexError) as e:
        print(f"Ошибка при извлечении данных: {e}")
        values = []
    # Вычисляем среднее значение
    if values:
        average_value = sum(values) / len(values)
        print(f"Среднее значение: {average_value}")
    else:
        print("Нет данных для вычисления среднего значения")

# with open(file_output, 'w') as f:
#     # Записываем заголовок
#     f.write('date;value\n')
#     for entry in data['data']['result']:
#         for timestamp, value in entry['values']:
#             # Преобразование Unix времени в читаемый формат
#             dt = datetime.fromtimestamp(timestamp)
#             formatted_date = dt.strftime('%Y-%m-%d %H:%M:%S')
#             # Запись в CSV с разделителем ';'
#             f.write(f'{formatted_date};{value}\n')

# # 2 # Response Time - Pass (95th pct)
# file_response = os.path.join(basedir, f'json_{start_time}/Pct_95_response_time.json')
# file_output = os.path.join(basedir, f'csv_{start_time}/Pct_95_response_time.csv')
# with open(file_response, 'r') as f:
#     data = json.load(f)
# with open(file_output, 'w') as f:
#     f.write('date;value\n')
#     for entry in data['data']['result']:
#         for timestamp, value in entry['values']:
#             dt = datetime.fromtimestamp(timestamp)
#             f.write(f'{formatted_date};{value}\n')
#
# # 3 # Virtual Users
# file_response = os.path.join(basedir, f'json_{start_time}/Virtual_Users.json')
# file_output = os.path.join(basedir, f'csv_{start_time}/Virtual_Users.csv')
# with open(file_response, 'r') as f:
#     data = json.load(f)
# with open(file_output, 'w') as f:
#     f.write('date;value\n')
#     for entry in data['data']['result']:
#         for timestamp, value in entry['values']:
#             dt = datetime.fromtimestamp(timestamp)
#             formatted_date = dt.strftime('%Y-%m-%d %H:%M:%S')
#             f.write(f'{formatted_date};{value}\n')
#
# # 4 # Total Error
# file_response = os.path.join(basedir, f'json_{start_time}/Total_Error.json')
# file_output = os.path.join(basedir, f'csv_{start_time}/Total_Error.csv')
# with open(file_response, 'r') as f:
#     data = json.load(f)
# with open(file_output, 'w') as f:
#     f.write('date;value\n')
#     for entry in data['data']['result']:
#         for timestamp, value in entry['values']:
#             dt = datetime.fromtimestamp(timestamp)
#             formatted_date = dt.strftime('%Y-%m-%d %H:%M:%S')
#             f.write(f'{formatted_date};{value}\n')
#
# # 5 # % Error
# file_response = os.path.join(basedir, f'json_{start_time}/Percent_Error.json')
# file_output = os.path.join(basedir, f'csv_{start_time}/Percent_Error.csv')
# with open(file_response, 'r') as f:
#     data = json.load(f)
# with open(file_output, 'w') as f:
#     f.write('date;value\n')
#     for entry in data['data']['result']:
#         for timestamp, value in entry['values']:
#             dt = datetime.fromtimestamp(timestamp)
#             formatted_date = dt.strftime('%Y-%m-%d %H:%M:%S')
#             f.write(f'{formatted_date};{value}\n')
