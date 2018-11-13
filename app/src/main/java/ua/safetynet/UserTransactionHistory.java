package ua.safetynet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import ua.safetynet.group.Group;
import ua.safetynet.user.User;

public class UserTransactionHistory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_transaction_history);

        final TextView UserName = findViewById(R.id.textViewUserID);
        Database db = new Database();

        db.getUser(db.getUID(), new Database.DatabaseUserListener() {
            @Override
            public void onUserRetrieval(User user) {
                UserName.setText(user.getName());
            }
        });
    }
}
