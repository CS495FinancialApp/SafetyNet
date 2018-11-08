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

public class Group_home2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_home2);

        getIncomingIntent();

        Button withdrawalButton = (Button) findViewById(R.id.buttonGroupWithdrawal);
        Button depositButton = (Button) findViewById(R.id.buttonGroupDeposit);

        withdrawalButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent withdrawalIntent = new Intent(Group_home2.this, Withdrawal.class);
                startActivity(withdrawalIntent);
            }
        });

        depositButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent depositIntent = new Intent(Group_home2.this, Deposit.class);
                startActivity(depositIntent);
            }
        });
    }

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
