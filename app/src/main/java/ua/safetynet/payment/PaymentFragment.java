/***
 * @author Jeremy McCormick
 * Fragment class facilitating payments through Braintree gateway. Takes card, paypal and Venmo
 * Has to first fetch a client token from the server to authenticate with the Braintree Drop-In UI
 * Drop In UI returns a payment method nonce which can then be sent to the server to make the transaction
 */
package ua.safetynet.payment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ua.safetynet.Database;
import ua.safetynet.R;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import ua.safetynet.group.Group;
import ua.safetynet.user.User;


public class PaymentFragment extends Fragment {
    //Interface to define callback in which transaction Id will be returned
    public interface OnPaymentCompleteListener {
        public void onPaymentComplete(String transactionId);
    }


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "PAYMENT ACTIVITY";
    private static final String AMOUNT = "amount";
    private static final String GROUPID = "groupId";
    private static final String USERID = "userId";
    private static int REQUEST_CODE;
    private static final String SERVERURL = "https://safetynet-495.herokuapp.com/";
    private static final String SERVERTOKEN = "client_token/";
    private static final String SERVERTRANS = "checkout";
    private static final int SERVERPORT = 443;
    private BigDecimal amount = new BigDecimal(0);
    private String groupId = null;
    private String userId = null;
    private User user = null;
    private String clientToken = null;
    private EditText amountText;
    MaterialSpinner spinner;
    OnPaymentCompleteListener mPaymentListener;

    public PaymentFragment() {
        // Required empty public constructor
    }

    public static PaymentFragment newInstance(BigDecimal amount, String groupId) {
        PaymentFragment fragment = new PaymentFragment();
        Bundle args = new Bundle();
        args.putString(AMOUNT, amount.toPlainString());
        args.putString(GROUPID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    /***
     * OnCreate method can accept the groupId of the group being deposited into and the
     * amount as a string as well. Uses these to prepopulate the values themselves and the
     * UI
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            amount = new BigDecimal(getArguments().getString(AMOUNT));
            groupId = getArguments().getString(GROUPID);
            userId = getArguments().getString(USERID);
        }
        if (userId == null) {
            userId = FirebaseAuth.getInstance().getUid();
            Log.d(TAG, "Getting Uid from firebase." + userId);
        }
        //Get user obj from firestore
        Database db = new Database();
        db.getUser(userId, new Database.DatabaseUserListener() {
                    @Override
                    public void onUserRetrieval(User user) {
                        updateUser(user);
                    }
                });
        getClientToken();
    }

    /**
     * Sets up the view. Formats the amount text, sets up the spinner and the button onClick
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        Button checkoutButton = view.findViewById(R.id.checkout_button);
        //Set amount edittext and set it to initial value;
        amountText = view.findViewById(R.id.payment_amount);
        String formatted = NumberFormat.getCurrencyInstance().format(amount);
        amountText.setText(formatted);
        //Set onTextChanged Listener for amount Edit text to help with formatting
        setupAmountEditTextListener();
        //Setup spinner for group list
        spinner = view.findViewById(R.id.payment_group_spinner);
        setupGroupSpinner();
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInputs())
                    launchDropIn();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPaymentCompleteListener) {
            mPaymentListener = (OnPaymentCompleteListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPaymentCompleteListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPaymentListener = null;
    }

    /**
     * Once the client token has been retrieved, we can launch the Braintree Drop in UI to get payment method nonce
     */
    private void launchDropIn() {
        DropInRequest dropInRequest = new DropInRequest().clientToken(clientToken).vaultManager(true);
        startActivityForResult(dropInRequest.getIntent(this.getContext()), REQUEST_CODE);
    }

    /**
     * Uses HTTP library to make call to server to fetch client token. Passes userId to server
     * so Braintree can treat them as a returning customer
     */
    private void getClientToken() {
        AsyncHttpClient client = new AsyncHttpClient(SERVERPORT);
        client.get(SERVERURL + SERVERTOKEN + userId, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable throwable) {
                Log.d(TAG, "Failed to fetch client token");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                clientToken = new String(responseBody);
            }
        });
    }

    /**
     * Creates the actual transaction once we have recieved a payment method nonce from drop in UI
     * Sends the user and group ID to server for logging with the transaction.
     * Calls onPaymentComplete Listener with transaction data if successful
     * @param nonce Payment method nonce
     */
    private void createTransaction(PaymentMethodNonce nonce) {
        AsyncHttpClient client = new AsyncHttpClient(SERVERPORT);
        RequestParams params = new RequestParams();
        params.put("payment_method_nonce", nonce.getNonce());
        params.put("groupId", groupId);
        params.put("amount", amount);
        params.put("userId",userId);
        params.put("email", user.getEmail());
        params.put("name", user.getName());
        client.post(SERVERURL + SERVERTRANS, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBytes, Throwable throwable) {
                Log.d(TAG, "Failed to send nonce to server. Status Code: " + statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBytes) {
                String transactionId = new String(responseBytes);
                mPaymentListener.onPaymentComplete(transactionId);
            }
        });

    }

    /**
     * Handles return from Braintree DropIn UI. Gets the payment method nonce from the response and
     * calls createTransaction with it
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Returned from Braintree Drop-In");
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                createTransaction(result.getPaymentMethodNonce());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.d(TAG, error.toString());
            }
        }
    }

    /**
     * Adds text changed listener to amount text box. Formats it in a money style with digits shifting down
     * as you enter them
     */
    public void setupAmountEditTextListener() {
        amountText.addTextChangedListener(new TextWatcher() {
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
                    amountText.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^0-9]", "");
                    BigDecimal parsed = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                    amount = parsed;
                    String formatted = NumberFormat.getCurrencyInstance().format(parsed);
                    current = formatted;
                    amountText.setText(formatted);
                    amountText.setSelection(formatted.length());

                    amountText.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Populates the group selection spinner
     */
    public void setupGroupSpinner() {
        Database db = new Database();
        db.queryGroups(new Database.DatabaseGroupsListener() {
            @Override
            public void onGroupsRetrieval(ArrayList<Group> groups) {
                ArrayList<String> groupsNames = new ArrayList<>();
                for(Group g : groups) {
                    groupsNames.add(g.getName());
                }
                spinner.setItems(groupsNames);
            }
        });
        if (groupId == null)
            spinner.setSelected(false);
        else {
            //Create group to compare to and set to passed in groupID
            Group compGroup = new Group();
            compGroup.setGroupId(groupId);
            int index = spinner.getItems().indexOf(compGroup);
            spinner.setSelectedIndex(index);
        }
    }

    /**
     * Checks inputs to make sure they are correct, throws a toasts if not.
     * @return
     */
    private boolean checkInputs() {
        if(amount.equals(new BigDecimal(0))) {
            Toast.makeText(getContext(),"Please enter amount", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!spinner.hasSelection()) {
            Toast.makeText(getContext(), "Please enter group", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Helper to update user inside an asynch listener
     * @param user
     */
    private void updateUser(User user) {
        this.user = user;
    }
}
