package ua.safetynet.user;

import android.content.Context;
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.annotation.Nullable;

import ua.safetynet.Database;
import ua.safetynet.group.Group;
import ua.safetynet.user.User;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(JUnit4.class)
public class DatabaseTest {
    @Mock
    Database db;
    @Mock
    User dbUser;
    ArrayList<String> ids;
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void dbSetup(){
        MockitoAnnotations.initMocks(this);
        db = mock(Database.class);
        ids = mock(ArrayList.class);
    }

    @Test
    public void testDbUser() {
        //Make a new user and add him to the db
        ids.add("test1");
        ids.add("test2");
        final User user = new User("test",
                "usertest@example.com",
                "Alan Turing",
                ids,
                ids);
        db.setUser(user); //Add new user to db

        //Get user from db

        db.getUser("1", (dbUser) -> {
                Log.d("TESTING","DB userId is: " + dbUser.getName());
                Assert.assertEquals(user.getUserId(), dbUser.getUserId());
                Assert.assertEquals(user.getName(),dbUser.getName());
                Assert.assertEquals(user.getEmail(),dbUser.getEmail());
                Assert.assertEquals(user.getGroups(),ids);
                Assert.assertEquals(user.getTransactions(), ids);
            }
        );

    }
    @Test
    public void testDbGroup() {
        //Make a new user and add him to the db
        ids.add("test3");
        ids.add("test4");
        BigDecimal bigDecimal = new BigDecimal(5);
        final Group group = new Group("Turing Group",
                "test",
                bigDecimal,
                bigDecimal,
                ids,
                ids,
                ids);
        db.setGroup(group); //Add new user to db

        //Get user from db

        db.getGroup("test", (groupDb -> {
                Log.d("TESTING","DB userId is: " + groupDb.getName());
                Assert.assertEquals(group.getGroupId(), groupDb.getGroupId());
                Assert.assertEquals(group.getName(), groupDb.getName());
                Assert.assertEquals(group.getFunds(), groupDb.getFunds());
                Assert.assertEquals(group.getWithdrawalLimit(),groupDb.getWithdrawalLimit() );
                Assert.assertEquals(group.getAdmins(), groupDb.getAdmins());
                Assert.assertEquals(group.getUsers(), groupDb.getUsers());
                Assert.assertEquals(group.getWithdrawals(),groupDb.getWithdrawals());
            })
        );

    }
}
