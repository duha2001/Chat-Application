package com.example.d_five.fragments;

import static com.example.d_five.model.Default.createTimeStamp;

import static org.linphone.mediastream.MediastreamerAndroidContext.getContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.d_five.ConversationViewChat;
import com.example.d_five.R;
import com.example.d_five.activities.ChatRoomBoxActivity;
import com.example.d_five.activities.MainActivity;
import com.example.d_five.adapter.ChatAdapter;
import com.example.d_five.adapter.MessageThreadAdapter;
import com.example.d_five.adapter.UserAdapter;
import com.example.d_five.dao.ConnectionDB;
import com.example.d_five.dao.DAOFactory;
import com.example.d_five.dao.implementDAO.MessageDAO;
import com.example.d_five.listeners.ItemClickListener;
import com.example.d_five.model.Contacts;
import com.example.d_five.model.Conversation;
import com.example.d_five.model.Message;
import com.example.d_five.model.Participant;
import com.example.d_five.model.User;
import com.example.d_five.services.LinphoneService;
import com.example.d_five.utilities.Constants;
import com.example.d_five.utilities.PreferenceManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {


    // Database
    private PreferenceManager preferenceManager;
    private DAOFactory daoFactory;


    // view list chat
    private List<ConversationViewChat> conversationViewChats ;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<User> users;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private AppCompatImageView appCompatImageView;
    private TextView chatRoomName, chatTitle;
    MainActivity mainActivity;
    // create group
    ItemClickListener itemClickListener;
    UserAdapter userAdapter;
    LinphoneService linphoneService;
    private User myself;
    private List<Participant> participantList;
    View rootView;
    ChatAdapter chatAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        preferenceManager = new PreferenceManager(getContext());
        daoFactory = new DAOFactory(ConnectionDB.connection);
        conversationViewChats = new ArrayList<>();
        dataInitialize();
        Log.i("CHAT", "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("CHAT", "ondestroy");
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mainActivity = (MainActivity) this.getContext();
        assert mainActivity != null;
        linphoneService = mainActivity.getLinphoneService();
        rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        searchView = rootView.findViewById(R.id.searchView);
        recyclerView = rootView.findViewById(R.id.chatsRecyclerView);
        appCompatImageView = rootView.findViewById(R.id.create_group_btn);
        chatRoomName = rootView.findViewById((R.id.name_group));
        chatTitle = rootView.findViewById(R.id.chats);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefresh);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                getContext(),R.style.BottomSheetDialogTheme
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        appCompatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetBehavior<View> bottomSheetBehavior;

                View bottomSheetView = LayoutInflater.from(getContext())
                        .inflate(
                                R.layout.bottom_sheet_layout,
                                null
                        );
                bottomSheetDialog.setContentView(bottomSheetView);

                bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                CoordinatorLayout layout = bottomSheetDialog.findViewById(R.id.bottom_sheet_container);
                assert layout != null;
                layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);

                //bottomSheetDialog.show();
                // array your choice
                final ArrayList<User> arrayListYourChoice = new ArrayList();

                ExecutorService service = Executors.newSingleThreadExecutor();
                service.execute(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bottomSheetDialog.findViewById(R.id.progressBarCreateGroup).setVisibility(View.VISIBLE);
                            }
                        });

                        // load data
                        dataInitializeForUser();


                        itemClickListener = new ItemClickListener() {
                            @Override
                            public void onSelectUserChange(ArrayList<User> arrayList) {

                                // arraylist your choice
                                arrayListYourChoice.clear();
                                arrayListYourChoice.addAll(arrayList);
                            }
                        };
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bottomSheetDialog.findViewById(R.id.progressBarCreateGroup).setVisibility(View.GONE);
                                // set adapter
                                RecyclerView listUserRecycler = bottomSheetView.findViewById(R.id.recyclerView_list_user_group);
                                listUserRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                                listUserRecycler.setHasFixedSize(true);
                                userAdapter = new UserAdapter(getContext(),users, itemClickListener);
                                listUserRecycler.setAdapter(userAdapter);
                            }
                        });
                    }
                });

                // cancel click
                bottomSheetView.findViewById(R.id.cancel_action).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });

                // create click
                bottomSheetView.findViewById(R.id.create_action).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (arrayListYourChoice.size() >= 2) {

                            EditText nameGroup = bottomSheetView.findViewById(R.id.name_group);
                            bottomSheetDialog.hide();

                            // new conversation
                            Conversation conversation = new Conversation();
                            try {
                                User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));

                                // get name group
                                if (nameGroup.getText().toString().equals("")) {
                                    StringBuilder stringBuilder = new StringBuilder(myself.getUsername());
                                    for (User user : arrayListYourChoice) {
                                        stringBuilder.append(", ").append(user.getUsername());
                                    }
                                    nameGroup.setText(stringBuilder.toString());
                                }

                                conversation = daoFactory.getUserDAO().newConversation(null, null, nameGroup.getText().toString(), true, myself.getId());

                                daoFactory.getUserDAO().insertParticipant(myself.getId(), conversation.getId()); // insert my self
                                for (int i = 0; i < arrayListYourChoice.size(); i++) {
                                    daoFactory.getUserDAO().insertParticipant(arrayListYourChoice.get(i).getId(), conversation.getId());
                                }

                                onActivityChange(conversation.getId());

                                // switch new activity
                                Intent intent = new Intent(getContext(), ChatRoomBoxActivity.class);
                                intent.putExtra("Group Name", nameGroup.getText().toString());
                                intent.putExtra("Conversation Id", conversation.getId());
                                getContext().startActivity(intent);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getContext(), "You must at least 2 participants", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                // show dialog
                bottomSheetDialog.show();
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    hideKeyboard();
                }
            }

        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });
        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });
        return rootView;
    }

    private void filterList(String newText) throws Exception {
        ArrayList<ConversationViewChat> filteredList = new ArrayList<>();
        for (ConversationViewChat conversationViewChat : conversationViewChats){
            if (conversationViewChat.getName().contains(newText))
                filteredList.add(conversationViewChat);
                //System.out.println(nameConversation);}

            if (filteredList.isEmpty())
                Toast.makeText(getContext(), "No conversation found", Toast.LENGTH_SHORT).show();
            else
                chatAdapter.setFilteredList(filteredList);
        }
    }

    private void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = requireActivity().getCurrentFocus();
        if(focusedView != null){
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
            focusedView.clearFocus();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.chatsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);


        chatAdapter = new ChatAdapter(getContext(), conversationViewChats);
        recyclerView.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
    }

    static class SortByTime implements Comparator<ConversationViewChat> {
        @Override
        public int compare(ConversationViewChat a, ConversationViewChat b) {
            int result = 1;
            if (b.getTimeTime() == null || a.getTimeTime() == null) {
                Log.i("Sort", "Time conversation view chat is null");
            } else {
                result = b.getTimeTime().compareTo(a.getTimeTime());
            }

            return result;
        }
    }

    public void dataInitialize() {
        try {

            // get info my self
            User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));

            // get List participant
            participantList = daoFactory.getParticipantsDAO().getListConversationOfUser(myself.getId());

            for(int i = 0; i < participantList.size(); i ++ ) {

                Conversation conversation = daoFactory.getConversationDAO().getInfoConversation(participantList.get(i).getConversation_id());

                System.out.println(conversation.getLast_message_id());
                if (conversation.getLast_message_id() == 0) {
                    continue;
                }

                // create one view conversation chat
                ConversationViewChat conversationViewChat = new ConversationViewChat();
                conversationViewChat.setConversation_id(conversation.getId());

                Participant participant = daoFactory.getParticipantsDAO().getInfoParticipant(myself.getId(), conversation.getId());
                conversationViewChat.setRead(participant.getIs_read());

                if(conversation.getLast_message_id() != null && conversation.getLast_message_id() != 0){
                    Message msg = new MessageDAO(ConnectionDB.connection).getInfoMessage(conversation.getLast_message_id());
                    if(msg.getIsType().equals("IMAGE")){
                        conversationViewChat.setLast_message("IMAGE!!");
                    } else {
                        conversationViewChat.setLast_message(msg.getContent());
                    }
                    conversationViewChat.setTime(msg.getCreated_at());
                } else {
                    conversationViewChat.setLast_message(null);
                    conversationViewChat.setTime(null);
                }

                if(conversation.isGroup()) {
                    // group chat
                    conversationViewChat.setName(conversation.getName_conversation());
                } else {
                    List<Participant> userListOfConversation = daoFactory.getParticipantsDAO().getListUserOfConversation(conversation.getId());
                    // get Name of User
                    for(int j = 0; j < userListOfConversation.size(); j ++ ){
                        Long idUserSelect = userListOfConversation.get(j).getUser_id();
                        if(idUserSelect != myself.getId()){
                            try {
                                User infoUserSelect = daoFactory.getUserDAO().getInfoUser(idUserSelect);
                                conversationViewChat.setName(infoUserSelect.getUsername());
                                conversationViewChat.setAvatar(infoUserSelect.getAvatar());
                                break;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }


                // add list conversation view chat using for adapter
                conversationViewChats.add(0, conversationViewChat);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        conversationViewChats.sort(new SortByTime());
    }

    private void dataInitializeForUser() {
        users = new ArrayList<>();
        List<Contacts> list;
        try {
            User user = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));
            list = daoFactory.getContactsDAO().getListContacts(user.getId());
            for (int i = 0; i < list.size(); i++) {
                users.add(daoFactory.getUserDAO().getInfoUser(list.get(i).getUser_contact_id()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void markAsRead(int position) {
        ConversationViewChat conversationViewChat = conversationViewChats.get(position);
        conversationViewChat.setRead(true);
        chatAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void markAsRead(Long conversationId) {
        try {
//            preferenceManager = new PreferenceManager(mainActivity.getApplicationContext());
//            daoFactory = new DAOFactory(ConnectionDB.connection);
            User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));
            daoFactory.getParticipantsDAO().updateIsRead(myself.getId(), conversationId, true);
            for (int i = 0; i < conversationViewChats.size(); i++) {
                if (Objects.equals(conversationViewChats.get(i).getConversation_id(), conversationId)) {
                    ConversationViewChat conversationViewChat = conversationViewChats.get(i);
                    conversationViewChats.remove(i);
                    conversationViewChat.setRead(true);
                    conversationViewChats.add(i, conversationViewChat);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //chatAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onActivityChange(Long conversationId) {
        try {
//            preferenceManager = new PreferenceManager(getContext());
            daoFactory = new DAOFactory(ConnectionDB.connection);
            Conversation conversation = daoFactory.getConversationDAO().getInfoConversation(conversationId);
            if (conversation.getLast_message_id() == 0) {
                return;
            }


            Message msg = daoFactory.getMessageDAO().getInfoMessage(conversation.getLast_message_id());
            User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));

            boolean checkExits = false;
            for (int i = 0; i < conversationViewChats.size(); i++) {
                if (Objects.equals(conversationViewChats.get(i).getConversation_id(), conversationId)) {
                    Participant participant = daoFactory.getParticipantsDAO().getInfoParticipant(myself.getId(), conversationId);
                    ConversationViewChat conversationViewChat = conversationViewChats.get(i);
                    conversationViewChats.remove(i);
                    if(msg.getIsType().equals("IMAGE")){
                        conversationViewChat.setLast_message("IMAGE!!");
                    } else {
                        conversationViewChat.setLast_message(msg.getContent());
                    }
                    conversationViewChat.setTime(msg.getCreated_at()); // Temp
                    conversationViewChat.setRead(participant.getIs_read());
                    Log.i("ChatFragment", participant.getIs_read().toString());

                    if (conversation.isGroup()) {
                        conversationViewChat.setName(conversation.getName_conversation());
                    }

                    conversationViewChats.add(0, conversationViewChat);
                    checkExits = true;
                    break;
                }
            }

            if (!checkExits) {
                ConversationViewChat conversationViewChat = new ConversationViewChat();
                conversationViewChat.setConversation_id(conversation.getId());
                if(conversation.getLast_message_id() != null && conversation.getLast_message_id() != 0){
                    if(msg.getIsType().equals("IMAGE")){
                        conversationViewChat.setLast_message("IMAGE!!");
                    } else {
                        conversationViewChat.setLast_message(msg.getContent());
                    }
                    conversationViewChat.setTime(msg.getCreated_at());
                    if (msg.getUser_id() == myself.getId()) {
                        conversationViewChat.setRead(true);
                    }
                } else {
                    conversationViewChat.setLast_message(null);
                }


                if (conversation.isGroup()) {
                    conversationViewChat.setName(conversation.getName_conversation());
                } else {

                    List<Participant> userListOfConversation = daoFactory.getParticipantsDAO().getListUserOfConversation(conversation.getId());
                    // get Name of User
                    for(int j = 0; j < userListOfConversation.size(); j ++ ){
                        Long idUserSelect = userListOfConversation.get(j).getUser_id();
                        if(idUserSelect != myself.getId()){
                            try {
                                User infoUserSelect = daoFactory.getUserDAO().getInfoUser(idUserSelect);
                                conversationViewChat.setName(infoUserSelect.getUsername());
                                conversationViewChat.setAvatar(infoUserSelect.getAvatar());
                                break;
                            } catch (Exception e ){
                                e.printStackTrace();
                            }
                        }
                    }
                }

                conversationViewChats.add(0, conversationViewChat);
            }

            chatAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteConversation(Long conversationId) {
        try {

            for (ConversationViewChat conversationViewChat : conversationViewChats) {
                if (Objects.equals(conversationViewChat.getConversation_id(), conversationId)) {
                    conversationViewChats.remove(conversationViewChat);
                    break;
                }
            }

            chatAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void refresh() {
        conversationViewChats.clear();

        dataInitialize();
        // Notify the adapter that the data has changed
        if (chatAdapter != null) {
            chatAdapter.notifyDataSetChanged();
        }
    }
}