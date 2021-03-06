package ua.safetynet.payment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import ua.safetynet.Database;
import ua.safetynet.R;
import ua.safetynet.group.Group;
import ua.safetynet.user.User;

/**
 * @author Jake Bailey
 * Activity for displaying the details of a particular transaction
 */
public class DetailedTransactionFragment extends Fragment {
    private static final String TRANSACTION = "transaction";
    private Transaction transaction;
    public DetailedTransactionFragment() {
        // Required empty public constructor
    }


    public static DetailedTransactionFragment newInstance(Transaction transaction) {
        DetailedTransactionFragment fragment = new DetailedTransactionFragment();
        Bundle args = new Bundle();
        args.putParcelable(TRANSACTION, transaction);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transaction = getArguments().getParcelable(TRANSACTION);
            Log.d("DETAILED TRANSACTION", "Trans obj gotten from bundle");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_transaction,container, false );
        //Set repay button to gone initially
        view.findViewById(R.id.payoff_btn).setVisibility(View.GONE);
        TextView transID = view.findViewById(R.id.textViewTransID);
        TextView transUser = view.findViewById(R.id.textViewTransUser);
        TextView transGroup = view.findViewById(R.id.textViewTransGroup);
        TextView transAmount = view.findViewById(R.id.textViewTransValue);
        TextView transTimestamp = view.findViewById(R.id.textViewTransTime);
        TextView repaymentTimestamp = view.findViewById(R.id.textViewTransRepay);
        TextView repaymentLabel = view.findViewById(R.id.textViewTransLabel6);
        Button repayButton = view.findViewById(R.id.payoff_btn);
        final NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);    //Dollar Format
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);    //Standard Date Format
        Database db = new Database();
        transID.setText(transaction.getTransId());
        //Get user object for proper username
        db.getUser(transaction.getUserId(), new Database.DatabaseUserListener() {
            @Override
            public void onUserRetrieval(User user) {
                if(transaction.shouldAnonymous())
                    transUser.setText("*****");
                else
                    transUser.setText(user.getName());
                //If is users transaction and is a withdrawal, show repay button
                if(user.getTransactions().contains(transaction.getTransId()) && transaction.getRepayTimestamp() != null) {
                    repayButton.setVisibility(View.VISIBLE);
                    //set on click for repay btn
                    repayButton.setOnClickListener(v -> startRepay(transaction.getFunds().negate(),transaction.getGroupId()));
                }
            }
        });
        //Get group object for proper groupname
        db.getGroup(transaction.getGroupId(), new Database.DatabaseGroupListener() {
            @Override
            public void onGroupRetrieval(Group group) {
                transGroup.setText(group.getName());
            }
        });
        transAmount.setText(format.format(transaction.getFunds()));
        //Format timestamp into readable state
        transTimestamp.setText(dateFormat.format(transaction.getTimestamp().toDate()));
        //If transaction is a deposit
        if(transaction.getRepayTimestamp() == null) {
            repaymentLabel.setVisibility(View.GONE);
            repaymentTimestamp.setVisibility(View.GONE);
        }
        else
            repaymentTimestamp.setText(dateFormat.format(transaction.getRepayTimestamp().toDate()));

        return view;
    }
    private void startRepay(BigDecimal amt, String groupId) {
        PaymentFragment paymentFragment = PaymentFragment.newInstance(amt, groupId);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, paymentFragment).addToBackStack(null).commit();
    }
}
