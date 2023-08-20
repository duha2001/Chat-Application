package com.example.d_five.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d_five.R;
import com.example.d_five.activities.DetailContactActivity;
import com.example.d_five.dao.ConnectionDB;
import com.example.d_five.dao.DAOFactory;
import com.example.d_five.dao.implementDAO.UserDAO;
import com.example.d_five.model.Contacts;
import com.example.d_five.model.User;
import com.example.d_five.utilities.ImageHandler;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private  DAOFactory daoFactory;
    Context context;
    ArrayList<Contacts> arrayContact;

    public ContactAdapter(Context context, ArrayList<Contacts> listContact){
        this.context = context;
        this.arrayContact = listContact;

        daoFactory = new DAOFactory(ConnectionDB.connection);
    }
    public void setFilteredList(ArrayList<Contacts> filteredList){
        this.arrayContact = filteredList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact_view,parent,false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contacts contact = arrayContact.get(position);
        //holder.name.setText(contact.getUser_contact_id().toString());

        try {
            User user = daoFactory.getUserDAO().getInfoUser(contact.getUser_contact_id());
            holder.name.setText(user.getUsername());
            ImageHandler imageHandler = new ImageHandler(context);
            holder.avatar.setImageBitmap(imageHandler.decodeResource(user.getAvatar()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.moreDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailContactActivity.class);
                intent.putExtra("name_detail_user", holder.name.getText());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayContact.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder{
        CircleImageView avatar;
        TextView name;
        Button moreDetail;

        public ContactViewHolder(@NonNull View itemView){
            super(itemView);
            avatar = itemView.findViewById(R.id.imageUser);
            name = itemView.findViewById(R.id.name_contact);
            moreDetail = itemView.findViewById(R.id.detail);
        }
    }
}
