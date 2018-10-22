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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ua.safetynet.R;
import ua.safetynet.auth.FirebaseAuthActivity;
import ua.safetynet.group.CreateGroupFragment;
import ua.safetynet.payment.PaymentFragment;

public class MainPageActivity extends AppCompatActivity implements CreateGroupFragment.OnFragmentInteractionListener, MainViewFragment.OnFragmentInteractionListener, PaymentFragment.OnFragmentInteractionListener
{

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

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

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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
    private void setupNavigationDrawer()
    {
        NavigationView navigationView = findViewById(R.id.nav_view);
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
                            case R.id.nav_logout:
                                FirebaseAuth.getInstance().signOut();
                                break;
                        }
                        if(fragment != null)
                        {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        }
                        return false;
                    }
                }
        );
    }
    private void setupDrawerToggle()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
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

    }

}
