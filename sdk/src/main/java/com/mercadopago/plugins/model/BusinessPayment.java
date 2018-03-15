package com.mercadopago.plugins.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.mercadopago.R;
import com.mercadopago.util.TextUtils;

public final class BusinessPayment implements PluginPayment, Parcelable {

    private final String help;
    private final int iconId;
    private final String title;
    private final Status status;
    private final boolean hasPaymentMethod;
    private final ButtonAction buttonActionPrimary;
    private final ButtonAction buttonActionSecondary;

    private BusinessPayment(Builder builder) {
        this.help = builder.help;
        this.title = builder.title;
        this.status = builder.status;
        this.iconId = builder.iconId;
        this.hasPaymentMethod = builder.hasPaymentMethod;
        this.buttonActionPrimary = builder.buttonPrimary;
        this.buttonActionSecondary = builder.buttonSecondary;
    }

    protected BusinessPayment(Parcel in) {
        iconId = in.readInt();
        title = in.readString();
        hasPaymentMethod = in.readByte() != 0;
        buttonActionPrimary = in.readParcelable(ButtonAction.class.getClassLoader());
        buttonActionSecondary = in.readParcelable(ButtonAction.class.getClassLoader());
        status = Status.fromName(in.readString());
        help = in.readString();
    }

    public static final Creator<BusinessPayment> CREATOR = new Creator<BusinessPayment>() {
        @Override
        public BusinessPayment createFromParcel(Parcel in) {
            return new BusinessPayment(in);
        }

        @Override
        public BusinessPayment[] newArray(int size) {
            return new BusinessPayment[size];
        }
    };

    @Override
    public void process(final Processor processor) {
        processor.process(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(iconId);
        dest.writeString(title);
        dest.writeByte((byte) (hasPaymentMethod ? 1 : 0));
        dest.writeParcelable(buttonActionPrimary, flags);
        dest.writeParcelable(buttonActionSecondary, flags);
        dest.writeString(status.name);
        dest.writeString(help);
    }

    public Status getStatus() {
        return status;
    }

    public int getIcon() {
        return iconId;
    }

    public String getTitle() {
        return title;
    }

    public boolean hasPrimaryButton() {
        return buttonActionPrimary != null;
    }

    public boolean hasSecondaryButton() {
        return buttonActionSecondary != null;
    }

    public boolean hasHelp() {
        return TextUtils.isNotEmpty(help);
    }

    public ButtonAction getSecondaryAction() {
        return buttonActionSecondary;
    }

    public ButtonAction getPrimaryAction() {
        return buttonActionPrimary;
    }

    public String getHelp() {
        return help;
    }

    public enum Status {
        APPROVED("approved", R.color.mpsdk_green_payment_result_background, R.drawable.mpsdk_badge_check, 0),
        REJECTED("rejected", R.color.mpsdk_red_payment_result_background, R.drawable.mpsdk_badge_error, R.string.mpsdk_rejection_label),
        PENDING("pending", R.color.mpsdk_orange_payment_result_background, R.drawable.mpsdk_badge_pending_orange, 0);

        public final String name;
        public final int resColor;
        public final int badge;
        public final int message;

        Status(final String name,
               @ColorRes final int resColor,
               @DrawableRes final int badge,
               @StringRes final int message) {
            this.name = name;
            this.resColor = resColor;
            this.badge = badge;
            this.message = message;
        }

        public static Status fromName(String text) {
            for (Status s : Status.values()) {
                if (s.name.equalsIgnoreCase(text)) {
                    return s;
                }
            }
            throw new IllegalStateException("Invalid status");
        }
    }


    public static class Builder {

        // Mandatory values
        @NonNull
        private final Status status;
        @DrawableRes
        private final int iconId;
        @NonNull
        private final String title;

        // Optional values
        private boolean hasPaymentMethod;
        private ButtonAction buttonPrimary;
        private ButtonAction buttonSecondary;
        private String help;

        public Builder(@NonNull Status status,
                       @DrawableRes int iconId,
                       @NonNull String title) {
            this.title = title;
            this.status = status;
            this.iconId = iconId;
            this.hasPaymentMethod = false;
            this.buttonPrimary = null;
            this.buttonSecondary = null;
            this.help = null;
        }

        public BusinessPayment build() {
            return new BusinessPayment(this);
        }

        /**
         * if button action is set, then a big primary button
         * will appear and the click action will trigger a resCode
         * that will be the same of the Button action added.
         *
         * @param buttonAction a {@link ButtonAction }
         * @return builder
         */
        public Builder setPrimaryButton(@Nullable ButtonAction buttonAction) {
            this.buttonPrimary = buttonAction;
            return this;
        }

        /**
         * if button action is set, then a small secondary button
         * will appear and the click action will trigger a resCode
         * that will be the same of the Button action added.
         *
         * @param buttonAction a {@link ButtonAction }
         * @return builder
         */
        public Builder setSecondaryButton(@Nullable ButtonAction buttonAction) {
            this.buttonSecondary = buttonAction;
            return this;
        }

        /**
         * if help is set, then a small box with help instructions will appear
         *
         * @param help a help message
         * @return builder
         */
        public Builder setHelp(@Nullable String help) {
            this.help = help;
            return this;
        }

        /**
         * If value true is set, then payment method box
         * will appear with the amount value and payment method
         * options that were selected by the user.
         *
         * @param visible visibility mode
         * @return builder
         */
        public Builder setPaymentMethod(boolean visible) {
            throw new UnsupportedOperationException("Not implemented yet :( under development");
//            this.hasPaymentMethod = visible;
//            return this;
        }

    }


}