package com.example.face.Helper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class RectOverlay extends GraphicsOverlay.Graphic{

    private  int mRectColor= Color.GREEN;
    private float mStrokeWidth=4.0f;
    private Paint mReactPaint;
    private  GraphicsOverlay graphicsOverlay;
    private  Rect rect;




    public RectOverlay(GraphicsOverlay graphicsoverlay , Rect rect){
        super(graphicsoverlay);
        mReactPaint=new Paint();
        mReactPaint.setColor(mRectColor);
        mReactPaint.setStyle(Paint.Style.STROKE);
        mReactPaint.setStrokeWidth(mStrokeWidth);

        this.graphicsOverlay=graphicsOverlay;
        this.rect=rect;

        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas){

        RectF rectF = new RectF(rect);
        rectF.left = translateX(rectF.left);
        rectF.right= translateX(rectF.right);
        rectF.top=translateX(rectF.top);
        rectF.bottom = translateX(rectF.bottom);

        canvas.drawRect(rectF, mReactPaint);




    }

}
