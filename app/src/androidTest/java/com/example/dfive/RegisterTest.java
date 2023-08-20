package com.example.dfive;


import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import static org.hamcrest.CoreMatchers.*;

import androidx.test.annotation.UiThreadTest;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ServiceTestRule;
import com.example.d_five.activities.MainActivity;

import com.example.d_five.services.CallBackData;
import com.example.d_five.services.LinphoneService;
import com.google.common.truth.BooleanSubject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linphone.core.Account;
import org.linphone.core.AccountParams;
import org.linphone.core.RegistrationState;
import org.linphone.core.Core;
import org.linphone.core.Factory;


import java.util.concurrent.TimeoutException;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
//public class RegisterTest {
//    @Rule
//    public final ServiceTestRule serviceRule = new ServiceTestRule();
//
//    LinphoneService service;
//
//    @Before
//    public void setUp() throws TimeoutException, InterruptedException {
//        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);
//
//        IBinder iBinder = serviceRule.bindService(intent);
//
//        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
//        service = binder.getService();
//        Thread.sleep(5000);
//    }
//
//    // Test case 1: Login Success
//    @Test
//    public void RegisterSuccess() throws TimeoutException, InterruptedException {
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);
//
//        IBinder iBinder = serviceRule.bindService(intent);
//
//        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
//        LinphoneService service = binder.getService();
//
//        Log.d("RegistrationTest", "Registration Success");
//        service.initialRegister("dfive-ims.dek.vn","du", "123", null);
//        Thread.sleep(5000);
//
//
//
//        Account account = service.getCore().getDefaultAccount();
//        AccountParams accountParams = account.getParams();
//        account.getState();
//        Log.d("Registration State: ", account.getState().toString());
//
//        assertThat((account.getState() == RegistrationState.Ok),is(true));
//        assertEquals("com.example.d_five", appContext.getPackageName());
//
//
//    }
//
//    // Test case 2: Login Fail Wrong Domain
//    @Test
//    public void RegisterWrongDomain() throws TimeoutException, InterruptedException {
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);
//
//        IBinder iBinder = serviceRule.bindService(intent);
//
//        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
//        LinphoneService service = binder.getService();
//        Log.d("RegistrationTest", "Registration Success");
//        service.initialRegister("ddfive-ims.dek.vn","du", "123", null);
//        Thread.sleep(5000);
//        Account account = service.getCore().getDefaultAccount();
//        AccountParams accountParams = account.getParams();
//        account.getState();
//        Log.d("Registration State: ", account.getState().toString());
//
//        assertThat((account.getState() == RegistrationState.Failed),is(true));
//        assertEquals("com.example.d_five", appContext.getPackageName());
//    }
//
//    // Test case 3: Login Fail Wrong UserName
//    @Test
//    public void RegisterWrongUserName() throws TimeoutException, InterruptedException {
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);
//
//        IBinder iBinder = serviceRule.bindService(intent);
//
//        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
//        LinphoneService service = binder.getService();
//        Log.d("RegistrationTest", "Registration Success");
//        service.initialRegister("dfive-ims.dek.vn","duuu", "123", null);
//        Thread.sleep(5000);
//        Account account = service.getCore().getDefaultAccount();
//        AccountParams accountParams = account.getParams();
//        account.getState();
//        Log.d("Registration State: ", account.getState().toString());
//
//        assertThat((account.getState() == RegistrationState.Failed),is(true));
//        assertEquals("com.example.d_five", appContext.getPackageName());
//    }
//
//    // Test case 4: Login Fail Wrong Password
//    @Test
//    public void RegisterWrongPassword() throws TimeoutException, InterruptedException {
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);
//
//        IBinder iBinder = serviceRule.bindService(intent);
//
//        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
//        LinphoneService service = binder.getService();
//        Log.d("RegistrationTest", "Registration Success");
//        service.initialRegister("dfive-ims.dek.vn","du", "du", null);
//        Thread.sleep(5000);
//        Account account = service.getCore().getDefaultAccount();
//        AccountParams accountParams = account.getParams();
//        account.getState();
//        Log.d("Registration State: ", account.getState().toString());
//
//        assertThat((account.getState() == RegistrationState.Failed),is(true));
//        assertEquals("com.example.d_five", appContext.getPackageName());
//    }
//
//    // Test case 45: reRegister
//    @Test
//    public void reRegister() throws TimeoutException, InterruptedException {
//        Log.d("RegistrationTest", "Re-Registration");
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);
//
//        IBinder iBinder = serviceRule.bindService(intent);
//
//        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
//        LinphoneService service = binder.getService();
//
//        service.initialRegister("dfive-ims.dek.vn","du", "123", null);
//        Thread.sleep(5000);
//
//        Account account = service.getCore().getDefaultAccount();
//        Log.d("Registration State1: ", account.getState().toString());
//
//        service.reRegister(null);
//        Thread.sleep(5000);
//        Log.d("Registration State2: ", account.getState().toString());
//
//        assertThat((account.getState() == RegistrationState.Ok),is(true));
//        assertEquals("com.example.d_five", appContext.getPackageName());
//    }
//
//    // Test case 6: deRegister
//    @Test
//    public void deRegister() throws TimeoutException, InterruptedException {
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);
//
//        IBinder iBinder = serviceRule.bindService(intent);
//
//        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
//        LinphoneService service = binder.getService();
//
//        Log.d("RegistrationTest", "De-Registration");
//        service.initialRegister("dfive-ims.dek.vn","du", "123", null);
//        Thread.sleep(5000);
//
//        Account account = service.getCore().getDefaultAccount();
//        Log.d("Registration State1: ", account.getState().toString());
//
//        service.deRegister();
//        Thread.sleep(5000);
//
//        Log.d("Registration State2: ", account.getState().toString());
//
//        assertThat((account.getState() == RegistrationState.Cleared),is(true));
//        assertEquals("com.example.d_five", appContext.getPackageName());
//    }
//
//    // Test case 7: Login fail wrong username & password
//    @Test
//    public void RegisterWrongUsernamePassword() throws TimeoutException, InterruptedException {
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);
//
//        IBinder iBinder = serviceRule.bindService(intent);
//
//        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
//        LinphoneService service = binder.getService();
//
//        Log.d("RegistrationTest", "Registration Success");
//        service.initialRegister("dfive-ims.dek.vn","duu", "duu", null);
//        Thread.sleep(5000);
//
//        Account account = service.getCore().getDefaultAccount();
//        account.getState();
//        Log.d("Registration State: ", account.getState().toString());
//
//        assertThat((account.getState() == RegistrationState.Failed),is(true));
//        assertEquals("com.example.d_five", appContext.getPackageName());
//    }
//
//    // Test case 8: Login fail wrong username & domain
//    @Test
//    public void RegisterWrongUsernameDomain() throws TimeoutException, InterruptedException {
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);
//
//        IBinder iBinder = serviceRule.bindService(intent);
//
//        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
//        LinphoneService service = binder.getService();
//
//        Log.d("RegistrationTest", "Registration Success");
//        service.initialRegister("dfive-ims.dek.com","duu", "123", null);
//        Thread.sleep(5000);
//        Account account = service.getCore().getDefaultAccount();
//        account.getState();
//        Log.d("Registration State: ", account.getState().toString());
//
//        assertThat((account.getState() == RegistrationState.Failed),is(true));
//        assertEquals("com.example.d_five", appContext.getPackageName());
//    }
//
//    // Test case 9: Login fail wrong username & domain & password
//    @Test
//    public void RegisterWrongUsernameDomainPassword() throws TimeoutException, InterruptedException {
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);
//
//        IBinder iBinder = serviceRule.bindService(intent);
//
//        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
//        LinphoneService service = binder.getService();
//
//        Log.d("RegistrationTest", "Registration Success");
//        service.initialRegister("dfive-ims.dek.com","duu", "duuu", null);
//        Thread.sleep(5000);
//
//        Account account = service.getCore().getDefaultAccount();
//        account.getState();
//        Log.d("Registration State: ", account.getState().toString());
//
//        assertThat((account.getState() == RegistrationState.Failed),is(true));
//        assertEquals("com.example.d_five", appContext.getPackageName());
//    }
//
//}
public class RegisterTest {
    @Rule
    public final ServiceTestRule serviceRule = new ServiceTestRule();


