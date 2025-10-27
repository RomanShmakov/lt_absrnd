package JointImport.InsertClient;

import JointImport.Tables.Balance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.ydb.auth.AuthProvider;
import tech.ydb.auth.iam.CloudAuthHelper;
import tech.ydb.core.grpc.GrpcTransport;
import tech.ydb.table.SessionRetryContext;
import tech.ydb.table.TableClient;
import tech.ydb.table.query.Params;
import tech.ydb.table.settings.BulkUpsertSettings;
import tech.ydb.table.values.*;

import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

public final class InsertBalance {

    private static final Logger logger = LoggerFactory.getLogger(InsertBalance.class);


    // задаем информацию для подключения к БД
    private static final String usernameYDB = "user1";

    // password
    //    private static final String passwordYDB = "passw0rd";
    private static final String passwordYDB = "Pa$$w0rd";

    // url
    //    private static final String connectionString = "grpcs://rnd-ydb1.moscow.alfaintra.net:2136/?database=/Root/asdb";
    //    private static final String connectionString = "grpcs://rnd-ydb1.dev.moscow.alfaintra.net:2136/?database=/Root/asdb";
    private static final String connectionString = "grpcs://lb.etn5e3bvin4d4i2rtgo0.ydb.mdb.yandexcloud.net:2135/?database=/ru-central1/b1g7sejgqt5m832v9oso/etn5e3bvin4d4i2rtgo0";

    // TODO = crt
    //    private static final String saKeyFile = "C:\\ydb\\ca.crt";
    //    private static final String saKeyFile = "/home/u_m2hx7/ca.crt";
//    private static final String saKeyFile = "/Users/romansmakov/Documents/ydb/key.json";
    private static final String saKeyFile = "/home/u_m2hx7/key.json";

    // Имя таблицы
    //    private static final String tableNameAccount = "tpcc/t_account";
    //    private static final String tableNameBalance = "/Root/asdb/tpcc/t_balance";
    private static final String tableNameAccount = "main/t_account";
    private static final String tableNameBalance = "/ru-central1/b1g7sejgqt5m832v9oso/etn5e3bvin4d4i2rtgo0/remains/t_balance";

    //
    // TODO: Количество запросов
    //  25000 = 100 gb
    // Количество (строк Values / объектов BulkUpsert) в одном запросе
    private static final int COUNT_QUERY = 250000;
//    private static final int COUNT_ROW_IN_QUERY = 1000;
    private static final int COUNT_ROW_IN_QUERY = 400;
    // ИТОГ ЗАПИСАННЫХ СТРОК = COUNT_QUERY * COUNT_ROW_IN_QUERY
    //
    // Данные для вставки в таблицу
    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String ALLOWED_NUMBERS = "0123456789";
    private static final int CUSTOMER_ID_LENGTH = 20;
    private static final int ACCOUNT_NUMBER_LENGTH = 20;
    private static final int AMOUNT_LENGTH = 20;
    private static final Random random = new SecureRandom();
    //
    // TODO = Путь к файлу для записи
    //    private static final String csvFileAccountsForJMeter = "C:\\git\\lt\\uc\\grpc\\accounts_for_jmeter.csv";
    //    private static final String csvFileAccountsForJMeter = "/home/u_m2hx7/accounts_for_jmeter.csv";
//    private static final String csvFileAccountsForJMeter = "/Users/romansmakov/Documents/ydb/accounts_for_jmeter.csv";
    private static final String csvFileAccountsForJMeter = "/home/u_m2hx7/accounts_for_jmeter.csv";

    //
    public static void InsertByOneAccount() {
        try {
//            byte[] certBytes = Files.readAllBytes(Paths.get(saKeyFile));

//            StaticCredentials authProvider = new StaticCredentials(usernameYDB, passwordYDB);

            AuthProvider authProvider = CloudAuthHelper.getServiceAccountFileAuthProvider(saKeyFile);

            try (GrpcTransport transport = GrpcTransport.forConnectionString(connectionString)
//                    .withSecureConnection(certBytes)
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

        List<Balance> batchBalance = new ArrayList<>();

        // Генерируем параметры для запроса
        for (int i = 1; i <= COUNT_ROW_IN_QUERY; i++) {
            // Генерируем значения для вставки в запрос
            // account
            Instant now = Instant.now();
            UUID c_account_id = UUID.randomUUID();
            Instant c_changed = now;
            //balance
            String c_balance_type = "CUSCOR";
            DecimalValue c_amount = DecimalType.of(20, 0).newValue(generateAmount());
            // Вставляем значения
            // balance
            batchBalance.add(new Balance(c_account_id, c_balance_type, c_amount, c_changed));

        }


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