package ua.safetynet.user;

import android.content.Context;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowLooper;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
@RunWith(RobolectricTestRunner.class)
public class DatabaseTest {
    Database db;
    User dbUser;
    ArrayList<String> ids;

    @Before
    public void dbSetup(){
        FirebaseFirestoreSettings.Builder settings = new FirebaseFirestoreSettings.Builder();
        settings.setPersistenceEnabled(true);
        FirebaseApp.initializeApp(RuntimeEnvironment.application);
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        fs.setFirestoreSettings(settings.build());
        db = new Database();
        ids =new ArrayList<>();
    }

    @Test
    public void testDbUser() {
        CountDownLatch latch = new CountDownLatch(1);
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
                latch.countDown();
            }
        );
        try{Thread.sleep(500);latch.await(2,TimeUnit.SECONDS );}
        catch (InterruptedException e) {e.printStackTrace();}
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
    @Test
    public void testQueryGroups() {
        //Make a new user and add him to the db
        ids.add("test5");
        ids.add("test6");
        BigDecimal bigDecimal = new BigDecimal(5);
        final Group group = new Group("Particles Group",
                "tart",
                bigDecimal,
                bigDecimal,
                ids,
                ids,
                ids);
        db.setGroup(group); //Add new user to db

        final Group group2 = new Group("Danglers Group",
                "dang",
                bigDecimal,
                bigDecimal,
                ids,
                ids,
                ids);
        db.setGroup(group2); //Add new group to db

        //Get user from db

        db.queryGroups("test5", (groupDb -> {
                    Log.d("TESTING","DB userId is: " + groupDb.get(0).getName());
                    Assert.assertEquals(group.getGroupId(), groupDb.get(0).getGroupId());
                    Assert.assertEquals(group2.getGroupId(), groupDb.get(1).getGroupId());
                    Assert.assertEquals(group.getName(), groupDb.get(0).getName());
                    Assert.assertEquals(group2.getName(), groupDb.get(1).getName());
                    Assert.assertEquals(group.getFunds(), groupDb.get(0).getFunds());
                    Assert.assertEquals(group2.getFunds(), groupDb.get(1).getFunds());
                    Assert.assertEquals(group.getWithdrawalLimit(),groupDb.get(0).getWithdrawalLimit() );
                    Assert.assertEquals(group2.getWithdrawalLimit(),groupDb.get(1).getWithdrawalLimit() );
                    Assert.assertEquals(group.getAdmins(), groupDb.get(0).getAdmins());
                    Assert.assertEquals(group2.getAdmins(), groupDb.get(1).getAdmins());
                    Assert.assertEquals(group.getUsers(), groupDb.get(0).getUsers());
                    Assert.assertEquals(group2.getUsers(), groupDb.get(1).getUsers());
                    Assert.assertEquals(group.getWithdrawals(),groupDb.get(0).getWithdrawals());
                    Assert.assertEquals(group2.getWithdrawals(),groupDb.get(1).getWithdrawals());
                })
        );

    }
    @Test
    public void testQueryUserTransactions() {
        //Make a new user and add him to the db
        ids.add("test7");
        ids.add("test8");
        BigDecimal bigDecimal = new BigDecimal(5);
        Timestamp timestamp = Timestamp.now();
        ua.safetynet.payment.Transaction trans;
        trans = new ua.safetynet.payment.Transaction();
        trans.setTransId("transtest");
        trans.setUserId("user1");
        trans.setGroupId("group1");
        trans.setFunds(bigDecimal);
        trans.setTimestamp(timestamp);
        trans.setRepayTimestamp(timestamp);
        db.addTransaction(trans); //Add new user to db

        db.queryUserTransactions("user1", (transDb -> {
                    Log.d("TESTING","DB userId is: " + transDb.get(0).getTransId());
                    Assert.assertEquals(trans.getTransId(), transDb.get(0).getTransId());
                    Assert.assertEquals(trans.getUserId(), transDb.get(0).getUserId());
                    Assert.assertEquals(trans.getGroupId(), transDb.get(0).getGroupId());
                    Assert.assertEquals(trans.getFunds(), transDb.get(0).getFunds());
                    Assert.assertEquals(trans.getTimestamp(), transDb.get(0).getTimestamp());
                    Assert.assertEquals(trans.getRepayTimestamp(), transDb.get(0).getRepayTimestamp());

                })
        );

    }
    @Test
    public void testQueryGroupTransactions() {
        //Make a new user and add him to the db
        ids.add("test9");
        ids.add("test10");
        BigDecimal bigDecimal = new BigDecimal(5);
        Timestamp timestamp = null;
        ua.safetynet.payment.Transaction trans;
        trans = new ua.safetynet.payment.Transaction();
        trans.setTransId("transtest");
        trans.setUserId("user1");
        trans.setGroupId("group1");
        trans.setFunds(bigDecimal);
        trans.setTimestamp(timestamp);
        trans.setRepayTimestamp(timestamp);
        db.addTransaction(trans); //Add new user to db

        db.queryGroupTransactions("group1", (transDb -> {
                    Log.d("TESTING","DB userId is: " + transDb.get(0).getTransId());
                    Assert.assertEquals(trans.getTransId(), transDb.get(0).getTransId());
                    Assert.assertEquals(trans.getUserId(), transDb.get(0).getUserId());
                    Assert.assertEquals(trans.getGroupId(), transDb.get(0).getGroupId());
                    Assert.assertEquals(trans.getFunds(), transDb.get(0).getFunds());
                    Assert.assertEquals(trans.getTimestamp(), transDb.get(0).getTimestamp());
                    Assert.assertEquals(trans.getRepayTimestamp(), transDb.get(0).getRepayTimestamp());

                })
        );

    }
    @Test
    public void testQueryUserGroupTransactions() {
        //Make a new user and add him to the db
        ids.add("test11");
        ids.add("test12");
        BigDecimal bigDecimal = new BigDecimal(5);
        Timestamp timestamp = Timestamp.now();
        ua.safetynet.payment.Transaction trans;
        trans = new ua.safetynet.payment.Transaction();
        trans.setTransId("transtest");
        trans.setUserId("user1");
        trans.setGroupId("group1");
        trans.setFunds(bigDecimal);
        trans.setTimestamp(timestamp);
        trans.setRepayTimestamp(timestamp);
        db.addTransaction(trans); //Add new user to db

        db.queryUserGroupTransactions("user1","group1",(transDb -> {
                    Log.d("TESTING","DB userId is: " + transDb.get(0).getTransId());
                    Assert.assertEquals(trans.getTransId(), transDb.get(0).getTransId());
                    Assert.assertEquals(trans.getUserId(), transDb.get(0).getUserId());
                    Assert.assertEquals(trans.getGroupId(), transDb.get(0).getGroupId());
                    Assert.assertEquals(trans.getFunds(), transDb.get(0).getFunds());
                    Assert.assertEquals(trans.getTimestamp(), transDb.get(0).getTimestamp());
                    Assert.assertEquals(trans.getRepayTimestamp(), transDb.get(0).getRepayTimestamp());

                }));
    }
}
