package ua.safetynet.payment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import ua.safetynet.Database;
import ua.safetynet.R;
import ua.safetynet.group.Group;
import ua.safetynet.user.User;

public class DetailedTransactionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_transaction);

        TextView TransID = findViewById(R.id.textViewTransID);
        TextView TransUser = findViewById(R.id.textViewTransUser);
        TextView TransGroup = findViewById(R.id.textViewTransGroup);
        TextView TransAmount = findViewById(R.id.textViewTransValue);
        TextView TransTimestamp = findViewById(R.id.textViewTransTime);
        TextView RepaymentTimestamp = findViewById(R.id.textViewTransRepay);
        final NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        Transaction transaction = (Transaction) getIntent().getParcelableExtra("Transaction");
        Database db = new Database();

        /*TransID.setText(transaction.getTransId());
        db.getUser(transaction.getUserId(), new Database.DatabaseUserListener() {
            @Override
            public void onUserRetrieval(User user) {
                TransUser.setText(user.getName());
            }
        });
        db.getGroup(transaction.getGroupId(), new Database.DatabaseGroupListener() {
            @Override
            public void onGroupRetrieval(Group group) {
                TransGroup.setText(group.getName());
            }
        });
        TransAmount.setText(format.format(transaction.getFunds()));
        TransTimestamp.setText(transaction.getTimestamp().toString());
        RepaymentTimestamp.setText(transaction.getRepayTimestamp().toString());*/
    }
}
