package com.example.d_five.adapter;

import static com.example.d_five.dialogs.AddNewAccountDialog.binding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d_five.R;
import com.example.d_five.model.User;

import java.util.ArrayList;

public class ContactInDbAdapter extends RecyclerView.Adapter<ContactInDbAdapter.ContactDBViewHolder> {

    Context context;
    ArrayList<User> arrayUser;

    public ContactInDbAdapter(Context context, ArrayList<User> listContact){
        this.context = context;
        this.arrayUser = listContact;
    }
    public void setFilteredList(ArrayList<User> filteredList){
        this.arrayUser = filteredList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ContactInDbAdapter.ContactDBViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact_in_db,parent,false);
        return new ContactInDbAdapter.ContactDBViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactInDbAdapter.ContactDBViewHolder holder, int position) {
        User listUser = arrayUser.get(position);
        holder.name.setText(listUser.getUsername());

        holder.itemContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, holder.name.getText(), Toast.LENGTH_SHORT).show();
                binding.inputUsername.setQuery(holder.name.getText().toString(), false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayUser.size();
    }

    public static class ContactDBViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        LinearLayout itemContact;
        public ContactDBViewHolder(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.textNameDB);
            itemContact = itemView.findViewById(R.id.name_layout);
        }
    }
}
