package ru.alfabank.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import ru.alfabank.grpc.*;
import com.google.protobuf.Timestamp;

import java.util.concurrent.TimeUnit;

public class BankOrderClient {
    private final ManagedChannel channel;
    private final SettlementOperationServiceGrpc.SettlementOperationServiceBlockingStub blockingStub;

    public BankOrderClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = SettlementOperationServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void execBankOrder() {
        try {
            // Создаем Timestamp для operation_date
            Timestamp operationDate = Timestamp.newBuilder()
                    .setSeconds(1754524800)
                    .build();

            // Строим запрос
            BankOrderExecRequest request = BankOrderExecRequest.newBuilder()
                    .setOperationExternalUid("f48e9c18-091e-4d6a-b38b-2e0b8f220b3c")
                    .setOperationDate(operationDate)
                    .setAmount(150000)
                    .setAmountCurrency("RUR")
                    .setPayerAccount("45815810207160017364")
                    .setPayerName("КОРОЛЕВА ОКСАНА ВЛАДИМИРОВНА")
                    .setPayeeAccount("40817810510210367285")
                    .setPayeeName("Савельев Еремей Захарьевич")
                    .setPaymentPurpose("Тестовый перевод между клиентами")
                    .setPaymentPriority(5)
                    .build();

            System.out.println("Sending request...");
            BankOrderExecResponse response = blockingStub.execBankOrder(request);

            // Обрабатываем ответ
            System.out.println("Response received:");
            System.out.println("Operation Status: " + response.getOperationStatus());
            if (response.hasOperationError()) {
                System.out.println("Operation Error: " + response.getOperationError());
            }
            if (response.hasOperationErrorMessage()) {
                System.out.println("Operation Error Message: " + response.getOperationErrorMessage());
            }

        } catch (Exception e) {
            System.err.println("Error during gRPC call: " + e.getMessage());
            e.printStackTrace();
        }
    }


}