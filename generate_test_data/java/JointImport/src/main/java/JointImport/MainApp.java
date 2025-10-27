package JointImport;

import JointImport.BulkUpsert.BalanceAndJRFM;
import JointImport.InsertClient.InsertAccountAndBalance;
import JointImport.InsertClient.InsertAccountBalanceCustomer;

public class MainApp {

    public static void main(String[] args) {

        InsertAccountAndBalance.InsertByOneAccount();
//        InsertAccountBalanceCustomer.InsertByOneAccount();
//        BalanceAndJRFM.runBulkUpsert();

    }

}
