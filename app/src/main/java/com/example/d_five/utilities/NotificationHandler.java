package com.example.d_five.utilities;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.d_five.R;
import com.example.d_five.activities.ChatBoxActivity;
import com.example.d_five.activities.ChatRoomBoxActivity;
import com.example.d_five.activities.MainActivity;
import com.example.d_five.activities.SettingActivity;
import com.example.d_five.dao.ConnectionDB;
import com.example.d_five.dao.DAOFactory;
import com.example.d_five.model.Conversation;
import com.example.d_five.model.User;
import com.example.d_five.services.SaveSharedService;

import java.io.IOException;

public class NotificationHandler {
    public static MediaPlayer mediaCall, mediaChat;
    public static CameraManager cameraManager;
    public static String cameraId;
    public static boolean enableRing, enableFlash;
    public static Thread flashCallThread;


    public static void initNotification(Context context) { // just init once when onCreate() in MainActivity
        enableRing = SaveSharedService.getRing(context);
        enableFlash = SaveSharedService.getFlash(context);

        mediaChat = MediaPlayer.create(context, R.raw.notify);

        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    public static void switchRinging(Context context, boolean enableRingSetting) {
        SaveSharedService.setRing(context, enableRingSetting);
        enableRing = enableRingSetting;


    }

    public static void switchFlash(Context context, boolean enableFlashSetting) {
        SaveSharedService.setFlash(context, enableFlashSetting);
        enableFlash = enableFlashSetting;
    }

    public static void handleNewMessage(Context context) {
        try {
            if (enableRing) {
                mediaChat.start();
            }

            if(enableFlash && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                String flashString = "101010";

                for(int i = 0; i < flashString.length(); i++) {
                    if(flashString.charAt(i) == '1') {
                        cameraManager.setTorchMode(NotificationHandler.cameraId, true);
                    } else {
                        cameraManager.setTorchMode(NotificationHandler.cameraId, false);
                    }
                    Thread.sleep(50);
                }
            }
        } catch (InterruptedException exception) {
            Log.d("InterruptedException", exception.getMessage());
        } catch (CameraAccessException exception) {
            Log.d("CameraAccessException", exception.getMessage());
        }
    }

    public static void startIncomingCall(Context context) {
        if (enableRing) {
            mediaCall = MediaPlayer.create(context, R.raw.ringtone);
            mediaCall.start();
        }

        if (enableFlash && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            flashCallThread = new Thread() {
                public void run() {
                    boolean isFlash = true;
                    try {
                        while (!interrupted() && enableFlash) {
                            cameraManager.setTorchMode(cameraId, isFlash);
                            isFlash = !isFlash;
                            Thread.sleep(100);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (CameraAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            flashCallThread.start();
        }
    }

    public static void stopIncomingCall() {
        if (mediaCall != null) {
            mediaCall.stop();
        }

        if (flashCallThread != null) {
            flashCallThread.interrupt();
        }

        if (cameraManager != null) {
            try {
                cameraManager.setTorchMode(cameraId, false);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void handleBackCall(Context context, String username, Activity activity) { // show notification when get out of detail/incoming call activity
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);

        makeCallNotification(context, "CHANNEL_ID_IN_CALL", username, "Press to return call", activity);
    }

    public static void makeCallNotification(Context context, String channelID, String title, String message, Activity activity) {
        Intent resultIntent = new Intent(context, activity.getClass());
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 1, resultIntent, PendingIntent.FLAG_MUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID);
        builder.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_call_ok)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelID);

            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(channelID, message, NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        notificationManager.notify(1, builder.build());
    }
}
