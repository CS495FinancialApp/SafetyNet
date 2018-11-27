package ua.safetynet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;

import ua.safetynet.payment.Transaction;
import ua.safetynet.payment.TransactionRecyclerAdapter;
import ua.safetynet.user.User;

/**
 * @author Jake Bailey
 * Activity for tracking a user's transaction history
 */
public class UserTransactionHistory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_transaction_history);

        final TextView UserName = findViewById(R.id.textViewUserID);
        final RecyclerView UserTrans = findViewById(R.id.RecyclerViewUserTrans);
        Database db = new Database();

        //Get & set name of current user
        db.getUser(db.getUID(), new Database.DatabaseUserListener() {
            @Override
            public void onUserRetrieval(User user) {
                UserName.setText(user.getName());
            }
        });

        //Set linear layout
        UserTrans.setHasFixedSize(true);
        RecyclerView.LayoutManager UserManage = new LinearLayoutManager(this);
        UserTrans.setLayoutManager(UserManage);
        db.queryUserTransactions(new Database.DatabaseTransactionsListener() {
            @Override
            public void onTransactionsRetrieval(ArrayList<Transaction> transactions) {
                //Specify adapter
                RecyclerView.Adapter uAdapter = new TransactionRecyclerAdapter(transactions,1);
                UserTrans.setAdapter(uAdapter);
            }
        }, getIntent().getStringExtra("group_ID"));
    }
}
