package com.example.dfive;

import android.content.Intent;
import android.os.IBinder;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ServiceTestRule;

import com.example.d_five.services.LinphoneService;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linphone.core.Address;
import org.linphone.core.Call;

import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class VideoCallTest {
    public final ServiceTestRule serviceRule = new ServiceTestRule();
    static LinphoneService service;
    Address address;
    @Before
    public void setUp() throws TimeoutException, InterruptedException{
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);
        IBinder iBinder = serviceRule.bindService(intent);
        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
        service = binder.getService();
        Thread.sleep(5000);
    }


    @Test
    public void Test03_makeCallAndRemoteHangUp() throws InterruptedException {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        service.initialRegister("dfive-ims.dek.vn","khue", "khue");

        service.makeCall("sip:chi@dfive-ims.dek.vn", null, true);
        Call call= service.getCore().getCurrentCall();
        Thread.sleep(5000);

        assertEquals(Call.State.OutgoingEarlyMedia, call.getState());
    }

    @Test
    public void Test02_makeCallAndLocalHangUp() throws InterruptedException {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        service.initialRegister("dfive-ims.dek.vn","khue", "khue");

        service.makeCall("sip:chi@dfive-ims.dek.vn", null, true);

        Call call= service.getCore().getCurrentCall();
        Thread.sleep(5000);

        assertEquals(Call.State.OutgoingEarlyMedia, call.getState());
    }

    @Test
    public void Test01_makeCallWhenRemoteOffline() throws InterruptedException {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        service.initialRegister("dfive-ims.dek.vn","khue", "khue");

        service.makeCall("sip:du@dfive-ims.dek.vn", null, true);

        Call call= service.getCore().getCurrentCall();
        Thread.sleep(5000);

        assertEquals(Call.State.OutgoingEarlyMedia, call.getState());

        if (call.getState() == Call.State.Released) {
            assertEquals(Call.State.Released, call.getState());
        } else {
            assertEquals(Call.State.OutgoingEarlyMedia, call.getState());
//            service.rejectCall();
        }
    }

    @Test
    public void Test04_makeCallAndSameTime() throws InterruptedException {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        service.initialRegister("dfive-ims.dek.vn","khue", "khue");

        service.makeCall("sip:chi@dfive-ims.dek.vn", null, true);
        Call first_call= service.getCore().getCurrentCall();
        Thread.sleep(5000);
        service.makeCall("sip:diep@dfive-ims.dek.vn", null, true);
        Call second_call= service.getCore().getCurrentCall();
        Thread.sleep(5000);

        assertEquals(Call.State.OutgoingEarlyMedia, second_call.getState());


    }
    // Test case 3: Accepted Voice Call
//    @Test
//    public void voiceCallAccept() throws TimeoutException, InterruptedException{
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);
//
//        IBinder iBinder = serviceRule.bindService(intent);
//
//        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
//        LinphoneService service = binder.getService();
//
//        Log.d("Send Message Success", "Making Voice Call Success");
//        service.initialRegister("dfive-ims.dek.vn","nga", "nga", null);
//        Thread.sleep(5000);
//
//        service.initialRegister("dfive-ims.dek.vn","chi", "chi", null);
//        Thread.sleep(5000);
//
//        service.makeCall("sip:chi@dfive-ims.dek.vn", null, false);
////        Call call= service.getCore().getCurrentCall();
////        Thread.sleep(5000);
////        Log.d("Call Accept", call.getState().toString());
////        Thread.sleep(5000);
////        service.rejectCall();
////        Thread.sleep(5000);
//        service.acceptCall(null, false);
//        Thread.sleep(5000);
//        Call call= service.getCore().getCurrentCall();
//        Thread.sleep(5000);
//        service.rejectCall();
//        Log.d("Call Accept", call.getState().toString());
//        assertThat((call.getState() == Call.State.End),is(true));
//        assertEquals("com.example.d_five", appContext.getPackageName());
//        Thread.sleep(5000);
//    }



    //Test case 5: 2 users call a user at the same time
//    @Test
//    public void manyVoiceCallAtTime() throws TimeoutException, InterruptedException{
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);
//
//        IBinder iBinder = serviceRule.bindService(intent);
//
//        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
//        LinphoneService service = binder.getService();
//
//        Log.d("Send Message Success", "Making Voice Call Success");
//        service.initialRegister("dfive-ims.dek.vn","nga", "nga", null);
//        Thread.sleep(5000);
//
//        service.initialRegister("dfive-ims.dek.vn","chi", "chi", null);
//        Thread.sleep(5000);
//
//        service.makeCall("sip:chi@dfive-ims.dek.vn", null, false);
//        Call first_call= service.getCore().getCurrentCall();
//        Thread.sleep(5000);
//        Log.d("First Call State", first_call.getState().toString());
//
//        service.initialRegister("dfive-ims.dek.vn","diep", "diep", null);
//        Thread.sleep(5000);
//
//        service.makeCall("sip:chi@dfive-ims.dek.vn", null, false);
//        Call second_call= service.getCore().getCurrentCall();
//        Thread.sleep(5000);
//
//        Log.d("Second Call State", second_call.getState().toString());
//        Thread.sleep(5000);
//        assertThat((second_call.getState() == Call.State.OutgoingEarlyMedia),is(true));
//        assertEquals("com.example.d_five", appContext.getPackageName());
//    }

}


