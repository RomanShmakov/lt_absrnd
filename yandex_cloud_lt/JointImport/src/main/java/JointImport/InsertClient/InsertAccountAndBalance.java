package JointImport.InsertClient;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;


import JointImport.Tables.Balance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.ydb.auth.AuthProvider;
import tech.ydb.auth.iam.CloudAuthHelper;
import tech.ydb.common.transaction.TxMode;

import tech.ydb.core.grpc.GrpcTransport;
import tech.ydb.table.SessionRetryContext;
import tech.ydb.table.TableClient;
import tech.ydb.table.query.Params;
import tech.ydb.table.settings.BulkUpsertSettings;
import tech.ydb.table.values.*;
import tech.ydb.table.transaction.TableTransaction;


import java.io.FileWriter;
import java.io.IOException;

public final class InsertAccountAndBalance {

    private static final Logger logger = LoggerFactory.getLogger(InsertAccountAndBalance.class);

    // TODO: Изменить url на подключение к бд
    //  connectionString
    private static final String connectionString = "grpcs://lb.etn5e3bvin4d4i2rtgo0.ydb.mdb.yandexcloud.net:2135/?database=/ru-central1/b1g7sejgqt5m832v9oso/etn5e3bvin4d4i2rtgo0";

    // TODO: изменить путь к key/crt файлу для подключения к БД
    //  saKeyFile
    private static final String saKeyFile = "/home/u_m2hx7/key.json";

    // TODO: изменить полный путь к таблице balance (нужно для bulkUpsert)
    //  tableNameAccount
    //  tableNameBalance
    private static final String tableNameAccount = "main/t_account";
    private static final String tableNameBalance = "/ru-central1/b1g7sejgqt5m832v9oso/etn5e3bvin4d4i2rtgo0/remains/t_balance";

    // TODO: изменить путь к файлу с тестовыми данными для jmeter
    //  csvFileAccountsForJMeter
    private static final String csvFileAccountsForJMeter = "/home/u_m2hx7/accounts_for_jmeter.csv";


    // TODO: при необходимости изменить объем генерируемых данных
    //  (!!! внимание !!!) COUNT_QUERY отвечает за количество запросов, а COUNT_ROW_IN_QUERY за количество строчек, записанных за 1 запрос
    //  ИТОГ ЗАПИСАННЫХ СТРОК = COUNT_QUERY * COUNT_ROW_IN_QUERY
    //  25000 = +_ 1000 gb
    private static final int COUNT_QUERY = 25000;
    private static final int COUNT_ROW_IN_QUERY = 400;

    // Параметры для вставки в таблицу (для методов генерации данных)
    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String ALLOWED_NUMBERS = "0123456789";
    private static final int CUSTOMER_ID_LENGTH = 20;
    private static final int ACCOUNT_NUMBER_LENGTH = 20;
    private static final int AMOUNT_LENGTH = 20;
    private static final Random random = new SecureRandom();

