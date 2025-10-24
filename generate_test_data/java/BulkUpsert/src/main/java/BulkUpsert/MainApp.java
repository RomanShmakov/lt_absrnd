package BulkUpsert;

import BulkUpsert.UpsertByTable.BulkUpsertBalance;
import BulkUpsert.UpsertByTable.BulkUpsertRemainsappFundsMovement;
import tech.ydb.core.auth.StaticCredentials;
import tech.ydb.core.grpc.GrpcTransport;
import tech.ydb.table.SessionRetryContext;
import tech.ydb.table.TableClient;

import java.nio.file.Files;
import java.nio.file.Paths;

public class MainApp {

    // Подключение к YDB
    private static final String connectionString = "grpcs://rnd-ydb1.moscow.alfaintra.net:2136/?database=/Root/asdb";
    private static final String certPath = "C:\\ydb\\ca.crt";
    private static final String usernameYDB = "user1";
    private static final String passwordYDB = "passw0rd";
    //

    // Информация о таблице
    private static final String database = "/Root/asdb";
    private static final String tableNameBalance = "tpcc/t_balance";
    private static final String tableNameRemainsappFundsMovement = "tpcc/t_remainsapp_funds_movement";
    //

    public static void main(String[] args) {
        StaticCredentials authProvider = new StaticCredentials(usernameYDB, passwordYDB);
        try {
            byte[] certBytes = Files.readAllBytes(Paths.get(certPath));
            try (GrpcTransport transport = GrpcTransport.forConnectionString(connectionString)
                    .withSecureConnection(certBytes)
                    .withAuthProvider(authProvider)
                    .build()) {
                try (TableClient tableClient = TableClient.newClient(transport).build()) {
                    SessionRetryContext retryCtx = SessionRetryContext.create(tableClient).build();


                     /*
                                          Вставка данных в таблицу Balance
                     */


                    BulkUpsertBalance bulkUpsertBalance = new BulkUpsertBalance(database, tableNameBalance);
                    bulkUpsertBalance.ImportData(retryCtx);


                    /*
                                          Вставка данных в таблицу RemainsappFundsMovement
                     */


                    BulkUpsertRemainsappFundsMovement bulkUpsertRemainsappFundsMovement =
                            new BulkUpsertRemainsappFundsMovement(database, tableNameRemainsappFundsMovement);
                    bulkUpsertRemainsappFundsMovement.ImportData(retryCtx);


                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Cannot read certificate from file " + certPath + ": " + e.getMessage());
        }
    }
}
