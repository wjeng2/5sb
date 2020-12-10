package com.example.touchtrack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.VelocityTracker;
import android.widget.TextView;
import android.view.MotionEvent;
import android.widget.Toast;

import com.example.touchtrack.model.SwipeData;

import java.util.ArrayList;
import java.util.List;


public class PredictActivity extends AppCompatActivity {

    List<SwipeData> sdl = new ArrayList<SwipeData>();

    private TextView result = null;
    private float x;
    private float y;
    private VelocityTracker mVelocityTracker = null;
    private String username;
    private NetworkManager networkManager;
    private int height;
    private int width;

    int swipeCount = 1;
    String direction = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict);

        result = (TextView) findViewById(R.id.result);
        username = getIntent().getStringExtra("USERNAME");

        networkManager = NetworkManager.getInstance(getApplicationContext());
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        float pressure = event.getPressure(0);
        float touch_size = event.getTouchMajor(0);
        float newX;
        float newY;

        long ms;
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                ms = System.currentTimeMillis();
                x = event.getX();
                y = event.getY();
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }

                mVelocityTracker.addMovement(event);

                SwipeData ss = new SwipeData(ms, x, y, touch_size, pressure, 0, 0, "null");
                sdl.add(ss);
                break;

            case MotionEvent.ACTION_MOVE:

                newX = event.getX();
                newY = event.getY();

                float vx = mVelocityTracker.getXVelocity();
                float vy = mVelocityTracker.getYVelocity();
                ms = System.currentTimeMillis();

                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(100);

                //Predicting Direction
                direction = "null";
                float diffx = newX - x;
                float diffy = newY - y;

                if (Math.abs(diffx) > Math.abs(diffy)) {
                    if (diffx > 0) {
                        direction = "right";
                    } else {
                        direction = "left";
                    }
                } else if (Math.abs(diffx) < Math.abs(diffy)) {
                    if (diffy > 0) {
                        direction = "down";
                    } else {
                        direction = "up";
                    }
                } else {
                    direction = "null";
                }

                x = newX;
                y = newY;
                SwipeData sd = new SwipeData(ms, newX, newY, touch_size, pressure, vx, vy, direction);
                sdl.add(sd);

                break;

            case MotionEvent.ACTION_UP:
                mVelocityTracker.recycle();
                mVelocityTracker = null;

                switch (swipeCount){
                    case 1 :
                    case 2 :
                    case 3 :
                        swipeCount ++;
                        break;
                    case 4 :
                        // network request
                        networkManager.predict(username, new ArrayList<SwipeData>(sdl), width, height, new NetworkManager.OnPredictListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getApplicationContext(), "Successfully send predict!" , Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNetworkFail() {
                                Toast.makeText(getApplicationContext(), "predict network error!" , Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFail() {
                                Toast.makeText(getApplicationContext(), "predict fail!" , Toast.LENGTH_SHORT).show();
                            }
                        });

                        sdl = new ArrayList<SwipeData>();
                        swipeCount = 1;

                        break;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.recycle();
                mVelocityTracker = null;
        }
        return true;
    }
}