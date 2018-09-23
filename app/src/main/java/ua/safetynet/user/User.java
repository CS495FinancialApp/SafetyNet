package ua.safetynet.user;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class User
{
    private String userId;
    private String email;
    private String name;

    private ArrayList<String> transactions = new ArrayList<String>();
    private ArrayList<String> groups = new ArrayList<String>();

    public User()
    {
        userId = null;
        email = null;
        name = null;
        transactions.clear();
        groups.clear();
    }

    String getUserId()
    {
        return this.userId;
    }

    void setUserId(String userId)
    {
        this.userId = userId;
    }

    String getEmail()
    {
        return this.email;
    }

    void setEmail(String email)
    {
        this.email = email;
    }

    String getName()
    {
        return this.name;
    }

    void setName(String name)
    {
        this.name = name;
    }

    ArrayList<String> getTransactions()
    {
        return this.transactions;
    }

    ArrayList<String> getGroups()
    {
        return this.groups;
    }
}
