package com.example.d_five.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.d_five.R;
import com.example.d_five.activities.LoginActivity;
import com.example.d_five.activities.MainActivity;
import com.example.d_five.services.LinphoneService;
import com.example.d_five.services.SaveSharedService;

public class LogoutDialog {

    Context context;
    Dialog dialog;

    public LogoutDialog(Context context) {
        this.context = context;
    }

    public void showDialog(String title, String question) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.logout_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setOnCancelListener(null);
        dialog.setCanceledOnTouchOutside(true);

        TextView appName = dialog.findViewById(R.id.text_app_name);
        TextView message = dialog.findViewById(R.id.text_logout_dialog);
        Button btn_yes = dialog.findViewById(R.id.button_yes);
        Button btn_no = dialog.findViewById(R.id.button_no);
        appName.setText(title);
        message.setText(question);

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) context;

                LinphoneService linphoneService = mainActivity.getLinphoneService();
                if (linphoneService == null) {
                    Intent intent = new Intent(mainActivity.getApplicationContext(), LoginActivity.class);
                    mainActivity.startActivity(intent);
                } else {
                    mainActivity.getLinphoneService().deRegister();
                }
                SharedPreferences.Editor editor = SaveSharedService.getSharedPreferences(mainActivity.getApplicationContext()).edit();
                editor.clear(); //clear all stored data
                editor.commit();
            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
