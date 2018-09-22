package ua.safetynet.payment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.braintreepayments.api.dropin.DropInRequest;

import ua.safetynet.R;

public class PaymentActivity extends AppCompatActivity
{
    String tokenizationKey = "sandbox_pkfyhzmk_hjpn8nvr5356mmkw";
    int REQUEST_CODE;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        pay();
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
