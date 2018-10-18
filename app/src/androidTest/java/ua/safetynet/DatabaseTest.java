package ua.safetynet;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import javax.annotation.Nullable;

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
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);
        db = new Database();
    }

    @Test
    public void testUserSet() {
        //Make a new user and add him to the db
        final User user = new User("1",
                "email@local.com",
                "Alan Turing",
                new ArrayList<String>(),
                new ArrayList<String>());
        db.setUser(user); //Add new user to db

        //Get user from db

        db.getUser("1", new Database.DatabaseUserListener() {
            @Override
            public void onUserRetrieval(User dbUser) {
                Log.d("TESTING","DB userId is: " + dbUser.getName());
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
