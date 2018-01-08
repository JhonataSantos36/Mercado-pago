package com.mercadopago.util;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/**
 * Created by vaserber on 11/8/17.
 */

public class SuperscriptSpanAdjuster extends MetricAffectingSpan {
    double ratio = 0.5;

    public SuperscriptSpanAdjuster() {
    }

    public SuperscriptSpanAdjuster(double ratio) {
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