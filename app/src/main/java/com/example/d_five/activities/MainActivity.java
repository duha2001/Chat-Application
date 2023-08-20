package com.example.d_five.activities;

import static com.example.d_five.PagerAdapter.chatFragment;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.d_five.PagerAdapter;
import com.example.d_five.R;
import com.example.d_five.callsession.InComingCallActivity;
import com.example.d_five.dao.ConnectionDB;
import com.example.d_five.dao.implementDAO.ParticipantsDAO;
import com.example.d_five.dao.implementDAO.UserDAO;
import com.example.d_five.databinding.ActivityMainBinding;
import com.example.d_five.dialogs.NotificationDialog;
import com.example.d_five.model.LocationMessage;
import com.example.d_five.model.User;
import com.example.d_five.services.CallBackData;
import com.example.d_five.services.CallBackMessage;
import com.example.d_five.services.LinphoneService;
import com.example.d_five.services.SaveSharedService;
import com.example.d_five.utilities.Constants;
import com.example.d_five.utilities.NotificationHandler;
import com.example.d_five.utilities.PreferenceManager;

import org.linphone.core.Account;
import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.ChatMessage;
import org.linphone.core.Core;
import org.linphone.core.RegistrationState;

import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements CallBackData, CallBackMessage {
    private PreferenceManager preferenceManager;
    ActivityMainBinding binding;
    MenuItem prevMenuItem;
    private boolean isBound;
    public static Boolean darkmode;
    private LinphoneService linphoneService;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
            linphoneService = binder.getService();
            linphoneService.setMainActivityMsg(MainActivity.this);
            linphoneService.setMainActivity(MainActivity.this);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, LinphoneService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initPreferences();
        checkPermission();
        NotificationHandler.initNotification(getApplicationContext());

//        darkmode = SaveSharedService.getDarkMode(MainActivity.this);
//        if (darkmode){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        }
//        else{
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }
//
//        preferenceManager = new PreferenceManager(getApplicationContext());


        ViewPager2 viewPager = findViewById(R.id.view_pager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(pagerAdapter);

        binding.bottomNavBar.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.chat) {
                viewPager.setCurrentItem(0);
            } else if (item.getItemId() == R.id.call) {
                viewPager.setCurrentItem(1);
            } else if (item.getItemId() == R.id.contact) {
                viewPager.setCurrentItem(2);
            } else if (item.getItemId() == R.id.profile) {
                viewPager.setCurrentItem(3);
            }

            return false;
        });

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    binding.bottomNavBar.getMenu().getItem(0).setChecked(false);
                }
                binding.bottomNavBar.getMenu().getItem(position).setChecked(true);
                prevMenuItem = binding.bottomNavBar.getMenu().getItem(position);
            }
        });

        Log.i("main","ON CREATE");

    }

    public LinphoneService getLinphoneService() {
        if (isBound) {
            return linphoneService;
        } else {
            return null;
        }
    }

    @Override
    public void onDataChange(String key, Core core) {
        switch (key) {
            case "LOGOUT":
                Account account = core.getDefaultAccount();
                assert account != null;
                if (account.getState() == RegistrationState.Cleared) {
                    linphoneService.delete();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.logout_failed), Toast.LENGTH_SHORT).show();
                }
                break;
            case "NOTIFICATION":
                notificationReg();
                break;
            case Constants.MESSAGE_NEW:
                NotificationHandler.handleNewMessage(MainActivity.this);
                break;
            case Constants.CALL_INCOMING_RECEIVED:
                Call call = core.getCurrentCall();

                if (call == null) {
                    return;
                }
                Address remoteAddress = core.getCurrentCallRemoteAddress();

                Intent intent = new Intent(getApplicationContext(), InComingCallActivity.class);
                intent.putExtra("Name", remoteAddress.getUsername());
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onMessageChange(ChatMessage chatMessage, LocationMessage locationMessage) {
        Long conversationId = Long.valueOf(Objects.requireNonNull(chatMessage.getUtf8Text()));
        ParticipantsDAO participantsDAO = new ParticipantsDAO(ConnectionDB.connection);
        UserDAO userDAO = new UserDAO(ConnectionDB.connection);
        User user = null;
        try {
            preferenceManager = new PreferenceManager(getApplicationContext());
            user = userDAO.getInfoUser(preferenceManager.getString(Constants.USER_NAME));
            if (participantsDAO.checkUserInConversation(user.getId(), conversationId)) {
                chatFragment.onActivityChange(conversationId);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public Long getConversationId() {
        return null;
    }

    // Function to check and request permission
    void checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        boolean isCallPhone = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;

        boolean isMicrophone = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;

        boolean isCamera = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        boolean isNotification = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;

        if (isCallPhone && isMicrophone && isCamera && isNotification) {
            Log.i(Constants.TAG_PERMISSION, "Permissions Granted (Camera, Call phone, Record audio, Notification)");
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS,
            }, Constants.CALL_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.CALL_PERMISSION_CODE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
        } else {
            Log.i(Constants.TAG_PERMISSION, "Permissions Denied (Camera, Call phone, Record audio, Notification)");
        }
    }

    private void initPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        editor = sharedPreferences.edit();
        String savedData = sharedPreferences.getString("DATA", "");
        setLocale(savedData);
    }

    public void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = MainActivity.this.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, resources.getDisplayMetrics());
    }

    NotificationDialog notificationDialog;

    public void notificationReg() {
        notificationDialog = new NotificationDialog(this);
        notificationDialog.showDialog("DFIVE", getApplicationContext().getString(R.string.re_register));
    }
}