Генерация тестовых данных для функционала приложения
•	API Ввод проводки
•	Заполнение таблиц 
o	Таблицы с параметрами
o	Основные таблицы
•	Первичная настройка скриптов для заполнения основных таблиц
API Ввод проводки
Таблицы, которые необходимо заполнить для корректного выполнения ввода проводки
•	Таблицы с параметрами
1.	`remains/t_balance_type` 
2.	`main/t_parameter`
3.	`main/t_filial`
•	Основные таблицы
1.	`main/t_account`
2.	`main/t_parameter`
Заполнение таблиц
Таблицы с параметрами
Таблицы с параметрами можно заполнить вручную
Выполнить команды
•	UPSERT INTO `remains/t_balance_type` 
( c_key, c_description, c_changed )
VALUES (  "CUSCOR", "Доступный остаток Клиентский", Timestamp("2025-09-15T16:05:39.350576Z") );
•	UPSERT INTO `main/t_parameter`
( c_key, c_value, c_description, c_changed )
VALUES 
("BALANCE_TYPE_DEFAULT", "CUSCOR", "Умолчательный тип баланса при скрутке остатков", Timestamp("2025-09-15T16:05:39.350576Z")),
("BASE_CURRENCY", "RUR", "Базовая валюта", Timestamp("2025-09-15T16:05:39.350576Z")),
("PROCESSING_DATE", "2025-07-30", "Дата обработки", Timestamp("2025-09-15T16:05:39.350576Z"))
;
•	UPSERT INTO `main/t_filial`
( c_filial, c_bic, c_regno, c_description, c_EOD_switch_time, c_date_current, c_date_closed, c_changed )
VALUES ("MOCO","044525593","1326","АО \"АЛЬФА-БАНК\"",null,Date("2025-09-15"),Date("2025-09-14"),Timestamp("2025-09-15T16:05:39.350576Z"));
Основные таблицы
Основные таблицы заполняются с помощью скрипта, написанного на java.
Порядок заполнения основных таблиц
1.	Настроить скрипт, указав необходимые параметры
2.	Собрать jar-файл
3.	запустить генерацию данных: java -jar "JointImport_AccountBalance_yc-1.0-SNAPSHOT"
Первичная настройка скриптов для заполнения основных таблиц
•	Java-скрипт записывает данные в таблицы с помощью запросов Insert (в таблицу `main/t_account`, потому что в таблице есть уникальный синхронный вторичный индекс) и BulkUpsert (в таблицу `remains/t_balance`)
•	1 запись в таблице `main/t_account` весит 187 байт
•	1 запись в таблице `remains/t_balance` весит 83 байта
Скрипт для генерации тестовых данных находится в папке "JointImport", в проекте используется 21 версия java
Чтобы скрипт заполнил нужные таблицы, надо изменить следующие параметры в скрипте, которые отвечают за подключение к БД:
1.	в классе "MainApp" проверить, что вызывается нужный класс, который заполняет 2 таблицы: InsertAccountAndBalance.InsertByOneAccount();
2.	в классе "InsertAccountAndBalance" изменить следующие переменные с параметрами: 
1.	connectionString
2.	saKeyFile
3.	tableNameAccount
4.	tableNameBalance
5.	csvFileAccountsForJMeter
6.	COUNT_QUERY и COUNT_ROW_IN_QUERY (при необходимости)
3.	указать в pom.xml желаемое название для jar-файла: <artifactId>JointImport_AccountBalance_yc</artifactId>
