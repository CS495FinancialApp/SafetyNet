package ua.safetynet;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ua.safetynet.group.Group;
import ua.safetynet.user.User;

public class Database {

    private DatabaseReference databaseUsers;
    private DatabaseReference databaseGroups;

    public Database() {
        this.databaseUsers = FirebaseDatabase.getInstance().getReference("Users");
        this.databaseGroups = FirebaseDatabase.getInstance().getReference("Groups");
    }

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

    /* Code reference for future use:
        String id = databaseUsers.push().getKey()       this creates a blank entry in firebase and returns its autogen id
        User user = new User(incoming information);     create a new user to push
        databaseUsers.child(id).setValue(user);         push information onto the entry with matching id
     */
}

