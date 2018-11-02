package ua.safetynet.payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import ua.safetynet.R;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import java.math.BigDecimal;
import java.text.NumberFormat;

import cz.msebera.android.httpclient.Header;


public class PaymentFragment extends Fragment implements PaymentMethodNonceCreatedListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "PAYMENT ACTIVITY";
    private static final String AMOUNT = "amount";
    private static final String GROUPID = "groupId";
    private static final String USERID = "userId";
    private static int REQUEST_CODE;
    private static final String SERVERURL = "https://safetynet-495.herokuapp.com/";
    private static final String SERVERTOKEN = "client_token";
    private static final String SERVERTRANS = "checkout";
    private static final int SERVERPORT = 443;
    private BigDecimal amount= new BigDecimal(0);
    private String groupId =null;
    private String userId = null;
    private String clientToken = null;
    private OnFragmentInteractionListener mListener;
    private EditText amountText;

    public PaymentFragment() {
        // Required empty public constructor
    }

    public static PaymentFragment newInstance(BigDecimal amount, String groupId) {
        PaymentFragment fragment = new PaymentFragment();
        Bundle args = new Bundle();
        args.putDouble(AMOUNT, amount.doubleValue());
        args.putString(GROUPID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            amount = new BigDecimal(getArguments().getDouble(AMOUNT));
            groupId = getArguments().getString(GROUPID);
            userId = getArguments().getString(USERID);
        }
        getClientToken();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_payment, container, false);
        Button checkoutButton = view.findViewById(R.id.checkout_button);
        //Set amount edittext and set it to initial value;
        amountText = view.findViewById(R.id.payment_amount);
        String formatted = NumberFormat.getCurrencyInstance().format(amount);
        amountText.setText(formatted);
        //Set onTextChanged Listener for amount Edit text to help with formatting
        setupAmountEditTextListener();
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchDropIn();
            }
        });
        return view;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void launchDropIn() {
        DropInRequest dropInRequest = new DropInRequest().clientToken(clientToken);
        startActivityForResult(dropInRequest.getIntent(this.getContext()), REQUEST_CODE);
    }

    private void getClientToken(){
        AsyncHttpClient client = new AsyncHttpClient(SERVERPORT);
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        client.get(SERVERURL + SERVERTOKEN, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable throwable) {
                Log.d(TAG,"Failed to fetch client token");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                clientToken = new String(responseBody);
            }
        });
    }
    private void createTransaction(PaymentMethodNonce nonce) {
        AsyncHttpClient client = new AsyncHttpClient(SERVERPORT);
        RequestParams params = new RequestParams();
        params.put("payment_method_nonce", nonce.getNonce());
        params.put("amount", amount);
        client.post(SERVERURL + SERVERTRANS, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBytes, Throwable throwable) {
                Log.d(TAG,"Failed to send nonce to server. Status Code: " + statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBytes) {
                String responseString = new String(responseBytes);
                if(responseString.startsWith("created"))
                    Toast.makeText(getContext(),"Transaction Complete", Toast.LENGTH_LONG).show();
            }
        });

    }
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

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {

    }

    public void setupAmountEditTextListener() {
        amountText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            private String current = "";
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(current)){
                    //Throws an error if backspacing with all 0's, check for this and return if so
                    String emptyTest = s.toString().replaceAll("[^1-9]", "");
                    if(emptyTest.isEmpty())
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
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
