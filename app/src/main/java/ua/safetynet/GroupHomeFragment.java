package ua.safetynet;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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

import ua.safetynet.group.Group;
import ua.safetynet.user.User;
import ua.safetynet.user.ViewUserFragment;

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
        //final Intent UserIntent = new Intent(GroupHomeFragment.this, UserTransactionHistory.class);
        //final Intent EditIntent = new Intent(GroupHomeFragment.this, EditGroup.class);
        //final Intent GroupIntent = new Intent(GroupHomeFragment.this, GroupTransactionHistory.class);
        Database db = new Database();

        final boolean[] adminFlag = {false};

        db.getGroup(group.getGroupId(), new Database.DatabaseGroupListener() {
            @Override
            public void onGroupRetrieval(Group group) {
                name.setText(group.getName());
                balance.setText("Balance: " + format.format(group.getFunds()));
                //Pass groupID for future retrieval
                //GroupIntent.putExtra("group_ID", group.getGroupId());
                //EditIntent.putExtra("group_ID", group.getGroupId());
                //UserIntent.putExtra("group_ID", group.getGroupId());
                if (group.getAdmins().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    adminFlag[0] = true;
                }
            }
        });

        //Goes to UserTransactionHistory Activity
        userHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //startActivity(UserIntent);
            }
        });

        editGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adminFlag[0]) {
                    //startActivity(EditIntent);
                }
                else {
                    Toast.makeText(getContext(), "Only admins can edit groups", Toast.LENGTH_LONG).show();
                }
            }

        });
        //Goes to GroupTransactionHistory Activity
        groupHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //startActivity(GroupIntent);
            }
        });

        return view;
    }
}
