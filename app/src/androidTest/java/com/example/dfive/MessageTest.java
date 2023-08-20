package com.example.dfive;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linphone.core.Account;
import org.linphone.core.Address;
import org.linphone.core.ChatMessage;
import org.linphone.core.ChatMessageListener;
import org.linphone.core.RegistrationState;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ServiceTestRule;

import com.example.d_five.services.LinphoneService;

import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class MessageTest {

    @Rule
    public final ServiceTestRule serviceRule = new ServiceTestRule();
    LinphoneService service;
    Address address;
    @Before
    public void setUp() throws TimeoutException, InterruptedException{

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);
        IBinder iBinder = serviceRule.bindService(intent);
        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
        service = binder.getService();
        Thread.sleep(5000);
        Account account = service.getCore().getDefaultAccount();
        Thread.sleep(5000);
    }

    // Test case 1: Send message đến người dùng đang offline

    @Test
    public void sendMessageOffline() throws TimeoutException, InterruptedException{
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);

        IBinder iBinder = serviceRule.bindService(intent);

        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
        LinphoneService service = binder.getService();
        service.initialRegister("dfive-ims.dek.vn","du", "123");
        Thread.sleep(5000);

        Log.d("SendMessageFail", "Send Message To User Offline");
        service.createBasicChatRoom("sip:diep@dfive-ims.dek.vn");
        ChatMessage chatMessage = service.sendMessageToPerson(88L,"sip:diep@dfive-ims.dek.vn");
        if(chatMessage != null){
            chatMessage.getState();
            Thread.sleep(5000);
            Log.d("sendMessageOffline", chatMessage.getState().toString());
        }
        assertThat((chatMessage.getState() == ChatMessage.State.NotDelivered),is(true));
        assertEquals("com.example.d_five", appContext.getPackageName());
    }

    // Test case 2: Send message thành công
    @Test
    public void sendMessageSuccess() throws TimeoutException, InterruptedException{
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);

        IBinder iBinder = serviceRule.bindService(intent);

        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
        LinphoneService service = binder.getService();

        Log.d("SendMessageSuccess", "Send Message Success");
        service.initialRegister("dfive-ims.dek.vn","du", "123");
        Thread.sleep(5000);

        service.initialRegister("dfive-ims.dek.vn","chi", "chi");
        Thread.sleep(5000);

        Account account = service.getCore().getDefaultAccount();
        account.getState();
        Log.d("SendMessageSuccess", account.getState().toString());

        service.createBasicChatRoom("sip:chi@dfive-ims.dek.vn");
        ChatMessage chatMessage = service.sendMessageToPerson(88L,"sip:chi@dfive-ims.dek.vn");
        if(chatMessage != null){
            chatMessage.getState();
            Thread.sleep(5000);
        }
        assertThat((chatMessage.getState() == ChatMessage.State.Delivered),is(true));
        assertEquals("com.example.d_five", appContext.getPackageName());
    }

    // Test case 3: Gửi message đến địa chỉ người dùng sai
    @Test
    public void sendMessageWrongAddress() throws TimeoutException, InterruptedException{
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);

        IBinder iBinder = serviceRule.bindService(intent);

        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
        LinphoneService service = binder.getService();

        Log.d("SendMessageFail", "Send Message Wrong Address");
        service.initialRegister("dfive-ims.dek.vn","du", "123");
        Thread.sleep(5000);

        service.createBasicChatRoom("sip:chii@dfive-ims.dek.vn");
        ChatMessage chatMessage = service.sendMessageToPerson(88L,"sip:chii@dfive-ims.dek.vn");
        if(chatMessage != null){
            chatMessage.getState();
            Thread.sleep(5000);
            Log.d("sendMessageWrongAddress", chatMessage.getState().toString());
        }
        assertThat((chatMessage.getState() == ChatMessage.State.NotDelivered),is(true));
        assertEquals("com.example.d_five", appContext.getPackageName());
    }

    // send to yourself
    @Test
    public void sendMessageToYourself() throws TimeoutException, InterruptedException{
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);

        IBinder iBinder = serviceRule.bindService(intent);

        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
        LinphoneService service = binder.getService();

        Log.d("SendMessageFail", "Send Message Wrong Address");
        service.initialRegister("dfive-ims.dek.vn","du", "123");
        Thread.sleep(5000);


        service.createBasicChatRoom("sip:du@dfive-ims.dek.vn");
        ChatMessage chatMessage = service.sendMessageToPerson(88L,"sip:du@dfive-ims.dek.vn");
        //assertEquals("chat message is null ", !chatMessage, null);
        if(chatMessage != null){
            chatMessage.getState();
            Thread.sleep(5000);
        }
        assertThat((chatMessage.getState() == ChatMessage.State.Delivered),is(true));
        assertEquals("com.example.d_five", appContext.getPackageName());

    }

//    @Test
//    public void sendMessageToWrongFormatAddress() throws TimeoutException, InterruptedException{
////        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
////        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);
////
////        IBinder iBinder = serviceRule.bindService(intent);
////
////        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
////        LinphoneService service = binder.getService();
////
////        Log.d("SendMessageFail", "Send Message Wrong Address");
////        service.initialRegister("dfive-ims.dek.vn","du", "123", null);
////        Thread.sleep(5000);
////
////        service.createBasicChatRoom("chii@dfive-ims.dek.vn");
////        ChatMessage chatMessage = service.sendMessageToPerson(88L,"sip:chii@dfive-ims.dek.vn");
////        if(chatMessage != null){
////            chatMessage.getState();
////            Thread.sleep(5000);
////            Log.d("sendMessageWrongAddress", chatMessage.getState().toString());
////        }
////        assertThat((chatMessage.getState() == ChatMessage.State.NotDelivered),is(true));
////        assertEquals("com.example.d_five", appContext.getPackageName());
//         assertEquals(1,1);
//    }
}
