package ua.safetynet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import ua.safetynet.group.Group;
import ua.safetynet.user.User;

public class Group_home2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_home2);

        //getIncomingIntent();

        Button userHistory = (Button) findViewById(R.id.buttonUserHistory);
        Button groupHistory = (Button) findViewById(R.id.buttonGroupHistory);
        final TextView name = findViewById(R.id.textViewGroupName);
        final TextView balance = findViewById(R.id.textViewGroupBalance);
        final NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        final Intent UserIntent = new Intent(Group_home2.this, UserTransactionHistory.class);
        final Intent GroupIntent = new Intent(Group_home2.this, GroupTransactionHistory.class);

        Database db = new Database();

        db.getGroup(getIntent().getStringExtra("group_ID"), new Database.DatabaseGroupListener() {
            @Override
            public void onGroupRetrieval(Group group) {
                name.setText(group.getName());
                balance.setText("Balance: " + format.format(group.getFunds()));
                UserIntent.putExtra("group_ID", group.getGroupId());
                GroupIntent.putExtra("group_ID", group.getGroupId());
            }
        });

        //Goes to UserTransactionHistory Activity
        userHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(UserIntent);
            }
        });

        //Goes to GroupTransactionHistory Activity
        groupHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(GroupIntent);
            }
        });
    }

/**
    //Function recieves data from intent passed from Group_home2
     private void getIncomingIntent(){
        if(getIntent().hasExtra("group_name")) {
            TextView name = findViewById(R.id.textViewGroupName);
            String groupName = getIntent().getStringExtra("group_name");

            Database db = new Database();

            name.setText(groupName);
        }
        if(getIntent().hasExtra("group_balance")){
            TextView balance = findViewById(R.id.textViewGroupBalance);
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
            BigDecimal groupBalance = (BigDecimal) getIntent().getSerializableExtra("group_balance");

            balance.setText("Balance: " + format.format(groupBalance));
        }
    }**/
}
