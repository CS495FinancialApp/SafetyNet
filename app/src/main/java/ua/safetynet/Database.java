package ua.safetynet;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Context;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import ua.safetynet.group.CreateGroupFragment;
import ua.safetynet.group.Group;
import ua.safetynet.user.MainPageActivity;
import ua.safetynet.user.User;



public class Database {
    public interface DatabaseUserListener {
        void onUserRetrieval(User user);

    }
    public interface DatabaseGroupListener {
        void onGroupRetrieval(Group group);
    }
    private CollectionReference databaseUsers;
    private CollectionReference databaseGroups;

    public Database() {
        this.databaseUsers = FirebaseFirestore.getInstance().collection("Users");
        this.databaseGroups = FirebaseFirestore.getInstance().collection("Groups");
    }

    //returns the user ID of the user currently logged in to the device (via firebaseAuth)
    public String getUID(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    //returns a list of all users from the user collection (technically will only ever find one) whose FirebaseAuthentication ID matches the collection's userID
    //this can be used with recycler views and will likely be part of how we obtain group membership
    //this is a template for querying multiple things and can be modified easily
    public ArrayList<User> queryUser(){
        final ArrayList<User> userList = new ArrayList<>();
        Query userQuery = databaseUsers
                .whereEqualTo("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());

        userQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        //add user to an arraylist
                        userList.add(user);
                    }
                }
                else{
                    //error toast message goes here
                }
            }
        })
        return userList;
    }
    /***
     * Takes in userId and database listener. Database listener passes in the User object from firestore
     * @param userId User ID from firebase auth. User ID is the key for the user data in Firestore
     * @param dbListener Listener is called when Firebase returns with the User object. Passes the user object as a parameter
     */
    public void getUser(String userId, final Database.DatabaseUserListener dbListener) {
        databaseUsers.document("userId").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                dbListener.onUserRetrieval(user);
            }
        });
    }

    public void getGroup(String groupID, final Database.DatabaseGroupListener dbListener) {
        databaseGroups.document("groupID").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Group group = documentSnapshot.toObject(Group.class);
                dbListener.onGroupRetrieval(group);
            }
        });
    }

    //updates a user's data in firestore using a given user class
    public void setUser(User user) {
        databaseUsers.document(user.getUserId()).set(user);
    }

    //updates a group's data in firestore using a given group class
    public void setGroup(Group group){
        this.databaseGroups.document(group.getGroup_ID()).set(group);
    }

    //creates a new user entry in firestore using the passed in user class
    public void createUser(User user){
        this.databaseUsers.add(user);
    }

    //creates a new group entry in firestore using the passed in group class
    public void createGroup(Group group){
        this.databaseGroups.add(group);
    }



    /*LEGACY CODE BELOW
    NOT TO BE USED

    public String makeUserKey() {
        return this.databaseUsers.push().getKey();
    }

    public String makeGroupKey() {
        return this.databaseGroups.push().getKey();
    }

    public void updateUsers(String id, User user) {
        this.databaseGroups.child(id).setValue(user);
    }

    public void updateGroups(String id, Group group) {
        this.databaseGroups.child(id).setValue(group);
    }

    Code reference for future use:
        String id = databaseUsers.push().getKey()       this creates a blank entry in firebase and returns its autogen id
        User user = new User(incoming information);     create a new user to push
        databaseUsers.child(id).setValue(user);         push information onto the entry with matching id
    */
    }

