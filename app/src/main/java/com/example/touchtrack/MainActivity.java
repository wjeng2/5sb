package com.example.touchtrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Path;
import android.os.Bundle;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.TextView;
import android.view.MotionEvent;

public class MainActivity extends AppCompatActivity {

    private TextView position = null;
    private float x;
    private float y;
    private VelocityTracker mVelocityTracker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        position = (TextView) findViewById(R.id.position);
//        myLayout = (ConstraintLayout) findViewById(R.id.main_layout);


//        myLayout.setOnTouchListener(new View.OnTouchListener(){
//            @Override
//            public boolean onTouch(View v, MotionEvent event){
//                x = event.getX();
//                y = event.getY();
//
//                if(event.getAction() == MotionEvent.ACTION_MOVE){
//                    String pos = Float.toString(x) + "," + Float.toString(y);
//                    position.setText(pos);
//                }
//                if(event.getAction() == MotionEvent.ACTION_UP){
//                    position.setText("");
//                }
//                return true;
//            }
//        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);
        float pressure = event.getPressure(0);
        float touch_size = event.getTouchMajor(0);
        float newX;
        float newY;
        long ms = System.currentTimeMillis();

        switch(action){
            case MotionEvent.ACTION_DOWN:
                if(mVelocityTracker == null){
                    x = event.getX();
                    y = event.getY();
                    mVelocityTracker = VelocityTracker.obtain();
                    position.setText("Time: " + Long.toString(ms));
                    position.append("\nX: " + x + " Y: " + y);
                    position.append("\n Pressure: " + pressure);
                    position.append("\n Size: "+ touch_size);
                }
                else{
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(event);
            case MotionEvent.ACTION_MOVE:
                newX = event.getX();
                newY = event.getY();
                float vx = mVelocityTracker.getXVelocity();
                float vy = mVelocityTracker.getYVelocity();


                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(100);
                position.setText("Time: " + Long.toString(ms));
                position.append("\nX: " + newX + " Y: " + newY);
                position.append("\nPressure: " + pressure);
                position.append("\nSize: "+ touch_size);
                position.append("\nX velocity: " + vx);
                position.append("\nY velocity: " + vy);
                //Predicting Direction
                String direction = "null";
                if(vx > 10 && Math.abs(vy) < 10){
                    direction = "right";
                }
                else if(vx < -10 && Math.abs(vy) < 10){
                    direction = "left";
                }
                else if(vy > 10 && Math.abs(vx) < 10){
                    direction = "down";
                }
                else if(vy < -10 && Math.abs(vx) < 10){
                    direction = "up";
                }
                position.append("\nPredicted direction: " + direction);
                break;
            case MotionEvent.ACTION_UP:
//                mVelocityTracker.recycle();
                position.setText("");
                break;
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.recycle();
//                position.setText("");
                break;
        }
        return true;
    }


}