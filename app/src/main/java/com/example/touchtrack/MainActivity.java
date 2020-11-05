package com.example.touchtrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Path;
import android.os.Bundle;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.TextView;
import android.view.MotionEvent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import com.google.gson.*;

import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView position = null;
    private float x;
    private float y;
    private VelocityTracker mVelocityTracker = null;

    private MyView mv;
    Gson gson = new Gson();
    List<SwipeData> sdl = new ArrayList<SwipeData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        position = (TextView) findViewById(R.id.position);

    }

    class SwipeData {
        private long ts = 0;
        private float x = 0;
        private float y = 0;
        private float touch_size = 0;
        private float pressure = 0;
        private float velocity_x = 0;
        private float velocity_y = 0;

        SwipeData(long ts, float x, float y, float touch_size, float pressure, float velocity_x, float velocity_y) {
            this.ts = ts;
            this.x = x;
            this.y = y;
            this.touch_size = touch_size;
            this.pressure = pressure;
            this.velocity_x = velocity_x;
            this.velocity_y = velocity_y;
        }
    }

    public class MyView extends View {


        private static final float MINP = 0.25f;
        private static final float MAXP = 0.75f;

        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;


        private Paint mPaint;
        private MaskFilter mEmboss;
        private MaskFilter mBlur;

        public MyView(Context c) {
            super(c);

            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);


            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setColor(0xFFFF0000);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(12);

            mEmboss = new EmbossMaskFilter(new float[]{1, 1, 1},
                    0.4f, 6, 3.5f);

            mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
        }

    @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(0xFFAAAAAA);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

            canvas.drawPath(mPath, mPaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        public void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        public void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
            }
        }

        public void touch_up() {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

//        @Override
//        public boolean onTouchEvent(MotionEvent event) {
//            float x = event.getX();
//            float y = event.getY();
//
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    touch_start(x, y);
//                    invalidate();
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    touch_move(x, y);
//                    invalidate();
//                    break;
//                case MotionEvent.ACTION_UP:
//                    touch_up();
//                    invalidate();
//                    break;
//            }
//            return true;
//        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int index = event.getActionIndex();
        int pointerId = event.getPointerId(index);

        int action = event.getActionMasked();
        float pressure = event.getPressure(0);
        float touch_size = event.getTouchMajor(0);
        float newX;
        float newY;
        long ms = System.currentTimeMillis();

        position.bringToFront();
//        setContentView(new MyView(this));
//
//        mPaint = new Paint();
//        mPaint.setAntiAlias(true);
//        mPaint.setDither(true);
//        mPaint.setColor(0xFFFF0000);
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeJoin(Paint.Join.ROUND);
//        mPaint.setStrokeCap(Paint.Cap.ROUND);
//        mPaint.setStrokeWidth(12);
//        mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 },
//                0.4f, 6, 3.5f);
//
//        mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                    mVelocityTracker = VelocityTracker.obtain();
                position.setText("Time: " + Long.toString(ms));
                position.append("\nX: " + x + " Y: " + y);
                position.append("\n Pressure: " + pressure);
                    position.append("\n Size: " + touch_size);
                } else {
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(event);

//                drawPath.moveTo(newX, newY);

            case MotionEvent.ACTION_MOVE:
                newX = event.getX();
                newY = event.getY();

//                mv.touch_move(newX, newY);
//                mv.invalidate();

                float vx = mVelocityTracker.getXVelocity();
                float vy = mVelocityTracker.getYVelocity();

                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(100);
                position.setText("Time: " + Long.toString(ms));
                position.append("\nX: " + newX + " Y: " + newY);
                position.append("\nPressure: " + pressure);
                position.append("\nSize: " + touch_size);
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

                SwipeData sd = new SwipeData(ms, newX, newY, touch_size, pressure, vx, vy);
                System.out.println(gson.toJson(sd));
                sdl.add(sd);
                System.out.println(sdl.size());
                break;

            case MotionEvent.ACTION_UP:
//                mv.touch_up();
//                mv.invalidate();

//                mVelocityTracker.recycle();
//                position.setText("");
//                try (Writer writer = new FileWriter("./Output.json")) {

                String sdl_json_str = gson.toJson(sdl);
                System.out.println("sdl_json_str: " + sdl_json_str);
//                } catch (Exception e){
//                    e.printStackTrace();
//                }


                break;
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.recycle();
//                position.setText("");
                break;
        }
        return true;

    }


}