package ua.safetynet.group;

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

import java.text.NumberFormat;
import java.util.Locale;

import ua.safetynet.Database;
import ua.safetynet.R;
import ua.safetynet.user.UserTransactionHistoryFragment;

/**
 * @author Jake Bailey
 * Main activity for a single group
 */
public class GroupHomeFragment extends Fragment {
    private static final String GROUP = "group";
    private static final String TAG = "GROUP HOME FRAGMENT";
    private Group group;


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
        final TextView name = view.findViewById(R.id.textViewGroupName);
        final TextView balance = view.findViewById(R.id.textViewGroupBalance);
        final NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);    //Dollar format
        Database db = new Database();


        boolean isAdmin = (FirebaseAuth.getInstance().getCurrentUser() != null && group.getAdmins().contains(FirebaseAuth.getInstance().getCurrentUser().getUid()));
        name.setText(group.getName());
        balance.setText("Balance: " + format.format(group.getFunds()));

        //Goes to UserTransactionHistoryFragment Activity
        userHistory.setOnClickListener((v) -> {
           addUserHistoryFragment();
        });

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
        groupHistory.setOnClickListener((v) -> {
            addGroupHistoryFragment();
        });
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
}
