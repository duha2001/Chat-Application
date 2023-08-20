package com.example.d_five.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d_five.R;
import com.example.d_five.listeners.ItemClickListener;
import com.example.d_five.model.User;
import com.example.d_five.utilities.ImageHandler;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    Context context;
    private ImageHandler imageHandler;
    ArrayList<User> users;
    ItemClickListener itemClickListener;

    ArrayList<User> arrayList_0 = new ArrayList<>();

    boolean isHide;


    public UserAdapter(Context context, ArrayList<User> users, ItemClickListener itemClickListener) {
        this.context = context;
        this.users = users;
        this.itemClickListener = itemClickListener;
        isHide = false;
    }

    public UserAdapter(Context context, ArrayList<User> users, ItemClickListener itemClickListener, boolean isHide) {
        this.context = context;
        this.users = users;
        this.itemClickListener = itemClickListener;
        this.isHide = isHide;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_view,parent,false);
        return new UserAdapter.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User user = users.get(position);
        imageHandler = new ImageHandler(context);
        holder.avatar.setImageBitmap(imageHandler.decodeResource(user.getAvatar()));
        holder.name.setText(user.getUsername());


        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(users != null && users.size() > 0 ){
                    if(holder.checkBox.isChecked()){
                        arrayList_0.add(users.get(position));
                    } else {
                        arrayList_0.remove(users.get(position));
                    }
                    itemClickListener.onSelectUserChange(arrayList_0);
                }
            }
        });

        if (isHide) {
            holder.checkBox.setVisibility(View.GONE);
        }


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder{

        CircleImageView avatar;
        TextView name;
        CheckBox checkBox;

        public UserViewHolder(@NonNull View itemView){
            super(itemView);
            avatar = itemView.findViewById(R.id.userImage);
            name = itemView.findViewById(R.id.name_user);
            checkBox = itemView.findViewById(R.id.check_box);
        }
    }
}
