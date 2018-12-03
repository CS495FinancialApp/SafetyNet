package ua.safetynet.user;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import ua.safetynet.Database;
import ua.safetynet.R;
import ua.safetynet.group.Group;
import ua.safetynet.payment.Transaction;
import ua.safetynet.payment.TransactionRecyclerAdapter;
import ua.safetynet.user.User;

/**
 * @author Jake Bailey
 * Activity for tracking a user's transaction history
 */
public class UserTransactionHistoryFragment extends Fragment {

    private static final String GROUP = "group";
    private static final String TAG = "USER HOME FRAGMENT";
    private Group group;
    private TextView groupName;
    private List<Transaction> transactionList;
    private RecyclerView mTransactionRecycler;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    public UserTransactionHistoryFragment() {
        // Required empty public constructor
    }


    public static UserTransactionHistoryFragment newInstance(Group group) {
        UserTransactionHistoryFragment fragment = new UserTransactionHistoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_group_transaction_history, container, false);


        groupName = view.findViewById(R.id.textViewGroupID);
        groupName.setText(group.getName());
        //Set linear layout
        mLayoutManager = new LinearLayoutManager(container.getContext());
        mTransactionRecycler = view.findViewById(R.id.group_history_recycler);
        mTransactionRecycler.setHasFixedSize(true);
        mTransactionRecycler.setLayoutManager(mLayoutManager);
        mTransactionRecycler.setItemAnimator(new DefaultItemAnimator());
        getTransactions();
        return view;
    }

    private void getTransactions() {
        transactionList = new ArrayList<>();
        Database db = new Database();
        db.queryGroupTransactions(group.getGroupId(), (transactions) -> {
            //set transaction list
            transactionList = transactions;
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            transactionList.removeIf(transaction -> !transaction.getUserId().equals(uid));
            //Specify adapter
            mAdapter = new TransactionRecyclerAdapter(transactionList, TransactionRecyclerAdapter.NOIMAGE,getActivity().getSupportFragmentManager());
            mTransactionRecycler.setAdapter(mAdapter);

        });
    }
}