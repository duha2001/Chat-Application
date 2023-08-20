package com.example.d_five.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.d_five.R;

public class GettingStarted1Activity extends AppCompatActivity {
    private TextView skip1;
    private LinearLayout welcome_screen;
    GestureDetector gestureDetector;
    int SWIPE_THRESHOLD = 100;
    int SWIPE_VELOCITY_THRESHOLD = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gettingstarted1); // this will bind your MainActivity.class file with activity_main.

        skip1 = (TextView) findViewById(R.id.skip1);
        welcome_screen = (LinearLayout) findViewById(R.id.welcome_screen);
        gestureDetector = new GestureDetector(this, new GettingStarted1Activity.MyGesture());
        welcome_screen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });

        skip1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GettingStarted1Activity.this, LoginActivity.class);
                startActivity(i); // invoke the MainActivity.
                finish();
            }
        });
    }
    class MyGesture extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
            //kéo từ phải sang trái để chuyển từ màn hình Getting Started 1 sang Getting Started 2
            if (e1.getX() - e2.getX() > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD){
                Intent i = new Intent(GettingStarted1Activity.this, GettingStarted2Activity.class);
                startActivity(i);
                finish();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
