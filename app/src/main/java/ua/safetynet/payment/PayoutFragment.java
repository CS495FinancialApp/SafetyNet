package ua.safetynet.payment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import ua.safetynet.Database;
import ua.safetynet.R;
import ua.safetynet.group.Group;

/**
 * @author Jeremy McCormick
 * Payout Fragment to facilitate withdrawals from group funds. Uses PayPal Payouts. Can
 * be sent to corresponding paypal with either email or phone #
 */
public class PayoutFragment extends Fragment {
    private static final String TAG = "PAYOUT FRAGMENT";
    private static final String AMOUNT = "amount";
    private static final String GROUPID = "groupId";
    private static final String USERID = "userId";
    private static final int APIPORT = 443;
    private static final String APIBASEURL = "https://api.sandbox.paypal.com/";
    private static final String APITOKENURL = "v1/oauth2/token";
    private static final String APITRANS = "v1/payments/payouts";
    private String clientToken = null;
    private BigDecimal amount = new BigDecimal(0);
    private String groupId;
    private EditText amountText;
    private EditText emailText;
    private MaterialSpinner spinner;
    private OnFragmentInteractionListener mListener;

    public PayoutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param amount Big Decimal as amount
     * @param groupId
     * @return A new instance of fragment PayoutFragment.
     */
    public static PayoutFragment newInstance(BigDecimal amount, String groupId) {
        PayoutFragment fragment = new PayoutFragment();
        Bundle args = new Bundle();
        args.putString(AMOUNT, amount.toString());
        args.putString(GROUPID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Accepts amount and groupId in the bundle to prefill UI components and their values
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getClientToken();
        if (getArguments() != null) {
            amount = new BigDecimal(getArguments().getString(AMOUNT));
            groupId = getArguments().getString(GROUPID);
        }
    }

    /**
     * Sets up the fragment view. Formats amount text box, sets up button listener and sets up
     * group spinner
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_payout, container, false);
        //Set amount edittext and set it to initial value;
        amountText = view.findViewById(R.id.payout_amount_text);
        String formatted = NumberFormat.getCurrencyInstance().format(amount);
        amountText.setText(formatted);
        setupAmountEditTextListener();
        //Setup withdraw button listener
        Button withdrawButton = view.findViewById(R.id.payout_withdrawal_btn);
        withdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeWithdrawal();
            }
        });
        spinner = view.findViewById(R.id.payout_group_spinner);
        setupGroupSpinner();
        //Setup email edit text
        emailText = view.findViewById(R.id.payout_email_text);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    private void makeWithdrawal() {
       

    }

    /**
     * Gets client token from PayPal Payouts REST API. Uses clientId and secret stored in secrets.xml
     *
     */
    private void getClientToken() {
        String clientId = getString(R.string.paypal_client_id);
        String secret = getString(R.string.paypal_secret);
        AsyncHttpClient client = new AsyncHttpClient(APIPORT);
        client.setAuthenticationPreemptive(true);
        client.setBasicAuth(clientId, secret);
        client.addHeader("content-type", "application/x-www-form-urlencoded");
        RequestParams params = new RequestParams();
        params.put("grant_type", "client_credentials");
        client.post(APIBASEURL + APITOKENURL,params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    clientToken = response.getString("access_token");
                    Log.d(TAG, "Fetched client token");
                } catch (JSONException e) {
                    Log.d(TAG, "Could not parse client token xml data");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject json) {
                Log.d(TAG, "Could not fetch client token" + t.toString() + json.toString());
            }
        });
    }
    private JSONObject makePayoutsJson(String email, String amount) {
        JSONObject json = new JSONObject();
        JSONObject senderHeader = new JSONObject();
        JSONObject item = new JSONObject();
        JSONObject amountJson = new JSONObject();
        String id = UUID.randomUUID().toString().replace("-", "");
        try {

            senderHeader.put("sender_batch_id", id);
            senderHeader.put("email_subject", "You have a Payout!");
            senderHeader.put("recipient_type","EMAIL");

            amountJson.put("value",amount);
            amountJson.put("currency","USD");

            item.put("sender_item_id",id);
            item.put("receiver",email);
            item.put("amount",amountJson);

            JSONArray itemArray = new JSONArray();
            itemArray.put(item);
            json.put("sender_batch_header",senderHeader);
            json.put("items", itemArray);
            Log.d(TAG, json.toString(1));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
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
