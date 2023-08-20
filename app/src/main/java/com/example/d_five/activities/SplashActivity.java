package com.example.d_five.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.d_five.R;
import com.example.d_five.dialogs.LoadingDialog;
import com.example.d_five.services.CallBackData;
import com.example.d_five.services.LinphoneService;
import com.example.d_five.services.SaveSharedService;
import com.example.d_five.utilities.PreferenceManager;

import org.linphone.core.Account;
import org.linphone.core.Core;
import org.linphone.core.RegistrationState;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity implements CallBackData {
    private static final int SPLASH_SCREEN_TIME_OUT = 3000; // After completion of 2000 ms, the next activity will get started.
    private Animation upAnimation;
    private Animation downAnimation;
    LinphoneService linphoneService;
    private boolean isBound = false;
    private LoadingDialog loadingDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
            linphoneService = binder.getService();
            linphoneService.setLoginActivity(SplashActivity.this);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this.getApplicationContext(), LinphoneService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPreferences();

        // This method is used so that your splash activity can cover the entire screen.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        upAnimation = AnimationUtils.loadAnimation(this, R.anim.slideup);
        downAnimation = AnimationUtils.loadAnimation(this, R.anim.slidedown);

        RelativeLayout logo = findViewById(R.id.logo);
        TextView name_group = findViewById(R.id.name_group);

        logo.setAnimation(downAnimation);
        name_group.setAnimation(upAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String domain = SaveSharedService.getDomain(SplashActivity.this);
                String username = SaveSharedService.getUsername(SplashActivity.this);
                String password = SaveSharedService.getPassword(SplashActivity.this);

                // check signed in status
                if(domain.length() != 0 && username.length() != 0 && password.length() != 0 && isBound)
                {
                    loadingDialog = new LoadingDialog(SplashActivity.this);
                    loadingDialog.showDialog(getApplicationContext().getString(R.string.loading));
                    linphoneService.initialRegister(domain, username, password);
                }
                else
                {
                    Intent i = new Intent(SplashActivity.this, GettingStarted1Activity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }

    @Override
    public void onDataChange(String key, Core core) {
        loadingDialog.HideDialog();
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void initPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
        editor = sharedPreferences.edit();
        String savedData = sharedPreferences.getString("DATA", "");
        setLocale(savedData);
    }

    public void setLocale( String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = SplashActivity.this.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, resources.getDisplayMetrics());
    }
}
