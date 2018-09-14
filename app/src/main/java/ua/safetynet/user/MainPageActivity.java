package ua.safetynet.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ua.safetynet.R;
import ua.safetynet.auth.FirebaseAuthActivity;

public class MainPageActivity extends AppCompatActivity
{

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null)
        {
            //User isnt signed in, launch sign in activity
            startActivity(new Intent(this, FirebaseAuthActivity.class));
        }

            //Show main page, just show user info for temp
            setContentView(R.layout.activity_main_page);
            TextView username =  findViewById(R.id.username);
            TextView useremail = findViewById(R.id.useremail);

            username.setText(firebaseUser.getDisplayName());
            useremail.setText(firebaseUser.getEmail());


    }
}
