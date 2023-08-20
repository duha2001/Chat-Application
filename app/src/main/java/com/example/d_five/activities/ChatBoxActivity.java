package com.example.d_five.activities;
import static com.example.d_five.model.Default.BitMapToString;
import static com.example.d_five.PagerAdapter.chatFragment;
import static com.example.d_five.model.Default.createTimeStamp;
import static com.example.d_five.model.Default.mappingTypeMessage;

import static org.linphone.mediastream.MediastreamerAndroidContext.getContext;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d_five.MessageUtil;
import com.example.d_five.R;
import com.example.d_five.adapter.MessageThreadAdapter;
import com.example.d_five.dao.ConnectionDB;
import com.example.d_five.dao.DAOFactory;


import com.example.d_five.model.Conversation;
import com.example.d_five.model.LocationMessage;
import com.example.d_five.model.Message;
import com.example.d_five.model.TypeMessage;
import com.example.d_five.model.User;
import com.example.d_five.services.CallBackMessage;
import com.example.d_five.services.CallService;
import com.example.d_five.services.LinphoneService;
import com.example.d_five.utilities.Constants;
import com.example.d_five.utilities.ImageHandler;
import com.example.d_five.utilities.PreferenceManager;
import com.example.d_five.databinding.ActivityChattingBinding;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import org.linphone.core.ChatMessage;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatBoxActivity extends AppCompatActivity implements CallBackMessage {
    private PreferenceManager preferenceManager;
    private DAOFactory daoFactory;
    private ImageHandler imageHandler;

    private User peerUser;
    private ActivityChattingBinding binding;
    private RecyclerView mMessageRecycler;
    private MessageThreadAdapter mMessageAdapter;
    private String peerName;

    private String userName;

    private List<Message> messageList;

    private Long conversation_id;
    private ArrayList<MessageUtil> messageUtils;
    LinphoneService linphoneService;
    Boolean isBound;

    private static final int PICK_IMAGE = 1;


    /** Defines callbacks for service binding, passed to bindService(). */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
            linphoneService = binder.getService();
            linphoneService.setChatBoxActivity(ChatBoxActivity.this);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };



    @SuppressLint("NotifyDataSetChanged")
    private void updateChatMessages(String message, String sender, String receiver, String time, String messageId, LocationMessage locationMessage, String status, TypeMessage typeMessage){
        messageUtils.add(0,new MessageUtil(sender,receiver, message, time, locationMessage,status,messageId,typeMessage));
        //binding.listMessage.smoothScrollToPosition(0);
        mMessageAdapter.notifyDataSetChanged();
        //binding.listMessage.scrollToPosition(0);
        binding.listMessage.smoothScrollToPosition(0);
    }

    private void updateMessageStatus(String status, String messageID){
        for(int i = 0 ; i < messageUtils.size() ; i++){
            MessageUtil message = messageUtils.get(i);
            if(message.getMessageID().equals(messageID)){
                message.setStatus(status);
                mMessageAdapter.notifyItemChanged(i);
                return;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this.getApplicationContext(), LinphoneService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChattingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EmojiManager.install(new GoogleEmojiProvider());

        // Database init
        preferenceManager = new PreferenceManager(getApplicationContext());
        daoFactory = new DAOFactory(ConnectionDB.connection);
        imageHandler = new ImageHandler(getContext());

        //set user name in chatting screen
        Bundle extras = getIntent().getExtras();
        userName = preferenceManager.getString(Constants.USER_NAME);
        peerName = (String) extras.get("name_user");
        conversation_id = (Long) extras.get("conversation_id");

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.progressBar.setVisibility(View.VISIBLE);
                    }
                });

                // load data
                try {
                    messageList = daoFactory.getMessageDAO().getListMessages(conversation_id);
                    messageUtils = convertListMessage(messageList);
                    markAsRead();
                } catch (Exception e){
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.progressBar.setVisibility(View.GONE);
                        // set adapter
                        mMessageRecycler = (RecyclerView) findViewById(R.id.listMessage);
                        LinearLayoutManager manager = new LinearLayoutManager(getContext());
                        manager.setReverseLayout(true);
                        manager.setStackFromEnd(false);
                        mMessageAdapter = new MessageThreadAdapter(getContext(), messageUtils);
                        mMessageRecycler.setAdapter(mMessageAdapter);
                        mMessageRecycler.setLayoutManager(manager);

                        binding.listMessage.smoothScrollToPosition(0);
                    }
                });
            }
        });

        try {
            peerUser = getInfoUser(peerName);
        } catch (Exception e){
            e.printStackTrace();
        }

        binding.textUsername.setText(peerName);
        binding.avatarUser.setImageBitmap(imageHandler.decodeResource(peerUser.getAvatar()));
        binding.listMessage.scrollToPosition(0);



        EmojiPopup popup = EmojiPopup.Builder.fromRootView(binding.chatScreen).build(binding.textInputMessage);
        binding.listMessage.smoothScrollToPosition(0);


        //Handling onclick on button

        binding.btnAddIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.toggle();
            }
        });


        binding.btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.textInputMessage.getText().toString();
                sendMessage(message);
            }
        });

        binding.btnBackScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.btnMoreDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatBoxActivity.this, DetailConversationActivity.class);
                intent.putExtra("id_conversation", conversation_id);
                intent.putExtra("name_detail_user", binding.textUsername.getText());
                startActivity(intent);
            }
        });

        binding.getRoot().setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
                return true;
            }
            return false;
        });

        binding.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallService.makeVoiceCall(ChatBoxActivity.this, peerName);
            }
        });

        binding.btnVideocall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallService.makeVideoCall(ChatBoxActivity.this, peerName);
            }
        });


        binding.btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });


    }

    private void sendMessage(String message) {
        if(message.length() == 0){
            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.pls_enter_msg), Toast.LENGTH_SHORT).show();
        } else if (message.length() <= 256) {
            if (isBound) {
                binding.textInputMessage.setText("");

                Long msgID = sendMessageToDB(message,TypeMessage.MSG);
                updateNotRead(peerName);

                linphoneService.sendMessageToPerson(conversation_id,"sip:"+ peerName+"@dfive-ims.dek.vn");
                updateChatMessages(message,
                        preferenceManager.getString(Constants.USER_NAME),
                        peerName,
                        createTimeStamp(LocalDateTime.now()),
                        msgID.toString(),
                        LocationMessage.RIGHT,
                        "",
                        TypeMessage.MSG);
                binding.textInputMessage.setText("");
            } else {
                Toast.makeText(getApplicationContext(), "Has some error. Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),getApplicationContext().getString(R.string.too_long), Toast.LENGTH_SHORT).show();
            binding.textInputMessage.setText("");
        }
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if(result.getResultCode() == RESULT_OK){
                if(result.getData() != null){
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        String encodedImage = BitMapToString(bitmap);
                        Long msgID = sendMessageToDB(encodedImage,TypeMessage.IMG);
                        updateNotRead(peerName);
                        linphoneService.sendMessageToPerson(conversation_id,"sip:"+ peerName+"@dfive-ims.dek.vn");
                        updateChatMessages(encodedImage,
                                peerName,
                                "",
                                createTimeStamp(LocalDateTime.now()),
                                String.valueOf(msgID),
                                LocationMessage.RIGHT,
                                "",
                                TypeMessage.IMG);
                        Toast.makeText(getApplicationContext(), "Sent Image !", Toast.LENGTH_SHORT).show();

                    } catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    );


    private void updateNotRead(String peerName) {
        try {
            User peerUser = daoFactory.getUserDAO().getInfoUser(peerName);
            daoFactory.getParticipantsDAO().updateIsRead(peerUser.getId(), conversation_id, false);
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

    }

    private User getInfoUser(String username) {
        User result;
        try {
            result = daoFactory.getUserDAO().getInfoUser(username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            linphoneService.setChatBoxActivity(null);
        }
    }

    public Long sendMessageToDB(String message, TypeMessage typeMessage){
        // save message to database
        try {
            User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));
            String type;
            if(typeMessage.equals(TypeMessage.IMG))
                type = "IMAGE";
            else if(typeMessage.equals(TypeMessage.CALL))
                type = "CALL";
            else type = "MESSAGE";
            Message msg = daoFactory.getUserDAO().insertMessage(myself.getId(), conversation_id, message, type);
            Conversation conversationUpdate = daoFactory.getConversationDAO().getInfoConversation(conversation_id);

            if(msg.getIsType().equals("IMAGE")){
                conversationUpdate.setLast_message("IMAGE");
            } else {
                conversationUpdate.setLast_message(msg.getContent());
            }
            conversationUpdate.setLast_message_id(msg.getId());
            daoFactory.getConversationDAO().updateConversation(conversationUpdate);

            return msg.getId();
        } catch (Exception e){
            e.printStackTrace();
        }

        chatFragment.onActivityChange(conversation_id);
        return null;
    }

    @Override
    public void onMessageChange(ChatMessage chatMessage, LocationMessage locationMessage) {
        if (chatMessage == null) {
            try {
                User mySelf = daoFactory.getUserDAO().getInfoUser(Constants.USER_NAME);
                Conversation conversation = daoFactory.getConversationDAO().getInfoConversation(conversation_id);
                Message msg = daoFactory.getMessageDAO().getInfoMessage(conversation.getLast_message_id());
                if (msg.getUser_id() == mySelf.getId()) {
                    updateChatMessages(msg.getContent(),
                            peerName,
                            userName,
                            createTimeStamp(LocalDateTime.now()),
                            String.valueOf(msg.getId()),
                            locationMessage, "", TypeMessage.CALL);
                } else {
                    updateChatMessages(msg.getContent(),
                            userName,
                            peerName,
                            createTimeStamp(LocalDateTime.now()),
                            String.valueOf(msg.getId()),
                            locationMessage, "", TypeMessage.CALL);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            ViewGroup.LayoutParams layoutParams = binding.textStatusUser.getLayoutParams();
            try {
                Conversation conversation = daoFactory.getConversationDAO().getInfoConversation(conversation_id);
                Message msg = daoFactory.getMessageDAO().getInfoMessage(conversation.getLast_message_id());

                String type = msg.getIsType();

                if (locationMessage != LocationMessage.RIGHT) {
                    markAsRead();
                    chatFragment.markAsRead(conversation_id);
                    //get type
                    TypeMessage typeMessage;
                    if (type.equals("IMAGE"))
                        typeMessage = TypeMessage.IMG;
                    else if (type.equals("MESSAGE"))
                        typeMessage = TypeMessage.MSG;
                    else typeMessage = TypeMessage.CALL;
                    updateChatMessages(msg.getContent(), peerName, userName, createTimeStamp(LocalDateTime.now()), chatMessage.getMessageId(), locationMessage, "", typeMessage);
                } else if (locationMessage == LocationMessage.RIGHT ) {
                    ChatMessage.State state = chatMessage.getState();
                    String messageID = msg.getId().toString();

                    switch (state) {
                        case InProgress:
                            updateMessageStatus("Sending", messageID);
                            break;
                        case Delivered:
                            layoutParams.height = 100;
                            binding.textStatusUser.setLayoutParams(layoutParams);
                            binding.textStatusUser.setText("online");
                            binding.textStatusUser.setTextColor(Color.GREEN);
                            binding.textStatusUser.setVisibility(View.VISIBLE);
                            updateMessageStatus("Delivered", messageID);
                            break;
                        case NotDelivered:
                            updateMessageStatus("Not Available", messageID);
                            deleteMessageDB(Long.valueOf(messageID));
                            //System.out.println("message ID " + messageID );
                            layoutParams.height = 100;
                            binding.textStatusUser.setLayoutParams(layoutParams);
                            binding.textStatusUser.setText("offline");
                            binding.textStatusUser.setTextColor(Color.RED);
                            binding.textStatusUser.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public Long getConversationId() {
        return conversation_id;
    }

    private void markAsRead() {
        try {
            User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));
            daoFactory.getParticipantsDAO().updateIsRead(myself.getId(), conversation_id, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteMessageDB(Long messageId){
        try {
            Conversation conversationUpdate = daoFactory.getConversationDAO().getInfoConversation(conversation_id);

            System.out.println("list 1 :" + messageUtils);
            System.out.println("list 2:" + messageList);

            if(messageUtils.size() == 1){
                conversationUpdate.setLast_message_id(null);
                conversationUpdate.setLast_message(null);
            } else {
                conversationUpdate.setLast_message_id(messageList.get(0).getId());
                conversationUpdate.setLast_message(messageList.get(0).getIsType().equals("IMAGE") ? "IMAGE!" : messageList.get(0).getContent());
            }


            daoFactory.getConversationDAO().updateConversation(conversationUpdate);
            Message msg = daoFactory.getMessageDAO().deleteMessage(messageId);

            System.out.println("delete " + msg.getContent());

        } catch (Exception e){
            e.printStackTrace();

        }

        chatFragment.onActivityChange(conversation_id);
    }


    public ArrayList<MessageUtil> convertListMessage(List<Message> list){
        ArrayList<MessageUtil> arrayList = new ArrayList<>();

        try {
            User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));

            for (int i = 0; i < list.size(); i++) {
                User user = daoFactory.getUserDAO().getInfoUser(list.get(i).getUser_id());
                String date = createTimeStamp(list.get(i).getCreated_at());
                Log.i("database", user.getUsername());
                Log.i("database", myself.getUsername());

                switch (list.get(i).getIsType()) {
                    case "NOTIFY":
                        arrayList.add(new MessageUtil(myself.getUsername(),
                                user.getUsername(),
                                list.get(i).getContent(),
                                date,
                                LocationMessage.CENTER,
                                "Delivered",
                                list.get(i).getId().toString(),
                                list.get(i).getIsType().equals("IMAGE") ? TypeMessage.IMG : TypeMessage.MSG));
                        break;
                    default:
                        TypeMessage typeMessage = mappingTypeMessage(list.get(i).getIsType());
                        if(user.getUsername().equals(myself.getUsername())){
                            arrayList.add(new MessageUtil(myself.getUsername(),peerName,list.get(i).getContent(),date,LocationMessage.RIGHT,"Delivered",list.get(i).getId().toString(), typeMessage));
                        } else {
                            arrayList.add(new MessageUtil(peerName,myself.getUsername(),list.get(i).getContent(),date,LocationMessage.LEFT,"Delivered",list.get(i).getId().toString(), typeMessage));
                        }
                }
            }

            return arrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;

    }

}