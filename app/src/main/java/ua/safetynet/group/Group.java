package ua.safetynet.group;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Group {

    private String Group_name;
    private String Group_ID;
    private Bitmap groupImage;
    private BigDecimal Funds;
    private BigDecimal Withdrawal_Limit;

    private ArrayList<String> Withdrawals = new ArrayList<>();
    private ArrayList<String> Admins = new ArrayList<>();
    private ArrayList<String> Users = new ArrayList<>();

    public Group() {
        this.Group_name = null;
        this.Group_ID = null;
        this.Funds = new BigDecimal(0);
        this.Withdrawal_Limit = new BigDecimal(50);

        this.Withdrawals.clear();
        this.Admins.clear();
        this.Users.clear();

        //fetchGroupImage();
    }

    public Group(String group_name,String group_id, BigDecimal funds, BigDecimal withdrawal_Limit, ArrayList<String> withdrawals, ArrayList<String> admins, ArrayList<String> users) {
        this.Group_name = group_name;
        this.Group_ID = group_id;
        this.Funds = funds;
        this.Withdrawal_Limit = withdrawal_Limit;
        this.Withdrawals = withdrawals;
        this.Admins = admins;
        this.Users = users;

        fetchGroupImage();
    }

    public Group(String group_name,String group_id, Bitmap groupImage, BigDecimal funds, BigDecimal withdrawal_Limit, ArrayList<String> withdrawals, ArrayList<String> admins, ArrayList<String> users) {
        this.Group_name = group_name;
        this.Group_ID = group_id;
        this.Funds = funds;
        this.Withdrawal_Limit = withdrawal_Limit;
        this.Withdrawals = withdrawals;
        this.Admins = admins;
        this.Users = users;
        this.groupImage = groupImage;
        fetchGroupImage();
    }
    public String getGroup_name() {
        return this.Group_name;
    }

    public String getGroup_ID() {
        return Group_ID;
    }

    public BigDecimal getFunds() {
        return this.Funds;
    }

    public BigDecimal getWithdrawal_Limit() {
        return this.Withdrawal_Limit;
    }

    public ArrayList<String> getWithdrawals() {
        return this.Withdrawals;
    }

    public ArrayList<String> getAdmins() {
        return this.Admins;
    }

    public ArrayList<String> getUsers() {
        return this.Users;
    }

    public void setGroup_name(String group_name) {
        this.Group_name = group_name;
    }

    public void setGroup_ID(String group_ID) {
        this.Group_ID = group_ID;
    }

    public void setFunds(BigDecimal funds) {
        this.Funds = funds;
    }

    public void setWithdrawal_Limit(BigDecimal withdrawal_Limit) {
        this.Withdrawal_Limit = withdrawal_Limit;
    }

    public void setWithdrawals(ArrayList<String> withdrawals) {
        this.Withdrawals = withdrawals;
    }

    public void setAdmins(ArrayList<String> admins) {
        this.Admins = admins;
    }

    public void setUsers(ArrayList<String> users) {
        this.Users = users;
    }

    public void setGroupImage(Bitmap image) {
        groupImage = image;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference groupImageRef = storageRef.child("groupimages/"+ getGroup_ID() + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
                Log.d("Group", "onSuccess: Image Sucessfully Uploaded");
            }
        });
    }

    public void addAdmins(String item){
        this.Admins.add(item);
    }

    public void addUsers(String item){
        this.Users.add(item);
    }

    public void fetchGroupImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference groupImageRef = storageRef.child("groupimages/"+ getGroup_ID() + ".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;

        groupImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                setGroupImage(BitmapFactory.decodeByteArray(bytes, 0 , bytes.length));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Group","Couldn't fetch group image "+e.toString());
            }
        });
    }

    public Bitmap getGroupImage() {
        if(this.groupImage == null)
            fetchGroupImage();
        return groupImage;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId",this.Group_ID);
        map.put("name", this.Group_name);
        map.put("funds", this.Funds.toString());
        map.put("limit", this.Withdrawal_Limit.toString());
        Map<String, String> transMap = new HashMap<>();
        for(Integer i =0; i < this.Withdrawals.size(); i++ )
        {
            transMap.put("transaction" + i.toString(), this.Withdrawals.get(i));
        }
        map.put("transactions",transMap);

        Map<String, String> memberMap = new HashMap<>();
        for(Integer i =0; i < this.Users.size(); i++ )
        {
            memberMap.put("member" + i.toString(), this.Users.get(i));
        }
        map.put("members",memberMap);

        Map<String, String> adminMap = new HashMap<>();
        for(Integer i =0; i < this.Admins.size(); i++ )
        {
            memberMap.put("admin" + i.toString(), this.Admins.get(i));
        }
        map.put("admins",adminMap);

        return map;
    }

    public static Group fromMap(Map<String, Object> map) {
        Group group = new Group();
        group.setGroup_ID(map.get("groupId").toString());
        group.setGroup_name(map.get("name").toString());
        group.setFunds(new BigDecimal(map.get("funds").toString()));
        group.setWithdrawal_Limit(new BigDecimal(map.get("limit").toString()));

        Map<String, String> transMap = (Map<String, String>)map.get("transactions");
        ArrayList<String> trans = new ArrayList<>();
        for(Integer i = 0; ; i++) {
            if(transMap.containsKey("transaction" + i.toString()))
                trans.add(transMap.get("transaction" + i.toString()));
            else
                break;
        }
        group.setWithdrawals(trans);

        Map<String, String> memberMap = (Map<String, String>)map.get("members");
        ArrayList<String> members = new ArrayList<>();
        for(Integer i = 0; ; i++) {
            if(memberMap.containsKey("member" + i.toString()))
                members.add(memberMap.get("member" + i.toString()));
            else
                break;
        }
        group.setUsers(members);

        Map<String, String> adminMap = (Map<String, String>)map.get("admins");
        ArrayList<String> admins = new ArrayList<>();
        for(Integer i = 0; ; i++) {
            if(adminMap.containsKey("admin" + i.toString()))
                admins.add(adminMap.get("admin" + i.toString()));
            else
                break;
        }
        group.setAdmins(admins);
        return group;
    }

}
