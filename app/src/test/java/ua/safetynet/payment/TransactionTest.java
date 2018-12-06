package ua.safetynet.payment;


import com.google.firebase.Timestamp;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.util.Map;

@RunWith(JUnit4.class)
public class TransactionTest {
    Transaction transaction;
    Timestamp now = Timestamp.now();
    Timestamp later = Timestamp.now();
    @Before
    public void makeTransaction() {
        transaction = new Transaction();
        transaction.setUserId("1");
        transaction.setGroupId("2");
        transaction.setTransId("3");
        transaction.setFunds(new BigDecimal(1));
        transaction.setTimestamp(now);
        transaction.setRepayTimestamp(later);
    }

    @Test
    public void testGetSet() {
        Assert.assertEquals(transaction.getUserId(),"1");
        Assert.assertEquals(transaction.getGroupId(),"2");
        Assert.assertEquals(transaction.getTransId(),"3");
        Assert.assertTrue(transaction.getFunds().equals(BigDecimal.ONE));
        Assert.assertTrue(transaction.getTimestamp().equals(now));
        Assert.assertTrue(transaction.getRepayTimestamp().equals(later));
    }

    @Test
    public void testToFromMap() {
        Map<String, Object> map = transaction.toMap();
        Transaction mapTransaction = Transaction.fromMap(map);
        Assert.assertTrue(transaction.equals(mapTransaction));
    }
    @Test
    public void testToFromMapNull() {
        transaction = new Transaction();
        transaction.setUserId(null);
        transaction.setGroupId(null);
        transaction.setTransId(null);
        transaction.setFunds(new BigDecimal(0));
        transaction.setTimestamp(now);
        transaction.setRepayTimestamp(later);
        Map<String, Object> map = transaction.toMap();
        Transaction mapTransaction = Transaction.fromMap(map);
        Assert.assertTrue(transaction.equals(mapTransaction));
    }
}
