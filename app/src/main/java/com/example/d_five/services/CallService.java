package com.example.d_five.services;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.d_five.callsession.DetailCallActivity;
import com.example.d_five.utilities.Constants;

public class CallService {
    public static void alertGrantPermission(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();
    }

    public static void makeVoiceCall(Context context, String username) {
        if(ContextCompat.checkSelfPermission(context,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            Log.i(Constants.TAG_PERMISSION, "Permissions Granted (Call phone, Microphone)");
            Intent intent = new Intent(context, DetailCallActivity.class);
            intent.putExtra("Name", username);
            intent.putExtra("isOutgoing", true);
            intent.putExtra("isVideoCall", false);
            context.startActivity(intent);
        } else {
            alertGrantPermission(
                    context,
                    "Permission needed",
                    "Permissions (Call phone, Microphone) are needed. You must grant permission to the microphone and phone to make voice call");
        }
    }

    public static void makeVideoCall(Context context, String username) {
        if(ContextCompat.checkSelfPermission(context , Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context ,Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(Constants.TAG_PERMISSION, "Permissions Granted (Camera, Call phone, Microphone)");
            Intent intent = new Intent(context, DetailCallActivity.class);
            intent.putExtra("Name", username);
            intent.putExtra("isOutgoing", true);
            intent.putExtra("isVideoCall", true);
            context.startActivity(intent);
        } else {
            alertGrantPermission(
                    context,
                    "Permission needed",
                    "Permissions (Camera, Call phone, Microphone) are needed. You must grant permission to the microphone, the call phone amd the camera to make video call");
        }
    }
}
