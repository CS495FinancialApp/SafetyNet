package ua.safetynet.group;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import ua.safetynet.Group_home2;
import ua.safetynet.R;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * @author Jeremy McCormick
 * Adapter for group to list it in recylcer view with its image, name, and balance
 * Uses Firebase Storage UI and Glide libraries to populate image
 */
public class GroupRecyclerAdapter extends RecyclerView.Adapter<GroupRecyclerAdapter.ViewHolder> {
    private List<Group> groupList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mPicture;
        public TextView  mGroupName;
        public TextView  mAmount;
        public View layout;
        public ViewHolder(View v) {
            super(v);
            layout = v;
            mPicture = v.findViewById(R.id.main_recyclerview_icon);
            mGroupName = v.findViewById(R.id.main_recyclerview_group_name);
            mAmount = v.findViewById(R.id.main_recyclerview_group_amount);
        }
    }

    public GroupRecyclerAdapter(List<Group> list) {
        this.groupList = list;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GroupRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.main_recyclerview_row, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)

    /**
     * Replace contents of a view with groups values. Binds image view to
     * groups image storage reference in Firebase
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Group group = groupList.get(position);
        holder.mPicture.setImageBitmap(group.getImage());

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference groupImageRef = storageRef.child("groupimages/" + group.getGroupId() + ".jpg");
        Glide.with(holder.itemView).load(groupImageRef).apply(new RequestOptions()).into(holder.mPicture);

        holder.mGroupName.setText(group.getName());
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        holder.mAmount.setText(format.format(group.getFunds()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Group_home2.class);
                intent.putExtra("group_name", group.getName());
                intent.putExtra("group_balance", group.getFunds());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }
}

