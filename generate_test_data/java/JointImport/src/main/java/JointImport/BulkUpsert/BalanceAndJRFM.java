package JointImport.BulkUpsert;

import JointImport.Tables.Balance;
import JointImport.Tables.RemainsappFundsMovement;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import tech.ydb.core.auth.StaticCredentials;
import tech.ydb.core.grpc.GrpcTransport;
import tech.ydb.table.SessionRetryContext;
import tech.ydb.table.TableClient;
import tech.ydb.table.settings.BulkUpsertSettings;
import tech.ydb.table.values.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

public final class BalanceAndJRFM {
    // генератор UUID
    static TimeBasedEpochGenerator generator = Generators.timeBasedEpochGenerator();

    // задаем информацию для подключения к БД
    private static final String usernameYDB = "user1";

    // password
    private static final String passwordYDB = "Pa$$w0rd";

    // url
    private static final String connectionString = "grpcs://rnd-ydb1.dev.moscow.alfaintra.net:2136/?database=/Root/asdb";

    // crt
    private static final String saKeyFile = "/home/u_m2hx7/ca_dev.crt";

    // Имя таблицы
    private static final String tableNameBalance = "/Root/asdb/remains/t_balance";
    private static final String tableRFM = "/Root/asdb/journals/t_remainsapp_funds_movement";

    // Количество записей, которое планируется записать в каждую таблицу
    // (ДОЛЖНО БЫТЬ КРАТНО BATCH_ROW_COUNT, ЧТОБЫ ЗАПИСАЛИСЬ ВСЕ ЗАПИСИ)
    private static final long SUMMARY_COUNT_ROW = 2651370000L;

    // Количество записей в одном пакете для отправки (за 1 раз)
    private static final int BATCH_ROW_COUNT = 10000;

    // ИТОГ ЗАПИСАННЫХ СТРОК = SUMMARY_COUNT_ROW

    // Данные для вставки в таблицу
    private static final String ALLOWED_NUMBERS = "0123456789";
    private static final int AMOUNT_LENGTH = 20;
    private static final Random random = new SecureRandom();


    private static void writeBatchBalance(SessionRetryContext retryCtx, List<Balance> items) {
        ListValue rows = Balance.toListValue(items);
        retryCtx.supplyStatus(session -> session.executeBulkUpsert(
                tableNameBalance, rows, new BulkUpsertSettings()
        )).join().expectSuccess("bulk upsert in t_balance problem");
    }

    private static void writeBatchRFM(SessionRetryContext retryCtx, List<RemainsappFundsMovement> items) {
        ListValue rows = RemainsappFundsMovement.toListValue(items);
        retryCtx.supplyStatus(session -> session.executeBulkUpsert(
                tableRFM, rows, new BulkUpsertSettings()
        )).join().expectSuccess("bulk upsert in t_remainsapp_funds_movement problem");
    }

    public static void runBulkUpsert() {
        try {
            byte[] certBytes = Files.readAllBytes(Paths.get(saKeyFile));
            StaticCredentials authProvider = new StaticCredentials(usernameYDB, passwordYDB);
            try (GrpcTransport transport = GrpcTransport.forConnectionString(connectionString)
                    .withSecureConnection(certBytes)
                    .withAuthProvider(authProvider)
                    .build()) {
                try (TableClient tableClient = TableClient.newClient(transport).build()) {
                    SessionRetryContext retryCtx = SessionRetryContext.create(tableClient).build();

                    // подготовка
                    List<Balance> batchBalance = new ArrayList<>();
                    List<RemainsappFundsMovement> batchRFM = new ArrayList<>();

                    for (long i = 0; i < SUMMARY_COUNT_ROW; i++) {

                     /*
                                          Вставка данных в таблицу Balance
                     */

                        Balance newBalance = generateObjBalance();
                        batchBalance.add(newBalance);
                        // Выполняем пакетную запись в YDB при достижении BATCH_ROW_COUNT
                        if (batchBalance.size() == BATCH_ROW_COUNT) {

                            writeBatchBalance(retryCtx, batchBalance);
                            batchBalance.clear();
                        }

                    /*
                                          Вставка данных в таблицу RemainsappFundsMovement
                     */

                        RemainsappFundsMovement newRFM = generateObjRemainsappFundsMovement();
                        batchRFM.add(newRFM);
                        // Выполняем пакетную запись в YDB при достижении BATCH_ROW_COUNT
                        if (batchRFM.size() == BATCH_ROW_COUNT) {
                            writeBatchRFM(retryCtx, batchRFM);
                            batchRFM.clear();
                        }


                    }
                    System.out.println("Total successful records write in tables: " + tableNameBalance + ", " + tableRFM + " = " + SUMMARY_COUNT_ROW);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static Balance generateObjBalance() {
        Instant now = Instant.now();
        UUID c_account_id = generator.generate();
        String c_balance_type = "CUSCOR";
        DecimalValue c_amount = DecimalType.of(20, 0).newValue(generateAmount());
        Instant c_changed = now;
        return new Balance(c_account_id, c_balance_type, c_amount, c_changed);
    }

    public static RemainsappFundsMovement generateObjRemainsappFundsMovement() {
        Instant now = Instant.now();

        UUID c_document_id = generator.generate();
        Integer c_funds_movement_number = random.nextInt(2) + 1;
        String c_funds_movement_status = (random.nextBoolean() ? "DONE" : "CANCELED");
        String c_funds_direction = (random.nextBoolean() ? "FUNDS_DIRECTION_DEBIT" : "FUNDS_DIRECTION_CREDIT");
        UUID c_account_id = generator.generate();
        DecimalValue c_amount_currency = DecimalType.of(20, 0).newValue(random.nextInt(1000000) + 1);
        PrimitiveValue c_additional_info = null;
        UUID c_hold_id = generator.generate();
        Instant c_created = now;
        Instant c_changed = now;

        return new RemainsappFundsMovement(c_document_id, c_funds_movement_number, c_funds_movement_status,
                c_funds_direction, c_account_id, c_amount_currency, c_additional_info, c_hold_id, c_created, c_changed);
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