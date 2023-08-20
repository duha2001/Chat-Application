package com.example.d_five.activities;

import static com.example.d_five.model.Default.StringToBitMap;
import static com.example.d_five.model.Default.saveToInternalStorage;
import static com.example.d_five.model.Default.writeToFile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.d_five.R;
import com.example.d_five.databinding.ActivityDetailImageBinding;
import com.example.d_five.utilities.ExtendedDataHolder;

import java.io.IOException;

public class DetailImageActivity extends AppCompatActivity {

    private ActivityDetailImageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ExtendedDataHolder extras = ExtendedDataHolder.getInstance();
        if (extras.hasExtra("img_src")) {
            String src = (String) extras.getExtra("img_src");
            binding.imgDetail.setImageBitmap(StringToBitMap(src));
        }

        if (extras.hasExtra("sender")) {
            String sender = (String) extras.getExtra("sender");
            binding.textUsernameImg.setText(sender);
        }

        if (extras.hasExtra("time")) {
            String time = (String) extras.getExtra("time");
            binding.textShowTimeImg.setText("Sent at: " + time);
        }


        //System.out.println(src);
        //binding.imgDetail.setImageBitmap(StringToBitMap(src));
        binding.btnBackScreenImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extras.clear();
                finish();
            }
        });

        binding.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String res = saveToInternalStorage(StringToBitMap((String) extras.getExtra("img_src")), getApplicationContext());
                if(!res.equals(null)){
                    //System.out.println(res);
                    Toast.makeText(getApplicationContext(),"Downloaded", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    }
