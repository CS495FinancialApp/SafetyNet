package ua.safetynet.group;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

@RunWith(JUnit4.class)
public class GroupTest {
    Group group;
    @Before
    public void makeUser() {
        group = new Group();
        group.setGroupId("123456789");
        group.setName("John Doe");
        group.setWithdrawalLimit(new BigDecimal(20));
        group.setFunds(new BigDecimal(150));
        group.setRepaymentTime(10);
        ArrayList<String> trans = new ArrayList<String>();
        trans.add("1");
        trans.add("2");
        trans.add("3");
        group.setWithdrawals(trans);
        ArrayList<String> users = new ArrayList<String>();
        users.add("4");
        users.add("5");
        users.add("6");
        group.setUsers(users);
        ArrayList<String> admins = new ArrayList<String>();
        admins.add("7");
        admins.add("8");
        admins.add("9");
        group.setUsers(admins);
    }
    @Test
    public void testToFromMap() {
        Map<String, Object> map = group.toMap();
        Group mapGroup= Group.fromMap(map);
        Assert.assertEquals(group.getGroupId(), mapGroup.getGroupId());
        Assert.assertEquals(group.getName(), mapGroup.getName());
        Assert.assertEquals(group.getFunds(), mapGroup.getFunds());
        Assert.assertEquals(group.getRepaymentTime(), mapGroup.getRepaymentTime());
        Assert.assertEquals(group.getWithdrawalLimit(), mapGroup.getWithdrawalLimit());
        Assert.assertEquals(group.getWithdrawals(), mapGroup.getWithdrawals());
        Assert.assertEquals(group.getUsers(), mapGroup.getUsers());
        Assert.assertEquals(group.getAdmins(), mapGroup.getAdmins());
    }
}


