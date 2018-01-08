package com.mercadopago.paymentresult.props;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.mercadopago.paymentresult.formatter.AmountFormat;
import com.mercadopago.paymentresult.formatter.HeaderTitleFormatter;


/**
 * Created by vaserber on 10/20/17.
 */

public class HeaderProps {

    public static final String HEADER_MODE_WRAP = "wrap";
    public static final String HEADER_MODE_STRETCH = "stretch";

    public final String height;
    public final int background;
    public final int statusBarColor;
    public final int iconImage;
    public final int badgeImage;
    public final String title;
    public final String label;
    public final HeaderTitleFormatter amountFormat;

    public HeaderProps(final String height,
                       @DrawableRes final int background,
                       @DrawableRes final int statusBarColor,
                       @DrawableRes final int iconImage,
                       @DrawableRes final int badgeImage,
                       final String title,
                       final String label,
                       final HeaderTitleFormatter formatter) {
        this.height = height;
        this.background = background;
        this.statusBarColor = statusBarColor;
        this.iconImage = iconImage;
        this.badgeImage = badgeImage;
        this.title = title;
        this.label = label;
        this.amountFormat = formatter;
    }

    public HeaderProps(@NonNull final Builder builder) {
        this.height = builder.height;
        this.background = builder.background;
        this.statusBarColor = builder.statusBarColor;
        this.iconImage = builder.iconImage;
        this.badgeImage = builder.badgeImage;
        this.title = builder.title;
        this.label = builder.label;
        this.amountFormat = builder.amountFormat;
    }

    public Builder toBuilder() {
        return new Builder()
                .setHeight(this.height)
                .setBackground(this.background)
                .setStatusBarColor(this.statusBarColor)
                .setIconImage(this.iconImage)
                .setBadgeImage(this.badgeImage)
                .setTitle(this.title)
                .setLabel(this.label)
                .setAmountFormat(this.amountFormat);
    }

    public static class Builder {

        //TODO definir los valores default

        public String height;
        public int background;
        public int statusBarColor;
        public int iconImage;
        public int badgeImage;
        public String title;
        public String label;
        public HeaderTitleFormatter amountFormat;

        public Builder setBackground(@DrawableRes final int background) {
            this.background = background;
            return this;
        }

        public Builder setStatusBarColor(int statusBarColor) {
            this.statusBarColor = statusBarColor;
            return this;
        }

        public Builder setIconImage(@DrawableRes final int iconImage) {
            this.iconImage = iconImage;
            return this;
        }

        public Builder setBadgeImage(@DrawableRes final int badgeImage) {
            this.badgeImage = badgeImage;
            return this;
        }

        public Builder setHeight(@NonNull final String height) {
            this.height = height;
            return this;
        }

        public Builder setTitle(@NonNull final String title) {
            this.title = title;
            return this;
        }

        public Builder setLabel(@NonNull final String label) {
            this.label = label;
            return this;
        }

        public Builder setAmountFormat(@NonNull final HeaderTitleFormatter amountFormat) {
            this.amountFormat = amountFormat;
            return this;
        }

        public HeaderProps build() {
            return new HeaderProps(this);
        }
    }
}
