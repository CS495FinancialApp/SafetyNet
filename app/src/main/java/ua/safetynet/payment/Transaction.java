package ua.safetynet;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Transaction {

    private String transId;
    private String userId;
    private String groupId;
    private BigDecimal funds;
    private String timestamp;

    public Transaction() {
        this.transId = null;
        this.userId = null;
        this.groupId = null;
        this.funds = new BigDecimal(0);
        this.timestamp = null;
    }

    public String getTransId() {
        return this.transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public BigDecimal getFunds() {
        return this.funds;
    }

    public void setFunds(BigDecimal funds) {
        this.funds = funds;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    //The payments activity currently outputs userid instead of userId, so all references in to and from map are done as such
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("transId", this.transId);
        map.put("userid", this.userId);
        map.put("groupId",this.groupId);
        map.put("funds", this.funds.toString());
        map.put("timestamp", this.timestamp);
        return map;
    }

    //The payments activity currently outputs userid instead of userId, so all references in to and from map are done as such
    public static Transaction fromMap(Map<String, Object> map) {
        Transaction transaction = new Transaction();
        transaction.setTransId(map.get("transId").toString());
        transaction.setUserId(map.get("userid").toString());
        transaction.setGroupId(map.get("groupId").toString());
        transaction.setFunds(new BigDecimal(map.get("funds").toString()));
        transaction.setTimestamp(map.get("timestamp").toString());
        return transaction;
    }
}
