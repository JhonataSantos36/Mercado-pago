package com.mercadopago.util;

/**
 * Created by vaserber on 08/02/2018.
 */

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class QueryBuilder {

    public static final int WIDTH_DEFAULT = 100;
    public static final int WEIGHT_DEFAULT = 400;
    public static final int WEIGHT_LIGHT = 300;
    public static final float ITALIC_DEFAULT = 0f;

    @NonNull
    private String mFamilyName;

    @Nullable
    private Float mWidth = null;

    @Nullable
    private Integer mWeight = null;

    @Nullable
    private Float mItalic = null;

    @Nullable
    private Boolean mBesteffort = null;

    public QueryBuilder(@NonNull String familyName) {
        mFamilyName = familyName;
    }

    public QueryBuilder withFamilyName(@NonNull String familyName) {
        mFamilyName = familyName;
        return this;
    }

    public QueryBuilder withWidth(float width) {
        mWidth = width;
        return this;
    }

    public QueryBuilder withWeight(int weight) {
        mWeight = weight;
        return this;
    }

    public QueryBuilder withItalic(float italic) {
        mItalic = italic;
        return this;
    }

    public QueryBuilder withBestEffort(boolean bestEffort) {
        mBesteffort = bestEffort;
        return this;
    }

    public String build() {
        if (mWeight == null && mWidth == null && mItalic == null && mBesteffort == null) {
            return mFamilyName;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("name=").append(mFamilyName);
        if (mWeight != null) {
            builder.append("&weight=").append(mWeight);
        }
        if (mWidth != null) {
            builder.append("&width=").append(mWidth);
        }
        if (mItalic != null) {
            builder.append("&italic=").append(mItalic);
        }
        if (mBesteffort != null) {
            builder.append("&besteffort=").append(mBesteffort);
        }
        return builder.toString();
    }
}