    LinphoneService service;

    @Before
    public void setUp() throws TimeoutException, InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);

        IBinder iBinder = serviceRule.bindService(intent);

        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
        service = binder.getService();
        Thread.sleep(5000);
    }

    // Test case 1: Login Success
    @Test
    public void RegisterSuccess() throws TimeoutException, InterruptedException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);

        IBinder iBinder = serviceRule.bindService(intent);

        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
        LinphoneService service = binder.getService();

        Log.d("RegistrationTest", "Registration Success");
        service.initialRegister("dfive-ims.dek.vn","chien", "chien");
        Thread.sleep(5000);



        Account account = service.getCore().getDefaultAccount();
        account.getState();
        Log.d("Registration State: ", account.getState().toString());

        assertThat((account.getState() == RegistrationState.Ok),is(true));
        assertEquals("com.example.d_five", appContext.getPackageName());


    }

    // Test case 2: Login Fail Wrong Domain
    @Test
    public void RegisterWrongDomain() throws TimeoutException, InterruptedException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);

        IBinder iBinder = serviceRule.bindService(intent);

        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
        LinphoneService service = binder.getService();
        Log.d("RegistrationTest", "Registration Success");
        service.initialRegister("ddfive-ims.dek.vn","du", "123");
        Account account = service.getCore().getDefaultAccount();
        Thread.sleep(5000);
        Log.d("Registration State: ", account.getState().toString());

        assertThat((account.getState() == RegistrationState.Failed),is(true));
        assertEquals("com.example.d_five", appContext.getPackageName());
    }

    // Test case 3: Login Fail Wrong UserName
    @Test
    public void RegisterWrongUserName() throws TimeoutException, InterruptedException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);

        IBinder iBinder = serviceRule.bindService(intent);

        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
        LinphoneService service = binder.getService();


        service.initialRegister("dfive-ims.dek.vn","duu", "123");
        Account account = service.getCore().getDefaultAccount();
        Thread.sleep(5000);

        Log.d("RegisterWrongUserName2: ", account.getState().toString());
        assertThat((account.getState() == RegistrationState.None),is(true));
        assertEquals("com.example.d_five", appContext.getPackageName());
    }


    // Test case 4: Login Fail Wrong Password
    @Test
    public void RegisterWrongPassword() throws TimeoutException, InterruptedException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);

        IBinder iBinder = serviceRule.bindService(intent);

        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
        LinphoneService service = binder.getService();
        Log.d("RegistrationTest", "Registration Success");
        service.initialRegister("ddfive-ims.dek.vn","nup", "123");
        Account account = service.getCore().getDefaultAccount();
        Thread.sleep(5000);
        Log.d("RegisterWrongPassword: ", account.getState().toString());

        assertThat((account.getState() == RegistrationState.Failed),is(true));
        assertEquals("com.example.d_five", appContext.getPackageName());
    }

    // Test case 45: reRegister
    @Test

    public void reRegister() throws TimeoutException, InterruptedException {
        Log.d("RegistrationTest", "Re-Registration");
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);

        IBinder iBinder = serviceRule.bindService(intent);

        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
        LinphoneService service = binder.getService();

        service.initialRegister("dfive-ims.dek.vn","chien", "chien");
        Thread.sleep(5000);
        Account account = service.getCore().getDefaultAccount();
        service.reRegister();
        Thread.sleep(5000);
        assertThat((account.getState() == RegistrationState.Ok),is(true));
        assertEquals("com.example.d_five", appContext.getPackageName());
    }



    // Test case 6: deRegister
    @Test
    public void deRegister() throws TimeoutException, InterruptedException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);

        IBinder iBinder = serviceRule.bindService(intent);

        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
        LinphoneService service = binder.getService();

        Log.d("RegistrationTest", "De-Registration");
        service.initialRegister("dfive-ims.dek.vn","chien", "chien");
        Thread.sleep(5000);

        Account account = service.getCore().getDefaultAccount();
        Log.d("Registration State1: ", account.getState().toString());

        service.deRegister();
        Thread.sleep(5000);
        assertThat((account.getState() == RegistrationState.Cleared),is(true));
        assertEquals("com.example.d_five", appContext.getPackageName());
    }

    // Test case 10: Login fail with blank username
    @Test
    public void RegisterBlankUsername() throws TimeoutException, InterruptedException{
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);

        IBinder iBinder = serviceRule.bindService(intent);

        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
        LinphoneService service = binder.getService();

        Log.d("RegistrationTest", "Registration Success");
        service.initialRegister("dfive-ims.dek.vn", null, "123");

        Account account = service.getCore().getDefaultAccount();
        Thread.sleep(5000);
        Log.d("RegisterBlankUsername: ", account.getState().toString());

        assertThat((account.getState()==RegistrationState.None),is(true));
        assertEquals("com.example.d_five", appContext.getPackageName());
    }

    // Test case 11: Login fail with blank password
    @Test
    public void RegisterBlankPassword() throws TimeoutException, InterruptedException{
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);

        IBinder iBinder = serviceRule.bindService(intent);

        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
        LinphoneService service = binder.getService();

        Log.d("RegistrationTest", "Registration Success");
        service.initialRegister("dfive-ims.dek.vn", "khue", null);
        Thread.sleep(5000);
        Account account = service.getCore().getDefaultAccount();
        Thread.sleep(5000);
        assertThat((account.getState()==RegistrationState.Failed),is(true));
        assertEquals("com.example.d_five", appContext.getPackageName());
    }

    // Test case 13: Login fail with blank domain
    @Test
    public void RegisterBlankDomain() throws TimeoutException, InterruptedException{
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);

        IBinder iBinder = serviceRule.bindService(intent);

        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
        LinphoneService service = binder.getService();

        Log.d("RegistrationTest", "Registration Success");
        service.initialRegister(null, "du", "123");
        Account account = service.getCore().getDefaultAccount();
        Thread.sleep(5000);
        Log.d("Registration State: ", account.getState().toString());


        assertThat((account.getState()==RegistrationState.Failed),is(true));
        assertEquals("com.example.d_five", appContext.getPackageName());
    }

    // Test case 16: Login fail with username special character
    @Test
    public void RegisterUsernameSpecialCharacters() throws TimeoutException, InterruptedException{
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);

        IBinder iBinder = serviceRule.bindService(intent);

        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
        LinphoneService service = binder.getService();

        Log.d("RegistrationTest", "Registration Success");
        service.initialRegister("dfive-ims.dek.vn", "++du", "tri");
        Account account = service.getCore().getDefaultAccount();
        Thread.sleep(5000);
        Log.d("RegisterUsernameSpecialCharacters: ", account.getState().toString());


        assertThat((account.getState()==RegistrationState.None),is(true));
        assertEquals("com.example.d_five", appContext.getPackageName());
    }

    // Test case 17: Login fail with username special character
    @Test
    public void RegisterPasswordSpecialCharacters() throws TimeoutException, InterruptedException{
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LinphoneService.class);

        IBinder iBinder = serviceRule.bindService(intent);

        LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
        LinphoneService service = binder.getService();

        Log.d("RegistrationTest", "Registration Success");
        service.initialRegister("dfive-ims.dek.vn", "du", "&&123");
        Account account = service.getCore().getDefaultAccount();
        Thread.sleep(5000);
        Log.d("RegisterPasswordSpecialCharacters: ", account.getState().toString());

        assertThat((account.getState()==RegistrationState.None),is(true));
        assertEquals("com.example.d_five", appContext.getPackageName());
    }

}

