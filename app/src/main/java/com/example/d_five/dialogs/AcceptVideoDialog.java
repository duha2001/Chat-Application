package com.example.d_five.dialogs;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.d_five.R;
import com.example.d_five.callsession.DetailCallActivity;
import com.example.d_five.services.LinphoneService;

public class AcceptVideoDialog {

    Context context;
    Dialog dialog;
    LinphoneService linphoneService;

    public AcceptVideoDialog(Context context, LinphoneService linphoneService) {
        this.context = context;
        this.linphoneService = linphoneService;
    }

    public void showDialog(String title, String question) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.accept_video_dialog_layout);
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
                linphoneService.updateVideoCall(true);
                dialog.dismiss();
            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linphoneService.updateVideoCall(false);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
