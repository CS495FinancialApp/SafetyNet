package ua.safetynet.group;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import ua.safetynet.Database;
import ua.safetynet.R;
import ua.safetynet.payment.Transaction;
import ua.safetynet.user.UserTransactionHistoryFragment;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * @author Jake Bailey
 * Main activity for a single group
 */
public class GroupHomeFragment extends Fragment {
    private static final String GROUP = "group";
    private static final String TAG = "GROUP HOME FRAGMENT";
    private Group group;
    private Boolean isAdmin;
    private Database db;


    public GroupHomeFragment() {
        // Required empty public constructor
    }


    public static GroupHomeFragment newInstance(Group group) {
        GroupHomeFragment fragment = new GroupHomeFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_home, container, false);
        Button userHistory = view.findViewById(R.id.buttonUserHistory);
        Button editGroup = view.findViewById(R.id.buttonEditGroup);
        Button groupHistory = view.findViewById(R.id.buttonGroupHistory);
        Button leaveGroup = view.findViewById(R.id.buttonLeaveGroup);
        final TextView name = view.findViewById(R.id.textViewGroupName);
        final TextView balance = view.findViewById(R.id.textViewGroupBalance);
        final NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);    //Dollar format
        db = new Database();


        isAdmin = (FirebaseAuth.getInstance().getCurrentUser() != null && group.getAdmins().contains(FirebaseAuth.getInstance().getCurrentUser().getUid()));
        name.setText(group.getName());
        balance.setText("Balance: " + format.format(group.getFunds()));

        //Goes to UserTransactionHistoryFragment Activity
        userHistory.setOnClickListener((v) -> addUserHistoryFragment());

        editGroup.setOnClickListener((v) -> {
            Log.d(TAG, "Edit group clicked");
            if (isAdmin) {
                EditGroupFragment editGroupFragment = EditGroupFragment.newInstance(group);
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.fragment_container, editGroupFragment).addToBackStack(null).commit();

            } else {
                Toast.makeText(getContext(), "Only admins can edit groups", Toast.LENGTH_LONG).show();
            }
        });
        //Goes to UserTransactionHistoryFragment Activity
        groupHistory.setOnClickListener((v) -> addGroupHistoryFragment());

        //Removes the current user from the group
        leaveGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                db.queryUserGroupTransactions(new Database.DatabaseTransactionsListener() {
                    @Override
                    public void onTransactionsRetrieval(ArrayList<Transaction> transactions) {
                        BigDecimal total = new BigDecimal(0);
                        for (Transaction trans:transactions){
                            total = total.add(trans.getFunds());
                            Log.d("GroupHomeFragment",total.toString());
                        }
                        if (total.compareTo(new BigDecimal(0)) < 0){
                            Log.d("GroupHomeFragment","Negative Balance");
                            Toast.makeText(getContext(), "You must repay group debts before leaving", Toast.LENGTH_LONG).show();
                        }
                        else{
                            createDialog();
                        }
                    }
                }, group.getGroupId());
            }
        });
        Log.d("GroupHomeFragment", "Step 3");
        addGroupHistoryFragment();
        return view;
    }

    public void addGroupHistoryFragment() {
        GroupTransactionHistoryFragment groupFragment = GroupTransactionHistoryFragment.newInstance(group);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.group_home_fragment_container, groupFragment).commit();
    }
    public void addUserHistoryFragment() {
        UserTransactionHistoryFragment userFragment = UserTransactionHistoryFragment.newInstance(group);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.group_home_fragment_container, userFragment).commit();
    }

    //creates a confirmation dialog for leaving the group
    private void createDialog(){
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(getActivity());
        alertDlg.setMessage("Are you sure you want to leave the group?");
        alertDlg.setCancelable(false);
        alertDlg.setPositiveButton("Leave Group", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                group.removeUsers(FirebaseAuth.getInstance().getCurrentUser().getUid());
                if (isAdmin){
                    group.removeAdmins(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    db.setGroup(group);
                }
            }
        });
        alertDlg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDlg.create().show();
    }
}
