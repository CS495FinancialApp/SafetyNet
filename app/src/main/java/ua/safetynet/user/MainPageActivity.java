package ua.safetynet.user;


import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ua.safetynet.R;
import ua.safetynet.auth.FirebaseAuthActivity;
import ua.safetynet.group.CreateGroupFragment;

public class MainPageActivity extends AppCompatActivity implements CreateGroupFragment.OnFragmentInteractionListener
{

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle toggle;
    @Override
    protected void onStart()
    {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        if (firebaseUser == null) //If the user isnt already logged in
        {
            //User isnt signed in, launch sign in activity
            startActivity(new Intent(this, FirebaseAuthActivity.class));
        }

        updateUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Show main page, just show user info for temp
        setContentView(R.layout.activity_main_page);

        //Set mDrawerLayout
        mDrawerLayout = findViewById(R.id.main_drawer_layout);
        //Sets toolbar to Current action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Set Action bar button
        ActionBar actionbar = getSupportActionBar();
        actionbar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24px);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle("SafetyNet");
        updateUI();
        setLogoutListener(); //Set listener for logout button

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId())
                        {
                            case R.id.nav_groups:
                                CreateGroupFragment groupFragment = new CreateGroupFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.main_drawer_layout, groupFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                        }
                        return false;
                    }
                }
        );


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri)
    {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    /**
     * Sets listener for logout button. Upon press signs user out using Firebase AuthUI
     * and restarts main activity, thus prompting them to login again.
     */
    private void setLogoutListener()
    {
        setContentView(R.layout.activity_main_page);
        Button logoutBtn = findViewById(R.id.logoutButton);
        logoutBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                    AuthUI.getInstance()
                            .signOut(getApplicationContext())
                            .addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            recreate();
                        }
                    });
            }
        });
    }

    private void updateUI()
    {
        TextView username =  findViewById(R.id.username);
        TextView useremail = findViewById(R.id.useremail);
        if(firebaseUser != null)
        {
            username.setText(firebaseUser.getDisplayName());
            useremail.setText(firebaseUser.getEmail());
        }

    }
}
