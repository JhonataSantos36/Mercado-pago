package com.mercadopago.model;

import java.util.Calendar;

/**
 * Created by mreverter on 10/5/16.
 */
public class TransactionManager {

    private TransactionManager() {}

    private Long transactionId;

    private static TransactionManager mInstance = null;
    public static TransactionManager getInstance() {
        if (mInstance == null) {
            mInstance = new TransactionManager();
        }
        return mInstance;
    }

    public Long getTransactionId() {
        if(transactionId == null) {
            transactionId = Calendar.getInstance().getTimeInMillis() + Math.round(Math.random()) * Math.round(Math.random());
        }
        return transactionId;
    }

    public void releaseTransaction() {
        transactionId = null;
    }
}
