package com.mercadopago.exceptions;

import com.mercadopago.model.ApiException;

/**
 * Created by mromar on 3/2/16.
 */
public class MercadoPagoError {

    private String message;
    private String errorDetail;
    private String requestOrigin;
    private ApiException apiException;
    private final boolean recoverable;

    public MercadoPagoError(String message, boolean recoverable) {
        this.message = message;
        this.recoverable = recoverable;
    }

    public MercadoPagoError(String message, String detail, boolean recoverable) {
        this.message = message;
        errorDetail = detail;
        this.recoverable = recoverable;
    }

    public MercadoPagoError(ApiException apiException, String requestOrigin) {
        this.apiException = apiException;
        this.requestOrigin = requestOrigin;
        recoverable = apiException.isRecoverable();
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

    public String getRequestOrigin() {
        return requestOrigin;
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
