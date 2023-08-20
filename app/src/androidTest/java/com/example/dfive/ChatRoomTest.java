package com.example.dfive;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linphone.core.Account;
import org.linphone.core.Address;



import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ServiceTestRule;


import com.example.d_five.model.User;
import com.example.d_five.services.LinphoneService;


import java.util.ArrayList;
import java.util.List;
import org.junit.Before;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linphone.core.Account;
import org.linphone.core.Address;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class ChatRoomTest {

    private static final String username = "dfive";
    private static final String password = "dfive";
    private static final String host = "";
    private static final String port = "5432";
    private static final String database = "postgres";
    private static String Classes = "org.postgresql.Driver";
    private static String url = "jdbc:postgresql://"+host+":"+port+"/"+database;

    @Rule
    public final ServiceTestRule serviceRule = new ServiceTestRule();

    LinphoneService service;
    Address address;

    List<User> listUser;
    List<String> userNames;


    @Before
    public void setUp() throws TimeoutException, InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);

        IBinder iBinder = serviceRule.bindService(intent);

        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
        service = binder.getService();
        service.initialRegister("dfive-ims.dek.vn","tri", "tri");
        Thread.sleep(5000);
        Account account = service.getCore().getDefaultAccount();
        Thread.sleep(5000);


//        Class.forName(Classes);
//        Connection connection = null;
//        connection = DriverManager.getConnection(url, username, password);
//
//        ConversationDAO conversationDAO = new ConversationDAO(connection  );
//        UserDAO userDAO = new UserDAO(connection);
//        ParticipantsDAO participantsDAO  = new ParticipantsDAO(connection);

    }


   // Test case 1 : create group and send message to each person
    @Test
    public void createGroupChat() throws TimeoutException, InterruptedException{
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);

        IBinder iBinder = serviceRule.bindService(intent);

        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
        LinphoneService service = binder.getService();
        //create group chat of Tri, Hieu, Chien and Du
        userNames = new ArrayList<String>();
        userNames.add("tri");
        userNames.add("hieu");

        userNames.add("chien");
        userNames.add("du");

       for(String userName : userNames){
           service.createChatRoom(userName);
       }
       assertEquals("com.example.d_five", appContext.getPackageName());
    }
    @Test
    public void sendMessageChatRoomSuccess() throws TimeoutException, InterruptedException{
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);
        IBinder iBinder = serviceRule.bindService(intent);
        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
        LinphoneService service = binder.getService();
    }}


//    @Test
//    public void testSend() throws TimeoutException, InterruptedException {

//        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);
//
//        IBinder iBinder = serviceRule.bindService(intent);
//        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
//        LinphoneService service = binder.getService();
//
//        for(String userName : userNames){
//            return ;
//        }

