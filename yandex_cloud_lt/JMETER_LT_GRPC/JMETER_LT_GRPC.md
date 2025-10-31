JMeter - подача нагрузки по протоколу gRPC
Для подачи нагрузки используется Apache JMeter версии 5.6.3
Нагрузка подается с выделенной нагрузочной машины, в скрипте JMeter ссылки на файлы с тестовыми данными ориентированы на linux
•	Общий план запуска НТ
•	Первичная настройка JMeter
•	Первичная настройка gRPC клиента
•	Необходимые зависимости для корректной работы JMeter
•	Методология НТ 
o	Скрипт JMeter "uc_discret_yc.jmx"
o	Как происходит отправка запросов
o	Коды ответа
o	Посмотреть логи JMeter
•	Дашборд JMeter
Общий план запуска НТ
1.	Сгенерировать и загрузить тестовые данные (итогом данного шага будет являться файл со счетами по пути ~/account_for_jmeter.csv)
2.	Настроить JMeter для подачи нагрузки
3.	Запустить JMeter
Команды для запуска: 
•	~/apache-jmeter-5.6.3/bin/jmeter -n -t ~/lt/yandexcloud/uc_discret_yc.jmx
•	~/apache-jmeter-5.6.3/bin/jmeter -n -t ~/lt/yandexcloud/uc_stability_yc.jmx -Jadd_threads=800 -Juser_rpm=60000 -Jhold_time=57600
Скрипт "uc_stability_yc.jmx" позволяет из консоли задавать параметры для теста стабильности (количество потоков, запросов в минуту, удержание нагрузки)
Первичная настройка JMeter
1.	Сгенерировать jar-файл с gRPC клиентом ("posting_service_create_yc-1.0-SNAPSHOT.jar"), который находится в папке "posting_service_create_yc"
2.	Загрузить JMeter с официального сайта, последняя версия 5.6.3 
1.	вручную скачать apache-jmeter-5.6.3.zip по ссылке https://archive.apache.org/dist/jmeter/binaries/
2.	или командой wget https://dlcdn.apache.org//jmeter/binaries/apache-jmeter-5.6.3.tgzРаспаковать JMeter (tar -xvf apache-jmeter-5.6.3.tgz)
3.	Скопировать в папку "apache-jmeter-5.6.3/lib" jar-файл "jmeter-plugins-cmn-jmeter-0.7.jar"
4.	Скопировать в папку "apache-jmeter-5.6.3/lib/ext" jar-файлы: 
1.	"jmeter-plugins-casutg-3.1.1.jar"
2.	"jmeter-plugins-manager-1.10.jar"
3.	"posting_service_create_yc-1.0-SNAPSHOT.jar"
Первичная настройка gRPC клиента
java-клиент для отправки gRPC запросов находится в папке "posting_service_create_yc", в проекте используется 21 версия java
Чтобы клиент подключился к нужному приложению, надо изменить следующие параметры в java-классе "PostingClient":
•	absrndMvpHost
•	absrndMvpPort
После изменения данных параметров, можно сгенерировать jar-файл командой "mvn clean package"
Необходимые зависимости для корректной работы JMeter
•	/home/u_m2hx7/accounts_for_jmeter.csv (CSV Data Set Config)
В домашнюю папку добавить файл "accounts_for_jmeter.csv", либо поменять путь в конфиг-файле
•	import absrnd_mvp.client.PostingClient (в JSR223 Samper)
Надо добавить файл "posting_service_create_yc-1.0-SNAPSHOT.jar" в корневую папку JMeter
Методология НТ
Скрипт JMeter "uc_discret_yc.jmx"
•	User Definde Variables "[GENERAL] Parameters" - параметры для подключения к VictoriaMetrics
•	CSV Data Set Config "CSV account_number1", "CSV account_number2" - файлы с тестовыми данными для параметризации запросов
•	setUp Thread Group "setUp" - отправляет в VictoriaMetrics запрос о начале теста
•	Stepping Thread Group "STG" - основная катушка для подачи нагрузки, содержит основной запрос ввода проводки "create_request". Для регулирования интенсивности нагрузки, используется связка "Flow Control Action" + "Constant Throughput Timer"
•	tearDown Thread Group "tearDown" - отправляет в VictoriaMetrics запрос о начале теста и закрывает все сессии
•	"Backend Listener" - пишет метрики Jmeter в БД VictoriaMetrics
Как происходит отправка запросов
•	Отправка запросов происходит по протоколу gRPC, к JMeter подключается jar-файл "posting_service_create_yc-1.0-SNAPSHOT.jar" (добавляется в корневую папку JMeter) с клиентом "PostingClient" для отправки запросов
•	В папке "posting_service_create_yc" лежат proto-файлы, по которым сгенерированы java-классы (с помощью protoc). Сгенерированные классы и клиент "PostingClient" лежат в папке "src/main/java/absrnd_mvp"
•	В JMeter реализован JSR223 Sampler, который импортирует класс клиента "PostingClient". С помощью метода "sendRequest" отправляет запрос, дальше получает ответ и обрабатывает его.
•	Далее JMeter пишет метрики в БД VictoriaMetrics
Команда, с которой сгенерированы java-классы из proto-файлов:
•	protoc --grpc-java_out=src/main/java --java_out=src/main/java absrnd_mvp.proto absrnd_mvp.posting.mainapp.proto
Коды ответа
•	503 - если мы не смогли подключиться к приложению
•	500 - внутренняя ошибка JMeter
•	200 - успешный ввод проводки (RESULT_STATUS_SUCCESS)
•	1000, 1001, 2005 и тд- бизнес-коды приложения
Посмотреть логи JMeter
В папке, где запустили JMeter:
•	tail -n 20 jmeter.log
•	tail -f jmeter.log | grep ERROR
Дашборд JMeter
jmeter-dashboard-victoriametrics.json


