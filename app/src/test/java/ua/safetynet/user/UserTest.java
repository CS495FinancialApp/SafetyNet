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

    @Before
    public void makeUser() {
        user = new User();
        user.setUserId("123456789");
        user.setName("John Doe");
        user.setEmail("email@email.test");
        user.setPhoneNumber("5555555555");
        //user.setImage(Uri.parse("https://firebasestorage.googleapis.com/v0/b/safetynet-f2326.appspot.com/o/userimages%2Fnull.jpg?alt=media&token=35787dac-f9d6-4faa-8df3-bb021c4be09c"));
        user.setImage((Uri)null);
        ArrayList<String> trans = new ArrayList<String>();
        trans.add("1");
        trans.add("2");
        trans.add("3");
        user.setTransactions(trans);
        ArrayList<String> groups = new ArrayList<String>();
        groups.add("4");
        groups.add("5");
        groups.add("6");
        user.setGroups(groups);
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
}
