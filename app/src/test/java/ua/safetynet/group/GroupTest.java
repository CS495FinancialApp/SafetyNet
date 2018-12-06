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
    private Group group;
    ArrayList<String> trans;
    ArrayList<String> users;
    ArrayList<String> admins;
    @Before
    public void makeUser() {
        group = new Group();
        group.setGroupId("123456789");
        group.setName("John Doe");
        group.setWithdrawalLimit(new BigDecimal(20));
        group.setFunds(new BigDecimal(150));
        group.setRepaymentTime(10);
        trans = new ArrayList<String>();
        trans.add("1");
        trans.add("2");
        trans.add("3");
        group.setWithdrawals(trans);
        users = new ArrayList<String>();
        users.add("4");
        users.add("5");
        users.add("6");
        group.setUsers(users);
        admins = new ArrayList<String>();
        admins.add("7");
        admins.add("8");
        admins.add("9");
        group.setAdmins(admins);
    }
    @Test
    public void testGetSet() {
        Assert.assertEquals(group.getGroupId(),"123456789");
        Assert.assertEquals(group.getName(),"John Doe");
        Assert.assertEquals(group.getWithdrawalLimit(),new BigDecimal(20));
        Assert.assertEquals(group.getFunds(),new BigDecimal(150));
        Assert.assertEquals(group.getRepaymentTime(),10);
        Assert.assertEquals(group.getWithdrawals(),trans);
        Assert.assertEquals(group.getUsers(),users);
        Assert.assertEquals(group.getAdmins(),admins);
    }
    @Test
    public void testToFromMap() {
        Group mapGroup= Group.fromMap(group.toMap());
        Assert.assertEquals(group.getGroupId(), mapGroup.getGroupId());
        Assert.assertEquals(group.getName(), mapGroup.getName());
        Assert.assertEquals(group.getFunds(), mapGroup.getFunds());
        Assert.assertEquals(group.getRepaymentTime(), mapGroup.getRepaymentTime());
        Assert.assertEquals(group.getWithdrawalLimit(), mapGroup.getWithdrawalLimit());
        Assert.assertEquals(group.getWithdrawals(), mapGroup.getWithdrawals());
        Assert.assertEquals(group.getUsers(), mapGroup.getUsers());
        Assert.assertEquals(group.getAdmins(), mapGroup.getAdmins());
    }
    @Test
    public void testToFromMapNull() {
        group = new Group();
        group.setGroupId(null);
        group.setName(null);
        group.setWithdrawalLimit(new BigDecimal(0));
        group.setFunds(new BigDecimal(0));
        group.setRepaymentTime(0);
        trans = new ArrayList<String>();
        group.setWithdrawals(trans);
        users = new ArrayList<String>();
        group.setUsers(users);
        admins = new ArrayList<String>();
        group.setAdmins(admins);
        Group mapGroup= Group.fromMap(group.toMap());
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


