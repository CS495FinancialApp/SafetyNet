package ua.safetynet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;

import ua.safetynet.group.Group;
import ua.safetynet.user.User;

public class UserTransactionHistory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_transaction_history);

        final TextView UserName = findViewById(R.id.textViewUserID);
        RecyclerView UserTrans = findViewById(R.id.RecyclerViewUserTrans);
        UserTrans.setHasFixedSize(true);
        RecyclerView.LayoutManager UserManage = new LinearLayoutManager(this);
        UserTrans.setLayoutManager(UserManage);
        Database db = new Database();

        db.getUser(db.getUID(), new Database.DatabaseUserListener() {
            @Override
            public void onUserRetrieval(User user) {
                UserName.setText(user.getName());
            }
        });

        //UserName.setText("UserName");

        db.queryUserTransactions(new Database.DatabaseTransactionsListener() {
            @Override
            public void onTransactionsRetrieval(ArrayList<Transaction> transactions) {

            }
        }, getIntent().getStringExtra("group_ID"));
    }
}
