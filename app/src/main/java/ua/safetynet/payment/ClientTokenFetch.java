package ua.safetynet.payment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.os.Process;
import android.support.annotation.NonNull;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import ua.safetynet.R;

public class ClientTokenFetch {
    private String braintreeClientToken;
    private String paypalClientToken;
    private String userId;
    private Context context;
    private static final String SERVERURL = "https://safetynet-495.herokuapp.com/";
    private static final String SERVERTOKEN = "client_token/";
    private static final int SERVERPORT = 443;
    private static final int APIPORT = 443;
    private static final String APIBASEURL = "https://api.sandbox.paypal.com/";
    private static final String APITOKENURL = "v1/oauth2/token";
    private static final String TAG = "ClientTokenRunnable";
    private SharedPreferences sharedPrefs;

    public ClientTokenFetch(Context context) {
        this.context = context;
        sharedPrefs = this.context.getSharedPreferences("tokens",Context.MODE_PRIVATE );
    }
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Uses HTTP library to make call to server to fetch client token. Passes userId to server
     * so Braintree can treat them as a returning customer
     */
    public void fetchBraintreeToken() {
        AsyncHttpClient clientBt = new AsyncHttpClient(SERVERPORT);
        clientBt.setURLEncodingEnabled(true);
        clientBt.get(SERVERURL + SERVERTOKEN + userId, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable throwable) {
                Log.d(TAG, "Failed to fetch client token");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                braintreeClientToken = new String(responseBody);
                saveBtToSharedPrefs(braintreeClientToken);
                Log.d(TAG, "Got Braintree token");
            }
        });
    }
    public void fetchPaypalToken() {
        String clientId = context.getString(R.string.paypal_client_id);
        String secret = context.getString(R.string.paypal_secret);
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
                    paypalClientToken = response.getString("access_token");
                    savePaypalToSharedPrefs(paypalClientToken);
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
    private void saveBtToSharedPrefs(String token) {
        //Save token to shared prefs. apply() for async update
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("braintree", token);
        editor.apply();
    }
    private void savePaypalToSharedPrefs(String token) {
        //Save token to shared prefs. apply() for async update
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("paypal", token);
        editor.apply();
    }
    public String getBraintreeToken() {
        return braintreeClientToken;
    }
    public String getPaypalToken() {
        return paypalClientToken;
    }
}
