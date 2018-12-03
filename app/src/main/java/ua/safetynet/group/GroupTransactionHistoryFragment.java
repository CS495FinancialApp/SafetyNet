package ua.safetynet.group;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ua.safetynet.Database;
import ua.safetynet.R;
import ua.safetynet.payment.Transaction;
import ua.safetynet.payment.TransactionRecyclerAdapter;

/**
 * @author Jake Bailey
 * Activity for tracking a group's transaction history
 */
public class GroupTransactionHistoryFragment extends Fragment {
    private static final String GROUP = "group";
    private static final String TAG = "GROUP HOME FRAGMENT";
    private Group group;
    private RecyclerView groupTrans;
    private TextView groupName;
    private List<Transaction> transactionList;
    private RecyclerView mTransactionRecycler;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    public GroupTransactionHistoryFragment() {
        // Required empty public constructor
    }


    public static GroupTransactionHistoryFragment newInstance(Group group) {
        GroupTransactionHistoryFragment fragment = new GroupTransactionHistoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_group_transaction_history,container,false );


        groupName = view.findViewById(R.id.textViewGroupID);
        groupTrans = view.findViewById(R.id.RecyclerViewGroupTrans);
        groupName.setText(group.getName());
        //Set linear layout
        mLayoutManager = new LinearLayoutManager(container.getContext());
        mTransactionRecycler = view.findViewById(R.id.view_user_recycler);
        mTransactionRecycler.setHasFixedSize(true);
        mTransactionRecycler.setLayoutManager(mLayoutManager);
        mTransactionRecycler.setItemAnimator(new DefaultItemAnimator());

        return view;
    }
    private void getTransactions() {
        Database db = new Database();
        db.queryGroupTransactions(group.getGroupId(), (transactions) -> {
            //set transaction list
            transactionList = transactions;
            //Specify adapter
            mAdapter = new TransactionRecyclerAdapter(transactionList,TransactionRecyclerAdapter.USER);
            groupTrans.setAdapter(mAdapter);

        });
    }
}
