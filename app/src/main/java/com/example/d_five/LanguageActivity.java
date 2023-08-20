package com.example.d_five;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.example.d_five.databinding.ActivityLanguageBinding;

public class LanguageActivity extends AppCompatActivity {
    ActivityLanguageBinding binding;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLanguageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initPreferences();
        binding.btnVietNamese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String  data = "vi";
//                setLocale(data);
                editor.putString("DATA", data);
                editor.commit();
                Toast.makeText(getApplicationContext(), R.string.questionLanguage_en, Toast.LENGTH_SHORT).show();

            }
        });

        binding.btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String  data = "en";
//                setLocale(data);
                editor.putString("DATA", data);
                editor.commit();
                Toast.makeText(getApplicationContext(), R.string.questionLanguage_vn, Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();
        setImageLanguage ();
    }
    private void setImageLanguage ()
    {
        String savedData = sharedPreferences.getString("DATA", "");
        String vietnamese = "vi";
        if (  savedData.equals(vietnamese))
        {
            binding.btnCheckVietNamese.setImageResource(R.drawable.ic_check);
            binding.btnCheckEnglish.setImageResource(R.color.white);
        }
        else  {

            binding.btnCheckVietNamese.setImageResource(R.color.white);
            binding.btnCheckEnglish.setImageResource(R.drawable.ic_check);
        }
    }
}