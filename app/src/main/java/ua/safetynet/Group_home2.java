package ua.safetynet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

        getIncomingIntent();

        Button userHistory = (Button) findViewById(R.id.buttonUserHistory);
        Button groupHistory = (Button) findViewById(R.id.buttonGroupHistory);

        //Goes to UserTransactionHistory Activity
        userHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent depositIntent = new Intent(Group_home2.this, UserTransactionHistory.class);
                startActivity(depositIntent);
            }
        });

        //Goes to GroupTransactionHistory Activity
        groupHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent historyIntent = new Intent(Group_home2.this, GroupTransactionHistory.class);
                //Need to pass group for transaction history
                startActivity(historyIntent);
            }
        });
    }

    //Function recieves data from intent passed from Group_home2
     private void getIncomingIntent(){
        if(getIntent().hasExtra("group_name")) {
            TextView name = findViewById(R.id.textViewGroupName);
            String groupName = getIntent().getStringExtra("group_name");

            name.setText(groupName);
        }
        if(getIntent().hasExtra("group_balance")){
            TextView balance = findViewById(R.id.textViewGroupBalance);
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
            BigDecimal groupBalance = (BigDecimal) getIntent().getSerializableExtra("group_balance");

            balance.setText("Balance: " + format.format(groupBalance));
        }
    }
}
