package ua.safetynet.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;


import java.util.Arrays;

import java.util.List;



import ua.safetynet.R;
import ua.safetynet.user.MainPageActivity;


public class FirebaseAuthActivity extends AppCompatActivity
{

    private static final int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }


    // [START auth_fui_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            // Successfully signed in
            Intent in = new Intent(this, MainPageActivity.class);
            in.putExtra("EXTRA_IDP_RESPONSE", IdpResponse.fromResultIntent(data));
            startActivity(in);
            finish();
            return;
        }
        if(resultCode == RESULT_CANCELED)
        {
            Toast.makeText(getApplicationContext(),R.string.login_user_cancelled,Toast.LENGTH_LONG).show();
            return;
        }

    }
}


