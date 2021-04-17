package com.dana.puzzle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.view.View;


class V extends View {
    Path path = new Path();
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    float length;
    float[] intervals = {0, 0};

    public V(Context context) {
        super(context);
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        path.reset();
        RectF rect = new RectF(0, 0, w, h);
        float inset = paint.getStrokeWidth();
        rect.inset(inset, inset);

        path.addRoundRect(rect, 100, 100, Path.Direction.CW);
        length = new PathMeasure(path, false).getLength();
        intervals[0] = intervals[1] = length;
        PathEffect effect = new DashPathEffect(intervals, length);
        paint.setPathEffect(effect);
    }

    public void setProgress(int progress) {
        PathEffect effect = new DashPathEffect(intervals, length - length * progress / 100);
        paint.setPathEffect(effect);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }
}