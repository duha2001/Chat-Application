package com.example.d_five.dialogs;

import static com.example.d_five.PagerAdapter.chatFragment;
import static com.example.d_five.fragments.ContactFragment.contactAdapter;
import static com.example.d_five.fragments.ContactFragment.listContact;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.d_five.adapter.ContactInDbAdapter;
import com.example.d_five.dao.ConnectionDB;
import com.example.d_five.dao.implementDAO.ContactsDAO;
import com.example.d_five.dao.implementDAO.ConversationDAO;
import com.example.d_five.dao.implementDAO.UserDAO;
import com.example.d_five.model.Contacts;
import com.example.d_five.model.Conversation;
import com.example.d_five.model.User;
import com.example.d_five.utilities.Constants;
import com.example.d_five.utilities.PreferenceManager;
import com.example.d_five.databinding.AddContactDialogBinding;

import java.util.ArrayList;

public class AddNewAccountDialog extends AppCompatActivity {
    Context context;
    public static AddContactDialogBinding binding;
    ArrayList<User> listContactDb;
    ArrayList<User> users;
    private PreferenceManager preferenceManager;
    private UserDAO newUser;
    private ContactsDAO newContact;
    private UserDAO userDAO;
    private ContactInDbAdapter contactDbAdapter;

    @SuppressLint("NotifyDataSetChanged")
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = AddContactDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());
        newUser = new UserDAO(ConnectionDB.connection);
        newContact = new ContactsDAO(ConnectionDB.connection);
        userDAO = new UserDAO(ConnectionDB.connection);
        listContactDb = new ArrayList<>();

        try {
            dataContact();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        binding.listContact.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.listContact.setHasFixedSize(true);
        contactDbAdapter = new ContactInDbAdapter(getApplicationContext(), listContactDb);
        binding.listContact.setAdapter(contactDbAdapter);
        contactDbAdapter.notifyDataSetChanged();

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewContact();
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.inputUsername.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    filterList(newText);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
        });
        binding.layoutAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });
    }
    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view!= null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    private void dataContact() throws Exception {
        users = userDAO.getAll();
        for(int i = 0; i < users.size(); i++){
            User user = users.get(i);
            listContactDb.add(user);
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private void addNewContact(){
        try {
            User user_myself = newUser.getInfoUser(preferenceManager.getString(Constants.USER_NAME));
            User user_contact = newUser.getInfoUser(binding.inputUsername.getQuery().toString());

            if (preferenceManager.getString(Constants.USER_NAME).equals(binding.inputUsername.getQuery().toString())){
                Toast.makeText(getApplicationContext(), "It's you", Toast.LENGTH_SHORT).show();
            }
            else{
                if(user_contact.getId() != null) {
                    if (newContact.checkContact(user_myself.getId(), user_contact.getId())){
                        Toast.makeText(getApplicationContext(), "Account already exists", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Contacts contact = new Contacts();
                        contact.setUser_id(user_myself.getId());
                        contact.setUser_contact_id(user_contact.getId());
                        contact.setStatus("null");
                        listContact.add(contact);
                        contactAdapter.notifyDataSetChanged();
                        newContact.insertContact(contact);

                        // Create new conversation
                        ConversationDAO conversationDAO = new ConversationDAO(ConnectionDB.connection);
                        Long conversationID = conversationDAO.checkExistConversation2P(user_myself.getId(), user_contact.getId());

                        if(conversationID == null ) {
                            Conversation conversation = userDAO.newConversation(null, null, null, false, user_myself.getId());

                            // Add participants
                            userDAO.insertParticipant(user_myself.getId(), conversation.getId());
                            userDAO.insertParticipant(user_contact.getId(), conversation.getId());

                            chatFragment.onActivityChange(conversation.getId());
                            Log.i("Add contact", conversation.getId().toString());
                        }

                        Toast.makeText(getApplicationContext(), "Add new contact successfully", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Not exits user", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void filterList(String newText) throws Exception {
        ArrayList<User> filteredList = new ArrayList<>();
        for (User user : userDAO.getAll()){
            if (user.getUsername().contains(newText.toLowerCase()))
                filteredList.add(user);
        }

        if (filteredList.isEmpty())
            Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show();
        else
            contactDbAdapter.setFilteredList(filteredList);
    }

}
