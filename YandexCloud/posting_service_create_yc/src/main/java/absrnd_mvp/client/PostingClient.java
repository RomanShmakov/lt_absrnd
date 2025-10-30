package absrnd_mvp.client;

import absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp;
import absrnd_mvp.posting.mainapp.PostingServiceGrpc;
import absrnd_mvp.AbsrndMvp;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import com.google.protobuf.Timestamp;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PostingClient {

    // TODO: поменять на хост и порт, где развернуто приложение
    //  Параметры для подключения
    private static final String absrndMvpHost = "10.147.44.51";
    private static final int absrndMvpPort = 30002;

    // Пул подключений для закрытия сессии: Thread ID -> ManagedChannel
    private static final ConcurrentHashMap<Long, ManagedChannel> channelPool = new ConcurrentHashMap<>();

    // Пул stubs для JMeter: Thread ID -> BlockingStub
    private static final ConcurrentHashMap<Long, PostingServiceGrpc.PostingServiceBlockingStub> stubPool = new ConcurrentHashMap<>();
    // генератор UUID
    static TimeBasedEpochGenerator generator = Generators.timeBasedEpochGenerator();

    //Получить или создать подключение для текущего потока
    public static PostingServiceGrpc.PostingServiceBlockingStub getStubForCurrentThread() {
        long threadId = Thread.currentThread().getId();
        // Если у нас уже есть stub для этого потока, возвращаем его
        if (stubPool.containsKey(threadId)) {
            return stubPool.get(threadId);
        }
        // Создаем новое подключение
        ManagedChannel channel = ManagedChannelBuilder.forAddress(absrndMvpHost, absrndMvpPort)
                .usePlaintext()
                .build();
        PostingServiceGrpc.PostingServiceBlockingStub stub = PostingServiceGrpc.newBlockingStub(channel);
        // Сохраняем в пулах
        channelPool.put(threadId, channel);
        stubPool.put(threadId, stub);
        return stub;
    }

    // Создать запрос
    public static AbsrndMvpPostingMainapp.CreateRequest buildRequest(String accountNumber1, String accountNumber2) {
        // Генерация переменных
        String uuid_key = generator.generate().toString();
        String uuid_doc = generator.generate().toString();
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        String amountCurrency = String.valueOf((int) (Math.random() * 1000000) + 1);
        // Создание FundsMovement записей
        AbsrndMvpPostingMainapp.CreateRequest.FundsMovement movement1 =
                AbsrndMvpPostingMainapp.CreateRequest.FundsMovement.newBuilder()
                        .setFundsMovementNumber(1)
                        .setFundsDirection(AbsrndMvp.FundsDirection.FUNDS_DIRECTION_DEBIT)
                        .setAccountNumber(accountNumber1)
                        .setAmountCurrency(amountCurrency)
                        .setAmountCurrencyBase(amountCurrency)
                        .build();
        AbsrndMvpPostingMainapp.CreateRequest.FundsMovement movement2 =
                AbsrndMvpPostingMainapp.CreateRequest.FundsMovement.newBuilder()
                        .setFundsMovementNumber(2)
                        .setFundsDirection(AbsrndMvp.FundsDirection.FUNDS_DIRECTION_CREDIT)
                        .setAccountNumber(accountNumber2)
                        .setAmountCurrency(amountCurrency)
                        .setAmountCurrencyBase(amountCurrency)
                        .build();
        // Создание основного запроса
        return AbsrndMvpPostingMainapp.CreateRequest.newBuilder()
                .setIdempotencyKey(uuid_key)
                .setDocumentId(uuid_doc)
                .setPostingBalanceDate(Timestamp.newBuilder()
                        .setSeconds(currentTimeSeconds)
                        .setNanos(0)
                        .build())
                .addFundsMovements(movement1)
                .addFundsMovements(movement2)
                .setBalanceType("CUSCOR")
                .build();
    }

    // Отправить запрос
    public static String sendRequest(String accountNumber1, String accountNumber2) {
//        if (accountNumber1 == null || accountNumber2 == null) {
//            return "Success: false, Code: 400, Status: NULL_DATA, Description: account_number1 or account_number1 is null";
//        }
        try {
            // Получаем stub для текущего потока
            PostingServiceGrpc.PostingServiceBlockingStub stub = getStubForCurrentThread();
            // Создаем запрос
            AbsrndMvpPostingMainapp.CreateRequest request = buildRequest(accountNumber1, accountNumber2);
            // Отправляем запрос
            AbsrndMvpPostingMainapp.CreateResponse response = stub.create(request);
            return String.format("Success: %s, Code: %s, Status: %s, Description: %s",
                    response.getIsSuccessfull(),
                    response.getResultStatusValue(),
                    response.getResultStatus(),
                    response.hasResultDescription() ? response.getResultDescription() : "No description");
        } catch (Exception e) {
            return String.format("Success: false, Code: 503, Status: UNAVAILABLE, Description: %s", e.getMessage());
        }
    }

    // Закрыть все подключения
    public static void shutdown() {
        try {
            int closedCount = 0;
            // Закрываем все каналы
            for (ManagedChannel channel : channelPool.values()) {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                closedCount++;
            }
            // Очищаем пулы
            channelPool.clear();
            stubPool.clear();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Закрыть сессию, когда получили сообщение "SessionClosed"
    public static void closeSession() {
        try {
            // удаляем stub чтобы при следующем вызове в текущем потоке сессия создалась заново
            long threadId = Thread.currentThread().getId();
            // Закрываем канал
            channelPool.get(threadId).shutdown();
            // Если у нас уже есть stub для этого потока, удаляем его. Новая сессия создастся автоматически при отправке запроса через JMeter
            stubPool.remove(threadId);
            channelPool.remove(threadId);
        } catch (Exception e) {
            //Thread.currentThread().interrupt();
        }
    }

    // Получить статистику подключений
    public static String getConnectionStats() {
        return String.format("Active connections: %d, Active stubs: %d",
                channelPool.size(), stubPool.size());
    }

}
