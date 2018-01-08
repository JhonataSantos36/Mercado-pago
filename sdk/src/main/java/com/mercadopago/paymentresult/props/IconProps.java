package com.mercadopago.paymentresult.props;

import android.support.annotation.NonNull;

/**
 * Created by vaserber on 10/23/17.
 */

public class IconProps {

    public final int iconImage;
    public final int badgeImage;

    public IconProps(int iconImage, int badgeImage) {
        this.iconImage = iconImage;
        this.badgeImage = badgeImage;
    }

    public IconProps(@NonNull final Builder builder) {
        this.iconImage = builder.iconImage;
        this.badgeImage = builder.badgeImage;
    }

    public Builder toBuilder() {
        return new Builder()
                .setIconImage(iconImage)
                .setBadgeImage(badgeImage);
    }

    public static class Builder {

        public int iconImage;
        public int badgeImage;

        public Builder setIconImage(int iconImage) {
            this.iconImage = iconImage;
            return this;
        }

        public Builder setBadgeImage(int badgeImage) {
            this.badgeImage = badgeImage;
            return this;
        }

        public IconProps build() {
            return new IconProps(this);
        }
    }
}
