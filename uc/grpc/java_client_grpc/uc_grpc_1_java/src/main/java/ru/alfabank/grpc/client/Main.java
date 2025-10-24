package ru.alfabank.grpc.client;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        BankOrderClient client = new BankOrderClient("10.230.190.250", 9292);
        try {
            client.execBankOrder();
        } finally {
            client.shutdown();
        }
    }
}