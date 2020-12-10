package com.example.touchtrack;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.MaskFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.VelocityTracker;
import android.widget.TextView;
import android.view.MotionEvent;
import android.widget.Toast;

import com.example.touchtrack.model.SwipeData;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class TrainActivity extends AppCompatActivity {

    List<SwipeData> sdl = new ArrayList<SwipeData>();

    private TextView position = null;
    private float x;
    private float y;
    private VelocityTracker mVelocityTracker = null;
    private String username;
    private NetworkManager networkManager;
    private int height;
    private int width;

    //https://stackoverflow.com/questions/16650419/draw-in-canvas-by-finger-android
//    private MyView mv;
    private MaskFilter  mEmboss;
    private MaskFilter  mBlur;

    Gson gson = new Gson();

    private ArrayList<ArrayList<Float>> line;
    int swipeCount = 1;
    String direction = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        position = (TextView) findViewById(R.id.position);
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

        position.bringToFront();

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
                position.setText("Time: " + Long.toString(ms));
                position.append("\nName: " + username);
                position.append("\nX: " + x + " Y: " + y);
                position.append("\nPressure: " + pressure);
                position.append("\nSize: " + touch_size);
                position.append("\nX original: " + x);
                position.append("\nY original: " + y);
                position.append("step no. " + swipeCount);


                SwipeData ss = new SwipeData(ms, x, y, touch_size, pressure, 0, 0, "null", "");
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
                position.setText("Time: " + ms);
                position.setText("\nName: "+ username);
                position.append("\nX: " + newX + " Y: " + newY);
                position.append("\nPressure: " + pressure);
                position.append("\nSize: " + touch_size);
                position.append("\nX velocity: " + vx);
                position.append("\nY velocity: " + vy);
                position.append("\nX original: " + x);
                position.append("\nY original: " + y);
                position.append("\nstep no. " + swipeCount);

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
                position.append("\nPredicted direction: " + direction);
                x = newX;
                y = newY;
                SwipeData sd = new SwipeData(ms, newX, newY, touch_size, pressure, vx, vy, direction, username);
                sdl.add(sd);

                break;

            case MotionEvent.ACTION_UP:
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                String sdl_json_str = gson.toJson(sdl);

//                position.setText("");
                ms = System.currentTimeMillis();
                switch (swipeCount){
                    case 1 :
                    case 2 :
                    case 3 :
                        swipeCount ++;
                        break;
                    case 4 :
                        try {
                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("swipe" + ms + ".json", MODE_APPEND));
                            outputStreamWriter.write(sdl_json_str + "\n");
                            outputStreamWriter.close();
                        } catch (IOException e) {
                            Log.e("Exception", "File write failed: " + e.toString());
                        }

                        // network request
                        networkManager.sendData(username, new ArrayList<SwipeData>(sdl), width, height, new NetworkManager.OnSendDataListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getApplicationContext(), "Successfully send a data!" , Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNetworkFail() {
                                Toast.makeText(getApplicationContext(), "network error!" , Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFail() {
                                Toast.makeText(getApplicationContext(), "fail!" , Toast.LENGTH_SHORT).show();
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
//                position.setText("");
        }
        return true;

    }
}