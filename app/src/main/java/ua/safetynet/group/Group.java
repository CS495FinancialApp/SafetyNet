package ua.safetynet.group;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Group implements Parcelable {

    private String name;
    private String groupId;
    private int repaymentTime;
    private Uri imageUri;
    private BigDecimal funds;
    private BigDecimal withdrawalLimit;
    //private int withdrawalDate;

    //Three lists of IDs for linking data
    private ArrayList<String> withdrawals = new ArrayList<>();
    private ArrayList<String> admins = new ArrayList<>();
    private ArrayList<String> users = new ArrayList<>();

    public Group() {
        this.name = null;
        this.groupId = null;
        this.repaymentTime = 10;
        this.funds = new BigDecimal(0);
        this.withdrawalLimit = new BigDecimal(50);
        this.withdrawals.clear();
        this.admins.clear();
        this.users.clear();
    }

    public Group(String group_name,String group_id, BigDecimal funds, BigDecimal withdrawal_Limit, ArrayList<String> withdrawals, ArrayList<String> admins, ArrayList<String> users) {
        this.name = group_name;
        this.groupId = group_id;
        this.funds = funds;
        this.withdrawalLimit = withdrawal_Limit;
        this.withdrawals = withdrawals;
        this.admins = admins;
        this.users = users;
    }

    public Group(String group_name,String group_id, Bitmap groupImage, BigDecimal funds, BigDecimal withdrawal_Limit, ArrayList<String> withdrawals, ArrayList<String> admins, ArrayList<String> users) {
        this.name = group_name;
        this.groupId = group_id;
        this.funds = funds;
        this.withdrawalLimit = withdrawal_Limit;
        this.withdrawals = withdrawals;
        this.admins = admins;
        this.users = users;
        this.setImage(groupImage);

    }

    public String getName() {
        return this.name;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public int getRepaymentTime() {
        return this.repaymentTime;
    }

    public BigDecimal getFunds() {
        return this.funds;
    }

    public BigDecimal getWithdrawalLimit() {
        return this.withdrawalLimit;
    }

    public ArrayList<String> getWithdrawals() {
        return this.withdrawals;
    }

    public ArrayList<String> getAdmins() {
        return this.admins;
    }

    public ArrayList<String> getUsers() {
        return this.users;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setRepaymentTime(int repaymentTime) {
        this.repaymentTime = repaymentTime;
    }

    public void setFunds(BigDecimal funds) {
        this.funds = funds;
    }

    public void setWithdrawalLimit(BigDecimal withdrawalLimit) {
        this.withdrawalLimit = withdrawalLimit;
    }

    public void setWithdrawals(ArrayList<String> withdrawals) {
        this.withdrawals = withdrawals;
    }

    public void setAdmins(ArrayList<String> admins) {
        this.admins = admins;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public void setImage(Bitmap image) {
        if(image != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            final StorageReference groupImageRef = storageRef.child("groupimages/" + getGroupId() + ".jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 99, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = groupImageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Group", "Couldnt upload picture");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    groupImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            setImageUri(uri);
                        }
                    });
                    Log.d("Group", "onSuccess: Image Sucessfully Uploaded");
                }
            });
        }
    }
    public void setImage(Uri uri) {
        setImageUri(uri);
    }
    private void setImageUri(Uri uri) {
        this.imageUri = uri;
    }

    public void addAdmins(String item){
        this.admins.add(item);
    }

    public void removeAdmins(String item){
        this.admins.remove(item);
    }

    public void addUsers(String item){
        this.users.add(item);
    }

    public void removeUsers(String item){
        this.users.remove(item);
    }

    public void addWithdrawal(String item){
        this.withdrawals.add(item);
    }

    public Uri getImage() {
       return this.imageUri;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId",this.groupId);
        map.put("name", this.name);
        map.put("repaymentTime", Integer.toString(this.repaymentTime));
        map.put("funds", this.funds.toString());
        map.put("limit", this.withdrawalLimit.toString());
        map.put("imageuri",this.imageUri);
        map.put("users", this.users);
        map.put("admins", this.admins);
        map.put("withdrawals", this.withdrawals);
        return map;
    }

    /** Code removed from toMap()
     Map<String, String> transMap = new HashMap<>();
     for(Integer i = 0; i < this.withdrawals.size(); i++ )
     {
     transMap.put("transaction" + i.toString(), this.withdrawals.get(i));
     }
     map.put("transactions",transMap);


     Map<String, String> memberMap = new HashMap<>();
     for(Integer i = 0; i < this.users.size(); i++ )
     {
     memberMap.put("member" + i.toString(), this.users.get(i));
     }
     map.put("users",memberMap);

     Map<String, String> adminMap = new HashMap<>();
     for(Integer i = 0; i < this.admins.size(); i++ )
     {
     adminMap.put("admin" + i.toString(), this.admins.get(i));
     }
     map.put("admins",adminMap);
     **/

    @SuppressWarnings("unchecked")
    public static Group fromMap(Map<String, Object> map) {
        Group group = new Group();
        if(map.get("groupId") != null) group.setGroupId((String)map.get("groupId"));
        if(map.get("name") != null) group.setName((String)map.get("name"));
        if(map.get("repaymentTime") != null) group.setRepaymentTime(Integer.valueOf((String)map.get("repaymentTime")));
        if(map.get("funds") != null) group.setFunds(new BigDecimal(map.get("funds").toString()));
        if(map.get("limit") != null) group.setWithdrawalLimit(new BigDecimal(map.get("limit").toString()));
        if(map.get("imageuri") != null) group.setImage(Uri.parse(map.get("imageuri").toString()));
        if(map.get("admins") != null) group.setAdmins((ArrayList<String>) map.get("admins"));
        if(map.get("users") != null) group.setUsers((ArrayList<String>) map.get("users"));
        if(map.get("withdrawals") != null) group.setWithdrawals((ArrayList<String>) map.get("withdrawals"));
        return group;
    }


    @Override
    public boolean equals(Object o){
        if(o == this)
            return true;
        if(!(o instanceof Group))
            return false;
        Group grp = (Group)o;
        return groupId.equals(grp.getGroupId());
    }

        /*
        @SuppressWarnings("unchecked")
        Map<String, String> transMap = (Map<String, String>) map.get("transactions");
        ArrayList<String> trans = new ArrayList<>();
        for (Integer i = 0; ; i++) {
            if (transMap.containsKey("transaction" + i.toString()))
                trans.add(transMap.get("transaction" + i.toString()));
            else
                break;
        }
        group.setWithdrawals(trans);
        @SuppressWarnings("unchecked")
        Map<String, String> memberMap = (Map<String, String>) map.get("members");
        ArrayList<String> members = new ArrayList<>();
        for (Integer i = 0; ; i++) {
            if (memberMap.containsKey("member" + i.toString()))
                members.add(memberMap.get("member" + i.toString()));
            else
                break;
        }
        group.setUsers(members);
        @SuppressWarnings("unchecked")
        Map<String, String> adminMap = (Map<String, String>) map.get("admins");
        ArrayList<String> admins = new ArrayList<>();
        for (Integer i = 0; ; i++) {
            if (adminMap.containsKey("admin" + i.toString()))
                admins.add(adminMap.get("admin" + i.toString()));
            else
                break;
        }
        group.setAdmins(admins);
        */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.groupId);
        dest.writeInt(this.repaymentTime);
        dest.writeParcelable(this.imageUri, flags);
        dest.writeSerializable(this.funds);
        dest.writeSerializable(this.withdrawalLimit);
        dest.writeStringList(this.withdrawals);
        dest.writeStringList(this.admins);
        dest.writeStringList(this.users);
    }

    protected Group(Parcel in) {
        this.name = in.readString();
        this.groupId = in.readString();
        this.repaymentTime = in.readInt();
        this.imageUri = in.readParcelable(Uri.class.getClassLoader());
        this.funds = (BigDecimal) in.readSerializable();
        this.withdrawalLimit = (BigDecimal) in.readSerializable();
        this.withdrawals = in.createStringArrayList();
        this.admins = in.createStringArrayList();
        this.users = in.createStringArrayList();
    }

    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
}
