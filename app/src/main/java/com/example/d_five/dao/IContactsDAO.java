package com.example.d_five.dao;

import com.example.d_five.model.Contacts;

import java.util.ArrayList;
import java.util.List;

public interface IContactsDAO {

    // insert contact
    Contacts insertContact(Contacts contacts) throws Exception;

    // getList Contact
    ArrayList<Contacts> getListContacts(Long user_id) throws Exception;

    boolean deleteContact (Long user_contact_id) throws Exception;

    boolean checkContact(Long user_id, Long user_contact_id) throws Exception;
}
