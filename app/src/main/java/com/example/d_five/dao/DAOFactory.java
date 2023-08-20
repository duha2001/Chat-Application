package com.example.d_five.dao;

import com.example.d_five.dao.implementDAO.CallDAO;
import com.example.d_five.dao.implementDAO.ContactsDAO;
import com.example.d_five.dao.implementDAO.ConversationDAO;
import com.example.d_five.dao.implementDAO.MessageDAO;
import com.example.d_five.dao.implementDAO.ParticipantsDAO;
import com.example.d_five.dao.implementDAO.UserDAO;

import java.sql.Connection;

public class DAOFactory {

    private Connection connection;

    private UserDAO userDAO;
    private ParticipantsDAO participantsDAO;
    private CallDAO callDAO;
    private MessageDAO messageDAO;
    private ContactsDAO contactsDAO;
    private ConversationDAO conversationDAO;


    public DAOFactory(Connection connection) {
        this.connection = connection;
        this.userDAO = new UserDAO(connection);
        this.participantsDAO = new ParticipantsDAO(connection);
        this.callDAO = new CallDAO(connection);
        this.messageDAO = new MessageDAO(connection);
        this.contactsDAO = new ContactsDAO(connection);
        this.conversationDAO = new ConversationDAO(connection);
    }


    public IUserDAO getUserDAO() {
        return userDAO;
    }

    public IParticipantsDAO getParticipantsDAO() {
        return participantsDAO;

    }

    public ICallDAO getCallDAO() {
        return callDAO;
    }

    public IMessageDAO getMessageDAO() {
        return messageDAO;
    }

    public IContactsDAO getContactsDAO() {
        return contactsDAO;
    }

    public IConversationDAO getConversationDAO() {
        return conversationDAO;
    }
}
