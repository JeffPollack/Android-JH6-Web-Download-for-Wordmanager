package com.example.jeff.jh5_hangman_jdpollack;

/**
 * Created by Jeff on 10/27/2015.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class myDrawHangmanView extends View  {

    Context context;
    int drawingCount=0;

    // Keeping instance variables can cut down on the number of calls to "new" which
    // can ease the burden on the Garbage collector.  onDraw routines can be called a lot

    Paint redPen, bluePen;
    RectF rectf= new RectF();
    int backgroundColor;

    // initialize all of our instance variables one time.
    private void init()
    {
        Log.d("Mine","init called in MyDrawHangmanView");
        backgroundColor = getResources().getColor(R.color.backgroundColor);

        redPen = new Paint();
        redPen.setColor(getResources().getColor(R.color.RedPen));

        bluePen = new Paint();
        bluePen.setColor(getResources().getColor(R.color.bluePen));

    }


    public myDrawHangmanView(Context context) {
        super(context);
        init();
        this.context = context;
    }
    public myDrawHangmanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        this.context = context;
    }

    public myDrawHangmanView(Context context,
                             AttributeSet ats,
                             int defaultStyle) {
        super(context, ats, defaultStyle);
        init();

        this.context = context;
    }


    public void increment()
    {
        drawingCount+= 1;
        invalidate();
    }
    public void reset()
    {
        drawingCount = 0;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {

        float width = getWidth();
        float  height = getHeight();
        // Draw the background...
        Log.d("Mine", "start onDraw width=" + width + " height=" + height);
        canvas.save();
        canvas.scale(width / 600, height / 650);
        canvas.drawColor(backgroundColor);
        Paint paint = new Paint();
        paint.setStrokeWidth(50);

        for (int i=1; i <= drawingCount; i++)
        {
            switch(i)
            {
                case 1:
                    canvas.drawRect(20, 530, 570, 550, bluePen);//base
                    break;
                case 2:
                    canvas.drawRect(550,20,570,550,bluePen);//rise
                    break;
                case 3:
                    canvas.drawRect(300, 20, 570, 50, bluePen);//header
                    break;
                case 4:
                    canvas.drawRect(300, 20, 310, 100, bluePen);//hanger
                    break;
                case 5:
                    canvas.drawCircle(300, 100, 20, redPen);//head
                    break;
                case 6:
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(50);
                    canvas.drawLine(300, 100, 300, 250, redPen);//body
                    break;
                case 7:
                    canvas.drawLine(300, 150, 380, 100, redPen);//right arm
                    break;
                case 8:
                    canvas.drawLine(300, 150, 220, 100, redPen);//left arm
                    break;
                case 9:
                    canvas.drawLine(300, 250, 400, 450, redPen);//right leg
                    break;
                case 10:
                    canvas.drawLine(300, 250, 200, 450, redPen);//left leg
                    break;

            }
        }


        canvas.restore();
    }
}