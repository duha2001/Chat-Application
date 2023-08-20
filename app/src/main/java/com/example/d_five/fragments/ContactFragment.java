package com.example.d_five.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.d_five.dialogs.AddNewAccountDialog;
import com.example.d_five.R;
import com.example.d_five.adapter.ContactAdapter;
import com.example.d_five.dao.ConnectionDB;
import com.example.d_five.dao.DAOFactory;
import com.example.d_five.dao.implementDAO.ContactsDAO;
import com.example.d_five.dao.implementDAO.UserDAO;
import com.example.d_five.model.Contacts;
import com.example.d_five.model.User;
import com.example.d_five.utilities.Constants;
import com.example.d_five.utilities.PreferenceManager;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static ArrayList<Contacts> listContact;

    RecyclerView contactRecycleView;
    SearchView searchView;
    ImageButton addNewContact;

    // database
    private PreferenceManager preferenceManager;
    private DAOFactory daoFactory;
    @SuppressLint("StaticFieldLeak")
    public static ContactAdapter contactAdapter;

    public ContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);

        searchView = rootView.findViewById(R.id.searchView);
        searchView.clearFocus();
        contactRecycleView = rootView.findViewById(R.id.recyclerView_list);
        addNewContact = rootView.findViewById(R.id.btn_addNewContact);
        addNewContact = rootView.findViewById(R.id.btn_addNewContact);
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
        addNewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddNewAccountDialog.class);
                startActivity(intent);
            }
        });

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });
        contactRecycleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });
        return rootView;
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

        preferenceManager = new PreferenceManager(getContext());
        daoFactory = new DAOFactory(ConnectionDB.connection);
        try {

            dataContact();
        } catch (Exception e) {
            e.printStackTrace();
        }

        contactRecycleView = view.findViewById(R.id.recyclerView_list);
        contactRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactRecycleView.setHasFixedSize(true);

        contactAdapter = new ContactAdapter(getContext(),listContact);
        contactRecycleView.setAdapter(contactAdapter);
        contactAdapter.notifyDataSetChanged();
    }

    private void dataContact() throws Exception{
        listContact = new ArrayList<>();
        try {
            User user = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));
            listContact = daoFactory.getContactsDAO().getListContacts(user.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void filterList(String newText) throws Exception {
        ArrayList<Contacts> filteredList = new ArrayList<>();
        for (Contacts contacts : listContact){
            if (daoFactory.getUserDAO().getInfoUser(contacts.getUser_contact_id()).getUsername().toLowerCase().contains(newText.toLowerCase()))
                filteredList.add(contacts);
        }

        if (filteredList.isEmpty())
            Toast.makeText(getContext(), "No contact found", Toast.LENGTH_SHORT).show();
        else
            contactAdapter.setFilteredList(filteredList);
    }
}