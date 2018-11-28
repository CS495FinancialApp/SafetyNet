package ua.safetynet.payment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;


public class Transaction implements Parcelable {

    private String transId;
    private String userId;
    private String groupId;
    private BigDecimal funds;
    //private String timestamp;
    private Timestamp timestamp;
    private Timestamp repayTimestamp;

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

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Timestamp getRepayTimestamp() {
        return this.repayTimestamp;
    }

    public void setRepayTimestamp(Timestamp timestamp) {
        this.repayTimestamp = timestamp;
    }
    //The payments activity currently outputs userid instead of userId, so all references in to and from map are done as such
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("transId", this.transId);
        map.put("userId", this.userId);
        map.put("groupId",this.groupId);
        map.put("amount", this.funds.toString());
        map.put("timestamp", this.timestamp);
        map.put("repayTimestamp", this.repayTimestamp);
        return map;
    }

    public static Transaction fromMap(Map<String, Object> map) {
        Transaction transaction = new Transaction();
        if(map.get("transId") != null) transaction.setTransId(map.get("transId").toString());
        if(map.get("userId") != null) transaction.setUserId(map.get("userId").toString());
        if(map.get("groupId") != null)transaction.setGroupId(map.get("groupId").toString());
        if(map.get("amount") != null) transaction.setFunds(new BigDecimal(map.get("amount").toString()));
        if(map.get("timestamp") != null) transaction.setTimestamp((Timestamp)map.get("timestamp"));
        if(map.get("repayTimestamp") != null) transaction.setRepayTimestamp((Timestamp)map.get("repayTimestamp"));
        return transaction;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.transId);
        dest.writeString(this.userId);
        dest.writeString(this.groupId);
        dest.writeSerializable(this.funds);
        dest.writeParcelable(this.timestamp, flags);
        dest.writeParcelable(this.repayTimestamp, flags);
    }

    protected Transaction(Parcel in) {
        this.transId = in.readString();
        this.userId = in.readString();
        this.groupId = in.readString();
        this.funds = (BigDecimal) in.readSerializable();
        this.timestamp = in.readParcelable(Timestamp.class.getClassLoader());
        this.repayTimestamp = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel source) {
            return new Transaction(source);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
}

