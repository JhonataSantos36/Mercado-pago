package com.mercadopago.tracking.model;

/**
 * Created by vaserber on 6/5/17.
 */

public class DeviceInfo {

    private String model;
    private String os;
    private String systemVersion;
    private String resolution;
    private String screenSize;
    private String uuid;

    protected DeviceInfo() {

    }

    private DeviceInfo(Builder builder) {
        this.model = builder.model;
        this.os = builder.os;
        this.systemVersion = builder.systemVersion;
        this.resolution = builder.resolution;
        this.screenSize = builder.screenSize;
        this.uuid = builder.uuid;
    }

    public String getModel() {
        return model;
    }

    public String getOs() {
        return os;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public String getResolution() {
        return resolution;
    }

    public String getScreenSize() {
        return screenSize;
    }

    public static class Builder {

        private String model;
        private String os;
        private String systemVersion;
        private String resolution;
        private String screenSize;
        private String uuid;

        public Builder setModel(String model) {
            this.model = model;
            return this;
        }

        public Builder setOS(String os) {
            this.os = os;
            return this;
        }

        public Builder setSystemVersion(String systemVersion) {
            this.systemVersion = systemVersion;
            return this;
        }

        public Builder setResolution(String resolution) {
            this.resolution = resolution;
            return this;
        }

        public Builder setScreenSize(String screenSize) {
            this.screenSize = screenSize;
            return this;
        }

        public Builder setUuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public DeviceInfo build() {
            return new DeviceInfo(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceInfo that = (DeviceInfo) o;

        if (!model.equals(that.model)) return false;
        if (!os.equals(that.os)) return false;
        if (!systemVersion.equals(that.systemVersion)) return false;
        if (!resolution.equals(that.resolution)) return false;
        if (!uuid.equals(that.uuid)) return false;
        return screenSize.equals(that.screenSize);

    }

    @Override
    public int hashCode() {
        int result = model.hashCode();
        result = 31 * result + os.hashCode();
        result = 31 * result + systemVersion.hashCode();
        result = 31 * result + resolution.hashCode();
        result = 31 * result + screenSize.hashCode();
        result = 31 * result + uuid.hashCode();
        return result;
    }
}
