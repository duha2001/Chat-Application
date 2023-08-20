package com.example.d_five.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.d_five.dialogs.LoadingDialog;
import com.example.d_five.R;
import com.example.d_five.services.CallBackData;
import com.example.d_five.services.LinphoneService;

import com.example.d_five.databinding.ActivityLoginBinding;
import com.example.d_five.services.SaveSharedService;
import com.example.d_five.utilities.PreferenceManager;

import org.linphone.core.Account;
import org.linphone.core.Core;
import org.linphone.core.RegistrationState;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements CallBackData {

    ActivityLoginBinding binding;
    private LoadingDialog loadingDialog;
    LinphoneService linphoneService;
    String domain, username, password;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private boolean isBound = false;

    /** Defines callbacks for service binding, passed to bindService(). */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
            linphoneService = binder.getService();
            linphoneService.setLoginActivity(LoginActivity.this);
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPreferences();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.getRoot().setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
                return true;
            }
            return false;
        });

        binding.editDomain.setText("dfive-ims.dek.vn");

        binding.loginbutton.setOnClickListener(View -> {
            domain = binding.editDomain.getText().toString();
            username = binding.editUsername.getText().toString();
            password = binding.editPass.getText().toString();
            if (domain.isEmpty()) {
                binding.editDomain.setError(getApplicationContext().getString(R.string.pls_fill_domain));
            } else if (username.isEmpty()) {
                binding.editUsername.setError(getApplicationContext().getString(R.string.pls_fill_name));
            } else if (password.isEmpty()) {
                binding.editPass.setError(getApplicationContext().getString(R.string.pls_fill_pass));
            } else if (isBound) {
                binding.loginbutton.setEnabled(false);
                loadingDialog = new LoadingDialog(LoginActivity.this);
                loadingDialog.showDialog(getApplicationContext().getString(R.string.loading));
                linphoneService.initialRegister(domain, username, password);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (isBound) {
//            unbindService(serviceConnection);
//            isBound = false;
//        }
    }

    @Override
    public void onDataChange(String key, Core core) {
        updateUI(key, core);
    }

    private void updateUI(String key, Core core) {

        Account account = core.getDefaultAccount();
        if (account == null || account.getState() != RegistrationState.Ok) {
            loadingDialog.HideDialog();
            binding.loginbutton.setEnabled(true);
        } else {
            loadingDialog.HideDialog();
            SaveSharedService.setDomain(this, domain);
            SaveSharedService.setUsername(this, username);
            SaveSharedService.setPassword(this, password);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        editor = sharedPreferences.edit();
        String savedData = sharedPreferences.getString("DATA", "");
        setLocale(savedData);
    }

    public void setLocale( String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = LoginActivity.this.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, resources.getDisplayMetrics());
    }
}
