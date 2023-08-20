package com.example.d_five.model;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import org.linphone.core.Content;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

public class Default {

    // , "8.8.8.8", "8.8.4.4"
    public static String P_CSCF = "5060";
    public static String S_CSCF = "6060";

    public static String[] dnsList = {""};

    public static String identity(String domain, String username) {
        return "sip:" + username + "@" + domain + ":" + S_CSCF;
    }

    public static String serverAddress(String domain) {
        return "sip:" + domain + ":" + P_CSCF  + ";transport=tcp";
    }

    public static String S_CSCFAddress(String domain) {
        return "sip:uri-list" + domain + ":" + S_CSCF  + ";transport=tcp";
    }

    public static String formatTimeCreate(String timeCreate) {
        String day = timeCreate.substring(0, 10);
        String time = timeCreate.substring(11, 16);
        String dayOfWeek = "";

        try {
            Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(day);
            System.out.println(date1);
            dayOfWeek = new SimpleDateFormat("EEEE").format(date1);
            System.out.println(dayOfWeek);
        } catch (Exception e) {
            System.out.println(e);
        }
        timeCreate = dayOfWeek + ", " + time;
        return timeCreate;
    }

    public static String createAddress(String username) {
        return "sip:" + username + "@dfive-ims.dek.vn";
    }


    public static String createTimeStamp(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDateTime = new Date();
        String timeCreate = formatter.format(currentDateTime);
        return formatTimeCreate(timeCreate);
    }

    public static String createTimeStamp(LocalDateTime time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedDateTime = time.format(formatter);
        return formattedDateTime;
    }

    public static String BitMapToString(Bitmap bitmap){
        Bitmap converetdImage = getResizedBitmap(bitmap, 500);
        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        converetdImage.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static TypeMessage mappingTypeMessage(String type){
        switch (type) {
            case "IMAGE":
                return TypeMessage.IMG;
            case "CALL" :
                return TypeMessage.CALL;
            case "MESSAGE":
                return TypeMessage.MSG;
        }
        return null;
    }


    private static File getOutputMediaFile(Context context){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }
    public static void writeToFile(String src, Context context) throws IOException {
        File pictureFile = getOutputMediaFile(context);
        if (pictureFile == null) {

            Log.e(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            Bitmap image = StringToBitMap(src);
            image.compress(Bitmap.CompressFormat.PNG, 40, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        //refreshGallery(outFile);
    }

    public static String saveToInternalStorage(Bitmap bitmapImage, Context context){
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());

        File storageDir = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Camera/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        // Create imageDir
        String name = "DFive-" + UUID.randomUUID().toString() + ".jpg";
        File mypath = new File(storageDir,name);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 50, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        MediaScannerConnection.scanFile(context,
                new String[] { mypath.toString() }, null,
                (path, uri) -> {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                });
        return storageDir.getAbsolutePath();
    }

    public static String INCOMING_CALL = "INCOMING CALL";
    public static String LOGIN = "LOGIN";
    public static String LOGOUT = "LOGOUT";
}
