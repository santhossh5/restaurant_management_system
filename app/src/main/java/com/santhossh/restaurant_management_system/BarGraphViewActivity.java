package com.santhossh.restaurant_management_system;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class BarGraphViewActivity extends View {

    private List<Float> data = new ArrayList<>();
    private List<String> labels = new ArrayList<>();
    private Paint barPaint;
    private Paint labelPaint;
    private Paint axisPaint;
    private float barWidth;
    private float padding = 40f; // Padding between bars and graph borders
    private float axisPadding = 100f; // Space for axis labels (initial margin for Y-axis)
    private float barPadding = 20f; // Padding to separate the bars from Y-axis
    private float axisBottomPadding = 100f; // Space for bottom axis labels and margin
    private float yAxisMargin = 50f; // Adjust this for a left margin

    public BarGraphViewActivity(Context context) {
        super(context);
        init();
    }

    public BarGraphViewActivity(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarGraphViewActivity(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        barPaint = new Paint();
        labelPaint = new Paint();
        axisPaint = new Paint();

        labelPaint.setColor(Color.BLACK); // Color for day labels
        labelPaint.setTextSize(36f); // Adjust text size for labels

        axisPaint.setColor(Color.BLACK); // Color for axis lines
        axisPaint.setStrokeWidth(4); // Make axes bold

        barWidth = 100f; // Width of the bars, adjust if needed
    }

    public void setData(List<Float> data, List<String> labels) {
        this.data = data;
        this.labels = labels;
        invalidate(); // Trigger a redraw of the view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Check if data and labels are available
        if (data.isEmpty() || labels.isEmpty()) {
            return;
        }

        // Get the dimensions of the view
        float totalWidth = getWidth();
        float totalHeight = getHeight();
        float graphHeight = totalHeight - axisBottomPadding; // Graph area height, adjusting for bottom margin
        float graphBottom = totalHeight - 50; // Bottom margin for the labels and axes

        // Max value in data for scaling
        float maxDataValue = getMaxDataValue();

        // Draw the Y-Axis (value axis)
        canvas.drawLine(axisPadding + yAxisMargin, 50, axisPadding + yAxisMargin, graphBottom, axisPaint); // Left axis with left margin

        // Draw the X-Axis (day axis)
        canvas.drawLine(axisPadding + yAxisMargin, graphBottom, totalWidth - 50, graphBottom, axisPaint); // Bottom axis

        // Draw bars and labels
        for (int i = 0; i < data.size(); i++) {
            float barHeight = (data.get(i) / maxDataValue) * (graphHeight - 50); // Scale the bar height based on max value
            float barLeft = axisPadding + yAxisMargin + (i * (barWidth + padding)) + barPadding; // Add padding to separate bars from Y-axis
            float barRight = barLeft + barWidth;
            float barTop = graphBottom - barHeight; // Top of the bar

            // Set a unique color for each bar
            barPaint.setColor(getColorForBar(i));
            canvas.drawRect(barLeft, barTop, barRight, graphBottom, barPaint); // Draw the bar

            // Draw day labels under each bar
            float labelX = barLeft + (barWidth / 2) - 20; // Center the label under the bar
            float labelY = graphBottom + padding;
            canvas.drawText(labels.get(i), labelX, labelY, labelPaint); // Draw the day label

            // Draw the sale value at the top-center of each bar
            float textX = barLeft + (barWidth / 2); // Center text horizontally within the bar
            float textY = barTop - 10; // Position the text exactly above the top of the bar, with a slight margin
            canvas.drawText(String.format("%.0f", data.get(i)), textX, textY, labelPaint); // Display the sale value
        }

        // Add Y-axis labels (for the data values)
        drawYAxisLabels(canvas, maxDataValue, graphHeight);
    }

    // Get the max value from the data for scaling the bars
    private float getMaxDataValue() {
        float max = 0;
        for (float value : data) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    // Draw Y-axis labels on the left side, outside the axis line
    private void drawYAxisLabels(Canvas canvas, float maxDataValue, float graphHeight) {
        int numberOfLabels = 5; // Number of labels on the Y-axis
        for (int i = 0; i < numberOfLabels; i++) {
            float labelValue = (maxDataValue / (numberOfLabels - 1)) * i;
            float labelY = graphHeight - (graphHeight / (numberOfLabels - 1)) * i + 50;
            String label = String.format("%.0f", labelValue); // Format label as integer

            // Draw the label outside the axis line (to the left)
            canvas.drawText(label, axisPadding + yAxisMargin - 80, labelY, labelPaint); // Draw value with extra space for padding
            canvas.drawLine(axisPadding + yAxisMargin - 10, labelY, axisPadding + yAxisMargin, labelY, axisPaint); // Small tick mark
        }
    }

    // Method to return different colors for each bar
    private int getColorForBar(int index) {
        switch (index % 5) { // Change this number based on how many colors you want to cycle through
            case 0: return Color.RED;
            case 1: return Color.GREEN;
            case 2: return Color.BLUE;
            case 3: return Color.YELLOW;
            case 4: return Color.CYAN;
            default: return Color.GRAY; // Fallback color
        }
    }

    // Method to handle touch events
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            // Calculate the bar clicked based on the x position
            int clickedIndex = getClickedBarIndex(x);
            if (clickedIndex != -1) {
                // Display the selected day's food items, quantities, and prices
                // Update this logic if necessary
            }
        }
        return true;
    }

    // Method to find the bar clicked based on x position
    private int getClickedBarIndex(float x) {
        float barLeft = axisPadding + yAxisMargin + barPadding;
        for (int i = 0; i < data.size(); i++) {
            float barRight = barLeft + barWidth;
            if (x > barLeft && x < barRight) {
                return i; // Return the clicked bar index
            }
            barLeft += (barWidth + padding); // Update the bar position
        }
        return -1; // No bar clicked
    }
}
