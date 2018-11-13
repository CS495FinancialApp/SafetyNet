package ua.safetynet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

import ua.safetynet.group.Group;

public class GroupTransactionHistory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_transaction_history);

        final TextView GroupName = findViewById(R.id.textViewGroupID);
        GroupName.setText("IN PROGRESS");
    }
}
