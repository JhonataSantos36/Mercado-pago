package com.mercadopago.tracking.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaserber on 6/5/17.
 */

public class ErrorEvent extends Event {

    private String errorClass;
    private String errorMessage;
    @SerializedName("stacktrace")
    private List<StackTraceInfo> stackTraceList;

    private ErrorEvent(Builder builder) {
        super();
        setType(TYPE_ERROR);
        setTimestamp(System.currentTimeMillis());
        this.errorClass = builder.errorClass;
        this.errorMessage = builder.errorMessage;
        this.stackTraceList = builder.stackTraceList;
    }

    public String getErrorClass() {
        return errorClass;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<StackTraceInfo> getStackTraceList() {
        return stackTraceList;
    }


    public static class Builder {
        private String errorClass;
        private String errorMessage;
        @SerializedName("stacktrace")
        private List<StackTraceInfo> stackTraceList;

        public Builder setErrorClass(String errorClass) {
            this.errorClass = errorClass;
            return this;
        }

        public Builder setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder setStackTraceList(List<StackTraceInfo> stackTraceList) {
            if (this.stackTraceList == null) {
                this.stackTraceList = new ArrayList<>();
            }
            this.stackTraceList.addAll(stackTraceList);
            return this;
        }

        public Builder addStackTrace(StackTraceInfo stackTraceInfo) {
            if (this.stackTraceList == null) {
                this.stackTraceList = new ArrayList<>();
            }
            this.stackTraceList.add(stackTraceInfo);
            return this;
        }

        public ErrorEvent build() {
            return new ErrorEvent(this);
        }
    }
}
