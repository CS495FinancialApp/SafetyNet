package ua.safetynet.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;


import java.util.Arrays;

import java.util.List;



import ua.safetynet.R;
import ua.safetynet.user.EditUserFragment;
import ua.safetynet.user.MainPageActivity;


public class FirebaseAuthActivity extends AppCompatActivity implements EditUserFragment.OnFragmentInteractionListener
{
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private static final int RCSIGNIN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        setContentView(R.layout.activity_firebase_auth);
        if (firebaseUser == null) //If the user isnt already logged in
        {
            //User isnt signed in, launch sign in activity
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(Arrays.asList(
                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.PhoneBuilder().build()))
                    .setIsSmartLockEnabled(true)
                    .build(),RCSIGNIN);
        }
    }


    // [START auth_fui_result]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RCSIGNIN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                FirebaseUserMetadata metadata = FirebaseAuth.getInstance().getCurrentUser().getMetadata();
                //Check timestamps to see if a new user
                if(metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
                    setupNewUser();
                }
                else {
                    startActivity(new Intent(this,MainPageActivity.class));
                    finish();
                }
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    startActivity(new Intent(this, SplashScreenActivity.class));
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "Network Required to login", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, SplashScreenActivity.class));
                    return;
                }
                Log.e("Splash Screen Login", "Sign-in error: ", response.getError());
            }
        }
    }
    private void setupNewUser() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.firebase_auth_container, new EditUserFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}


