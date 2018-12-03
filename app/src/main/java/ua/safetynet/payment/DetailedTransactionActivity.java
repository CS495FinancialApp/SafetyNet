package ua.safetynet.payment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import ua.safetynet.Database;
import ua.safetynet.R;
import ua.safetynet.group.Group;
import ua.safetynet.user.User;

/**
 * @author Jake Bailey
 * Activity for displaying the details of a particular transaction
 */
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
        final NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);    //Dollar Format
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);    //Standard Date Format
        Transaction transaction = (Transaction) getIntent().getParcelableExtra("Transaction");  //Retrieve transaction object
        Database db = new Database();

        TransID.setText(transaction.getTransId());
        //Get user object for proper username
        db.getUser(transaction.getUserId(), new Database.DatabaseUserListener() {
            @Override
            public void onUserRetrieval(User user) {
                TransUser.setText(user.getName());
            }
        });
        //Get group object for proper groupname
        db.getGroup(transaction.getGroupId(), new Database.DatabaseGroupListener() {
            @Override
            public void onGroupRetrieval(Group group) {
                TransGroup.setText(group.getName());
            }
        });
        TransAmount.setText(format.format(transaction.getFunds()));
        //Format timestamp into readable state
        TransTimestamp.setText(dateFormat.format(transaction.getTimestamp().toDate()));
        //If transaction is a deposit
        if(transaction.getRepayTimestamp() == null)
            RepaymentTimestamp.setVisibility(View.GONE);
        else
            RepaymentTimestamp.setText(dateFormat.format(transaction.getRepayTimestamp().toDate()));
    }
}
