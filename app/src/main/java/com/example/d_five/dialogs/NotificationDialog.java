package com.example.d_five.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.d_five.R;

public class NotificationDialog {
    private Context context;
    private Dialog dialog;

    public NotificationDialog(Context context) {
        this.context = context;
    }

    public void showDialog(String title, String question) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.notification_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setOnCancelListener(null);
        dialog.setCanceledOnTouchOutside(true);

        TextView appName = dialog.findViewById(R.id.text_app_name);
        TextView message = dialog.findViewById(R.id.text_notification_dialog);
        Button okBtn = dialog.findViewById(R.id.button_ok);
        appName.setText(title);
        message.setText(question);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        if (dialog != null) {
            dialog.show();
        }
    }
}
