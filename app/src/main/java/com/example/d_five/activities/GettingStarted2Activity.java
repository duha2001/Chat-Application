package com.example.d_five.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.d_five.R;

public class GettingStarted2Activity extends AppCompatActivity {
    private TextView skip2;
    private LinearLayout message_screen;
    GestureDetector gestureDetector;
    int SWIPE_THRESHOLD = 100;
    int SWIPE_VELOCITY_THRESHOLD = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gettingstarted2);

        skip2 = (TextView) findViewById(R.id.skip2);
        message_screen = (LinearLayout) findViewById(R.id.message_screen);

        gestureDetector = new GestureDetector(this, new GettingStarted2Activity.MyGesture());
        message_screen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });

        skip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GettingStarted2Activity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
    class MyGesture extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
            //kéo từ trái sang phải để chuyển từ màn hình Getting Started 2 sang Getting Started 1
            if (e2.getX() - e1.getX() > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD){
                Intent i = new Intent(GettingStarted2Activity.this, GettingStarted1Activity.class);
                startActivity(i);
                finish();
            }

            //kéo từ phải sang trái để chuyển từ màn hình Getting Started 2 sang Getting Started 3
            if (e1.getX() - e2.getX() > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD){
                Intent i = new Intent(GettingStarted2Activity.this, GettingStarted3Activity.class);
                startActivity(i);
                finish();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
