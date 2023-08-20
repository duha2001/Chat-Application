package com.example.d_five.activities;


import static com.example.d_five.PagerAdapter.chatFragment;
import static com.example.d_five.model.Default.BitMapToString;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d_five.dialogs.LoadingDialog;
import com.example.d_five.MessageUtil;
import com.example.d_five.R;
import com.example.d_five.adapter.MessageThreadAdapter;
import com.example.d_five.dao.ConnectionDB;
import com.example.d_five.dao.DAOFactory;
import com.example.d_five.databinding.ActivityChattingBinding;
import com.example.d_five.model.Conversation;
import com.example.d_five.model.LocationMessage;
import com.example.d_five.model.Message;
import com.example.d_five.model.Participant;
import com.example.d_five.model.TypeMessage;
import com.example.d_five.model.User;
import com.example.d_five.services.CallBackMessage;
import com.example.d_five.services.LinphoneService;
import com.example.d_five.utilities.Constants;
import com.example.d_five.utilities.PreferenceManager;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import org.linphone.core.ChatMessage;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.Objects;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ChatRoomBoxActivity extends AppCompatActivity implements CallBackMessage {

    // Database
    private PreferenceManager preferenceManager;
    private DAOFactory daoFactory;
    private Long conversationId;
    private User myself;

    @SuppressLint("StaticFieldLeak")
    private static ActivityChattingBinding binding;
    private LoadingDialog loadingDialog;

    private RecyclerView mMessageRecycler;
    private MessageThreadAdapter mMessageAdapter;

    private List<User> userList;
    private String groupName;

    LinphoneService linphoneService;
    Boolean isBound;

    private String message;

    @Override
    public Long getConversationId() {
        return conversationId;
    }

    /** Defines callbacks for service binding, passed to bindService(). */
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
            linphoneService = binder.getService();
            linphoneService.setChatRoomBoxActivity(ChatRoomBoxActivity.this);

            if (userList != null) {
                for (User user : userList) {
                    if (user.getId() != myself.getId())  {
                        linphoneService.createChatRoom(user.getUsername());
                    }
                }
            }
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    ArrayList<MessageUtil> messageUtils;


    @SuppressLint("NotifyDataSetChanged")
    private void updateChatMessages(String message, String sender , String receiver, String time, LocationMessage locationMessage, String status, TypeMessage typeMessage){
        messageUtils.add(0,new MessageUtil(sender,receiver, message, time, locationMessage, "Delivered" , "test",typeMessage));
        mMessageAdapter.notifyDataSetChanged();
        binding.listMessage.scrollToPosition(0);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();


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
                Conversation conversation1 = new Conversation();
                try {
                    conversation1 = daoFactory.getConversationDAO().getInfoConversation(conversationId);

                    userList = getUserListOfConversation(conversationId);


                    // load mess
                    List<Message> messageList = daoFactory.getMessageDAO().getListMessages(conversationId);
                    ArrayList<MessageUtil> msgUtils = convertListMessage(messageList);
                    messageUtils = msgUtils;



                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                chatFragment.markAsRead(conversationId);

                Conversation finalConversation = conversation1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.progressBar.setVisibility(View.GONE);
                        groupName = finalConversation.getName_conversation();
                        binding.textUsername.setText(groupName);
                        mMessageAdapter = new MessageThreadAdapter(getContext(), messageUtils);
                        mMessageRecycler.setAdapter(mMessageAdapter);
                        mMessageAdapter.notifyDataSetChanged();
                    }
                });
            }
        });


