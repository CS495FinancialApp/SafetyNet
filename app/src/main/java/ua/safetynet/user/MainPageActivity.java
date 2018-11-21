package ua.safetynet.user;


import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import ua.safetynet.Database;
import ua.safetynet.R;
import ua.safetynet.auth.FirebaseAuthActivity;
import ua.safetynet.auth.SplashScreenActivity;
import ua.safetynet.group.CreateGroupFragment;
import ua.safetynet.payment.PaymentFragment;
import ua.safetynet.payment.PayoutFragment;

/**
 * Main activity our app opens into
 * Sets up the toolbar and navigation drawer. Menu views are delegated to fragments which are placed into a container
 * in the activities layout
 */
public class MainPageActivity extends AppCompatActivity implements CreateGroupFragment.OnFragmentInteractionListener, MainViewFragment.OnFragmentInteractionListener,
        PaymentFragment.OnPaymentCompleteListener, PayoutFragment.OnFragmentInteractionListener, EditUserFragment.OnFragmentInteractionListener
{

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private User user = null;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private static final int RCSIGNIN = 123;
    /**
     * Checks if the user is signed in or not onStart. Launches sign in flow if they aren't
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.getParcelable("user") != null)
            user = savedInstanceState.getParcelable("user");

        //Show main page, just show user info for temp
        setContentView(R.layout.activity_main_page);

        //Start the main view fragment
        MainViewFragment mainViewFragment = new MainViewFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mainViewFragment).addToBackStack(null).commit();

        //Set mDrawerLayout
        mDrawerLayout = findViewById(R.id.main_drawer_layout);

        setupDrawerToggle();
        setupNavigationDrawer();
    }

    /**
     * Setup navigation drawer onclicklistener. Creates fragment of selected menu and replaces the main menu fragment with it
     * Adds fragment to stack so you can use back button
     */
    private void setupNavigationDrawer()
    {
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        Fragment fragment = null;
                        switch (menuItem.getItemId())
                        {
                            case R.id.nav_home:
                                fragment = new MainViewFragment();
                                break;
                            case R.id.nav_groups:
                                fragment = new CreateGroupFragment();
                                break;
                            case R.id.nav_payment:
                                fragment = new PaymentFragment();
                                break;
                            case R.id.nav_payout:
                                fragment = new PayoutFragment();
                                break;
                            case R.id.nav_edit_user:
                                fragment = new EditUserFragment();
                                break;
                            case R.id.nav_logout:
                                AuthUI.getInstance().signOut(getApplicationContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        startActivity(new Intent(MainPageActivity.this, FirebaseAuthActivity.class));
                                        finish();
                                    }
                                });
                                firebaseAuth.signOut();
                                fragment = null;
                                break;
                        }
                        if(fragment != null)
                        {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        }
                        return false;
                    }
                }
        );
    }

    /**
     * Sets up the app toolbar
     */
    private void setupDrawerToggle()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            getSupportActionBar().setTitle("SafetyNet");
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onPaymentComplete(String transactionId) {

    }
}