    public static void InsertByOneAccount() {
        try {
            AuthProvider authProvider = CloudAuthHelper.getServiceAccountFileAuthProvider(saKeyFile);
            try (GrpcTransport transport = GrpcTransport.forConnectionString(connectionString)
                    .withAuthProvider(authProvider)
                    .build()) {
                try (TableClient tableClient = TableClient.newClient(transport).build()) {
                    SessionRetryContext retryCtx = SessionRetryContext.create(tableClient).build();
                    for (int i = 0; i < COUNT_QUERY; i++) {
                        try {
                            /*
                                    Заполнение 2х таблиц t_account и t_balance
                            */
                            insertQuery(retryCtx, tableNameAccount, tableNameBalance);

                        } catch (RuntimeException e) {
                            logger.info("Error" + e.getMessage());
                        }
                    }
                    System.out.println("Total successful records write in tables: " + tableNameAccount + ", " + tableNameBalance + " = " + COUNT_QUERY * COUNT_ROW_IN_QUERY);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void insertQuery(SessionRetryContext retryCtx, String tableNameAccount, String tableNameBalance) {
        // Переменные для генерации запроса
        StringBuilder sb = new StringBuilder();
        Map<String, Value<?>> paramMap = new HashMap<>();
        List<Balance> batchBalance = new ArrayList<>();

        FileWriter writer = null;
        try {
            writer = new FileWriter(csvFileAccountsForJMeter, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Генерируем параметры для запроса
        for (int i = 1; i <= COUNT_ROW_IN_QUERY; i++) {
            // Генерация нескольких строк Values, чтобы подставить в них значения
            sb.append("($c_account_id_").append(i)
                    .append(", $c_customer_id_").append(i)
                    .append(", $c_account_number_").append(i)
                    .append(", $c_ccy_").append(i)
                    .append(", $c_filial_").append(i)
                    .append(", $c_branch_").append(i)
                    .append(", $c_group_").append(i)
                    .append(", $c_description_").append(i)
                    .append(", $c_open_date_").append(i)
                    .append(", NULL")
                    .append(", $c_is_active_").append(i)
                    .append(", $c_is_blocked_debit_").append(i)
                    .append(", $c_is_blocked_credit_").append(i)
                    .append(", $c_is_blocked_").append(i)
                    .append(", $c_balance_check_").append(i)
                    .append(", $c_changed_").append(i)
                    .append(")");
            // запятая между записями
            if (i < COUNT_ROW_IN_QUERY) sb.append(",");
            //
            // Генерируем значения для вставки в запрос
            // account
            Instant now = Instant.now();
            UUID c_account_id = UUID.randomUUID();
            String c_customer_id = generateCustomerId();
            String c_account_number = generateAccountNumber();
            String c_ccy = "RUR";
            String c_filial = "MOCO";
            String c_branch = "";
            String c_group = "";
            String c_description = "";
            Instant c_open_date = now;
            boolean c_is_active = true;
            boolean c_is_blocked_debit = false;
            boolean c_is_blocked_credit = false;
            boolean c_is_blocked = false;
            String c_balance_check = "";
            Instant c_changed = now;
            //balance
            String c_balance_type = "CUSCOR";
            DecimalValue c_amount = DecimalType.of(20, 0).newValue(generateAmount());
            // Вставляем значения
            // account
            paramMap.put("$c_account_id_" + i, PrimitiveValue.newUuid(c_account_id));
            paramMap.put("$c_customer_id_" + i, PrimitiveValue.newText(c_customer_id));
            paramMap.put("$c_account_number_" + i, PrimitiveValue.newText(c_account_number));
            paramMap.put("$c_ccy_" + i, PrimitiveValue.newText(c_ccy));
            paramMap.put("$c_filial_" + i, PrimitiveValue.newText(c_filial));
            paramMap.put("$c_branch_" + i, PrimitiveValue.newText(c_branch));
            paramMap.put("$c_group_" + i, PrimitiveValue.newText(c_group));
            paramMap.put("$c_description_" + i, PrimitiveValue.newText(c_description));
            paramMap.put("$c_open_date_" + i, PrimitiveValue.newDate(c_open_date));
            paramMap.put("$c_is_active_" + i, PrimitiveValue.newBool(c_is_active));
            paramMap.put("$c_is_blocked_debit_" + i, PrimitiveValue.newBool(c_is_blocked_debit));
            paramMap.put("$c_is_blocked_credit_" + i, PrimitiveValue.newBool(c_is_blocked_credit));
            paramMap.put("$c_is_blocked_" + i, PrimitiveValue.newBool(c_is_blocked));
            paramMap.put("$c_balance_check_" + i, PrimitiveValue.newText(c_balance_check));
            paramMap.put("$c_changed_" + i, PrimitiveValue.newTimestamp(c_changed));
            // balance
            batchBalance.add(new Balance(c_account_id, c_balance_type, c_amount, c_changed));

            // записываем в файл account_for_jmeter номера счетов
            if (writer != null) {
                try {
                    writer.write(c_account_number + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // Явное закрытие файла
        if (writer != null) {
            try {
                writer.close();
                //System.out.println("The \"" + csvFileAccountsForJMeter + "\" file has been successfully updated");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Собираем запрос
        // Параметры для запроса
        String valuesForQuery = sb.toString();
        // Значения для параметров запроса
        Params params = Params.copyOf(paramMap);
        // SQL-запрос с параметрами
        String query = "DECLARE $c_account_id AS Uuid; "
                + "DECLARE $c_customer_id AS Utf8; "
                + "DECLARE $c_account_number AS Utf8; "
                + "DECLARE $c_ccy AS Utf8; "
                + "DECLARE $c_filial AS Utf8; "
                + "DECLARE $c_branch AS Utf8; "
                + "DECLARE $c_group AS Utf8; "
                + "DECLARE $c_description AS Utf8; "
                + "DECLARE $c_open_date AS Date; "
                + "DECLARE $c_is_active AS Bool; "
                + "DECLARE $c_is_blocked_debit AS Bool; "
                + "DECLARE $c_is_blocked_credit AS Bool; "
                + "DECLARE $c_is_blocked AS Bool; "
                + "DECLARE $c_balance_check AS Utf8; "
                + "DECLARE $c_changed AS Timestamp; "
                + "INSERT INTO `" + tableNameAccount + "` ("
                + "  c_account_id, c_customer_id, c_account_number, "
                + "  c_ccy, c_filial, c_branch, c_group, c_description, c_open_date, c_close_date, "
                + "  c_is_active, c_is_blocked_debit, c_is_blocked_credit, "
                + "  c_is_blocked, c_balance_check, c_changed)"
                + " VALUES "
                + valuesForQuery
                + ";";
        // Отправка запроса
        //account
        retryCtx.supplyStatus(session -> {
            TableTransaction transaction = session.createNewTransaction(TxMode.SERIALIZABLE_RW);
            // DataQueryResult result = transaction.executeDataQuery(query, params).join().getValue();
            transaction.executeDataQuery(query, params).join().getValue();
            return transaction.commit();
        }).join().expectSuccess("insert problem");
        // balance
        ListValue rows = Balance.toListValue(batchBalance);
        retryCtx.supplyStatus(session -> session.executeBulkUpsert(
                tableNameBalance, rows, new BulkUpsertSettings()
        )).join().expectSuccess("bulk upsert problem");
        //
    }

    /**
     * Генерирует случайный 20-символьный идентификатор клиента
     * из символов A-Z и цифр 0-9.
     *
     * @return Строка из 20 символов в верхнем регистре
     */
    public static String generateCustomerId() {
        StringBuilder sb = new StringBuilder(CUSTOMER_ID_LENGTH);
        for (int i = 0; i < CUSTOMER_ID_LENGTH; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARS.length());
            char randomChar = ALLOWED_CHARS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    /**
     * Генерирует случайный 20-символьный номер счета
     * из цифр 0-9.
     *
     * @return Строка из 20 символов
     */
    public static String generateAccountNumber() {
        StringBuilder sb = new StringBuilder(ACCOUNT_NUMBER_LENGTH);
        for (int i = 0; i < ACCOUNT_NUMBER_LENGTH; i++) {
            int randomIndex = random.nextInt(ALLOWED_NUMBERS.length());
            char randomChar = ALLOWED_NUMBERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    /**
     * Генерирует случайную 20-символьную сумму счета
     * из цифр 0-9.
     *
     * @return Строка из 20 символов
     */
    public static String generateAmount() {
        StringBuilder sb = new StringBuilder(AMOUNT_LENGTH);
        for (int i = 0; i < AMOUNT_LENGTH; i++) {
            int randomIndex = random.nextInt(ALLOWED_NUMBERS.length());
            char randomChar = ALLOWED_NUMBERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

}