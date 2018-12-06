package ua.safetynet.payment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
/**
 * @author Jeremy McCormick
 * Class to view user picture and info, as well as a list of their transactions
 */
public class TransactionDialog {
    AlertDialog dialog =null;

    public TransactionDialog(Context context, String groupName, Transaction transaction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //If payment
        if(transaction.getFunds().compareTo(BigDecimal.ZERO) > 0) {
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
            String formattedAmount = format.format(transaction.getFunds());
            builder.setMessage("Payment of " + formattedAmount + " to " + groupName + " is Complete!\nThank you! \nID: " + transaction.getTransId());
        }
        else {
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
            String formattedAmount = format.format(transaction.getFunds().negate());
            builder.setMessage("Payout of " + formattedAmount + " from " + groupName + " is Complete!\nThank you!\nID: " + transaction.getTransId());
        }
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
    }
    public TransactionDialog(Context context, String groupName, BigDecimal amount, String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //If payment
        if(amount.compareTo(BigDecimal.ZERO) > 0) {
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
            String formattedAmount = format.format(amount);
            builder.setMessage("Payment of " + formattedAmount + " to " + groupName + " is Complete!\nThank you! \nID: " + id);
        }
        else {
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
            String formattedAmount = format.format(amount.negate());
            builder.setMessage("Payout of " + formattedAmount + " from " + groupName + " is Complete!\nThank you!\nID: " + id);
        }
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
    }

    /**
     * Show dialog
     */
    public void show() {
        if(dialog == null)
            return;
        dialog.show();
    }

    /**
     * Dismiss Dialog
     */
    public void dismiss() {
        if(dialog == null)
            return;
        dialog.dismiss();
    }
}
