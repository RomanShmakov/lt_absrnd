package absrnd_mvp.client;

public class Main {
    public static void main(String[] args) {
        String accountNumber1 = "44296628929880553450";
        String accountNumber2 = "36242856337078128389";

        String result = PostingClient.sendRequest(accountNumber1, accountNumber2);
        System.out.println(result);
    }
}