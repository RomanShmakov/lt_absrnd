import os
import time
import requests
import urllib3
import yaml

from requests import RequestException

# пример:
# curl -k -H "Authorization: Bearer glsa_4Iq26vU7DEVRW4IUPN7h2Wq366ZQh0xW_9fbf59a0" "https://graf.protod.moscow.alfaintra.net/render/d-solo/b40939ab-c9b2-465a-abb1-4b479a2c2d4c/jmeter-dashboard-victoriametrics?orgId=1&panelId=35&from=2025-09-23T12:56:13.929Z&to=2025-09-23T14:16:14.826Z&width=1000&height=500" -o graph.png


# config #
from constants import (CONFIG_NAME)

base_path = '.'
config_file = os.path.join(base_path, CONFIG_NAME)
config = yaml.safe_load(open(config_file, 'r'))
# config #

# размеры изображения для скачивания
width_img = 1500
height_img = 1000

# дашборды #
# JMeter
dashboard_uid_jmeter = "b40939ab-c9b2-465a-abb1-4b479a2c2d4c"
slug_jmeter = "jmeter-dashboard-victoriametrics"
# DBStatus
dashboard_uid_DBStatus = "dbstatus_report_lt"
slug_DBStatus = "db-status-report-lt"
# DBOverview
dashboard_uid_DBOverview = "dboverview_report_lt"
slug_DBOverview = "db-overview-report-lt"

# параметры для подключения к API grafana
grafana_url = "https://graf.protod.moscow.alfaintra.net"

# Отключает предупреждения о небезопасном SSL-соединении
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

# config #
from constants import (CONFIG_NAME)

base_path = '.'
config_file = os.path.join(base_path, CONFIG_NAME)
config = yaml.safe_load(open(config_file, 'r'))

# config #
api_key = config["api_key"]

# авторизация через API key в grafana
headers = {
    "Authorization": f"Bearer {api_key}",
    "Content-Type": "application/json",
    "Accept-Language": "ru,en;q=0.9"
}

successful_download = True


def save_all_graf_from_grafana():
    global successful_download

    start_test = config["start_time"]
    end_test = config["end_time"]

    # JMETER #

    # 1_Throughput
    GetImageGrafFromJmeter(37, "image1", start_test, end_test)
    # 2_VirtualUsers
    GetImageGrafFromJmeter(22, "image2", start_test, end_test)
    # 3_ResponseTime
    GetImageGrafFromJmeter(44, "image3", start_test, end_test)
    # 4_PercentErrors
    GetImageGrafFromJmeter(42, "image4", start_test, end_test)

    # 5_ErrorByMessage
    GetImageGrafFromJmeter(46, "image5", start_test, end_test)

    # DB status #

    # 6_QueryLatency
    GetImageGrafFromDBStatus(70, "image6", start_test, end_test)
    # 7_SessionCount
    GetImageGrafFromDBStatus(12, "image7", start_test, end_test)

    # DB overview #

    # 8_Requests
    GetImageGrafFromDBOverview(2, "image8", start_test, end_test)
    # 9_ErrorsType
    GetImageGrafFromDBOverview(9, "image9", start_test, end_test)
    # 10_DroppedRequests
    GetImageGrafFromDBOverview(59, "image10", start_test, end_test)
    # 11_DroppedResponse
    GetImageGrafFromDBOverview(60, "image11", start_test, end_test)
    # 12_RequestsInFlight
    GetImageGrafFromDBOverview(61, "image12", start_test, end_test)
    # 13_UserPool
    GetImageGrafFromDBOverview(32, "image13", start_test, end_test)
    # 14_SystemPool
    GetImageGrafFromDBOverview(49, "image14", start_test, end_test)
    # 15_ICPool
    GetImageGrafFromDBOverview(51, "image15", start_test, end_test)
    # 16_BatchPool
    GetImageGrafFromDBOverview(50, "image16", start_test, end_test)
    # 17_IOPool
    GetImageGrafFromDBOverview(52, "image17", start_test, end_test)
    # 18_TxReadOnly
    GetImageGrafFromDBOverview(17, "image18", start_test, end_test)
    # 19_TxWriteOnly
    GetImageGrafFromDBOverview(19, "image19", start_test, end_test)
    # 20_TxReadWrite
    GetImageGrafFromDBOverview(20, "image20", start_test, end_test)
    # 21_DataShardThroughput
    GetImageGrafFromDBOverview(67, "image21", start_test, end_test)
    # 22_ShardDistribution
    GetImageGrafFromDBOverview(53, "image22", start_test, end_test)
    # 23_Memory
    GetImageGrafFromDBOverview(11, "image23", start_test, end_test)

    # DB status #

    # 24_Disk
    GetImageGrafFromDBStatus(31, "image24", start_test, end_test)

    return successful_download


