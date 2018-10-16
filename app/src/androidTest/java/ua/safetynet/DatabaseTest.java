package ua.safetynet;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.FirebaseApp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import ua.safetynet.user.User;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    Database db;
    User dbUser;
    @Before
    public void dbSetup(){
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        db = new Database();
    }

    @Test
    public void testUserSet() {
        //Make a new user and add him to the db
        User user = new User("1",
                "email@local.com",
                "Alan Turing",
                new ArrayList<String>(),
                new ArrayList<String>());
        db.setUser(user); //Add new user to db

        //Get user from db

        db.getUser("1", new Database.DatabaseUserListener() {
            @Override
            public void onUserRetrieval(User user) {
                Assert.assertEquals(user.getUserId(), dbUser.getUserId());
                Assert.assertEquals(user.getName(),dbUser.getName());
                Assert.assertEquals(user.getEmail(),dbUser.getEmail());
            }
        });


    }

    public void setDbUser( User user) {
        dbUser = user;
    }
}
