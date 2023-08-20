package com.example.d_five.callsession;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.d_five.R;
import com.example.d_five.activities.MainActivity;

import com.example.d_five.dao.ConnectionDB;
import com.example.d_five.dao.DAOFactory;
import com.example.d_five.dao.implementDAO.UserDAO;
import com.example.d_five.databinding.ActivityVoiceCallBinding;
import com.example.d_five.dialogs.AcceptVideoDialog;
import com.example.d_five.dialogs.LogoutDialog;
import com.example.d_five.model.Default;
import com.example.d_five.model.User;
import com.example.d_five.services.CallBackData;
import com.example.d_five.services.CallService;
import com.example.d_five.services.LinphoneService;
import com.example.d_five.utilities.Constants;
import com.example.d_five.utilities.NotificationHandler;

import org.linphone.core.AudioDevice;
import org.linphone.core.Call;
import org.linphone.core.CallParams;
import org.linphone.core.Core;

public class DetailCallActivity extends AppCompatActivity implements CallBackData {
    ActivityVoiceCallBinding binding;
    LinphoneService linphoneService;
    private long pauseOffset;
    private DAOFactory daoFactory;

    boolean isBound, running, isOutgoing, isSpeaker, isMute, isVideoCall;
    String username;
    AcceptVideoDialog acceptVideoDialog;

    /** Defines callbacks for service binding, passed to bindService(). */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LinphoneService.LocalBinder binder = (LinphoneService.LocalBinder) iBinder;
            linphoneService = binder.getService();
            isBound = true;

            String sipUrl = "sip:" + username + "@dfive-ims.dek.vn";
            if(isOutgoing) {
                linphoneService.makeCall(sipUrl, DetailCallActivity.this, isVideoCall);
            } else {
                linphoneService.acceptCall(DetailCallActivity.this, isVideoCall);
            }

            if (!linphoneService.isMicEnable()) {
                linphoneService.setMic(true);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVoiceCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        isSpeaker = false;
        isMute = false;
        isOutgoing = getIntent().getBooleanExtra("isOutgoing", false);
        isVideoCall = getIntent().getBooleanExtra("isVideoCall", false);
        username = getIntent().getStringExtra("Name");


        daoFactory = new DAOFactory(ConnectionDB.connection);
        try {
            User user = daoFactory.getUserDAO().getInfoUser(username);
            binding.displayAvatarUser.setImageBitmap(Default.StringToBitMap(user.getAvatar()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.displayUsername.setText(username);
        binding.showTimeCall.setTextColor(Color.TRANSPARENT);
        binding.btnSpeaker.setEnabled(false);
        binding.btnMic.setEnabled(false);
        binding.callsConferenceCameraVideoBtn.setEnabled(false);

        Intent intent = new Intent(this.getApplicationContext(), LinphoneService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        binding.btnEndCall.setOnClickListener(new View.OnClickListener() {  // End the call
            @Override
            public void onClick(View view) {
                if (isBound) {
                    linphoneService.rejectCall();
                    finish();
                }
            }
        });

        binding.btnSpeaker.setOnClickListener(new View.OnClickListener() { // Switch the speaker
            @Override
            public void onClick(View view) {
                if (isBound) {
                    linphoneService.toggleSpeaker();
                    changeIconSpeaker(linphoneService.getCore());
                }
            }
        });

        binding.btnMic.setOnClickListener(new View.OnClickListener() { // Turn on/off the mic
            @Override
            public void onClick(View v) {
                if (isBound) {
                    boolean hasMic = linphoneService.isMicEnable();

                    if (hasMic) {
                        binding.btnMic.setImageResource(R.drawable.ic_mute);
                    } else {
                        binding.btnMic.setImageResource(R.drawable.ic_unmute);
                    }
                    linphoneService.setMic(!hasMic);
                }
            }
        });

        binding.callsConferenceCameraVideoBtn.setOnClickListener(new View.OnClickListener() { // Open camera in call
            @Override
            public void onClick(View v) {
                boolean enabledToggleCamera = false;

                if(isBound) {
                    if(linphoneService.getIsVideoCall()) { // if turn on camera (permission needed)
                        if(ContextCompat.checkSelfPermission(DetailCallActivity.this ,
                                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            Log.i(Constants.TAG_PERMISSION, "Camera Permissions Granted");
                            enabledToggleCamera = true;
                        } else {
                            CallService.alertGrantPermission(
                                    DetailCallActivity.this,
                                    "Permission needed",
                                    "Camera permission is needed. You must grant permission to the camera to open your camera");
                        }
                    } else { // if turn of camera
                        enabledToggleCamera = true;
                    }

                    if(enabledToggleCamera) {
                        linphoneService.toggleVideo();
                    }
                }
            }
        });

        binding.switchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isBound) {
                    linphoneService.switchCamera();
                }
            }
        });

        binding.btnBackScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationHandler.handleBackCall(DetailCallActivity.this, username, DetailCallActivity.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        NotificationHandler.handleBackCall(DetailCallActivity.this, username, DetailCallActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            linphoneService.setDetailCallActivity(null);
        }
    }

    @Override
    public void onDataChange(String key, Core core) {
        try {
            updateUI(key, core);
        } catch (InterruptedException exception) {
            Log.d("InterruptedException", exception.getMessage());
        }
    }

    private void updateUI(String key, Core core) throws InterruptedException {
        switch (key) {
            case Constants.CALL_OUTGOING_INIT:
                binding.state.setText("Connecting...");
                break;
            case Constants.CALL_OUTGOING_RINGING:
                binding.state.setText("Ringing...");
                break;
            case Constants.CALL_UPDATE_BY_REMOTE:
                handleRequestVideo();
                break;
            case Constants.CALL_CONNECTED:
                binding.state.setText("Connected");
                binding.btnSpeaker.setEnabled(true);
                binding.btnMic.setEnabled(true);
                binding.callsConferenceCameraVideoBtn.setEnabled(true);
                break;
            case Constants.CALL_STREAMS_RUNNING:
                binding.state.setText("Calling");
                updateCallLayout(core);
                startTimer();
                break;
            case Constants.CALL_END:
                pauseTimer();
                break;
            case Constants.CALL_RELEASED:
                binding.state.setText("Cancel Call");
                finish();
                break;
            case Constants.CALL_RELEASED_DECLINE:
                binding.state.setText("Declined Call");

                Handler declinedHandler = new Handler();
                declinedHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 2000);
                break;
            case Constants.CALL_RELEASED_UNAVAILABLE:
                binding.state.setText(username + " is not available");

                Handler unavailableHandler = new Handler();
                unavailableHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 2000);
                break;
            default:
                break;
        }
    }

