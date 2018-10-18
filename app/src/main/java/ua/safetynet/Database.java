package ua.safetynet;

import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.Context;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    public void setUser(User user) {
        databaseUsers.document(user.getUserId()).set(user);
    }

    public void updateUsers(){

    }

    //creates a new group entry in firestore using the passed in group class
    public void createGroup(Group group){
        this.databaseGroups.add(group);
    }

    //creates a new user entry in firestore using the passed in user class
    public void createUser(User user){
        this.databaseUsers.add(user);
    }

    //updates a group to match the passed in group that matches the given string id
    public void updateGroup(Group group, String id){
        this.databaseGroups.document(id).set(group);
    }

    //updates a user to match the passed in user that matches the given string id
    public void updateUser(User user, String id){
        this.databaseUsers.document(id).set(user);
    }


    /*public String makeUserKey() {
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
    }*/

    /* Code reference for future use:
        String id = databaseUsers.push().getKey()       this creates a blank entry in firebase and returns its autogen id
        User user = new User(incoming information);     create a new user to push
        databaseUsers.child(id).setValue(user);         push information onto the entry with matching id
     */
}

