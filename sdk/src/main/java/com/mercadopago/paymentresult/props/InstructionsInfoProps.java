package com.mercadopago.paymentresult.props;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by vaserber on 11/13/17.
 */

public class InstructionsInfoProps {

    public final String infoTitle;
    public final List<String> infoContent;
    public final boolean bottomDivider;

    public InstructionsInfoProps(final String infoTitle, final List<String> infoContent, final boolean bottomDivider) {
        this.infoTitle = infoTitle;
        this.infoContent = infoContent;
        this.bottomDivider = bottomDivider;
    }

    public InstructionsInfoProps(@NonNull final Builder builder) {
        infoTitle = builder.infoTitle;
        infoContent = builder.infoContent;
        bottomDivider = builder.bottomDivider;
    }

    public Builder toBuilder() {
        return new Builder()
                .setInfoTitle(infoTitle)
                .setInfoContent(infoContent)
                .setBottomDivider(bottomDivider);
    }

    public static class Builder {

        public String infoTitle;
        public List<String> infoContent;
        public boolean bottomDivider;

        public Builder setInfoTitle(@NonNull String infoTitle) {
            this.infoTitle = infoTitle;
            return this;
        }

        public Builder setInfoContent(@NonNull List<String> infoContent) {
            this.infoContent = infoContent;
            return this;
        }

        public Builder setBottomDivider(boolean bottomDivider) {
            this.bottomDivider = bottomDivider;
            return this;
        }

        public InstructionsInfoProps build() {
            return new InstructionsInfoProps(this);
        }
    }
}
