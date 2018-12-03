package ua.safetynet.group;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import ua.safetynet.Database;
import ua.safetynet.R;
import ua.safetynet.user.User;

public class EditGroupFragment extends Fragment {


    ArrayAdapter <String> userAdapter;
    ArrayAdapter <String> adminAdapter;
    String userSelected;    //the ID of the user chosen in the user spinner
    String adminSelected;   //the ID of the admin chosen in the admin spinner
    private static final String TAG = "EDIT GROUP FRAGMENT";
    private static final String GROUP = "group";
    private Group group;
    private EditText withdrawalLimit;
    private BigDecimal withdrawAmount;

    public EditGroupFragment() {
        // Required empty public constructor
    }


    public static EditGroupFragment newInstance(Group group) {
        EditGroupFragment fragment = new EditGroupFragment();
        Bundle args = new Bundle();
        args.putParcelable(GROUP, group);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            group = getArguments().getParcelable(GROUP);
            Log.d(TAG, "Group obj gotten from bundle");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_group, container,false);
        final TextView groupName = view.findViewById(R.id.textViewGroupID);
        final TextView selectedUser = view.findViewById(R.id.textViewSelectedUser);
        final TextView selectedAdmin = view.findViewById(R.id.textViewSelectedAdmin);

        final EditText paymentTime = view.findViewById(R.id.editTextRepayTime);
        withdrawalLimit = view.findViewById(R.id.editTextWithdrawalLimit);
        withdrawAmount = group.getWithdrawalLimit();
        final EditText newUserEmail = view.findViewById(R.id.editTextNewUser);

        final Button addNewUser = view.findViewById(R.id.bttnAddUser);
        Button addAdmin = view.findViewById(R.id.bttnAddAdmin);
        Button removeUser = view.findViewById(R.id.bttnRemoveUser);
        Button removeAdmin = view.findViewById(R.id.bttnRemoveAdmin);
        Button confirmChanges = view.findViewById(R.id.bttnConfirmChanges);

        final Spinner users = view.findViewById(R.id.spinnerUsers);
        final Spinner admins = view.findViewById(R.id.spinnerAdmins);

        final Database db = new Database();
        final Group[] allGroup = {new Group()};

        final boolean[] adminFlag = {false};
        setupAmountEditTextListener();

        userAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1,group.getUsers());
        adminAdapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_list_item_1,group.getAdmins());
        groupName.setText(group.getName());
        paymentTime.setText(String.valueOf(group.getRepaymentTime()));
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        withdrawalLimit.setText(format.format(group.getWithdrawalLimit()));
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

        addNewUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("EDITGROUP","," + newUserEmail.getText().toString() + ",");
                db.queryUserEmail(newUserEmail.getText().toString(), new Database.DatabaseUserEmailListener(){
                    @Override
                    public void onUserEmailRetrieval(ArrayList<User> users) {
                        if (users.isEmpty()){
                            Log.d(TAG,"empty problem!");
                            Toast.makeText(view.getContext(), "No user has the specified email", Toast.LENGTH_LONG).show();
                        }
                        else {
                            allGroup[0].addUsers(users.get(0).getUserId());
                        }
                    }
                });
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
                    Toast.makeText(view.getContext(), "You cannot remove yourself.", Toast.LENGTH_LONG).show();
                }
            }
        });

        removeAdmin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!adminSelected.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    allGroup[0].removeAdmins(adminSelected);
                }
                else{
                    Toast.makeText(view.getContext(), "You cannot remove yourself.", Toast.LENGTH_LONG).show();
                }
            }
        });

        confirmChanges.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                allGroup[0].setRepaymentTime(Integer.parseInt(paymentTime.getText().toString()));
                allGroup[0].setWithdrawalLimit(withdrawAmount);
                db.setGroup(allGroup[0]);
                Toast.makeText(view.getContext(), "Changes Saved", Toast.LENGTH_SHORT).show();
            }
        });
        return view;

    }

    /**
     * Adds text changed listener to amount text box. Formats it in a money style with digits shifting down
     * as you enter them
     */
    public void setupAmountEditTextListener() {
        withdrawalLimit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            private String current = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    //Throws an error if backspacing with all 0's, check for this and return if so
                    String emptyTest = s.toString().replaceAll("[^1-9]", "");
                    if (emptyTest.isEmpty())
                        return;
                    withdrawalLimit.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^0-9]", "");
                    BigDecimal parsed = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                    withdrawAmount = parsed;
                    String formatted = NumberFormat.getCurrencyInstance().format(parsed);
                    current = formatted;
                    withdrawalLimit.setText(formatted);
                    withdrawalLimit.setSelection(formatted.length());

                    withdrawalLimit.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
