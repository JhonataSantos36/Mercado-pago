package com.mercadopago.paymentresult.props;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mercadopago.paymentresult.formatter.HeaderTitleFormatter;
import com.mercadopago.plugins.model.BusinessPayment;


public class HeaderProps {

    public static final String HEADER_MODE_WRAP = "wrap";
    public static final String HEADER_MODE_STRETCH = "stretch";

    public final String height;
    public final int background;
    public final int statusBarColor;
    public final int iconImage;
    public final int badgeImage;
    public final String iconUrl;
    public final String title;
    public final String label;
    public final HeaderTitleFormatter amountFormat;

    private HeaderProps(@NonNull final Builder builder) {
        height = builder.height;
        background = builder.background;
        statusBarColor = builder.statusBarColor;
        iconImage = builder.iconImage;
        iconUrl = builder.iconUrl;
        badgeImage = builder.badgeImage;
        title = builder.title;
        label = builder.label;
        amountFormat = builder.amountFormat;
    }

    public static HeaderProps from(@NonNull BusinessPayment businessPayment, @NonNull Context context) {
        BusinessPayment.Status status = businessPayment.getStatus();
        return new HeaderProps.Builder()
                .setHeight(HEADER_MODE_WRAP)
                .setBackground(status.resColor)
                .setStatusBarColor(status.resColor)
                .setIconImage(businessPayment.getIcon())
                .setBadgeImage(status.badge)
                .setTitle(businessPayment.getTitle())
                .setLabel(status.message == 0 ? null : context.getString(status.message))
                .build();
    }

    public Builder toBuilder() {
        return new Builder()
                .setHeight(height)
                .setBackground(background)
                .setStatusBarColor(statusBarColor)
                .setIconImage(iconImage)
                .setIconUrl(iconUrl)
                .setBadgeImage(badgeImage)
                .setTitle(title)
                .setLabel(label)
                .setAmountFormat(amountFormat);
    }

    public static class Builder {
        //TODO definir los valores default
        public String height;
        public int background;
        public int statusBarColor;
        public int iconImage;
        public int badgeImage;
        public String iconUrl;
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

        public Builder setIconUrl(final String iconUrl) {
            this.iconUrl = iconUrl;
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

        public Builder setLabel(@Nullable final String label) {
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
