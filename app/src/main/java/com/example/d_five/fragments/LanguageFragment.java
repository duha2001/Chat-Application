package com.example.d_five.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.d_five.R;
import com.example.d_five.databinding.FragmentLanguageBinding;

public class LanguageFragment extends Fragment {
    public static String TAG = "Language Fragment";
    private FragmentLanguageBinding binding;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public LanguageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLanguageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPreferences();
        binding.btnVietNamese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String  data = "vi";
                editor.putString("DATA", data);
                editor.commit();
                Toast.makeText(getActivity(), R.string.questionLanguage_en, Toast.LENGTH_SHORT).show();

            }
        });

        binding.btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String  data = "en";
                editor.putString("DATA", data);
                editor.commit();
                Toast.makeText(getActivity(), R.string.questionLanguage_vn, Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               getParentFragmentManager().popBackStack();
            }
        });
    }

    private void initPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
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