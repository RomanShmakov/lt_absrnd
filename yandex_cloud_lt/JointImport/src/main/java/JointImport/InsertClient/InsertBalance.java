package JointImport.InsertClient;

import JointImport.Tables.Balance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.ydb.auth.AuthProvider;
import tech.ydb.auth.iam.CloudAuthHelper;
import tech.ydb.core.grpc.GrpcTransport;
import tech.ydb.table.SessionRetryContext;
import tech.ydb.table.TableClient;
import tech.ydb.table.settings.BulkUpsertSettings;
import tech.ydb.table.values.*;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

public final class InsertBalance {
    private static final Logger logger = LoggerFactory.getLogger(InsertBalance.class);
    private static final String connectionString = "grpcs://lb.etn5e3bvin4d4i2rtgo0.ydb.mdb.yandexcloud.net:2135/?database=/ru-central1/b1g7sejgqt5m832v9oso/etn5e3bvin4d4i2rtgo0";
    private static final String saKeyFile = "/home/u_m2hx7/key.json";
    private static final String tableNameAccount = "main/t_account";
    private static final String tableNameBalance = "/ru-central1/b1g7sejgqt5m832v9oso/etn5e3bvin4d4i2rtgo0/remains/t_balance";
    private static final int COUNT_QUERY = 3000;
    private static final int COUNT_ROW_IN_QUERY = 5000;
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