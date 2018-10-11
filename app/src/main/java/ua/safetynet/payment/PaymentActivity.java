package ua.safetynet.payment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.braintreepayments.api.dropin.DropInRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import ua.safetynet.R;
import cz.msebera.android.httpclient.Header;

public class PaymentActivity extends AppCompatActivity
{
    String tokenizationKey = "sandbox_pkfyhzmk_hjpn8nvr5356mmkw";
    int REQUEST_CODE;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        final Button button = findViewById(R.id.payment_test_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeRequest();
            }
        });
        //pay();
    }
    private void makeRequest()
    {
        String url = "http://192.168.1.200:8080/hello";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("name", "Android");
        client.get(url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                String err = "Failed to get response";
                EditText editText  = findViewById(R.id.payment_test_response);
                editText.setText(err,TextView.BufferType.NORMAL);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                EditText editText  = findViewById(R.id.payment_test_response);
                editText.setText(responseString,TextView.BufferType.NORMAL);
                Log.d("PaymentActivityResponse",responseString );
            }
        });
    }
    private void pay()
    {
        DropInRequest dropInRequest = new DropInRequest().tokenizationKey(tokenizationKey);
        startActivityForResult(dropInRequest.getIntent(this),REQUEST_CODE);
    }
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                // use the result to update your UI and send the payment method nonce to your server
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            }
        }
    }*/

}
