package ua.safetynet.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

/***
 * @author Jeremy McCormick
 * Activity to handle login process for app. Splash Screen activity or onboarding activity should transfer here next
 */

public class FirebaseAuthActivity extends AppCompatActivity implements EditUserFragment.OnFragmentInteractionListener
{
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private static final int RCSIGNIN = 123;
    private static final String TAG = "AUTH ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_auth);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * onStart start AuthUI activity if not already logged in
     */
    @Override
    public void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) //If the user isnt already logged in
        {
            //User isnt signed in, launch sign in activity
            Log.d(TAG, "User isnt signed in, starting sign in activity");
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(Arrays.asList(
                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                            new AuthUI.IdpConfig.EmailBuilder().build()))
                    .setIsSmartLockEnabled(true)
                    .build(),RCSIGNIN);
        }
        else {
            Log.d(TAG, "Starting main page activity");
            startActivity(new Intent(this, MainPageActivity.class));
            finish();
        }
    }

    /***
     * Handle result from AuthUI activity
     * @param requestCode request Code
     * @param resultCode resulting code
     * @param data data returned
     */
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
                    //start new user
                    setupNewUser();
                    Log.d(TAG, "New user created");
                }
                else {
                    Log.d(TAG, "Starting main page activity");
                    startActivity(new Intent(this, MainPageActivity.class));
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

    /**
     * Start edit user prefilled with firebase info, so user can put in new info
     */
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

    /**
     * Check if first launch of the app
     * @param context context
     * @return if it is the first launch
     */
    public static boolean isFirstLaunch(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("firstlaunch", true);
    }

    /**
     * Set that the app is no longer launching for first time
     * @param context context
     */
    public static void setFirstLaunch(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstlaunch", false);
        editor.apply();
    }
}


