package com.example.cn.helloworld.ui.admin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class StatsBarView extends View {

    private float[] values = new float[0];
    private String[] labels = new String[0];
    private Paint barPaint;
    private Paint textPaint;
    private float barWidth;

    public StatsBarView(Context context) {
        super(context);
        init();
    }

    public StatsBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StatsBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setColor(Color.parseColor("#FF6F61"));
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(28f);
        textPaint.setColor(Color.DKGRAY);
        barWidth = 80f;
    }

    public void setData(float[] data, String[] labels) {
        if (data == null) {
            values = new float[0];
        } else {
            values = new float[data.length];
            System.arraycopy(data, 0, values, 0, data.length);
        }
        if (labels == null) {
            this.labels = new String[0];
        } else {
            this.labels = new String[labels.length];
            System.arraycopy(labels, 0, this.labels, 0, labels.length);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (values.length == 0) {
            return;
        }
        float max = 0f;
        for (int i = 0; i < values.length; i++) {
            if (values[i] > max) {
                max = values[i];
            }
        }
        if (max <= 0f) {
            return;
        }
        float availableHeight = getHeight() - getPaddingTop() - getPaddingBottom() - 60f;
        float startX = getPaddingLeft() + 40f;
        float baseY = getHeight() - getPaddingBottom() - 40f;
        float gap = (getWidth() - startX - getPaddingRight()) / values.length;
        if (gap <= 0f) {
            gap = barWidth + 20f;
        }
        for (int i = 0; i < values.length; i++) {
            float barHeight = (values[i] / max) * availableHeight;
            float left = startX + i * gap;
            float top = baseY - barHeight;
            float right = left + barWidth;
            canvas.drawRect(left, top, right, baseY, barPaint);
            if (i < labels.length) {
                canvas.drawText(labels[i], left, baseY + 30f, textPaint);
            }
        }
    }
}
