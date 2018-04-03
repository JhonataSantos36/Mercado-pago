package com.mercadopago.tracking.model;

/**
 * Created by vaserber on 6/5/17.
 */

public class AppInformation {

    private String flowId;
    private String version;
    private String platform;
    private String environment;

    protected AppInformation() {

    }

    private AppInformation(Builder builder) {
        this.flowId = builder.flowId;
        this.version = builder.version;
        this.platform = builder.platform;
        this.environment = builder.environment;
    }

    public String getFlowId() {
        return flowId;
    }

    public String getVersion() {
        return version;
    }

    public String getPlatform() {
        return platform;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public static class Builder {

        private String flowId;
        private String version;
        private String platform;
        private String environment;

        public Builder setFlowId(String flowId) {
            this.flowId = flowId;
            return this;
        }

        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder setPlatform(String platform) {
            this.platform = platform;
            return this;
        }

        public Builder setEnvironment(String environment) {
            this.environment = environment;
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

        if (!flowId.equals(that.flowId)) return false;
        if (!version.equals(that.version)) return false;
        if (!environment.equals(that.environment)) return false;
        return platform.equals(that.platform);

    }

    @Override
    public int hashCode() {
        int result = flowId.hashCode();
        result = 31 * result + environment.hashCode();
        result = 31 * result + version.hashCode();
        result = 31 * result + platform.hashCode();
        return result;
    }

    public AppInformation copy() {
        return new AppInformation.Builder()
                .setEnvironment(this.environment)
                .setFlowId(this.flowId)
                .setVersion(this.version)
                .setPlatform(this.platform)
                .build();
    }
}
