package com.example.touchtrack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.VelocityTracker;
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

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action = event.getActionMasked();
        float pressure = event.getPressure(0);
        float touch_size = event.getTouchMajor(0);
        float newX;
        float newY;


        switch(action){
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                long ms = System.currentTimeMillis();
                position.setText("Time: " + Long.toString(ms));
                position.append("\nX: " + x + " Y: " + y);
                position.append("\n Pressure: " + pressure);
                position.append("\n Size: "+ touch_size);
                if(mVelocityTracker == null){
                    mVelocityTracker = VelocityTracker.obtain();
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
                ms = System.currentTimeMillis();

                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(100);
                position.setText("Time: " + Long.toString(ms));
                position.append("\nX: " + newX + " Y: " + newY);
                position.append("\nPressure: " + pressure);
                position.append("\nSize: "+ touch_size);
                position.append("\nX velocity: " + vx);
                position.append("\nY velocity: " + vy);
                position.append("\nX original: " + x);
                position.append("\nY original: " + y);
                //Predicting Direction
                String direction = "null";
                float diffx = newX - x;
                float diffy = newY - y;

                if(Math.abs(diffx) > Math.abs(diffy)){
                    if(diffx>0){
                        direction = "right";
                    }
                    else{
                        direction = "left";
                    }
                }
                else if(Math.abs(diffx) < Math.abs(diffy)){
                    if(diffy > 0){
                        direction = "down";
                    }
                    else{
                        direction = "up";
                    }
                }
                else{
                    direction = "null";
                }
                position.append("\nPredicted direction: " + direction);
                break;

            case MotionEvent.ACTION_UP:
                position.setText("");
                break;
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.recycle();
                break;
        }
        return true;
    }


}