# ___Крадет график из графаны, требует (ид графика на дашборде ,имя и дату начала и конца теста)
def GetImageGrafFromJmeter(panel_id, name_file, start_test, end_test):
    # создаем директорию если ее нет
    os.makedirs("./save", exist_ok=True)
    # имя файла для сохранения графика
    file_path = os.path.join("./save/", f"{name_file}.png")
    # ссылка для получения графика из рендерера
    image_url = f"{grafana_url}/render/d-solo/{dashboard_uid_jmeter}/{slug_jmeter}?orgId=1&panelId={panel_id}&width={width_img}&height={height_img}&from={start_test}&to={end_test}&theme=light"
    # вызываем функцию для получения и сохранения графика из графаны в файл "image{i}.png" в папке "save/"
    get_image_from_grafana_with_ten_attempts(file_path, image_url)


def GetImageGrafFromDBStatus(panel_id, name_file, start_test, end_test):
    # создаем директорию если ее нет
    os.makedirs("./save", exist_ok=True)
    # имя файла для сохранения графика
    file_path = os.path.join("./save/", f"{name_file}.png")
    # ссылка для получения графика из рендерера
    image_url = f"{grafana_url}/render/d-solo/{dashboard_uid_DBStatus}/{slug_DBStatus}?orgId=1&panelId={panel_id}&width={width_img}&height={height_img}&from={start_test}&to={end_test}&theme=light"
    # вызываем функцию для получения и сохранения графика из графаны в файл "image{i}.png" в папке "save/"
    get_image_from_grafana_with_ten_attempts(file_path, image_url)


def GetImageGrafFromDBOverview(panel_id, name_file, start_test, end_test):
    # создаем директорию если ее нет
    os.makedirs("./save", exist_ok=True)
    # имя файла для сохранения графика
    file_path = os.path.join("./save/", f"{name_file}.png")
    # ссылка для получения графика из рендерера
    image_url = f"{grafana_url}/render/d-solo/{dashboard_uid_DBOverview}/{slug_DBOverview}?orgId=1&panelId={panel_id}&width={width_img}&height={height_img}&from={start_test}&to={end_test}&theme=light"
    # вызываем функцию для получения и сохранения графика из графаны в файл "image{i}.png" в папке "save/"
    get_image_from_grafana_with_ten_attempts(file_path, image_url)


# _____________________________________________________________________________________
def get_image_from_grafana_with_ten_attempts(file_path, image_url):
    global successful_download

    # Тайминг перед запросом нужен для того, чтобы рендерер стабилизировался после предыдущего запроса
    print("Waiting 30 seconds for the renderer to recover from the previous request")

    # TODO: сделать ожидание на 60 сек
    time.sleep(30)

    # устанавливаем 10 попыток на получения графика
    attempt = 0
    max_attempt = 10
    print(f"\nTry to get image \"{file_path}\" with {max_attempt} attempts from: \"{image_url}\"")
    success = False
    while attempt != max_attempt:
        attempt += 1
        print(f"attempt: {attempt}/{max_attempt}")
        try:
            response = requests.get(image_url, headers=headers, verify=False, timeout=60)
            if response.status_code == 200:
                with open(file_path, "wb") as f:
                    f.write(response.content)
                    print(f"Image \"{file_path}\" was saved successfully\n")
                    success = True
                break
            else:
                print(
                    f"Error HTTP code: \"{response.status_code}\". We didn't receive image. Message error: \"{response.content}\"")
        except RequestException as e:
            print(f"Connection error: \"{e}\"")
        if attempt < max_attempt:
            # если не получили 200й успешный ответ, ждем 30 сек и пробуем заново
            print("Try again after 30 seconds")
            time.sleep(30)
    if not success:
        print(f"!!! ATTENTION !!! ERROR !!! Couldn't get image {file_path} after all attempts !!!")
        successful_download = False
