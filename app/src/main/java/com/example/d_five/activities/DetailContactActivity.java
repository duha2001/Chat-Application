package com.example.d_five.activities;


import static com.example.d_five.fragments.ContactFragment.contactAdapter;
import static com.example.d_five.fragments.ContactFragment.listContact;

import static org.linphone.mediastream.MediastreamerAndroidContext.getContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d_five.CallHistory;
import com.example.d_five.R;
import com.example.d_five.adapter.CallAdapter;
import com.example.d_five.adapter.MessageThreadAdapter;
import com.example.d_five.dao.ConnectionDB;
import com.example.d_five.dao.DAOFactory;
import com.example.d_five.dao.implementDAO.CallDAO;
import com.example.d_five.dao.implementDAO.ContactsDAO;
import com.example.d_five.dao.implementDAO.ConversationDAO;
import com.example.d_five.dao.implementDAO.ParticipantsDAO;
import com.example.d_five.dao.implementDAO.UserDAO;
import com.example.d_five.databinding.DetailInformationUserBinding;
import com.example.d_five.model.Contacts;
import com.example.d_five.model.Conversation;
import com.example.d_five.model.Message;
import com.example.d_five.model.User;
import com.example.d_five.services.CallService;
import com.example.d_five.utilities.Constants;
import com.example.d_five.utilities.ImageHandler;
import com.example.d_five.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailContactActivity extends AppCompatActivity {
    private static DetailInformationUserBinding binding;

    // database
    private PreferenceManager preferenceManager;
    private DAOFactory daoFactory;

    ArrayList<Contacts> arrayContact;
    private ImageHandler imageHandler;
    private RecyclerView recyclerView;
    private CallAdapter callAdapter;
    private ArrayList<CallHistory> callHistories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = DetailInformationUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        String usernameContact = (String) extras.get("name_detail_user");


        // init
        preferenceManager = new PreferenceManager(getApplicationContext());
        daoFactory = new DAOFactory(ConnectionDB.connection);
        imageHandler = new ImageHandler(getApplicationContext());

        User u = new User();
        try {
            u = daoFactory.getUserDAO().getInfoUser(usernameContact);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        ExecutorService service = Executors.newSingleThreadExecutor();
        User finalU = u;
        service.execute(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.progressBar.setVisibility(View.VISIBLE);
                    }
                });
                try {
                    // load data
                    dataInitialize(finalU.getId());


                } catch (Exception e) {
                    e.printStackTrace();
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.progressBar.setVisibility(View.GONE);
                        // set adapter

                        binding.listRecentCall.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        binding.listRecentCall.setHasFixedSize(true);

                        callAdapter = new CallAdapter(getApplicationContext(), callHistories);
                        binding.listRecentCall.setAdapter(callAdapter);
                    }
                });
            }
        });

        // set UI
        binding.displayImpu.setText("sip:"+ usernameContact +"@dfive-ims.dek.vn");
        binding.displayUsername.setText(usernameContact);
        binding.dispalyAvatarUser.setImageBitmap(imageHandler.decodeResource(finalU.getAvatar()));



//        try {
//            User u = daoFactory.getUserDAO().getInfoUser(usernameContact);
//            // load data
//            dataInitialize(u.getId());
//
//            // set UI
//            binding.displayImpu.setText("sip:"+ usernameContact +"@dfive-ims.dek.vn");
//            binding.displayUsername.setText(usernameContact);
//            binding.dispalyAvatarUser.setImageBitmap(imageHandler.decodeResource(u.getAvatar()));
//
//            binding.listRecentCall.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//            binding.listRecentCall.setHasFixedSize(true);
//
//            callAdapter = new CallAdapter(getApplicationContext(), callHistories);
//            binding.listRecentCall.setAdapter(callAdapter);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        // Set Onclick
        binding.btnBackScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.btnDeleteUser.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                try {
                    User contactDelete = daoFactory.getUserDAO().getInfoUser((String) binding.displayUsername.getText());
                    //Remove on UI
                    for (Contacts contacts : listContact) {
                        if (contacts.getUser_contact_id() == contactDelete.getId()) {
                            listContact.remove(contacts);
                            contactAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                    //Remove on database
                    if (!daoFactory.getContactsDAO().deleteContact(contactDelete.getId())){
                        Toast.makeText(getApplicationContext(), "Delete contact successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Delete contact unsuccessfully", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        binding.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // create new conversation
                try {

                    User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));
                    User peer = daoFactory.getUserDAO().getInfoUser((String) binding.displayUsername.getText());

                    // check for conversation
                    Long conID = daoFactory.getConversationDAO().checkExistConversation2P(myself.getId(),peer.getId());
                    Intent intent = new Intent(getApplicationContext(), ChatBoxActivity.class);
                    intent.putExtra("name_user", binding.displayUsername.getText());

                    if(conID == null){
                        Conversation conversation = daoFactory.getUserDAO().newConversation(null,null, null, false, myself.getId()) ;
                        daoFactory.getUserDAO().insertParticipant(myself.getId(), conversation.getId());
                        daoFactory.getUserDAO().insertParticipant(peer.getId(), conversation.getId());
                        intent.putExtra("conversation_id", conversation.getId());
                        System.out.println("new conversation");
                    } else {
                        intent.putExtra("conversation_id", conID);
                        System.out.println("existed conversation");
                    }

                    startActivity(intent);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        binding.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallService.makeVoiceCall(DetailContactActivity.this, (String) extras.get("name_detail_user"));
            }
        });

        binding.btnVideocall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallService.makeVideoCall(DetailContactActivity.this, (String) extras.get("name_detail_user"));
            }
        });
    }

    private void dataInitialize(Long user_contact_id) {
        callHistories = new ArrayList<>();

        try {
            // Get info my self
            User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));

            // get list history call
            callHistories = (ArrayList<CallHistory>) daoFactory.getCallDAO().getListHistoryCallContact(myself.getId(), user_contact_id);


        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
