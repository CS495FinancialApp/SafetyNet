package ua.safetynet.user;


import android.support.v4.app.Fragment;

import com.google.firebase.FirebaseApp;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;

import ua.safetynet.BuildConfig;
import ua.safetynet.R;

@RunWith(RobolectricTestRunner.class)
public class MainPageTest {
    private MainPageActivity activity;

    @Before
    public void setUp() {
        FirebaseApp.initializeApp(RuntimeEnvironment.application);
        activity = Robolectric.setupActivity(MainPageActivity.class);
    }

    @Test
    public void shouldNotBeNull(){
        assertNotEquals(activity,null );
        assertNotNull(activity);
    }

    @Test
    public void shouldHaveMainFragment() {
        assertNotNull( activity.getSupportFragmentManager());
        Fragment fragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        assertNotNull(fragment );
        assertTrue(fragment instanceof MainViewFragment);
        assertTrue(fragment.isVisible());
    }

    @Test
    public void openNavDrawer() {
        Shadows.shadowOf(activity).clickMenuItem(R.id.nav_menu_button_icon);
        assertTrue(activity.getSupportActionBar().isShowing());
    }
}
