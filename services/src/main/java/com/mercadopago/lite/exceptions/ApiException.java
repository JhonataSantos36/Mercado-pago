package com.mercadopago.lite.exceptions;

import com.mercadopago.lite.model.Cause;
import com.mercadopago.lite.util.ApiUtil;

import java.util.List;

public class ApiException {

    private List<Cause> cause;
    private String error;
    private String message;
    private Integer status;

    public List<Cause> getCause() {
        return cause;
    }

    public void setCause(List<Cause> cause) {
        this.cause = cause;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean isRecoverable() {
        return getStatus() == null || getStatus() != ApiUtil.StatusCodes.NOT_FOUND
                && (getCause() == null || getCause().isEmpty());
    }

    public boolean containsCause(String code) {
        boolean found = false;
        if (cause != null && code != null) {
            for (Cause currentCause : cause) {
                if (code.equals(currentCause.getCode())) {
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    public class ErrorCodes {
        public static final String CUSTOMER_NOT_ALLOWED_TO_OPERATE = "2021";
        public static final String COLLECTOR_NOT_ALLOWED_TO_OPERATE = "2022";
        public static final String INVALID_USERS_INVOLVED = "2035";
        public static final String CUSTOMER_EQUAL_TO_COLLECTOR = "3000";
        public static final String INVALID_CARD_HOLDER_NAME = "3001";
        public static final String UNAUTHORIZED_CLIENT = "3010";
        public static final String PAYMENT_METHOD_NOT_FOUND = "3012";
        public static final String INVALID_SECURITY_CODE = "3013";
        public static final String SECURITY_CODE_REQUIRED = "3014";
        public static final String INVALID_PAYMENT_METHOD = "3015";
        public static final String INVALID_CARD_NUMBER = "3017";
        public static final String EMPTY_EXPIRATION_MONTH = "3019";
        public static final String EMPTY_EXPIRATION_YEAR = "3020";
        public static final String EMPTY_CARD_HOLDER_NAME = "3021";
        public static final String EMPTY_DOCUMENT_NUMBER = "3022";
        public static final String EMPTY_DOCUMENT_TYPE = "3023";
        public static final String INVALID_PAYMENT_TYPE_ID = "3028";
        public static final String INVALID_PAYMENT_METHOD_ID = "3029";
        public static final String INVALID_CARD_EXPIRATION_MONTH = "3030";
        public static final String INVALID_CARD_EXPIRATION_YEAR = "4000";
        public static final String INVALID_PAYER_EMAIL = "4050";
        public static final String INVALID_PAYMENT_WITH_ESC = "2107";
        public static final String INVALID_IDENTIFICATION_NUMBER = "2067";

        //Token creation error cause codes
        public static final String INVALID_CARD_HOLDER_IDENTIFICATION_NUMBER = "324";
        public static final String INVALID_ESC = "E216";
        public static final String INVALID_FINGERPRINT = "E217";
    }
}

