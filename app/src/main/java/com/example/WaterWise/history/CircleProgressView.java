package com.example.WaterWise.history;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircleProgressView extends View {
    private Paint circlePaint;
    private Paint arcPaint;
    private RectF arcBounds;
    private int percentage = 0;

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(8f);
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.LTGRAY); // Background circle color

        arcPaint = new Paint();
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(8f);
        arcPaint.setAntiAlias(true);
        arcPaint.setColor(Color.BLUE); // Progress color

        arcBounds = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int padding = 10;
        arcBounds.set(padding, padding, w - padding, h - padding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the background circle
        canvas.drawOval(arcBounds, circlePaint);

        // Draw the progress arc
        float sweepAngle = (percentage / 100f) * 360f;
        canvas.drawArc(arcBounds, -90, sweepAngle, false, arcPaint);
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
        invalidate(); // Redraw the view with the new percentage
    }
}
