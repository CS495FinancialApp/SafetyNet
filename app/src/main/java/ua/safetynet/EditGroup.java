package ua.safetynet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.math.BigDecimal;

import ua.safetynet.group.Group;
import ua.safetynet.user.User;

public class EditGroup extends AppCompatActivity {


    ArrayAdapter <String> userAdapter;
    ArrayAdapter <String> adminAdapter;
    String userSelected;    //the ID of the user chosen in the user spinner
    String adminSelected;   //the ID of the admin chosen in the admin spinner

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        final TextView groupName = findViewById(R.id.textViewGroupID);
        final TextView selectedUser = findViewById(R.id.textViewSelectedUser);
        final TextView selectedAdmin = findViewById(R.id.textViewSelectedAdmin);

        final EditText paymentTime = findViewById(R.id.editTextRepayTime);
        final EditText withdrawalLimit = findViewById(R.id.editTextWithdrawalLimit);
        final EditText newUser = findViewById(R.id.editTextNewUser);

        final Button addNewUser = findViewById(R.id.bttnAddUser);
        Button addAdmin = findViewById(R.id.bttnAddAdmin);
        Button removeUser = findViewById(R.id.bttnRemoveUser);
        Button removeAdmin = findViewById(R.id.bttnRemoveAdmin);
        Button confirmChanges = findViewById(R.id.bttnConfirmChanges);

        final Spinner users = findViewById(R.id.spinnerUsers);
        final Spinner admins = findViewById(R.id.spinnerAdmins);

        final Database db = new Database();
        final Group[] allGroup = {new Group()};

        final boolean[] adminFlag = {false};

        db.getGroup(getIntent().getStringExtra("group_ID"), new Database.DatabaseGroupListener() {
            @Override
            public void onGroupRetrieval(final Group group) {
                userAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,group.getUsers());
                adminAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,group.getAdmins());
                groupName.setText(group.getName());
                paymentTime.setText(String.valueOf(group.getRepaymentTime()));
                withdrawalLimit.setText(group.getWithdrawalLimit().toString());
                allGroup[0] = group;

                users.setAdapter(userAdapter);
                admins.setAdapter(adminAdapter);

                users.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        userSelected = String.valueOf(group.getUsers().get(position));
                        db.getUser(userSelected, new Database.DatabaseUserListener() {
                            @Override
                            public void onUserRetrieval(User user) {
                                selectedUser.setText(user.getName());
                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                admins.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        adminSelected = String.valueOf(group.getAdmins().get(position));
                        db.getUser(adminSelected, new Database.DatabaseUserListener() {
                            @Override
                            public void onUserRetrieval(User user) {
                                selectedAdmin.setText(user.getName());
                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

        addNewUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                allGroup[0].addUsers(addNewUser.getText().toString());
            }
        });

        addAdmin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                allGroup[0].addAdmins(userSelected);
            }
        });

        removeUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!userSelected.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    allGroup[0].removeUsers(userSelected);
                }
                else{
                    Toast.makeText(EditGroup.this, "You cannot remove yourself.", Toast.LENGTH_LONG).show();
                }
            }
        });

        removeAdmin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!adminSelected.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    allGroup[0].removeAdmins(adminSelected);
                }
                else{
                    Toast.makeText(EditGroup.this, "You cannot remove yourself.", Toast.LENGTH_LONG).show();
                }
            }
        });

        confirmChanges.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                allGroup[0].setRepaymentTime(Integer.parseInt(paymentTime.getText().toString()));
                allGroup[0].setWithdrawalLimit(new BigDecimal(withdrawalLimit.getText().toString()));
                db.setGroup(allGroup[0]);
            }
        });

    }


}
