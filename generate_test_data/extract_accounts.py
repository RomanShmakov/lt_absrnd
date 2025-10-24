import csv
import os
import sys

# Определяем базовую директорию (где находится скрипт)
base_dir = os.path.dirname(os.path.abspath(__file__))
# Путь к исходному файлу
input_file = os.path.join(base_dir, "csv", "ydb", "t_account.csv")
# Путь к выходному файлу в домашней директории (~/)
output_file = os.path.join(os.path.expanduser('~'), "accounts_for_jmeter.csv")

# Проверяем существование директории и создаем при необходимости
os.makedirs(os.path.dirname(input_file), exist_ok=True)

# Проверяем существование входного файла
if not os.path.exists(input_file):
    print(f"Ошибка: Файл {input_file} не найден!")
    sys.exit(1)  # Выходим с кодом ошибки

# Обработка CSV
with open(input_file, 'r', newline='', encoding='utf-8') as infile, \
     open(output_file, 'w', newline='', encoding='utf-8') as outfile:
    
    reader = csv.reader(infile)
    writer = csv.writer(outfile)

    # Пропускаем первые две строки, которые предназначены для ФТ
    #next(reader, None)
    #next(reader, None)

    # Записываем заголовок
    #writer.writerow(['account_number'])
    
    # Обрабатываем каждую строку
    for row in reader:
        if len(row) >= 3:
            # Удаляем кавычки и лишние пробелы
            account = row[2].strip().strip('"')
            writer.writerow([account])

print(f'Успешно создан файл: {output_file}')
print(f'Для использования в JMeter укажите путь: {output_file}')