//        try {
//            Conversation conversation = daoFactory.getConversationDAO().getInfoConversation(conversationId);
//            groupName = conversation.getName_conversation();
//            binding.textUsername.setText(groupName);
//            userList = getUserListOfConversation(conversationId);
//
//
//            // load mess
//            List<Message> messageList = daoFactory.getMessageDAO().getListMessages(conversationId);
//            ArrayList<MessageUtil> msgUtils = convertListMessage(messageList);
//            messageUtils = msgUtils;
//            mMessageAdapter = new MessageThreadAdapter(this, msgUtils);
//            mMessageRecycler.setAdapter(mMessageAdapter);
//            mMessageAdapter.notifyDataSetChanged();
//
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

        //chatFragment.markAsRead(conversationId);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this.getApplicationContext(), LinphoneService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void markAsRead() {
        try {
            User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));
            daoFactory.getParticipantsDAO().updateIsRead(myself.getId(), conversationId, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChattingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // init
        preferenceManager = new PreferenceManager(getApplicationContext());
        daoFactory = new DAOFactory(ConnectionDB.connection);

        messageUtils = new ArrayList<>();

        //set user name in chatting screen
        Bundle extras = getIntent().getExtras();
        groupName = (String) extras.get("Group Name");

        binding.textUsername.setText(groupName);
        binding.avatarUser.setImageResource(R.drawable.ic_groups);
        conversationId = (Long) extras.get("Conversation Id");

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

                // load mess
                try {
                    List<Message> messageList = daoFactory.getMessageDAO().getListMessages(conversationId);
                    messageUtils = convertListMessage(messageList);
                    userList = getUserListOfConversation(conversationId);
                } catch (Exception e){
                    e.printStackTrace();
                }
                markAsRead();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.progressBar.setVisibility(View.GONE);
                        // set adapter
                        LinearLayoutManager manager = new LinearLayoutManager(getContext());
                        manager.setReverseLayout(true);
                        manager.setStackFromEnd(false);
                        //
                        mMessageRecycler = (RecyclerView) findViewById(R.id.listMessage);
                        mMessageAdapter = new MessageThreadAdapter(getContext(), messageUtils);
                        mMessageRecycler.setAdapter(mMessageAdapter);

                        mMessageRecycler.setLayoutManager(manager);
                        binding.listMessage.smoothScrollToPosition(0);
                    }
                });
            }
        });




        // handle send message
        binding.btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = binding.textInputMessage.getText().toString();

                sendMessageToGroup(message);
            }
        });

        binding.btnBackScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        EmojiManager.install(new GoogleEmojiProvider());
        EmojiPopup popup = EmojiPopup.Builder.fromRootView(binding.chatScreen).build(binding.textInputMessage);
        binding.btnAddIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.toggle();
            }
        });

        binding.btnMoreDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatRoomBoxActivity.this, DetailConversationActivity.class);
                intent.putExtra("id_conversation", conversationId);
                intent.putExtra("name_detail_user", groupName);
                startActivity(intent);
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
                Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnVideocall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void sendMessageToGroup(String message) {
        if (message.length() == 0){
            Toast.makeText(getApplicationContext(),getApplicationContext().getString(R.string.pls_enter_msg), Toast.LENGTH_SHORT).show();
        } else if (message.length() < 255) {
            if (isBound) {
                if (checkDelete()) {
                    updateChatMessages(message,
                            preferenceManager.getString(Constants.USER_NAME),
                            groupName,
                            createTimeStamp(LocalDateTime.now()),
                            LocationMessage.RIGHT,
                            "Delivered",
                            TypeMessage.MSG);

                    // Send message
                    saveMessageToDb(message, TypeMessage.MSG);
                    boolean checkUser = true;
                    for (User user : userList) {
                        if (!Objects.equals(user.getId(), myself.getId())) {
                            if (updateUserNotRead(user.getId())) {
                                linphoneService.sendMessage(user.getUsername(), conversationId);
                            } else {
                                checkUser = false;
                            }
                        }
                    }

                    // update userList if 1 user is deleted
                    if (!checkUser) {
                        userList = getUserListOfConversation(conversationId);
                    }

                    binding.textInputMessage.setText("");

                    chatFragment.onActivityChange(conversationId);
                } else {
                    Toast.makeText(getApplicationContext(),
                            getApplicationContext().getString(R.string.you_was_deleted) + " "+ groupName,
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        "Error on send a message to " + groupName + ".Please try again!",
                        Toast.LENGTH_SHORT).show();
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
                        if (checkDelete()) {
                            updateChatMessages(encodedImage,
                                    preferenceManager.getString(Constants.USER_NAME),
                                    groupName,
                                    createTimeStamp(LocalDateTime.now()),
                                    LocationMessage.RIGHT,
                                    "Delivered",
                                    TypeMessage.IMG);

                            // Send message
                            saveMessageToDb(encodedImage, TypeMessage.IMG);
                            boolean checkUser = true;
                            for (User user : userList) {
                                if (user.getId() != myself.getId()) {
                                    if (updateUserNotRead(user.getId())) {
                                        linphoneService.sendMessage(user.getUsername(), conversationId);
                                    } else {
                                        checkUser = false;
                                    }
                                }
                            }

                            // update userList if 1 user is deleted
                            if (!checkUser) {
                                userList = getUserListOfConversation(conversationId);
                            }

                            binding.textInputMessage.setText("");

                            chatFragment.onActivityChange(conversationId);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    getApplicationContext().getString(R.string.you_was_deleted) + groupName, Toast.LENGTH_SHORT).show();
                        }
                    } catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    );

    private boolean checkDelete() {
        boolean result = false;
        try {
            result = daoFactory.getParticipantsDAO().checkUserInConversation(myself.getId(), conversationId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private boolean updateUserNotRead(Long userId) {
        boolean result = false;
        try {
            if (daoFactory.getParticipantsDAO().checkUserInConversation(userId, conversationId)) {
                daoFactory.getParticipantsDAO().updateIsRead(userId, conversationId, false);
                Log.i("Update DB", "Mark not read" + userId);
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private void saveMessageToDb(String message, TypeMessage typeMessage) {
        try {
            User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));
            String type;
            if(typeMessage.equals(TypeMessage.IMG))
                type = "IMAGE";
            else if(typeMessage.equals(TypeMessage.CALL))
                type = "CALL";
            else type = "MESSAGE";

            Message msg = daoFactory.getUserDAO().insertMessage(myself.getId(), conversationId, message, type);
            Conversation conversationUpdate = daoFactory.getConversationDAO().getInfoConversation(conversationId);
            if(msg.getIsType().equals("IMAGE")){
                conversationUpdate.setLast_message("IMAGE");
            } else {
                conversationUpdate.setLast_message(msg.getContent());
            }
            conversationUpdate.setLast_message_id(msg.getId());
            daoFactory.getConversationDAO().updateConversation(conversationUpdate);

            //Conversation conversation = userDAO.newConversation("hello", null);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            linphoneService.setChatRoomBoxActivity(null);
//            unbindService(serviceConnection);
//            isBound = false;
        }
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

    private int count;

    @Override
    public void onMessageChange(ChatMessage chatMessage, LocationMessage locationMessage) {

        // load mess
        try {
            if (locationMessage != LocationMessage.RIGHT) {

                Conversation conversation = daoFactory.getConversationDAO().getInfoConversation(conversationId);
                groupName = conversation.getName_conversation();
                binding.textUsername.setText(groupName);
                Message msg = daoFactory.getMessageDAO().getInfoMessage(conversation.getLast_message_id());

                if (msg.getIsType().equals("NOTIFY")) {
                    locationMessage = LocationMessage.CENTER;
                }

                TypeMessage type;

                if(msg.getIsType().equals("IMAGE"))
                    type = TypeMessage.IMG;
                else if(msg.getIsType().equals("CALL"))
                    type = TypeMessage.CALL;
                else type = TypeMessage.MSG;

                updateChatMessages(msg.getContent(),
                        chatMessage.getFromAddress().getUsername(),
                        groupName, createTimeStamp(LocalDateTime.now()),
                        locationMessage, "",
                        type);
                markAsRead();
                userList = getUserListOfConversation(conversationId);
                chatFragment.markAsRead(conversationId);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
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
                            arrayList.add(new MessageUtil(myself.getUsername(),user.getUsername(),list.get(i).getContent(),date,LocationMessage.RIGHT,"Delivered",list.get(i).getId().toString(), typeMessage));
                        } else {
                            arrayList.add(new MessageUtil(user.getUsername(),myself.getUsername(),list.get(i).getContent(),date,LocationMessage.LEFT,"Delivered",list.get(i).getId().toString(), typeMessage));
                        }
                }
            }

            return arrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    private List<User> getUserListOfConversation(Long idCon) {
        List<User> result = new ArrayList<>();

        try {
            myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));
            List<Participant> participantList = daoFactory.getParticipantsDAO().getListUserOfConversation(idCon);

            for (int i = 0; i < participantList.size(); i++) {
                User user = daoFactory.getUserDAO().getInfoUser(participantList.get(i).getUser_id());
                result.add(user);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }


}