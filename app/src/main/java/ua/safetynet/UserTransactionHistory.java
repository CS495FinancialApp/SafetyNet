package ua.safetynet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class UserTransactionHistory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_transaction_history);

        TextView GroupName = findViewById(R.id.textViewUserID);
        GroupName.setText("IN PROGRESS");
    }
}
