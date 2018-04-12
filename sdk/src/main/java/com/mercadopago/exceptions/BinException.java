package com.mercadopago.exceptions;


import com.mercadopago.model.Bin;

public class BinException extends RuntimeException {

    public BinException(int binLength) {
        super("Invalid bin: " + Bin.BIN_LENGTH + " digits needed, " + binLength + " found");
    }
}
