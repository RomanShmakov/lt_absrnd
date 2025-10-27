package JointImport;

import JointImport.BulkUpsert.BalanceAndJRFM;
import JointImport.InsertClient.InsertAccountAndBalance;
import JointImport.InsertClient.InsertAccountBalanceCustomer;
import JointImport.InsertClient.InsertBalance;

public class MainApp {

    public static void main(String[] args) {

        InsertAccountAndBalance.InsertByOneAccount();
//        InsertBalance.InsertByOneAccount();



//        InsertAccountBalanceCustomer.InsertByOneAccount();
//        BalanceAndJRFM.runBulkUpsert();

    }

}
