package ua.safetynet.group;

import android.support.annotation.VisibleForTesting;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Group {

    private String Group_name;
    private Integer Funds;
    private Integer Withdrawal_Limit;

    private ArrayList<String> Withdrawals = new ArrayList<>();
    private ArrayList<String> Admins = new ArrayList<>();
    private ArrayList<String> Users = new ArrayList<>();

    public Group() {
        this.Group_name = null;
        this.Funds = 0;
        this.Withdrawal_Limit = 0;

        this.Withdrawals.clear();
        this.Admins.clear();
        this.Users.clear();
    }

    public Group(String group_name, Integer funds, Integer withdrawal_Limit, ArrayList<String> withdrawals, ArrayList<String> admins, ArrayList<String> users) {
        this.Group_name = group_name;
        this.Funds = funds;
        this.Withdrawal_Limit = withdrawal_Limit;
        this.Withdrawals = withdrawals;
        this.Admins = admins;
        this.Users = users;
    }

    String getGroup_name() {
        return this.Group_name;
    }

    Integer getFunds() {
        return this.Funds;
    }

    Integer getWithdrawal_Limit() {
        return this.Withdrawal_Limit;
    }

    ArrayList<String> getWithdrawals() {
        return this.Withdrawals;
    }

    ArrayList<String> getAdmins() {
        return this.Admins;
    }

    ArrayList<String> getUsers() {
        return this.Users;
    }

    void setGroup_name(String group_name) {
        this.Group_name = group_name;
    }

    void setFunds(Integer funds) {
        this.Funds = funds;
    }

    void setWithdrawal_Limit(Integer withdrawal_Limit) {
        this.Withdrawal_Limit = withdrawal_Limit;
    }

    void setWithdrawals(ArrayList<String> withdrawals) {
        this.Withdrawals = withdrawals;
    }

    void setAdmins(ArrayList<String> admins) {
        this.Admins = admins;
    }

    void setUsers(ArrayList<String> users) {
        this.Users = users;
    }
}
