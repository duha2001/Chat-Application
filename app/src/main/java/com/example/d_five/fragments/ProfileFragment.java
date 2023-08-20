package com.example.d_five.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.d_five.LanguageActivity;
import com.example.d_five.R;
import com.example.d_five.activities.MainActivity;
import com.example.d_five.activities.SettingActivity;
import com.example.d_five.dao.ConnectionDB;
import com.example.d_five.dao.implementDAO.UserDAO;
import com.example.d_five.dialogs.LogoutDialog;
import com.example.d_five.model.User;
import com.example.d_five.services.SaveSharedService;
import com.example.d_five.utilities.Constants;
import com.example.d_five.utilities.ImageHandler;
import com.example.d_five.utilities.PreferenceManager;

import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private PreferenceManager preferenceManager;
    private SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static boolean dark_mode;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button logoutBtn, refreshBtn, subscribeBtn, language, changeName, setting;
    private CircleImageView image;
    private String encodeImage;
    private TextView display_username;
    private TextView display_impu;
    private UserDAO userDAO;
    private User myself;
    LogoutDialog logoutDialog;
    ImageHandler imageHandler;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        logoutBtn = view.findViewById(R.id.btn_logout);
        refreshBtn = view.findViewById(R.id.btn_refresh);
        subscribeBtn = view.findViewById(R.id.btn_subscribe);
        image = view.findViewById(R.id.display_avatar_user);
        display_username = view.findViewById(R.id.display_username);
        display_impu = view.findViewById(R.id.display_impu);
        language = view.findViewById(R.id.btn_language);
        changeName = view.findViewById(R.id.btn_changeName);
        setting = view.findViewById(R.id.btn_setting);

        // init DB
        imageHandler = new ImageHandler(getContext());
        preferenceManager = new PreferenceManager(getContext());
        userDAO = new UserDAO(ConnectionDB.connection);

        try {
            myself = userDAO.getInfoUser(preferenceManager.getString(Constants.USER_NAME));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        // set View UI
        if (myself.getNickname() == null)
            display_username.setText(preferenceManager.getString(Constants.USER_NAME));
        else {
            display_username.setText(myself.getNickname());
        }
        display_impu.setText("sip:" + preferenceManager.getString(Constants.USER_NAME) + "@dfive-ims.dek.vn");
        image.setImageBitmap(imageHandler.decodeResource(myself.getAvatar()));


        // set ONCLICK
        image.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        logoutBtn.setOnClickListener(view1 -> logout());
        refreshBtn.setOnClickListener(view1 -> refresh());
        subscribeBtn.setOnClickListener(view1 -> subscribe());

        language.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), LanguageActivity.class);
            startActivity(intent);
        });
        setting.setOnClickListener(view1 -> {
                    Intent intent = new Intent(getActivity(), SettingActivity.class);
                    startActivity(intent);
                });

        //darkMode.setOnClickListener(view13 -> setDarkMode());
        changeName.setOnClickListener(view12 -> changeName());

        return view;
    }

    private void changeName() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        final View inflator = layoutInflater.inflate(R.layout.change_name_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(inflator);

        EditText nickname = inflator.findViewById(R.id.inputNewName);
        Button btnChange = inflator.findViewById(R.id.btnChange);
        Button btnCancel = inflator.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnChange.setOnClickListener(view -> {
            String newName = nickname.getText().toString();
            display_username.setText(newName);
            myself.setNickname(newName);
            try {
                userDAO.updateInfoUser(myself);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(view -> dialog.cancel());
        dialog.show();
    }

    private void setDarkMode() {
        if (dark_mode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor = sharedPreferences.edit();
            editor.putBoolean("night", false);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor = sharedPreferences.edit();
            editor.putBoolean("night", true);
        }
        editor.apply();
    }

    private void subscribe() {
        MainActivity mainActivity = (MainActivity) this.getContext();
        if (mainActivity != null) {
            mainActivity.getLinphoneService().subscribe();
            if (mainActivity.getLinphoneService().isSubscribe()) {
                Drawable img = getContext().getResources().getDrawable(R.drawable.ic_unsubscribe);
                subscribeBtn.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                subscribeBtn.setText(getContext().getString(R.string.unsubscribe));
            } else {
                Drawable img = getContext().getResources().getDrawable(R.drawable.ic_subscribe);
                subscribeBtn.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                subscribeBtn.setText(getContext().getString(R.string.subscribe));
            }
        }
    }

    private void refresh() {
        MainActivity mainActivity = (MainActivity) this.getContext();
        assert mainActivity != null;
        mainActivity.getLinphoneService().reRegister();
    }

    private void logout() {
        logoutDialog = new LogoutDialog(this.getContext());
        logoutDialog.showDialog("DFIVE", getContext().getString(R.string.exit_or_not));
    }


    private void updateInfoDB(User user) {
        try {
            User u = userDAO.updateInfoUser(user);
            System.out.println(u);
            Toast.makeText(getContext(), "Update Image OK !", Toast.LENGTH_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = this.getContext().getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            image.setImageBitmap(bitmap);
                            ImageHandler imageHandler = new ImageHandler(getContext());
                            encodeImage = imageHandler.encodeImage(bitmap); // String : use save to db

                            myself.setAvatar(encodeImage);
                            updateInfoDB(myself);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
}