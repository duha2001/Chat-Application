package com.example.d_five.utilities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.d_five.R;
import com.example.d_five.activities.MainActivity;

public class DeleteMessageDialog {
    Context context;
    Dialog dialog;

    DeleteMessageDialog(Context context){
        this.context = context;
    }

    public void showDialog(String title, String question) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.delete_message_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setOnCancelListener(null);
        dialog.setCanceledOnTouchOutside(true);


        TextView appName = dialog.findViewById(R.id.del_text_app_name);
        TextView message = dialog.findViewById(R.id.delete_dialog);
        Button btn_yes = dialog.findViewById(R.id.delete_mess_yes);
        Button btn_no = dialog.findViewById(R.id.delete_mess_no);
        appName.setText(title);
        message.setText(question);

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                MainActivity mainActivity = (MainActivity) context;
//                mainActivity.getLinphoneService().deRegister(mainActivity);
////                LinphoneService.getInstance().deRegister();
            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.create();
        dialog.show();
    }

    public void HideDialog() {
        dialog.dismiss();
    }
}
