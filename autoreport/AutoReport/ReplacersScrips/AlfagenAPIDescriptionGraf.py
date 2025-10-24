import os
import re
import time

import urllib3
import yaml
import requests
# config
from constants import (CONFIG_NAME)

# config #
base_path = '.'
config_file = os.path.join(base_path, CONFIG_NAME)
config = yaml.safe_load(open(config_file, 'r'))

# Отключает предупреждения о небезопасном SSL-соединении
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

api_key = config["alfagen_api_key"]


def get_description_for_table(table_error_data, purpose_graf, purpose_test):
    # получаем описание графика от ИИ
    description = post_request_to_llm_table(table_error_data, purpose_graf, purpose_test)
    return description


def get_description_for_graf(image_name, purpose_graf, purpose_test):
    # загружаем файл

    # получаем taskId файла
    task_id = post_file_image_taskId(image_name)
    # task_id = '827c8ad7-7d95-4363-bcfd-f7f1721bde4e'

    # получаем fileId файла
    file_id = get_file_image_fileId(task_id)
    # file_id = '199869ed-df30-431b-b8ca-b736a58e354a'
    # получаем описание графика от ИИ
    description = post_request_to_llm(purpose_graf, purpose_test, file_id)
    return description


# 1
def post_file_image_taskId(image_name):
    file_path = image_name

    url = "https://agenapisandbox.moscow.alfaintra.net/internal/llm/v1/upload-file"
    # Формируем данные для отправки
    files = {
        "file": (file_path, open(file_path, "rb"), "image/png")
    }
    # авторизация через API key в grafana
    headers = {
        "systemId": "sanduser",
        "Authorization": api_key,
    }
    # Отправляем POST-запрос
    try:
        response = requests.post(url, files=files, headers=headers, verify=False, timeout=300)
        response.raise_for_status()  # Вызывает исключение для кодов 4xx/5xx
        print("Успешно! Ответ сервера:")
        print(response.json())
    except requests.exceptions.RequestException as e:
        print(f"Произошла ошибка: {e}")
    return response.json()["taskId"]


# 2
def get_file_image_fileId(task_id):
    url = f"https://agenapisandbox.moscow.alfaintra.net/internal/llm/v1/upload-file/{task_id}/sse"
    # авторизация через API key в grafana
    headers = {
        "systemId": "sanduser",
        "Authorization": api_key
    }
    isNotCompleted = True
    while isNotCompleted:
        # Отправляем POST-запрос
        try:
            response = requests.get(url, headers=headers, verify=False, timeout=300)
            response.raise_for_status()  # Вызывает исключение для кодов 4xx/5xx
            print("Успешно! Ответ сервера:")
            print(response.json())
        except requests.exceptions.RequestException as e:
            print(f"Произошла ошибка: {e}")

        # Регулярное выражение для извлечения status
        status_pattern = r'"status":"([^"]*)"'
        status_match = re.search(status_pattern, response.text)
        if status_match:
            status = status_match.group(1)
            print(f"Status: {status}")

        if status == "COMPLETED":
            # Регулярное выражение для извлечения fileId
            fileid_pattern = r'"fileId":"([^"]*)"'
            fileid_match = re.search(fileid_pattern, response.text)
            if fileid_match:
                file_id = fileid_match.group(1)
                print(f"File ID: {file_id}")
            return file_id
        else:
            time.sleep(30)


