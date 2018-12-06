package ua.safetynet.user;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.ui.auth.data.model.PhoneNumber;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ua.safetynet.payment.Transaction;

/**
 * @author Jeremy McCormick
 * User class to hold data related to user and their groups
 */
public class User implements  Parcelable {
    private String userId;
    private String email;
    private String name;
    private String phoneNumber;
    private Uri imageUri;
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
        setImage(userImage);
        this.transactions = transactions;
        this.groups = groups;
    }
    public String getPhoneNumber() {
        return this.phoneNumber;
    }
    public void setPhoneNumber(String phone) {
        this.phoneNumber = phone;
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
    public void addTransaction(String transactionId) {
        this.transactions.add(transactionId);
    }
    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }

    /**
     * Sets the local object image to the new one as well as uploading the new image to Firebase storage
     * @param uri
     */
    public void setImage(Uri uri) {
        setImageUri(uri);
    }

    /**
     * Just updates the local image object
     * @param image
     */

    /**
     * Overwrites the image associated with the userID in Firebase Storage to the new image passed in
     * @param image
     */
    public void setImage(Bitmap image) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference userImageRef = storageRef.child("userimages/"+ this.getUserId() + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 99, baos);
        byte[] data = baos.toByteArray();

        final UploadTask uploadTask = userImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("User", "Couldnt upload picture");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                userImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        setImageUri(uri);
                        Log.d("User", "User image uploaded uri=" + uri.toString());
                    }
                });

            }
        });

    }
    private void setImageUri(Uri uri) {
        this.imageUri = uri;
    }
    public Uri getImage() {
        return this.imageUri;
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
        map.put("phone", this.phoneNumber);
        map.put("transactions",this.transactions);
        map.put("groups", this.groups);
        if(this.imageUri != null) map.put("imageuri", this.imageUri.toString());
        return map;
    }

    /**
     * Creates and returns a user object based on the pass in map.
     * Must follow the keys name and structure in toMap
     * @param map map of user data
     * @return new user object from passed in map
     */
    @SuppressWarnings("unchecked")
    public static User fromMap(Map<String, Object> map) {
        if(map == null)
            return new User();
        User user = new User();
        if(map.get("userId") != null) user.setUserId(map.get("userId").toString());
        if(map.get("name") != null) user.setName(map.get("name").toString());
        if(map.get("email") != null) user.setEmail(map.get("email").toString());
        if(map.get("phone") != null) user.setPhoneNumber(map.get("phone").toString());
        if(map.get("imageuri") != null) user.setImage(Uri.parse(map.get("imageuri").toString()));
        if(map.get("groups") != null) user.setGroups((ArrayList<String>) map.get("groups"));
        if(map.get("transactions") != null) user.setTransactions((ArrayList<String>) map.get("transactions"));
        return user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.email);
        dest.writeString(this.name);
        dest.writeString(this.phoneNumber);
        dest.writeParcelable(this.imageUri, flags);
        dest.writeStringList(this.transactions);
        dest.writeStringList(this.groups);
    }

    protected User(Parcel in) {
        this.userId = in.readString();
        this.email = in.readString();
        this.name = in.readString();
        this.phoneNumber = in.readString();
        this.imageUri = in.readParcelable(Uri.class.getClassLoader());
        this.transactions = in.createStringArrayList();
        this.groups = in.createStringArrayList();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
