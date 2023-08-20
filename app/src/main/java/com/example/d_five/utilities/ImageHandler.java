package com.example.d_five.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.d_five.R;
import com.example.d_five.model.Default;

import java.io.ByteArrayOutputStream;

public class ImageHandler {

    private Context context;
    public ImageHandler(Context context){
        this.context = context;
    }
    public String encodeImage(Bitmap bitmap){
        return Default.BitMapToString(bitmap);
    }

    public Bitmap decodeResource(int imageResource){
        return BitmapFactory.decodeResource(context.getResources(), imageResource);
    }

    public Bitmap decodeResource(String textResource){
       return Default.StringToBitMap(textResource);
    }
}
