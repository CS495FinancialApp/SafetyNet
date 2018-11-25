package ua.safetynet.payment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ua.safetynet.Database;
import ua.safetynet.R;
import ua.safetynet.group.GlideApp;
import ua.safetynet.group.Group;
import ua.safetynet.group.GroupRecyclerAdapter;
import ua.safetynet.user.User;

public class TransactionRecyclerAdapter extends RecyclerView.Adapter<TransactionRecyclerAdapter.ViewHolder>{
    private List<Transaction> transactionList;
    private int showType = 2;
    public static final int NOIMAGE = 0;
    public static final int USER = 1;
    public static final int GROUP = 2;
    //Variable of how many days of anonymity to provide
    public int repaymentTime = 0;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameText;
        public TextView amountText;
        public ImageView imageView;
        public View view;
        public ViewHolder(View v) {
            super(v);
            this.view = v;
            this.nameText = v.findViewById(R.id.transaction_recycler_name);
            this.amountText = v.findViewById(R.id.transaction_recycler_amount);
            this.imageView = v.findViewById(R.id.transaction_recycler_image);
        }
    }

    public TransactionRecyclerAdapter(List<Transaction> list) {
        this.transactionList = list;
    }
    public TransactionRecyclerAdapter(List<Transaction> list, int showType) {
        this.transactionList = list;
    }
    @NonNull
    @Override
    public TransactionRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.transaction_recyclerview_row, parent, false);
        return new TransactionRecyclerAdapter.ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(final TransactionRecyclerAdapter.ViewHolder holder, int position) {
        final Transaction transaction = transactionList.get(position);
        //Get image based on what we should be showing
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("groupimages/" + transaction.getGroupId() + ".jpg");
        switch(showType) {
            case NOIMAGE: holder.imageView.setVisibility(View.GONE);
                break;
            case GROUP: imageRef = storageRef.child("groupimages/" + transaction.getGroupId() + ".jpg");
                break;
            case USER: imageRef = storageRef.child("userimages/" + transaction.getUserId() + ".jpg");
                break;
        }
        if(showType != NOIMAGE)
            Glide.with(holder.itemView).load(imageRef).apply(new RequestOptions()).into(holder.imageView);
        //Get user name or group name based on what we should be showing
        if(showType == USER) {
            //Check if transaction should be anonymous
            if(checkAnon(transaction))
                holder.nameText.setText("*****");
            else {
                Database db = new Database();
                db.getUser(transaction.getUserId(), new Database.DatabaseUserListener() {
                    @Override
                    public void onUserRetrieval(User user) {
                        holder.nameText.setText(user.getName());
                        Glide.with(holder.itemView).load(user.getImage()).into(holder.imageView);
                    }
                });
            }
        }
        else {
            //Check if transaction should be anonymous
            if(checkAnon(transaction))
                holder.nameText.setText("*****");
            else {
                Database db = new Database();
                db.getGroup(transaction.getGroupId(), new Database.DatabaseGroupListener() {
                    @Override
                    public void onGroupRetrieval(Group group) {
                        holder.nameText.setText(group.getName());
                        Glide.with(holder.itemView).load(group.getImage()).into(holder.imageView);
                    }
                });
            }
        }
        //Set amount
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        holder.amountText.setText(format.format(transaction.getFunds()));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    //Check's if given transaction's anonymity has expired or not
    private boolean checkAnon(Transaction transaction){
        //ToDo: Potentially use java date strings instead of calendar objects
        Database db = new Database();
        String timestamp = transaction.getTimestamp();
        //ToDo: Confirm format of datetime
        DateFormat df = DateFormat.getDateInstance();
        //Get current datetime
        Calendar current = Calendar.getInstance();
        Calendar deadline = Calendar.getInstance();

        //Get set group repaymentTime
        db.getGroup(transaction.getGroupId(), new Database.DatabaseGroupListener() {
            @Override
            public void onGroupRetrieval(Group group) {
                repaymentTime = group.getRepaymentTime();
            }
        });
        //Set timestamp of transaction
        try {
            deadline.setTime(df.parse(timestamp));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Add repayment time to timestamp
        deadline.add(Calendar.DATE, repaymentTime);
        //If current date is after designated deadline
        if(current.after(deadline))
            return true;    //Hide username
        else
            return false;   //Display username
    }

}
