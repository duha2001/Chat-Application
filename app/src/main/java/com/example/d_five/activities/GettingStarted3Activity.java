package com.example.d_five.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.d_five.R;

public class GettingStarted3Activity extends AppCompatActivity {
    private Button nextLoginScreen;
    private LinearLayout calling_screen;
    GestureDetector gestureDetector;
    int SWIPE_THRESHOLD = 100;
    int SWIPE_VELOCITY_THRESHOLD = 100;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gettingstarted3);

        nextLoginScreen = (Button) findViewById(R.id.btn_nextLoginScreen);
        calling_screen = (LinearLayout) findViewById(R.id.calling_screen);

        gestureDetector = new GestureDetector(this, new GettingStarted3Activity.MyGesture());
        calling_screen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });

        nextLoginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GettingStarted3Activity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
    class MyGesture extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
            //kéo từ trái sang phải để chuyển từ màn hình Getting Started 3 sang Getting Started 2
            if (e2.getX() - e1.getX() > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD){
                Intent i = new Intent(GettingStarted3Activity.this, GettingStarted2Activity.class);
                startActivity(i);
                finish();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
