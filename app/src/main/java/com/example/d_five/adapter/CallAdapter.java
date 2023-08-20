package com.example.d_five.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d_five.CallHistory;
import com.example.d_five.R;
import com.example.d_five.activities.ChatBoxActivity;
import com.example.d_five.dao.ConnectionDB;
import com.example.d_five.dao.DAOFactory;
import com.example.d_five.fragments.CallFragment;
import com.example.d_five.model.Conversation;
import com.example.d_five.model.User;
import com.example.d_five.services.CallService;
import com.example.d_five.utilities.Constants;
import com.example.d_five.utilities.ImageHandler;
import com.example.d_five.utilities.PreferenceManager;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.CallViewHolder> {
    // Database
    private PreferenceManager preferenceManager;
    private DAOFactory daoFactory;
    Context context;
    ArrayList<CallHistory> calls;
    private CallFragment.ItemClickedListener itemClickedListener;

    public CallAdapter(Context context, ArrayList<CallHistory> callsArrayList){
        this.context = context;
        this.calls = callsArrayList;

        preferenceManager = new PreferenceManager(context);
        daoFactory = new DAOFactory(ConnectionDB.connection);
    }

    public void setItemClickedListener(CallFragment.ItemClickedListener itemClickedListener) {
        this.itemClickedListener = itemClickedListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<CallHistory> list){
        this.calls = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CallAdapter.CallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_call,parent,false);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new CallViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull CallViewHolder holder, int position) {
        CallHistory call = calls.get(position);

        ImageHandler imageHandler = new ImageHandler(context);
        Bitmap bitmap = imageHandler.decodeResource(call.getAvatar());

        holder.avatar.setImageBitmap(bitmap);
        holder.name.setText(call.getUsername());
        holder.status.setText(call.getStatus());
        holder.time.setText(call.getTimeWithSlash());
        holder.duration.setText(call.getDurationHMS());

        holder.setIsRecyclable(false);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClickedListener != null){
                    itemClickedListener.onItemClick(view,holder.getAdapterPosition(), call);
                    holder.constraintLayout.setVisibility(call.isExpandable() ? View.VISIBLE : View.GONE);
                }
            }
        });

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallService.makeVoiceCall(context, call.getUsername());
            }
        });

        holder.videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallService.makeVideoCall(context, call.getUsername());
            }
        });

        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));
                    User peer = daoFactory.getUserDAO().getInfoUser(call.getUsername());

                    // check for conversation
                    Long conID = daoFactory.getConversationDAO().checkExistConversation2P(myself.getId(),peer.getId());
                    Intent intent = new Intent(context, ChatBoxActivity.class);
                    intent.putExtra("name_user", call.getUsername());

                    if(conID == null){
                        Conversation conversation =  daoFactory.getUserDAO().newConversation(null,null, null, false, myself.getId()) ;
                        daoFactory.getUserDAO().insertParticipant(myself.getId(), conversation.getId());
                        daoFactory.getUserDAO().insertParticipant(peer.getId(), conversation.getId());
                        intent.putExtra("conversation_id", conversation.getId());
                        System.out.println("new conversation");
                    } else {
                        intent.putExtra("conversation_id", conID);
                        System.out.println("existed conversation");
                    }

                    context.startActivity(intent);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        if(call.getStatus().equals("OutGoing")){
            if(call.getType().equals("audio")){
                holder.statusIcon.setImageResource(R.drawable.ic_call_outgoing);
            }
            else
                holder.statusIcon.setImageResource(R.drawable.ic_outgoing_video_call);
        }
        else if(call.getStatus().equals("InComing")){
            if(call.getType().equals("audio")){
                holder.statusIcon.setImageResource(R.drawable.ic_call_incoming);
            }
            else
                holder.statusIcon.setImageResource(R.drawable.ic_incoming_video_call);
        }
        else if(call.getStatus().equals("Missed")){
            if(call.getType().equals("audio")){
                holder.statusIcon.setImageResource(R.drawable.ic_call_missed);
                holder.status.setTextColor(Color.RED);            }
            else {
                holder.statusIcon.setImageResource(R.drawable.ic_missed_video_call);
                holder.status.setTextColor(Color.RED);            }
        }
    }

    @Override
    public int getItemCount() {
        return calls.size();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {

    }

    public static class CallViewHolder extends RecyclerView.ViewHolder{
        RoundedImageView avatar;
        TextView name;
        TextView status;
        ImageView statusIcon;
        TextView time;
        TextView duration;
        ImageButton call;
        ImageButton videoCall;
        ImageButton chat;
        ConstraintLayout constraintLayout;

        public CallViewHolder(@NonNull View itemView){
            super(itemView);
            avatar = itemView.findViewById(R.id.imageProfile);
            name = itemView.findViewById(R.id.textName);
            status = itemView.findViewById(R.id.callStatus);
            statusIcon = itemView.findViewById(R.id.statusIcon);
            time = itemView.findViewById(R.id.startTime);
            duration = itemView.findViewById(R.id.durationTime);
            call = itemView.findViewById(R.id.btn_call);
            videoCall = itemView.findViewById(R.id.btn_videocall);
            chat = itemView.findViewById(R.id.btn_chat);
            constraintLayout = itemView.findViewById(R.id.expandedCall);
        }
    }

}