# 3
def post_request_to_llm(purpose_graf, purpose_test, file_id):
    url = "https://agenapisandbox.moscow.alfaintra.net/internal/llm/v1/chat/completions"
    headers = {
        "systemId": "sanduser",
        "Authorization": api_key,
        "Content-Type": "application/json"
    }

    # Данные запроса для графика
    data = {
        "model": "qwen-2.5-32b-vl",
        "n": 1,
        "messages": [
            {
                "role": "user",
                "content": [
                    {
                        "type": "text",
                        "text": "Отвечай как ведущий инженер по нагрузочному тестированию. Твоя задача — выявлять признаки деградации системы по графикам и давать краткий структурированный анализ. Представь, что у тебя есть доступ к графику и его таймстампам; внимательно его проанализируй и выдай выводы в заданном формате (см. ниже)."
                    },
                    {
                        "type": "text",
                        "text": "Правила и формат ответа (обязательно соблюдай): 1. Формат ответа — компактный и структурированный: 1.1. Краткий вывод (Да / Нет — деградация), 1.2. Короткое обоснование (конкретные временные интервалы и метрики), 1.3. Оценка серьёзности (минимальная / умеренная / критическая), 1.4. Уровень уверенности в процентах, 1.5. Рекомендуемые смежные графики для проверки (список), 1.6. Рекомендуемые первые действия для расследования. Каждый пункт нумеруй. 2. Будь предельно конкретен: указывай временные интервалы (например, «с 12:10 до 12:35»), относительные изменения в процентах (например, «падение на 30%»), и какие аномалии увидел (падение RPS, рост latency, рост ошибок и т.п.). 3. Не давай длинных рассуждений — только сжато по пунктам. Если причина неочевидна, пометь как гипотеза и кратко объясни, какие дополнительные данные нужны для подтверждения. 4. Если видишь аномалию, перечисли 3–5 смежных метрик/графиков, которые первыми следует проверить (например: latency P95, error rate, CPU на нодах, GC pause time, queue length), и кратко — зачем. 5. Не делай категоричных причинных утверждений без корреляции по времени и метрикам — вместо этого помечай как «возможная причина»."
                    },
                    {
                        "type": "text",
                        "text": "Примеры формулировок (ориентир): 1. «Наблюдение: RPS (ExecuteDataQuery, ExecuteQuery, BeginTransaction, CommitTransaction) стабильно коррелирует с подаваемой нагрузкой по всему тесту. Интервалы аномалий не обнаружены. Вывод: деградации нет.» 2. «Наблюдение: RPS упала на ~90% в середине теста (с 12:15 до 12:25). Возможные причины: отключение компонентов, рост ошибок, недоступность приложения. Рекомендуемые проверки: error rate, downstream service availability, логи на время падения.» 3. «Наблюдение: ExecuteDataQuery нестабильна — после выхода на нагрузку метрики ExecuteQuery/BeginTransaction/CommitTransaction просели на 20–50%. Интервал: 12:05–12:20. Возможная причина: деградация по времени ответа или рост ошибок. Рекомендуемые проверки: latency P95/P99, error rate, CPU и GC на DB-нодах.»"
                    },
                    {
                        "type": "text",
                        "text": f"Вопрос (исходные данные для анализа): {purpose_graf} {purpose_test}. Внимательно посмотри на график и ответь по формату: 1. Есть ли деградация? 2. Короткое обоснование с временными интервалами и процентными изменениями. 3. Степень серьёзности. 4. Уровень уверенности (0–100%). 5. 3–5 смежных графиков для проверки (и почему). 6. Первые шаги для расследования."
                    },
                    {
                        "type": "file",
                        "file": {
                            "file_id": f"{file_id}"
                        }
                    }
                ]
            }
        ]
    }

    try:
        # Отправка POST запроса
        # используем параметр json для автоматической сериализации
        response = requests.post(url, json=data, headers=headers, timeout=300, verify=False)

        # Проверка ответа
        if response.status_code == 200:
            print("✅ Запрос выполнен успешно!")
            result = response.json()
            result_content = result["choices"][0]["message"]["content"]
            return result_content
        else:
            print(f"❌ Ошибка: {response.status_code}")
            print(f"Ответ сервера: {response.text}")
            return None

    except requests.exceptions.RequestException as e:
        print(f"🚫 Ошибка соединения: {e}")
        return None


