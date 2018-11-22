package ua.safetynet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class EditGroup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        final TextView GroupName = findViewById(R.id.textViewGroupID);
        final EditText paymentTime = findViewById(R.id.editTextRepayTime);
        final EditText withdrawalLimit = findViewById(R.id.editTextWithdrawalLimit);
        final Spinner users = findViewById(R.id.spinnerUsers);
        final Spinner admins = findViewById(R.id.spinnerAdmins);
    }
}
