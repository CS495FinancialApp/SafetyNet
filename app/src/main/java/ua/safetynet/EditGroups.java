package ua.safetynet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import ua.safetynet.group.Group;

public class EditGroups extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_group);

        final TextView GroupName = findViewById(R.id.textViewGroupID);
        final EditText paymentTime = findViewById(R.id.editTextRepayTime);
        final EditText withdrawalLimit = findViewById(R.id.editTextWithdrawalLimit);
        final Spinner users = findViewById(R.id.spinnerUsers);
        final Spinner admins = findViewById(R.id.spinnerAdmins);

        Database db = new Database();

        db.getGroup(getIntent().getStringExtra("group_ID"), new Database.DatabaseGroupListener() {
            @Override
            public void onGroupRetrieval(Group group) {
                GroupName.setText(group.getName());
                paymentTime.setText(group.getRepaymentTime());
                withdrawalLimit.setText((CharSequence) group.getWithdrawalLimit());
            }
        });
    }
}
