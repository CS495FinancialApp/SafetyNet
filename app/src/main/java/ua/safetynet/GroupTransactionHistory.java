package ua.safetynet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;

import ua.safetynet.group.Group;
import ua.safetynet.payment.Transaction;
import ua.safetynet.payment.TransactionRecyclerAdapter;

public class GroupTransactionHistory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_transaction_history);

        final TextView GroupName = findViewById(R.id.textViewGroupID);
        final RecyclerView GroupTrans = findViewById(R.id.RecyclerViewGroupTrans);
        Database db = new Database();

        //Get & set name of current group
        db.getGroup(getIntent().getStringExtra("group_ID"), new Database.DatabaseGroupListener() {
            @Override
            public void onGroupRetrieval(Group group) {
                GroupName.setText(group.getName());
            }
        });

        //Set linear layout
        GroupTrans.setHasFixedSize(true);
        RecyclerView.LayoutManager GroupManage = new LinearLayoutManager(this);
        GroupTrans.setLayoutManager(GroupManage);
        db.queryGroupTransactions(getIntent().getStringExtra("group_ID"), new Database.DatabaseTransactionsListener() {
            @Override
            public void onTransactionsRetrieval(ArrayList<Transaction> transactions) {
                //Specify adapter
                RecyclerView.Adapter gAdapter = new TransactionRecyclerAdapter(transactions,2);
                GroupTrans.setAdapter(gAdapter);
            }
        });

    }
}
