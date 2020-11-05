// package com.example.touchtrack;

// import android.content.Context;
// import android.graphics.Canvas;
// import android.graphics.Color;
// import android.graphics.Paint;
// import android.util.AttributeSet;
// import android.view.MotionEvent;
// import android.view.View;

// public class DrawingView extends View {

//     int mStartX;
//     int mStartY;
//     int mEndX;
//     int mEndY;

//     Paint mPaint = new Paint();

//     int mSelectedColor = Color.BLACK;

//     public DrawingView(Context context, AttributeSet attrs, int defStyle) {

//         super(context, attrs, defStyle);

//         mPaint.setColor(mSelectedColor);
//         mPaint.setStrokeWidth(5);
//         mPaint.setStyle(Paint.Style.STROKE);

//         setFocusable(true);
//     }

//     public DrawingView(Context context, AttributeSet attrs) {
//         this(context, attrs, 0);
//     }

//     @Override
//     public boolean onTouchEvent(MotionEvent event) {

//         switch (event.getActionMasked()) {

//             case MotionEvent.ACTION_DOWN:

//                 mStartX = (int) event.getX();
//                 mStartY = (int) event.getY();

//                 break;

//             case MotionEvent.ACTION_MOVE:

//                 mEndX = (int) event.getX();
//                 mEndY = (int) event.getY();

//                 invalidate();

//                 break;

//             case MotionEvent.ACTION_UP:


//                 mEndX = (int) event.getX();
//                 mEndY = (int) event.getY();

//                 invalidate();

//                 break;

//             default:

//                 super.onTouchEvent(event);

//                 break;
//         }

//         return true;
//     }

//     @Override
//     protected void onDraw(Canvas canvas) {

//         super.onDraw(canvas);

//         canvas.drawRect(mStartX, mStartY, mEndX, mEndY, mPaint);
//     }
// }