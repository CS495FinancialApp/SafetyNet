package ua.safetynet.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ua.safetynet.user.MainPageActivity;

public class SplashScreenActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainPageActivity.class);
        startActivity(intent);
        finish();
    }
}
