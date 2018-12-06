package ua.safetynet.user;

import android.net.Uri;
import android.os.Parcel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

@RunWith(JUnit4.class)
public class UserTest {
    User user;
    ArrayList<String> trans;
    ArrayList<String> groups;
    @Before
    public void makeUser() {
        user = new User();
        user.setUserId("123456789");
        user.setName("John Doe");
        user.setEmail("email@email.test");
        user.setPhoneNumber("5555555555");
        //user.setImage(Uri.parse("https://firebasestorage.googleapis.com/v0/b/safetynet-f2326.appspot.com/o/userimages%2Fnull.jpg?alt=media&token=35787dac-f9d6-4faa-8df3-bb021c4be09c"));
        user.setImage((Uri)null);
        trans = new ArrayList<String>();
        trans.add("1");
        trans.add("2");
        trans.add("3");
        user.setTransactions(trans);
        groups = new ArrayList<String>();
        groups.add("4");
        groups.add("5");
        groups.add("6");
        user.setGroups(groups);
    }
    @Test
    public void testGetSet() {
        Assert.assertEquals(user.getUserId(), "123456789");
        Assert.assertEquals(user.getName(), "John Doe");
        Assert.assertEquals(user.getEmail(), "email@email.test");
        Assert.assertEquals(user.getTransactions(), trans);
        Assert.assertEquals(user.getGroups(), groups);
    }
    @Test
    public void testToFromMap() {
        User mapUser = User.fromMap(user.toMap());
        Assert.assertEquals(user.getUserId(), mapUser.getUserId());
        Assert.assertEquals(user.getName(), mapUser.getName());
        Assert.assertEquals(user.getEmail(), mapUser.getEmail());
        Assert.assertEquals(user.getTransactions(), mapUser.getTransactions());
        Assert.assertEquals(user.getGroups(), mapUser.getGroups());
    }
    @Test
    public void testToFromMapNull() {
        user = new User();
        user.setUserId(null);
        user.setName(null);
        user.setEmail(null);
        user.setPhoneNumber(null);
        //user.setImage(Uri.parse("https://firebasestorage.googleapis.com/v0/b/safetynet-f2326.appspot.com/o/userimages%2Fnull.jpg?alt=media&token=35787dac-f9d6-4faa-8df3-bb021c4be09c"));
        user.setImage((Uri)null);
        trans = new ArrayList<String>();
        user.setTransactions(trans);
        groups = new ArrayList<String>();
        user.setGroups(groups);
        User mapUser = User.fromMap(user.toMap());
        Assert.assertEquals(user.getUserId(), mapUser.getUserId());
        Assert.assertEquals(user.getName(), mapUser.getName());
        Assert.assertEquals(user.getEmail(), mapUser.getEmail());
        Assert.assertEquals(user.getTransactions(), mapUser.getTransactions());
        Assert.assertEquals(user.getGroups(), mapUser.getGroups());
    }
}
