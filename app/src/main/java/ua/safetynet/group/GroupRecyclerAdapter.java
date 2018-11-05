package ua.safetynet.group;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import ua.safetynet.Group_home2;
import ua.safetynet.R;

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
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Group group = groupList.get(position);
        holder.mPicture.setImageBitmap(group.getImage());
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
