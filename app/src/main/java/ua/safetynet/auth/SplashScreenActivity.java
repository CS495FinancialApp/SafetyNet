package ua.safetynet.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import ua.safetynet.R;
import ua.safetynet.user.MainPageActivity;

/**
 * @author Jeremy McCormick
 * Activity to start off app with just displaying a splash screen.
 * Check first launch and goes to either onboarding or firebase auth
 */
public class SplashScreenActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Check if first launch
        if(FirebaseAuthActivity.isFirstLaunch(getBaseContext())) {
            //Set first launch if it is
            FirebaseAuthActivity.setFirstLaunch(getBaseContext());
            Intent intent = new Intent(this, OnboardingActivity.class);
            startActivityForResult(intent, 1);
        }
        else {
            Intent intent = new Intent(this, FirebaseAuthActivity.class);
            startActivity(intent);
            finish();
        }


    }

    /**
     * Start firbase auth activity when done with onboarding
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Intent intent = new Intent(this, FirebaseAuthActivity.class);
        startActivity(intent);
        finish();
    }
}

