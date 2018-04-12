package com.mercadopago.lite.util;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class SpanAdjuster extends MetricAffectingSpan {
    double ratio = 0.5;

    public SpanAdjuster(double ratio) {
        this.ratio = ratio;
    }

    @Override
    public void updateDrawState(TextPaint paint) {
        paint.baselineShift += (int) (paint.ascent() * ratio);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        paint.baselineShift += (int) (paint.ascent() * ratio);
    }
}