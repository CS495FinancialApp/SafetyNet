package ua.safetynet.user;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Map;

@RunWith(JUnit4.class)
public class UserTest {
    User user;

    @Before
    public void makeUser() {
        user = new User();
        user.setUserId("123456789");
        user.setName("John Doe");
        user.setEmail("email@email.test");
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
        Map<String, Object> map = user.toMap();
        User mapUser = User.fromMap(map);
        Assert.assertEquals(user.getUserId(), mapUser.getUserId());
        Assert.assertEquals(user.getName(), mapUser.getName());
        Assert.assertEquals(user.getEmail(), mapUser.getEmail());
        Assert.assertEquals(user.getTransactions(), mapUser.getTransactions());
        Assert.assertEquals(user.getGroups(), mapUser.getGroups());
    }
}
