package ua.safetynet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import ua.safetynet.group.Group;
import ua.safetynet.user.User;

/**
 * @author Jake Bailey
 * Main activity for a single group
 */
public class Group_home2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_home2);

        Button userHistory = (Button) findViewById(R.id.buttonUserHistory);
        Button editGroup = (Button) findViewById(R.id.buttonEditGroup);
        Button groupHistory = (Button) findViewById(R.id.buttonGroupHistory);
        final TextView name = findViewById(R.id.textViewGroupName);
        final TextView balance = findViewById(R.id.textViewGroupBalance);
        final NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);    //Dollar format
        final Intent UserIntent = new Intent(Group_home2.this, UserTransactionHistory.class);
        final Intent EditIntent = new Intent(Group_home2.this, EditGroup.class);
        final Intent GroupIntent = new Intent(Group_home2.this, GroupTransactionHistory.class);
        Database db = new Database();

        final boolean[] adminFlag = {false};

        db.getGroup(getIntent().getStringExtra("group_ID"), new Database.DatabaseGroupListener() {
            @Override
            public void onGroupRetrieval(Group group) {
                name.setText(group.getName());
                balance.setText("Balance: " + format.format(group.getFunds()));
                //Pass groupID for future retrieval
                GroupIntent.putExtra("group_ID", group.getGroupId());
                EditIntent.putExtra("group_ID", group.getGroupId());
                UserIntent.putExtra("group_ID", group.getGroupId());
                if (group.getAdmins().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    adminFlag[0] = true;
                }
            }
        });

        //Goes to UserTransactionHistory Activity
        userHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(UserIntent);
            }
        });

        editGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adminFlag[0]) {
                    startActivity(EditIntent);
                }
                else {
                    Toast.makeText(Group_home2.this, "Only admins can edit groups", Toast.LENGTH_LONG).show();
                }
            }

        });
        //Goes to GroupTransactionHistory Activity
        groupHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(GroupIntent);
            }
        });
    }
}
