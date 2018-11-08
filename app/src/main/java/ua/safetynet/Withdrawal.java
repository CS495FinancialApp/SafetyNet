package ua.safetynet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class Withdrawal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);

        final EditText dollarField = (EditText) findViewById(R.id.editWithdrawalAmount);
        Button historyButton = (Button) findViewById(R.id.buttonUserHistory);

        dollarField.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                dollarField.setText("$" + dollarField.getText().toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent historyIntent = new Intent(Withdrawal.this, UserTransactionHistory.class);
                //Need to pass group for transaction history
                startActivity(historyIntent);
            }
        });
    }
}
