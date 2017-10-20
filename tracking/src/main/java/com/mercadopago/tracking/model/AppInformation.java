package com.mercadopago.tracking.model;

/**
 * Created by vaserber on 6/5/17.
 */

public class AppInformation {

    private String publicKey;
    private String checkoutVersion;
    private String platform;

    protected AppInformation() {

    }

    private AppInformation(Builder builder) {
        this.publicKey = builder.publicKey;
        this.checkoutVersion = builder.checkoutVersion;
        this.platform = builder.platform;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getCheckoutVersion() {
        return checkoutVersion;
    }

    public String getPlatform() {
        return platform;
    }

    public static class Builder {

        private String publicKey;
        private String checkoutVersion;
        private String platform;

        public Builder setPublicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public Builder setCheckoutVersion(String checkoutVersion) {
            this.checkoutVersion = checkoutVersion;
            return this;
        }

        public Builder setPlatform(String platform) {
            this.platform = platform;
            return this;
        }

        public AppInformation build() {
            return new AppInformation(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppInformation that = (AppInformation) o;

        if (!publicKey.equals(that.publicKey)) return false;
        if (!checkoutVersion.equals(that.checkoutVersion)) return false;
        return platform.equals(that.platform);

    }

    @Override
    public int hashCode() {
        int result = publicKey.hashCode();
        result = 31 * result + checkoutVersion.hashCode();
        result = 31 * result + platform.hashCode();
        return result;
    }
}
