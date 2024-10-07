package com.example.WaterWise.history;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * CircleProgressView is a custom view that displays a circular progress indicator.
 * It consists of a background circle and a progress arc that is drawn based on the
 * provided percentage value.
 */
public class CircleProgressView extends View {
    // Paint objects for drawing the background circle and progress arc
    private Paint circlePaint;
    private Paint arcPaint;
    // RectF defining the bounds for the arc
    private RectF arcBounds;
    // The percentage of progress to display, ranges from 0 to 100
    private float percentage = 0f;

    /**
     * Constructor for initializing the CircleProgressView.
     *
     * @param context The context of the application.
     * @param attrs The attribute set from the XML layout.
     */
    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(); // Initialize paint objects and arc bounds
    }

    /**
     * Initializes the paints for the background circle and progress arc,
     * and sets up the initial bounds for the arc.
     */
    private void init() {
        // Initialize the paint for the background circle
        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.STROKE); // Only draw the stroke of the circle
        circlePaint.setStrokeWidth(8f); // Set the width of the circle stroke
        circlePaint.setAntiAlias(true); // Enable anti-aliasing for smoother edges
        circlePaint.setColor(Color.LTGRAY); // Background circle color

        // Initialize the paint for the progress arc
        arcPaint = new Paint();
        arcPaint.setStyle(Paint.Style.STROKE); // Only draw the stroke of the arc
        arcPaint.setStrokeWidth(8f); // Set the width of the arc stroke
        arcPaint.setAntiAlias(true); // Enable anti-aliasing for smoother edges
        arcPaint.setColor(Color.BLUE); // Progress color

        // Initialize the arc bounds
        arcBounds = new RectF();
    }

    /**
     * Called when the size of the view changes, for example, when the view is first drawn
     * or when the screen orientation changes.
     *
     * @param w The new width of the view.
     * @param h The new height of the view.
     * @param oldw The old width of the view.
     * @param oldh The old height of the view.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Define the bounds for the arc, adding padding to the view's dimensions
        int padding = 2;
        arcBounds.set(padding, padding, w - padding, h - padding);
    }

    /**
     * Called when the view is drawn on the screen. Draws the background circle and
     * the progress arc based on the current percentage.
     *
     * @param canvas The canvas on which the background circle and progress arc are drawn.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the background circle
        canvas.drawOval(arcBounds, circlePaint);

        // Calculate the sweep angle based on the percentage (0 to 360 degrees)
        float sweepAngle = (percentage / 100f) * 360f;

        // Draw the progress arc, starting from the top (-90 degrees)
        canvas.drawArc(arcBounds, -90, sweepAngle, false, arcPaint);
    }

    /**
     * Sets the progress percentage and invalidates the view, causing it to be redrawn.
     *
     * @param percentage The progress percentage to display (0-100).
     */
    public void setPercentage(float percentage) {
        this.percentage = percentage;
        invalidate(); // Redraw the view with the new percentage
    }
}
