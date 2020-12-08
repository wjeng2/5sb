package com.example.touchtrack;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.MaskFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.VelocityTracker;
import android.widget.TextView;
import android.view.MotionEvent;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

    }

    static class SwipeData {
        private long ts = 0;
        private float x = 0;
        private float y = 0;
        private float touch_size = 0;
        private float pressure = 0;
        private float velocity_x = 0;
        private float velocity_y = 0;
        private String direction = "";
        private String name = "";

        SwipeData(long ts, float x, float y, float touch_size, float pressure, float velocity_x, float velocity_y, String dir, String name) {
            this.ts = ts;
            this.x = x;
            this.y = y;
            this.touch_size = touch_size;
            this.pressure = pressure;
            this.velocity_x = velocity_x;
            this.velocity_y = velocity_y;
            this.direction = dir;
            this.name = name;
        }
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

                        sdl = new ArrayList<SwipeData>();
                        swipeCount = 1;

                        break;
                }

                new SendJson().execute(sdl_json_str);
                break;

            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.recycle();
                mVelocityTracker = null;
//                position.setText("");
        }
        return true;

    }

    // https://stackoverflow.com/questions/6053602/what-arguments-are-passed-into-asynctaskarg1-arg2-arg3
    class SendJson extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            System.out.println("Sending request to echo server " + params[0]);

            String serverReply = "";

            URL url = null;
            HttpURLConnection con = null;

            try {
                url = new URL("https://postman-echo.com/post");
                con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);
                String jsonInputString = params[0];
                try(OutputStream os = con.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Json sent, length " + params[0].length());

            try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                serverReply = response.toString();
                System.out.println(serverReply);
            } catch (Exception e) {
                e.printStackTrace();
            }


            return serverReply;
        }

        @Override
        protected void onPostExecute(String serverReply) {
            super.onPostExecute(serverReply);
            Log.e("*****Server Reply*****:", serverReply+"");
        }
    }



}