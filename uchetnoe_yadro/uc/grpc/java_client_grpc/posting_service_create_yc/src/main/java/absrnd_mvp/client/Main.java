package absrnd_mvp.client;

public class Main {
    public static void main(String[] args) {
        String accountNumber1 = "95121414528178950879";
        String accountNumber2 = "45142505639095698962";

        String result = PostingClient.sendRequest(accountNumber1, accountNumber2);
        System.out.println(result);
    }
}