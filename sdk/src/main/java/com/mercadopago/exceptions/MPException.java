package com.mercadopago.exceptions;

import com.mercadopago.model.ApiException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mromar on 3/2/16.
 */
public class MPException implements Serializable{

    private String message;
    private ApiException apiException;
    private boolean recoverable;

    public MPException(String message, boolean recoverable) {
        this.message = message;
        this.recoverable = recoverable;
    }

    public MPException(ApiException apiException) {
        this.apiException = apiException;
        //TODO analizar casos
        this.recoverable = true;
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
}
