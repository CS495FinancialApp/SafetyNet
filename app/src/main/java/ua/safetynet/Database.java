package ua.safetynet;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Database {

    private DatabaseReference databaseUsers;
    private DatabaseReference databaseGroups;

    public Database() {
    }

    public void setDatabaseUsers() {
        this.databaseUsers = FirebaseDatabase.getInstance().getReference("Users");
    }

    public void setDatabaseGroups() {
        this.databaseGroups = FirebaseDatabase.getInstance().getReference("Groups");
    }

    /* Code reference for future use:
        String id = databaseUsers.push().getKey()       this creates a blank entry in firebase and returns its autogen id
        User user = new User(incoming information);     create a new user to push
        databaseUsers.child(id).setValue(user);         push information onto the entry with matching id
     */
}

