package com.mercadopago.exceptions;

import com.mercadopago.model.ApiException;

/**
 * Created by mromar on 3/2/16.
 */
public class MPException {

    private String message;
    private String errorDetail;
    private ApiException apiException;
    private boolean recoverable;

    public MPException(String message, boolean recoverable) {
        this.message = message;
        this.recoverable = recoverable;
    }

    public MPException(String message, String detail, boolean recoverable) {
        this.message = message;
        this.errorDetail = detail;
        this.recoverable = recoverable;
    }

    public MPException(ApiException apiException) {
        this.apiException = apiException;
        this.recoverable = apiException.isRecoverable();
    }

    public ApiException getApiException() {
        return apiException;
    }

    public boolean isRecoverable() {
        return recoverable;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorDetail() {
        if (errorDetail == null) {
            errorDetail = "";
        }
        return errorDetail;
    }

    public boolean isApiException() {
        return apiException != null;
    }
}
