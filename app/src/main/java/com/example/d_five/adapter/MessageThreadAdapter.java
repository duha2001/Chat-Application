package com.example.d_five.adapter;


import static com.example.d_five.model.Default.StringToBitMap;
import static com.example.d_five.model.Default.createTimeStamp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d_five.CallHistory;
import com.example.d_five.MessageUtil;
import com.example.d_five.R;
import com.example.d_five.activities.DetailImageActivity;
import com.example.d_five.dao.ConnectionDB;
import com.example.d_five.dao.implementDAO.UserDAO;
import com.example.d_five.model.LocationMessage;
import com.example.d_five.model.TypeMessage;
import com.example.d_five.model.User;
import com.example.d_five.services.CallService;
import com.example.d_five.utilities.ExtendedDataHolder;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;


class ReceivedCallHolder extends RecyclerView.ViewHolder {

    TextView content, sender;
    ImageView avatar;
    TextView tapToCallBack;

    ImageButton button;
    Context context;
    ReceivedCallHolder(View itemView) {
        super(itemView);
        sender = itemView.findViewById(R.id.show_NameReceiver);
       content = (TextView) itemView.findViewById(R.id.receive_call_message);
       avatar = itemView.findViewById(R.id.message_call_avatar_user);
       tapToCallBack = itemView.findViewById(R.id.receive_call_tap);
       button = itemView.findViewById(R.id.call_icon_l);
       context = itemView.getContext();
    }

    void bind(MessageUtil message) {
        //image.setImageResource(R.drawable.welcome);
        try {
            User user = (new UserDAO(ConnectionDB.connection)).getInfoUser(message.getSender());
            avatar.setImageBitmap(StringToBitMap(user.getAvatar()));
            sender.setText(message.getSender());
            content.setText(message.getMessage());
            String type = message.getMessage().split(":")[0];
            if(type.equals("video"))
                button.setImageResource(R.drawable.ic_videocall);
            else {
                button.setImageResource(R.drawable.ic_phone);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        tapToCallBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = message.getMessage().split(":")[0];
                if(type.equals("audio"))
                    CallService.makeVoiceCall(context, message.getSender());
                else {
                    CallService.makeVideoCall(context,message.getSender());
                }
            }
        });


    }
}

class SentCallHolder extends RecyclerView.ViewHolder  {
    TextView  content;
    TextView tapToCallBack;

    ImageButton button;

    Context context;
    SentCallHolder(View itemView) {
        super(itemView);
        content = itemView.findViewById(R.id.sent_call_message);
        tapToCallBack = itemView.findViewById(R.id.sent_call_tap);
        context = itemView.getContext();
        button = itemView.findViewById(R.id.call_icon_r);
    }


    void bind(MessageUtil message) {

        content.setText(message.getMessage());

        String type = message.getMessage().split(":")[0];
        if(type.equals("video"))
            button.setImageResource(R.drawable.ic_videocall);
        else {
            button.setImageResource(R.drawable.ic_phone);
        }

        tapToCallBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = message.getMessage().split(":")[0];
                if(type.equals("audio")){
                    CallService.makeVoiceCall(context, message.getReceiver());
                } else {
                    CallService.makeVideoCall(context,message.getReceiver());
                }
            }
        });
    }
}

// notification
class NotifyHolder extends RecyclerView.ViewHolder {

    TextView messageText, showTime;

    NotifyHolder(View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.notify_message);
        showTime = (TextView) itemView.findViewById(R.id.notify_time);
        ViewGroup.LayoutParams layoutParams = showTime.getLayoutParams();

        messageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutParams.height == 80){
                    layoutParams.height = 0;
                    showTime.setLayoutParams(layoutParams);
                }
                else{
                    layoutParams.height = 80;
                    showTime.setLayoutParams(layoutParams);
                }
            }
        });
    }

    void bind(MessageUtil message) {
        try {
            showTime.setText(message.getTimestamp());
            messageText.setText(message.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


//image
class ReceivedImgHolder extends RecyclerView.ViewHolder {

    TextView showTime, sender;
    ImageView profileImage, image;


    ReceivedImgHolder(View itemView) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.image_msg_left);
        profileImage = (ImageView) itemView.findViewById(R.id.message_avatar_user);
        sender = itemView.findViewById(R.id.show_NameReceiver);
        showTime = (TextView) itemView.findViewById(R.id.show_timeMessage);
        ViewGroup.LayoutParams layoutParams = showTime.getLayoutParams();

    }

    void bind(MessageUtil message) {
        //image.setImageResource(R.drawable.welcome);
        try {
            User user = (new UserDAO(ConnectionDB.connection)).getInfoUser(message.getSender());
            image.setImageBitmap(StringToBitMap(message.getMessage()));
            profileImage.setImageBitmap(StringToBitMap(user.getAvatar()));
            sender.setText(message.getSender());
            showTime.setText(message.getTimestamp());
        } catch (Exception e) {
            e.printStackTrace();
        }


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();

                Intent intent = new Intent(context.getApplicationContext(), DetailImageActivity.class);
                ExtendedDataHolder extras = ExtendedDataHolder.getInstance();
                //intent.putExtra("img_src", message.getMessage());
                extras.putExtra("img_src", message.getMessage());
                extras.putExtra("sender",message.getSender());
                extras.putExtra("time",message.getTimestamp());
                context.startActivity(intent);
            }
        });
    }
}

class SentImgHolder extends RecyclerView.ViewHolder  {
    TextView  timeStamp;
    ImageView image;


    SentImgHolder(View itemView) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.image_msg_right);
        timeStamp = (TextView) itemView.findViewById(R.id.message_sent_time);
        ViewGroup.LayoutParams layoutParams = timeStamp.getLayoutParams();
        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

    }


    void bind(MessageUtil message) {
        image.setImageBitmap(StringToBitMap(message.getMessage()));
        String txt;
        if(message.getStatus().equals("Not Available")){
            timeStamp.setTextColor(Color.RED);
            txt = message.getStatus();
            timeStamp.setVisibility(View.VISIBLE);
        }else {
            timeStamp.setTextColor(Color.BLACK);
            txt =message.getTimestamp()  + message.getStatus();
        }
        timeStamp.setText(txt);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context.getApplicationContext(),  DetailImageActivity.class);
                ExtendedDataHolder extras = ExtendedDataHolder.getInstance();
                //intent.putExtra("img_src", message.getMessage());
                extras.putExtra("img_src", message.getMessage());
                extras.putExtra("sender",message.getReceiver());
                extras.putExtra("time",message.getTimestamp());
                context.startActivity(intent);
                //startActivity(intent);
            }
        });
    }
}

// message
class ReceivedMessageHolder extends RecyclerView.ViewHolder {

    TextView messageText, showTime, sender;
    ImageView profileImage;

    ReceivedMessageHolder(View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.receive_message);
        profileImage = (ImageView) itemView.findViewById(R.id.message_avatar_user);
        sender = itemView.findViewById(R.id.show_NameReceiver);
        showTime = (TextView) itemView.findViewById(R.id.show_timeMessage);
        ViewGroup.LayoutParams layoutParams = showTime.getLayoutParams();

        messageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutParams.height == 80){
                    layoutParams.height = 0;
                    showTime.setLayoutParams(layoutParams);
                }
                else{
                    layoutParams.height = 80;
                    showTime.setLayoutParams(layoutParams);
                }
            }
        });
    }

    void bind(MessageUtil message) {
        try {
            User user = (new UserDAO(ConnectionDB.connection)).getInfoUser(message.getSender());
            profileImage.setImageBitmap(StringToBitMap(user.getAvatar()));
            sender.setText(message.getSender());
            showTime.setText(message.getTimestamp());
            messageText.setText(message.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class SentMessageHolder extends RecyclerView.ViewHolder {
    TextView messageText, timeStamp;
    SentMessageHolder(View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.sent_message);
        timeStamp = (TextView) itemView.findViewById(R.id.message_sent_time);
        ViewGroup.LayoutParams layoutParams = timeStamp.getLayoutParams();
        messageText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (layoutParams.height == 80){
                    layoutParams.height = 0;
                    timeStamp.setLayoutParams(layoutParams);
                }
                else{
                    layoutParams.height = 80;
                    timeStamp.setLayoutParams(layoutParams);
                }
            }
        });

        messageText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

    }

    void bind(MessageUtil message) {
        messageText.setText(message.getMessage());
        String txt;
        if(message.getStatus().equals("Not Available")){
            timeStamp.setTextColor(Color.RED);
            txt = message.getStatus() + " : cannot send";
            timeStamp.setVisibility(View.VISIBLE);
        }else {
            timeStamp.setTextColor(Color.BLACK);
            txt = message.getTimestamp() + " : " + message.getStatus();
        }
        timeStamp.setText(txt);
        //jjtimeStamp.setVisibility(View.VISIBLE);
    }
}



public class MessageThreadAdapter extends RecyclerView.Adapter {

    private static final int SENT_TYPE = 1;
    private static final int RECV_TYPE = 2;
    private static final int SENT_IMG_TYPE = 3;
    private static final int RECV_IMG_TYPE = 4;
    private static final int NOTIFY_TYPE  = 5;

    private static final int SENT_CALL_TYPE  = 6;

    private static final int RECV_CALL_TYPE  = 7;

    private Context mContext;
    private ArrayList<MessageUtil> mMessageList;

    public MessageThreadAdapter(Context mContext, ArrayList<MessageUtil> mMessageList) {
        this.mContext = mContext;
        this.mMessageList = mMessageList;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case SENT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
                return new SentMessageHolder(view);
            case RECV_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
                return new ReceivedMessageHolder(view);
            case SENT_IMG_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_right, parent, false);
                return new SentImgHolder(view);
            case RECV_IMG_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_left, parent, false);
                return new ReceivedImgHolder(view);
            case NOTIFY_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_center, parent, false);
                return new NotifyHolder(view);
            case SENT_CALL_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_call_message_right, parent, false);
                return new SentCallHolder(view);
            case RECV_CALL_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_call_message_left, parent, false);
                return new ReceivedCallHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

         MessageUtil message = (MessageUtil) mMessageList.get(position);
        //ChatMessage message = (ChatMessage) mMessageList.get(position);

        switch (holder.getItemViewType()){
            case SENT_TYPE:
                ((SentMessageHolder) holder).bind(message);
                break;
            case RECV_TYPE:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
            case SENT_IMG_TYPE:
                ((SentImgHolder) holder).bind(message);
                break;
            case RECV_IMG_TYPE:
                ((ReceivedImgHolder) holder).bind(message);
                break;
            case NOTIFY_TYPE:
                ((NotifyHolder) holder).bind(message);
                break;
            case SENT_CALL_TYPE:
                ((SentCallHolder) holder).bind(message);
                break;
            case RECV_CALL_TYPE:
                ((ReceivedCallHolder) holder).bind(message);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    // HARD CODE FOR TEST
    @Override
    public int getItemViewType(int position) {
        MessageUtil message = (MessageUtil) mMessageList.get(position);

        // message notify in center
        int result = -1;
        switch (message.getLocationMessage()) {
            case LEFT:
                if(message.getTypeMessage().equals(TypeMessage.IMG)){
                    result = RECV_IMG_TYPE;
                } else if(message.getTypeMessage().equals(TypeMessage.MSG)){
                    result = RECV_TYPE;
                } else {
                    result = RECV_CALL_TYPE;
                }
                break;
            case RIGHT:
                if(message.getTypeMessage().equals(TypeMessage.IMG)){
                    result = SENT_IMG_TYPE;
                } else if(message.getTypeMessage().equals(TypeMessage.MSG)) {
                    result = SENT_TYPE;
                } else {
                    result = SENT_CALL_TYPE;
                }
                break;
            case CENTER:
                result = NOTIFY_TYPE;
                break;
        }
       return result;
    }
}
