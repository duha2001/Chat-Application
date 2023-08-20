package com.example.d_five.callsession;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.example.d_five.R;
import com.example.d_five.activities.MainActivity;
import com.example.d_five.dao.ConnectionDB;
import com.example.d_five.dao.DAOFactory;
import com.example.d_five.databinding.ActivityInComingCallBinding;
import com.example.d_five.model.Default;
import com.example.d_five.model.User;
import com.example.d_five.services.CallBackData;
import com.example.d_five.services.LinphoneService;
import com.example.d_five.utilities.Constants;
import com.example.d_five.utilities.NotificationHandler;

import org.linphone.core.Core;

public class InComingCallActivity extends AppCompatActivity implements CallBackData {
    ActivityInComingCallBinding binding;
    LinphoneService linphoneService;
    boolean isBound, isVideoCall;
    String username;
    private DAOFactory daoFactory;

    /**
     * Defines callbacks for service binding, passed to bindService().
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
            linphoneService = binder.getService();
            isBound = true;

            linphoneService.setIncommingActivity(InComingCallActivity.this);
            isVideoCall = linphoneService.getCore().getCurrentCall().getRemoteParams().isVideoEnabled();

            // Set image for accept button
            if (isVideoCall) {
                binding.btnOkCall.setImageResource(R.drawable.ic_camon);
            } else {
                binding.btnOkCall.setImageResource(R.drawable.ic_call_ok);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInComingCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        username = getIntent().getStringExtra("Name");
        binding.displayUsername.setText(username);
        isVideoCall = false;

        NotificationHandler.startIncomingCall(InComingCallActivity.this);

        daoFactory = new DAOFactory(ConnectionDB.connection);
        try {
            User user = daoFactory.getUserDAO().getInfoUser(username);
            binding.dispalyAvatarUser.setImageBitmap(Default.StringToBitMap(user.getAvatar()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this.getApplicationContext(), LinphoneService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        binding.btnCancelCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBound == true) {
                    linphoneService.rejectCall();
                    finish();
                }
            }
        });

        binding.btnOkCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InComingCallActivity.this, DetailCallActivity.class);
                intent.putExtra("Name", username);
                intent.putExtra("isOutgoing", false);
                intent.putExtra("isVideoCall", isVideoCall);
                startActivity(intent);
                finish();
            }
        });

        binding.btnBackScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationHandler.handleBackCall(InComingCallActivity.this, username, InComingCallActivity.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        NotificationHandler.handleBackCall(InComingCallActivity.this, username, InComingCallActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationHandler.stopIncomingCall();

        if (isBound) {
            linphoneService.setIncommingActivity(null);
        }
    }

    @Override
    public void onDataChange(String key, Core core) {
        if (key == Constants.CALL_RELEASED) {
            finish();
        }
    }
}