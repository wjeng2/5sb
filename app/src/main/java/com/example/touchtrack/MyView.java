//package com.example.touchtrack;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.view.View;
//
//public static class MyView extends View {
//    public Bitmap mBitmap;
//    public Canvas mCanvas;
//    public Path mPath;
//    public Paint mBitmapPaint;
//    public Paint mPaint;
//
//    public MyView(Context c) {
//        super(c);
//
//        mPath = new Path();
//        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
//    }
//
//    @Override
//    public void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//        mCanvas = new Canvas(mBitmap);
//    }
//
//    @Override
//    public void onDraw(Canvas canvas) {
//        canvas.drawColor(Color.TRANSPARENT);
//        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
//        canvas.drawPath(mPath, mPaint);
//    }
//
//
//    public float mX, mY;
//    public static final float TOUCH_TOLERANCE = 4;
//
//    public void touch_start(float x, float y) {
//        mPath.reset();
//        mPath.moveTo(x, y);
//        mX = x;
//        mY = y;
//    }
//    public void touch_move(float x, float y) {
//        float dx = Math.abs(x - mX);
//        float dy = Math.abs(y - mY);
//        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
//            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
//            mX = x;
//            mY = y;
//        }
//    }
//    public void touch_up() {
//        mPath.lineTo(mX, mY);
//        // commit the path to our offscreen
//        mCanvas.drawPath(mPath, mPaint);
//        // kill this so we don't double draw
//        mPath.reset();
//    }
//}