def post_request_to_llm_table(purpose_graf, purpose_test, file_id):
    url = "https://agenapisandbox.moscow.alfaintra.net/internal/llm/v1/chat/completions"
    headers = {
        "systemId": "sanduser",
        "Authorization": api_key,
        "Content-Type": "application/json"
    }

    # Данные запроса для таблицы
    data = {
        "model": "qwen-2.5-32b-vl",
        "n": 1,
        "messages": [
            {
                "role": "user",
                "content": [
                    {
                        "type": "text",
                        "text": "Отвечай как ведущий инженер по нагрузочному тестированию. Твоя задача — выявлять признаки деградации системы по графикам и давать краткий структурированный анализ. Представь, что у тебя есть доступ к графику и его таймстампам; внимательно его проанализируй и выдай выводы в заданном формате (см. ниже)."
                    },
                    {
                        "type": "text",
                        "text": "Правила и формат ответа (обязательно соблюдай): 1. Формат ответа — компактный и структурированный: 1.1. Краткий вывод (Да / Нет — деградация), 1.2. Короткое обоснование (конкретные временные интервалы и метрики), 1.3. Оценка серьёзности (минимальная / умеренная / критическая), 1.4. Уровень уверенности в процентах, 1.5. Рекомендуемые смежные графики для проверки (список), 1.6. Рекомендуемые первые действия для расследования. Каждый пункт нумеруй. 2. Будь предельно конкретен: указывай временные интервалы (например, «с 12:10 до 12:35»), относительные изменения в процентах (например, «падение на 30%»), и какие аномалии увидел (падение RPS, рост latency, рост ошибок и т.п.). 3. Не давай длинных рассуждений — только сжато по пунктам. Если причина неочевидна, пометь как гипотеза и кратко объясни, какие дополнительные данные нужны для подтверждения. 4. Если видишь аномалию, перечисли 3–5 смежных метрик/графиков, которые первыми следует проверить (например: latency P95, error rate, CPU на нодах, GC pause time, queue length), и кратко — зачем. 5. Не делай категоричных причинных утверждений без корреляции по времени и метрикам — вместо этого помечай как «возможная причина»."
                    },
                    {
                        "type": "text",
                        "text": "Примеры формулировок (ориентир): 1. «Наблюдение: RPS (ExecuteDataQuery, ExecuteQuery, BeginTransaction, CommitTransaction) стабильно коррелирует с подаваемой нагрузкой по всему тесту. Интервалы аномалий не обнаружены. Вывод: деградации нет.» 2. «Наблюдение: RPS упала на ~90% в середине теста (с 12:15 до 12:25). Возможные причины: отключение компонентов, рост ошибок, недоступность приложения. Рекомендуемые проверки: error rate, downstream service availability, логи на время падения.» 3. «Наблюдение: ExecuteDataQuery нестабильна — после выхода на нагрузку метрики ExecuteQuery/BeginTransaction/CommitTransaction просели на 20–50%. Интервал: 12:05–12:20. Возможная причина: деградация по времени ответа или рост ошибок. Рекомендуемые проверки: latency P95/P99, error rate, CPU и GC на DB-нодах.»"
                    },
                    {
                        "type": "text",
                        "text": f"Вопрос (исходные данные для анализа): {purpose_test}. Внимательно посмотри на график и ответь по формату: 1. Есть ли деградация? 2. Короткое обоснование с временными интервалами и процентными изменениями. 3. Степень серьёзности. 4. Уровень уверенности (0–100%). 5. 3–5 смежных графиков для проверки (и почему). 6. Первые шаги для расследования."
                    },
                    {
                        "type": "text",
                        "text": purpose_graf
                    }
                ]
            }
        ]
    }

    try:
        # Отправка POST запроса
        # используем параметр json для автоматической сериализации
        response = requests.post(url, json=data, headers=headers, timeout=300, verify=False)

        # Проверка ответа
        if response.status_code == 200:
            print("✅ Запрос выполнен успешно!")
            result = response.json()
            result_content = result["choices"][0]["message"]["content"]
            return result_content
        else:
            print(f"❌ Ошибка: {response.status_code}")
            print(f"Ответ сервера: {response.text}")
            return None

    except requests.exceptions.RequestException as e:
        print(f"🚫 Ошибка соединения: {e}")
        return None
