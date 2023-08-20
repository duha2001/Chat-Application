package com.example.d_five.activities;

import static com.example.d_five.PagerAdapter.chatFragment;

import static org.linphone.mediastream.MediastreamerAndroidContext.getContext;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d_five.R;
import com.example.d_five.adapter.MessageThreadAdapter;
import com.example.d_five.adapter.UserAdapter;
import com.example.d_five.dao.ConnectionDB;
import com.example.d_five.dao.DAOFactory;
import com.example.d_five.dao.implementDAO.ContactsDAO;
import com.example.d_five.dao.implementDAO.ConversationDAO;
import com.example.d_five.dao.implementDAO.ParticipantsDAO;
import com.example.d_five.dao.implementDAO.UserDAO;
import com.example.d_five.databinding.DetailConservationBinding;
import com.example.d_five.listeners.ItemClickListener;
import com.example.d_five.model.Contacts;
import com.example.d_five.model.Conversation;
import com.example.d_five.model.Message;
import com.example.d_five.model.Participant;
import com.example.d_five.model.User;
import com.example.d_five.services.LinphoneService;
import com.example.d_five.utilities.Constants;
import com.example.d_five.utilities.ImageHandler;
import com.example.d_five.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailConversationActivity extends AppCompatActivity {
    private DetailConservationBinding binding;

    // database
    private PreferenceManager preferenceManager;
    private DAOFactory daoFactory;

    private Long conversationId;
    ArrayList<User> listParticipantUser;
    ArrayList<User> listContacts;
    private ImageHandler imageHandler;

    private LinphoneService linphoneService;
    private boolean isBound;

    /** Defines callbacks for service binding, passed to bindService(). */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
            linphoneService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this.getApplicationContext(), LinphoneService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void renameGroupDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(DetailConversationActivity.this);
        final View inflator = layoutInflater.inflate(R.layout.change_name_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailConversationActivity.this);
        builder.setView(inflator);

        EditText inputNewName = inflator.findViewById(R.id.inputNewName);
        Button btnChange = inflator.findViewById(R.id.btnChange);
        Button btnCancel = inflator.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnChange.setOnClickListener(view1 -> {
            String newName = inputNewName.getText().toString();

            if(newName.trim().isEmpty()){
                Toast.makeText(getApplicationContext(), "Please Enter name !", Toast.LENGTH_SHORT).show();
            } else {
                renameGroup(newName);
                chatFragment.onActivityChange(conversationId);
                dialog.dismiss();
            }

        });


        // button cancel
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = DetailConservationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        conversationId = (Long) extras.get("id_conversation");
        String usernameChat = (String) extras.get("name_detail_user");



        // init
        preferenceManager = new PreferenceManager(getApplicationContext());
        daoFactory = new DAOFactory(ConnectionDB.connection);
        imageHandler = new ImageHandler(getApplicationContext());

        try {
            User user = daoFactory.getUserDAO().getInfoUser(usernameChat);


            // set init UI
            binding.displayUsername.setText(usernameChat);
            binding.displayAvatarUser.setImageBitmap(imageHandler.decodeResource(user.getAvatar()));

            // load data
            checkIsGroup(conversationId);
            dataParticipants();
            dataContacts();
        } catch (Exception e) {
            e.printStackTrace();
        }



        binding.btnBackScreen.setOnClickListener(v -> onBackPressed());


        // Button change name
        binding.btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkGroup(conversationId)) {
                    renameGroupDialog();
                } else {
                    Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.btnAddParticipate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(DetailConversationActivity.this);
                final View inflator = layoutInflater.inflate(R.layout.add_new_participate_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailConversationActivity.this);
                builder.setView(inflator);

                SearchView inputNewName = inflator.findViewById(R.id.inputUsername);
                Button btnAdd = inflator.findViewById(R.id.btn_add);
                Button btnCancel = inflator.findViewById(R.id.btn_cancel);
                ProgressBar progressBar = (ProgressBar) inflator.findViewById(R.id.progressBar);
                RecyclerView recyclerView = inflator.findViewById(R.id.listContact);

                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setHasFixedSize(true);


                final ArrayList<User> selectListAdd = new ArrayList<>();

                // getlist data from UI
                ItemClickListener itemClickListener = new ItemClickListener() {
                    @Override
                    public void onSelectUserChange(ArrayList<User> arrayList) {
                        selectListAdd.clear();
                        selectListAdd.addAll(arrayList);
                    }
                };

                UserAdapter userAdapter = new UserAdapter(getApplicationContext(), listContacts, itemClickListener);
                recyclerView.setAdapter(userAdapter);
                userAdapter.notifyDataSetChanged();

                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // button add
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isBound) {


                            ExecutorService service = Executors.newSingleThreadExecutor();
                            service.execute(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.VISIBLE);
                                            btnAdd.setVisibility(View.GONE);
                                            btnCancel.setVisibility(View.GONE);

                                        }
                                    });

                                    // add database
                                    addParticipant(selectListAdd);
                                    chatFragment.onActivityChange(conversationId);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.add_success), Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            });


                        }
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });

        binding.btnDeleteParticipate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(DetailConversationActivity.this);
                final View inflator = layoutInflater.inflate(R.layout.delete_participate_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailConversationActivity.this);
                builder.setView(inflator);

                SearchView inputNewName = inflator.findViewById(R.id.inputUsername);
                Button btnDelete = inflator.findViewById(R.id.btn_delete);
                Button btnCancel = inflator.findViewById(R.id.btn_cancel);
                RecyclerView recyclerView = inflator.findViewById(R.id.listContact);
                ProgressBar progressBar = (ProgressBar) inflator.findViewById(R.id.progressBar);


                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setHasFixedSize(true);


                final ArrayList<User> selectUserDelete = new ArrayList<>();

                // listener
                ItemClickListener itemClickListener = new ItemClickListener() {
                    @Override
                    public void onSelectUserChange(ArrayList<User> arrayList) {
                        selectUserDelete.clear();
                        selectUserDelete.addAll(arrayList);
                    }
                };

                boolean isHide = false;

                if(!checkAdminGroup()){
                    btnDelete.setVisibility(View.GONE);
                    isHide = true;

                }
                UserAdapter userAdapter = new UserAdapter(getApplicationContext(), listParticipantUser, itemClickListener, isHide);
                recyclerView.setAdapter(userAdapter);
                userAdapter.notifyDataSetChanged();

                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (checkDeleteParticipant(selectUserDelete)) {
                            ExecutorService service = Executors.newSingleThreadExecutor();
                            service.execute(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.VISIBLE);
                                            btnDelete.setVisibility(View.GONE);
                                            btnCancel.setVisibility(View.GONE);

                                        }
                                    });

                                    // add database
                                    deleteParticipant(selectUserDelete);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                                            chatFragment.onActivityChange(conversationId);
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            });


                        } else {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.cant_delete), Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });

        binding.btnLeaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkGroup(conversationId)) {
                    if (isBound && leaveGroup()) {

                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error on leave group. Please try again!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkGroup(Long conversationId) {
        boolean result = false;

        try {
            Conversation conversation = daoFactory.getConversationDAO().getInfoConversation(conversationId);
            if (conversation.isGroup()) {
                result = true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private void renameGroup(String newName) {
        try {
            binding.displayUsername.setText(newName);
            Conversation newConversation = daoFactory.getConversationDAO().getInfoConversation(conversationId);
            newConversation.setName_conversation(newName);
            Conversation updateConversation = daoFactory.getConversationDAO().updateConversation(newConversation);

            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.update_name_success), Toast.LENGTH_SHORT).show();

//                                System.out.println("Update name: " + updateConversation.getName_conversation());
            User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));
            saveNotification(myself.getUsername() + " " + getApplicationContext().getString(R.string.named_group) + " " + newName, false);
            for (User user : listParticipantUser) {
                if (user.getId() != myself.getId() && isBound)  {
                    daoFactory.getParticipantsDAO().updateIsRead(user.getId(), conversationId, false);
                    linphoneService.sendMessage(user.getUsername(), conversationId);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveNotification(String message, boolean isImage) {
        try {
            User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));
            Message msg = daoFactory.getUserDAO().insertMessage(myself.getId(), conversationId, message, "NOTIFY");
            Conversation conversationUpdate = daoFactory.getConversationDAO().getInfoConversation(conversationId);
            if(msg.getIsType().equals("IMAGE")) {
                conversationUpdate.setLast_message("IMAGE!!!");
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

    private boolean leaveGroup() {
        boolean result = false;

        try {
            User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));
            daoFactory.getParticipantsDAO().deleteParticipant(myself.getId(), conversationId);

            String notify = myself.getUsername() + " " + getApplicationContext().getString(R.string.leave_group);
            saveNotification(notify, false);

            for (User user : listParticipantUser) {
                if (!Objects.equals(user.getId(), myself.getId()))  {
                    daoFactory.getParticipantsDAO().updateIsRead(user.getId(), conversationId, false);
                    linphoneService.sendMessage(user.getUsername(), conversationId);
                }
            }

            chatFragment.deleteConversation(conversationId);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private boolean checkDeleteParticipant(ArrayList<User> selectUsers) {
        boolean result = true;
        try {
            User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));
            for (User user : selectUsers) {
                if (Objects.equals(user.getId(), myself.getId()))  {
                    result = false;
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }


    private void deleteParticipant(ArrayList<User> selectUserDelete) {
        try {
            User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));

            StringBuilder stringBuilder = new StringBuilder(myself.getUsername() +
                    " removed " +
                    selectUserDelete.get(0).getUsername());
            daoFactory.getParticipantsDAO().deleteParticipant(selectUserDelete.get(0).getId(), conversationId);
            listParticipantUser.remove(selectUserDelete.get(0));
            listContacts.add(selectUserDelete.get(0));
            for (int i = 1; i < selectUserDelete.size(); i++) {
                daoFactory.getParticipantsDAO().deleteParticipant(selectUserDelete.get(i).getId(), conversationId);
                stringBuilder.append(", ").append(selectUserDelete.get(i).getUsername());
                listParticipantUser.remove(selectUserDelete.get(i));
                listContacts.add(selectUserDelete.get(i));
            }
            stringBuilder.append(" ").append(getApplicationContext().getString(R.string.out_group));
            saveNotification(stringBuilder.toString(), false);

            for (User user : listParticipantUser) {
                if (user.getId() != myself.getId() && isBound)  {
                    daoFactory.getParticipantsDAO().updateIsRead(user.getId(), conversationId, false);
                    linphoneService.sendMessage(user.getUsername(), conversationId);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void checkIsGroup(Long id_conversation) throws Exception{
        Conversation conversation = daoFactory.getConversationDAO().getInfoConversation(id_conversation);
        if(conversation.isGroup()){
            ViewGroup.LayoutParams layoutParams_delete = binding.btnDeleteParticipate.getLayoutParams();
            ViewGroup.LayoutParams layoutParams_add = binding.btnAddParticipate.getLayoutParams();
            layoutParams_add.height = layoutParams_delete.height = 150;
            binding.btnAddParticipate.setLayoutParams(layoutParams_add);
            binding.btnDeleteParticipate.setLayoutParams(layoutParams_delete);
            binding.displayAvatarUser.setImageResource(R.drawable.ic_groups);
        }
    }

    private void dataParticipants() throws Exception{
        listParticipantUser = new ArrayList<>();
        List<Participant> participantList = daoFactory.getParticipantsDAO().getListUserOfConversation(conversationId);
        for (int i = 0; i < participantList.size(); i++) {
            User user = daoFactory.getUserDAO().getInfoUser(participantList.get(i).getUser_id());
            listParticipantUser.add(user);
        }

    }

    private void addParticipant(ArrayList<User> list){
        try {
            User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));

            StringBuilder stringBuilder = new StringBuilder(myself.getUsername() + " " +
                    getApplicationContext().getString(R.string.added) + " " +
                    list.get(0).getUsername());
            listParticipantUser.add(list.get(0));
            listContacts.remove(list.get(0));
            daoFactory.getUserDAO().insertParticipant(list.get(0).getId(), conversationId);
            for (int i = 1; i < list.size(); i++) {
                User user = list.get(i);
                stringBuilder.append(", ").append(user.getUsername());
                daoFactory.getUserDAO().insertParticipant(user.getId(), conversationId);
                listParticipantUser.add(user);
                listContacts.remove(list.get(i));

            }
            stringBuilder.append(" ")
                    .append(getApplicationContext()
                    .getString(R.string.to_group));
            saveNotification(stringBuilder.toString(), false);

            for (User user : listParticipantUser) {
                if (!Objects.equals(user.getId(), myself.getId())) {
                    daoFactory.getParticipantsDAO().updateIsRead(user.getId(), conversationId, true);
                    linphoneService.sendMessage(user.getUsername(), conversationId);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void dataContacts() throws Exception{
        listContacts = new ArrayList<>();

        try {
            User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));
            ArrayList<Contacts> list = daoFactory.getContactsDAO().getListContacts(myself.getId());

            for (int i = 0; i < list.size(); i++) {
                User user = daoFactory.getUserDAO().getInfoUser(list.get(i).getUser_contact_id());
                Optional<User> userFound = findUser(listParticipantUser, user.getId());

                if(!userFound.isPresent()){
                   listContacts.add(user);
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private final Optional<User> findUser(Collection<User> yourList, Long codeIsin){
        // This stream will simply return any carnet that matches the filter. It will be wrapped in a Optional object.
        // If no carnets are matched, an "Optional.empty" item will be returned
        return yourList.stream().filter(c -> c.getId().equals(codeIsin)).findAny();
    }

    private boolean checkAdminGroup() {

        boolean check = false;
        try {
            User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));

            Conversation conversation = daoFactory.getConversationDAO().getInfoConversation(conversationId);
            // check
            if(myself.getId() == conversation.getAdmin_id()) check = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return check;
    }

}