    private void startTimer() {
        if (!running) {
            binding.showTimeCall.setBase(SystemClock.elapsedRealtime() - pauseOffset); // Returns milliseconds since boot, including time spent in sleep.
            binding.showTimeCall.start();
            running = true;
        }
    }

    private void pauseTimer() {
        if (running) {
            binding.showTimeCall.stop();
            pauseOffset = SystemClock.elapsedRealtime() - binding.showTimeCall.getBase();
            running = false;
        }
    }

    private void changeIconSpeaker(Core core) {
        if (core.getCurrentCall().getOutputAudioDevice().getType() == AudioDevice.Type.Speaker) {
            binding.btnSpeaker.setImageResource(R.drawable.ic_speaker);
        } else {
            binding.btnSpeaker.setImageResource(R.drawable.ic_speaker_off);
        }
    }

    private void handleRequestVideo() {
        if(isBound) {
            acceptVideoDialog = new AcceptVideoDialog(DetailCallActivity.this, linphoneService);
            acceptVideoDialog.showDialog("DFIVE", DetailCallActivity.this.getString(R.string.accept_video));
        }
    }

    private void updateCallLayout(Core core) {
        if (core.getCallsNb() == 0)
            return;

        Call call = core.getCurrentCall() != null ? core.getCurrentCall() : core.getCalls()[0];
        if (call == null) {
            return;
        }

        boolean isVideoActive, isRemoteVideoActive;
        if (call.getParams().isVideoEnabled()) {
            isVideoActive = true;
        } else {
            isVideoActive = false;
        }

        CallParams remoteParams = call.getRemoteParams();
        if (remoteParams != null && remoteParams.isVideoEnabled()) {
            isRemoteVideoActive = true;
        } else {
            isRemoteVideoActive = false;
        }

        if(isVideoActive && isRemoteVideoActive) {
            switchVideoCallLayout(true);
        }

        if (!isVideoActive && !isRemoteVideoActive) {
            switchVideoCallLayout(false);
        }
    }

    public void switchVideoCallLayout(boolean isVideoCall) {
        if(isVideoCall) { // Voice call <-> Video call
            binding.callsConferenceVoiceCallLayout.setVisibility(View.GONE);
            binding.callsConferenceVideoCallLayout.setVisibility(View.VISIBLE);
            binding.btnSpeaker.setVisibility(View.GONE);
            binding.switchCamera.setVisibility(View.VISIBLE);
            binding.callsConferenceCameraVideoBtn.setImageResource(R.drawable.ic_camon);
            binding.showTimeCall.setTextColor(Color.TRANSPARENT);

            if (isBound) {
                linphoneService.externalSpeaker();
                linphoneService.videoSetUp();
                linphoneService.videoDisplay(binding.localPreviewVideoSurface, binding.remoteVideoSurface);
            }
        } else {
            binding.callsConferenceVoiceCallLayout.setVisibility(View.VISIBLE);
            binding.callsConferenceVideoCallLayout.setVisibility(View.GONE);
            binding.btnSpeaker.setVisibility(View.VISIBLE);
            binding.switchCamera.setVisibility(View.GONE);
            binding.callsConferenceCameraVideoBtn.setImageResource(R.drawable.ic_camoff);
            binding.showTimeCall.setTextColor(Color.BLACK);

            if(isBound) {
                linphoneService.internalSpeaker();
            }
        }
    }

    private void handleBackCall() { // show notification when get out of detail call activiy
        Intent intent = new Intent(DetailCallActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

        makeNotification(username, "Press to return call");
    }

    private void makeNotification(String title, String message){
        Intent resultIntent = new Intent(getApplicationContext(), DetailCallActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, resultIntent, PendingIntent.FLAG_MUTABLE);

        String channelID = "CHANNEL_ID_IN_CALL";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelID);
        builder.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_call_ok)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelID);

            if(notificationChannel == null) {
                notificationChannel = new NotificationChannel(channelID, message, NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        notificationManager.notify(1, builder.build());
    }
}
