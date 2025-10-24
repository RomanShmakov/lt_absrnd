import requests
import json

def send():
    # Адрес сервера
    url = "http://10.230.190.20:8282/api/v1/bank-orders/exec"

    # Тело запроса
    payload = {
        "operationExternalUid": "f48e9c18-091e-4d6a-b38b-2e0b8f220b3c",
        "operationDate": {"seconds": 1754524800},
        "amount": 150000,
        "amountCurrency": "RUR",
        "payerAccount": "45815810207160017364",
        "payerName": "КОРОЛЕВАОКСАНАВЛАДИМИРОВНА",
        "payeeAccount": "40817810510210367285",
        "payeeName": "СавельевЕремейЗахарьевич",
        "paymentPurpose": "Тестовыйпереводмеждуклиентами",
        "paymentPriority": 5
    }

    # Заголовки
    headers = {
        "Content-Type": "application/json; charset=utf-8"
    }

    # Отправляем POST-запрос
    response = requests.post(url, headers=headers, data=json.dumps(payload))

    # Печатаем результат
    print("Status code:", response.status_code)
    print("Response body:", response.text)

if __name__ == '__main__':
    send()