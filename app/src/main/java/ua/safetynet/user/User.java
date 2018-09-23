package ua.safetynet.user;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class User
{
    String userId;
    String email;

    ArrayList<String> transactions = new ArrayList<String>();
    ArrayList<String> groups = new ArrayList<String>();
}
