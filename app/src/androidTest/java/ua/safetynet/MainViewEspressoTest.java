package ua.safetynet;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ua.safetynet.user.MainPageActivity;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainViewEspressoTest {

    @Rule
    public ActivityTestRule<MainPageActivity> mActivityRule =
        new ActivityTestRule<>(MainPageActivity.class);

    @Before
    public void logout() {
        FirebaseAuth.getInstance().signOut();
    }
    @Test
    public void loginViewShown() {
        onView(withText("Sign In with Email")).perform(click());
        //onView(isDisplayed()).perform(typeText());
    }
}
