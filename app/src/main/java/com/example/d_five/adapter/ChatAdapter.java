package com.example.d_five.adapter;

import static com.example.d_five.PagerAdapter.chatFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d_five.R;
import com.example.d_five.activities.ChatBoxActivity;

import com.example.d_five.ConversationViewChat;
import com.example.d_five.activities.ChatRoomBoxActivity;
import com.example.d_five.dao.ConnectionDB;
import com.example.d_five.dao.DAOFactory;
import com.example.d_five.model.Conversation;
import com.example.d_five.model.Participant;
import com.example.d_five.model.User;
import com.example.d_five.utilities.Constants;
import com.example.d_five.utilities.ImageHandler;

import com.example.d_five.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    Context context;
    private PreferenceManager preferenceManager;
    private DAOFactory daoFactory;

    List<ConversationViewChat> conversationViewChats;

    public ChatAdapter(Context context, List<ConversationViewChat> conversationViewChats){
        this.context = context;
        this.conversationViewChats = conversationViewChats;

        // init
        preferenceManager = new PreferenceManager(context);
        daoFactory = new DAOFactory(ConnectionDB.connection);
    }
    public void setFilteredList(ArrayList<ConversationViewChat> filteredList){
        this.conversationViewChats = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat,parent,false);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new ChatViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, @SuppressLint("RecyclerView") int position) {

        // decode image and encode image
        ImageHandler imageHandler = new ImageHandler(context);
        ConversationViewChat conversationViewChat = conversationViewChats.get(position);
        //holder.avatar.setImageResource(R.drawable.ic_account);
        holder.avatar.setImageBitmap(imageHandler.decodeResource(conversationViewChat.getAvatar()));

        holder.name.setText(conversationViewChat.getName());
        holder.message.setText(conversationViewChat.getLast_message());

        if (!conversationViewChat.isRead()) {
            holder.message.setTypeface(null, Typeface.BOLD);
        }

        holder.time.setText(conversationViewChat.getTime());

        holder.setIsRecyclable(false);

        holder.itemChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Conversation conversation = daoFactory.getConversationDAO().getInfoConversation(conversationViewChat.getConversation_id());
                    User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));

                    if(conversation.isGroup()) {
                        if (checkGroup(conversation.getId(), myself.getId())) {
                            Intent intent = new Intent(context, ChatRoomBoxActivity.class);
                            intent.putExtra("Group Name", conversation.getName_conversation());
                            intent.putExtra("Conversation Id", conversation.getId());
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, context.getString(R.string.deleted_from) + conversation.getName_conversation(), Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Intent intent = new Intent(context, ChatBoxActivity.class);
                        intent.putExtra("name_user", holder.name.getText());
                        intent.putExtra("conversation_id", conversationViewChat.getConversation_id());
                        context.startActivity(intent);
                    }

                    if (!conversationViewChat.isRead()) {
                        chatFragment.markAsRead(position);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean checkGroup(Long groupId, Long userId) {
        boolean result = false;
        try {
            result = daoFactory.getParticipantsDAO().checkUserInConversation(userId, groupId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public int getItemCount() {
        return conversationViewChats.size();

    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder{

        CircleImageView avatar;
        TextView name;
        TextView message;
        TextView time;
        ConstraintLayout itemChat;

        public ChatViewHolder(@NonNull View itemView){
            super(itemView);
            avatar = itemView.findViewById(R.id.imageProfile);
            name = itemView.findViewById(R.id.textName);
            message = itemView.findViewById(R.id.textMessage);
            time = itemView.findViewById(R.id.date);
            itemChat = itemView.findViewById(R.id.item_chat);
        }
    }

    private List<User> getListUser(Long conversation_id) throws Exception {
        List<Participant> list = daoFactory.getParticipantsDAO().getListUserOfConversation(conversation_id);

        List<User> userList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            User u = daoFactory.getUserDAO().getInfoUser(list.get(i).getUser_id());
            userList.add(u);
        }
        return userList;
    }

}
