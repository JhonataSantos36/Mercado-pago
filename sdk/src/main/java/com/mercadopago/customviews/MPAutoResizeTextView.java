package com.mercadopago.customviews;

/**
 * Created by marlanti on 2/21/17.
 */


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;


public class MPAutoResizeTextView extends MPTextView {

    private static final int NO_LINE_LIMIT = -1;
    private final RectF mAvailableSpaceRect = new RectF();
    private final SizeTester mSizeTester;
    private float mMaxTextSize, mSpacingMult = 1.0f, mSpacingAdd = 0.0f, mMinTextSize;
    private int mWidthLimit, mMaxLines;
    private boolean mInitialized = false;
    private TextPaint mPaint;

    private interface SizeTester {
        /**
         * @param suggestedSize  Size of text to be tested
         * @param availableSpace available space in which text must fit
         * @return an integer < 0 if after applying {@code suggestedSize} to
         * text, it takes less space than {@code availableSpace}, > 0
         * otherwise
         */
        int onTestSize(int suggestedSize, RectF availableSpace);
    }

    public MPAutoResizeTextView(final Context context) {
        this(context, null, android.R.attr.textViewStyle);
    }

    public MPAutoResizeTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public MPAutoResizeTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        // using the minimal recommended font size.
        mMinTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        mMaxTextSize = getTextSize();
        mPaint = new TextPaint(getPaint());
        if (mMaxLines == 0)
            // no value was assigned during construction
            mMaxLines = NO_LINE_LIMIT;
        // prepare size tester:
        mSizeTester = new SizeTester() {
            final RectF textRect = new RectF();

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public int onTestSize(final int suggestedSize, final RectF availableSpace) {
                mPaint.setTextSize(suggestedSize);
                final TransformationMethod transformationMethod = getTransformationMethod();
                final String text;
                if (transformationMethod != null)
                    text = transformationMethod.getTransformation(getText(), MPAutoResizeTextView.this).toString();
                else
                    text = getText().toString();
                final boolean singleLine = getMaxLines() == 1;
                if (singleLine) {
                    textRect.bottom = mPaint.getFontSpacing();
                    textRect.right = mPaint.measureText(text);
                } else {
                    final StaticLayout layout = new StaticLayout(text, mPaint, mWidthLimit, Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, true);
                    // return early if we have more lines
                    if (getMaxLines() != NO_LINE_LIMIT && layout.getLineCount() > getMaxLines())
                        return 1;
                    textRect.bottom = layout.getHeight();
                    int maxWidth = -1;
                    int lineCount = layout.getLineCount();
                    for (int i = 0; i < lineCount; i++) {
                        int end = layout.getLineEnd(i);
                        if (i < lineCount - 1 && end > 0 && !isValidWordWrap(text.charAt(end - 1), text.charAt(end)))
                            return 1;
                        if (maxWidth < layout.getLineRight(i) - layout.getLineLeft(i))
                            maxWidth = (int) layout.getLineRight(i) - (int) layout.getLineLeft(i);
                    }

                    textRect.right = maxWidth;
                }
                textRect.offsetTo(0, 0);
                if (availableSpace.contains(textRect))
                    // may be too small, don't worry we will find the best match
                    return -1;
                // else, too big
                return 1;
            }
        };
        mInitialized = true;
    }


    public boolean isValidWordWrap(char before, char after) {
        return before == ' ' || before == '-';
    }

    @Override
    public void setAllCaps(boolean allCaps) {
        super.setAllCaps(allCaps);
        adjustTextSize();
    }

    @Override
    public void setTypeface(final Typeface tf) {
        super.setTypeface(tf);
        adjustTextSize();
    }

    @Override
    public void setTextSize(final float size) {
        mMaxTextSize = size;
        adjustTextSize();
    }

    @Override
    public void setMaxLines(final int maxLines) {
        super.setMaxLines(maxLines);
        mMaxLines = maxLines;
        adjustTextSize();
    }

    @Override
    public int getMaxLines() {
        return mMaxLines;
    }

    @Override
    public void setSingleLine() {
        super.setSingleLine();
        mMaxLines = 1;
        adjustTextSize();
    }

    @Override
    public void setSingleLine(final boolean singleLine) {
        super.setSingleLine(singleLine);
        if (singleLine)
            mMaxLines = 1;
        else mMaxLines = NO_LINE_LIMIT;
        adjustTextSize();
    }

    @Override
    public void setLines(final int lines) {
        super.setLines(lines);
        mMaxLines = lines;
        adjustTextSize();
    }

    @Override
    public void setTextSize(final int unit, final float size) {
        final Context c = getContext();
        Resources r;
        if (c == null)
            r = Resources.getSystem();
        else r = c.getResources();
        mMaxTextSize = TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
        adjustTextSize();
    }

    @Override
    public void setLineSpacing(final float add, final float mult) {
        super.setLineSpacing(add, mult);
        mSpacingMult = mult;
        mSpacingAdd = add;
    }

    /**
     * Set the lower text size limit and invalidate the view
     *
     * @param minTextSize
     */
    public void setMinTextSize(final float minTextSize) {
        mMinTextSize = minTextSize;
        adjustTextSize();
    }

    private void adjustTextSize() {

        final int startSize;

        if (!mInitialized)
            return;

        String text = getText().toString().replaceAll("\\s", "");

        if (text.length() >= 18) {
            float minTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
            startSize = (int) minTextSize;
        } else {
            startSize = (int) mMinTextSize;
        }

        final int heightLimit = getMeasuredHeight() - getCompoundPaddingBottom() - getCompoundPaddingTop();
        mWidthLimit = getMeasuredWidth() - getCompoundPaddingLeft() - getCompoundPaddingRight();
        if (mWidthLimit <= 0)
            return;
        mPaint = new TextPaint(getPaint());
        mAvailableSpaceRect.right = mWidthLimit;
        mAvailableSpaceRect.bottom = heightLimit;
        superSetTextSize(startSize);


    }

    private void superSetTextSize(int startSize) {
        int textSize = binarySearch(startSize, (int) mMaxTextSize, mSizeTester, mAvailableSpaceRect);
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    private int binarySearch(final int start, final int end, final SizeTester sizeTester, final RectF availableSpace) {
        int lastBest = start, lo = start, hi = end - 1, mid;
        while (lo <= hi) {
            mid = lo + hi >>> 1;
            final int midValCmp = sizeTester.onTestSize(mid, availableSpace);
            if (midValCmp < 0) {
                lastBest = lo;
                lo = mid + 1;
            } else if (midValCmp > 0) {
                hi = mid - 1;
                lastBest = hi;
            } else return mid;
        }
        // make sure to return last best
        // this is what should always be returned
        return lastBest;
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        super.onTextChanged(text, start, before, after);
        adjustTextSize();
    }

    @Override
    protected void onSizeChanged(final int width, final int height, final int oldwidth, final int oldheight) {
        super.onSizeChanged(width, height, oldwidth, oldheight);
        if (width != oldwidth || height != oldheight)
            adjustTextSize();
    }


}
