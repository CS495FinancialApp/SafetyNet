package ua.safetynet.user;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User
{
    private String userId;
    private String email;
    private String name;
    private Bitmap userImage;
    private ArrayList<String> transactions = new ArrayList<>();
    private ArrayList<String> groups = new ArrayList<>();

    public User()
    {
        userId = null;
        email = null;
        name = null;
        transactions.clear();
        groups.clear();
    }
    
    public User(String userId, String email, String name, ArrayList<String> transactions, ArrayList<String> groups) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.transactions = transactions;
        this.groups = groups;
    }
    public User(String userId, String email, String name, Bitmap userImage, ArrayList<String> transactions, ArrayList<String> groups) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.userImage = userImage;
        this.transactions = transactions;
        this.groups = groups;
    }

    public String getUserId()
    {
        return this.userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getEmail()
    {
        return this.email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public ArrayList<String> getTransactions()
    {
        return this.transactions;
    }

    public ArrayList<String> getGroups()
    {
        return this.groups;
    }

    public void setTransactions(ArrayList<String> transactions) {
        this.transactions = transactions;
    }

    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }
    public void setImage(Bitmap image) {
        this.storeImageLocal(image);
        this.storeImageFirebase(image);
    }
    private void storeImageLocal(Bitmap image) {
        this.userImage = image;
    }
    private void storeImageFirebase(Bitmap image) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference userImageRef = storageRef.child("userimages/"+ this.getUserId() + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = userImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("User", "Couldnt upload picture");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("User", "onSuccess: Image Sucessfully Uploaded");
            }
        });
    }
    public void fetchImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference userImageRef = storageRef.child("userimages/"+ this.getUserId() + ".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;

        userImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                storeImageLocal(BitmapFactory.decodeByteArray(bytes, 0 , bytes.length));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Group","Couldn't fetch group image "+e.toString());
            }
        });
    }

    public Bitmap getImage() {
        if(this.userImage == null)
            this.fetchImage();
        return this.userImage;
    }

    /***
     * Returns a map of the current user object
     * @return Map of the current user object
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId",this.userId);
        map.put("name", this.name);
        map.put("email", this.email);
        Map<String, String> transMap = new HashMap<>();
        for(Integer i =0; i < this.transactions.size(); i++ )
        {
            transMap.put("transaction" + i.toString(), this.transactions.get(i));
        }
        map.put("transactions",transMap);

        Map<String, String> groupMap = new HashMap<>();
        for(Integer i =0; i < this.transactions.size(); i++ )
        {
            groupMap.put("group" + i.toString(), this.groups.get(i));
        }
        map.put("groups",groupMap);

        return map;
    }

    public static User fromMap(Map<String, Object> map) {
        User user = new User();
        user.setUserId(map.get("userId").toString());
        user.setName(map.get("name").toString());
        user.setEmail(map.get("email").toString());

        @SuppressWarnings("unchecked")
        Map<String, String> transMap = (Map<String, String>)map.get("transactions");
        ArrayList<String> trans = new ArrayList<>();
        for(Integer i = 0; ; i++) {
            if(transMap.containsKey("transaction" + i.toString()))
                trans.add(transMap.get("transaction" + i.toString()));
            else
                break;
        }
        user.setTransactions(trans);

        @SuppressWarnings("unchecked")
        Map<String, String> groupsMap = (Map<String, String>)map.get("groups");
        ArrayList<String> groups = new ArrayList<>();
        for(Integer i = 0; ; i++) {
            if(groupsMap.containsKey("group" + i.toString()))
                groups.add(groupsMap.get("group" + i.toString()));
            else
                break;
        }
        user.setGroups(groups);
        return user;
    }
}
