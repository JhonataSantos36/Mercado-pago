package com.mercadopago.paymentresult.props;

import android.support.annotation.NonNull;

/**
 * Created by vaserber on 11/13/17.
 */

public class InstructionsSubtitleProps {

    public final String subtitle;

    public InstructionsSubtitleProps(@NonNull final String subtitle) {
        this.subtitle = subtitle;
    }

    public InstructionsSubtitleProps(@NonNull final Builder builder) {
        subtitle = builder.subtitle;
    }

    public Builder toBuilder() {
        return new InstructionsSubtitleProps.Builder()
                .setSubtitle(subtitle);
    }

    public static class Builder {

        public String subtitle;

        public Builder setSubtitle(@NonNull String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public InstructionsSubtitleProps build() {
            return new InstructionsSubtitleProps(this);
        }
    }
}
