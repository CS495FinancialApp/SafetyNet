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

public class Group {

    private String Group_name;
    private String Group_ID;
    private Bitmap groupImage;
    private double Funds;
    private double Withdrawal_Limit;

    private ArrayList<String> Withdrawals = new ArrayList<>();
    private ArrayList<String> Admins = new ArrayList<>();
    private ArrayList<String> Users = new ArrayList<>();

    public Group() {
        this.Group_name = null;
        this.Group_ID = null;
        this.Funds = 0;
        this.Withdrawal_Limit = 50;

        this.Withdrawals.clear();
        this.Admins.clear();
        this.Users.clear();

        fetchGroupImage();
    }

    public Group(String group_name,String group_id, double funds, double withdrawal_Limit, ArrayList<String> withdrawals, ArrayList<String> admins, ArrayList<String> users) {
        this.Group_name = group_name;
        this.Group_ID = group_id;
        this.Funds = funds;
        this.Withdrawal_Limit = withdrawal_Limit;
        this.Withdrawals = withdrawals;
        this.Admins = admins;
        this.Users = users;

        fetchGroupImage();
    }

    public Group(String group_name,String group_id, Bitmap groupImage, double funds, double withdrawal_Limit, ArrayList<String> withdrawals, ArrayList<String> admins, ArrayList<String> users) {
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

    public double getFunds() {
        return this.Funds;
    }

    public double getWithdrawal_Limit() {
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

    public void setFunds(double funds) {
        this.Funds = funds;
    }

    public void setWithdrawal_Limit(double withdrawal_Limit) {
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
        return groupImage;
    }
}
