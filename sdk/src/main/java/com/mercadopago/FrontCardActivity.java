package com.mercadopago;


import android.os.Bundle;

import com.mercadopago.model.PaymentMethod;

public abstract class FrontCardActivity extends MercadoPagoActivity implements CardInterface {

    public static final String EXPIRY_MONTH = "mExpiryMonth";
    public static final String EXPIRY_YEAR = "mExpiryYear";
    public static final String CARD_IMAGE_PREFIX = "ico_card_";
    public static final String CARD_COLOR_PREFIX = "mpsdk_";
    public static final String CARD_FONT_PREFIX = "mpsdk_font_";

    protected String mCardNumber;
    protected String mCardHolderName;
    protected String mExpiryMonth;
    protected String mExpiryYear;
    protected String mCardIdentificationNumber;
    protected String mErrorState;
    protected String mSecurityCode = "";
    protected PaymentMethod mCurrentPaymentMethod;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXPIRY_MONTH, mExpiryMonth);
        outState.putString(EXPIRY_YEAR, mExpiryYear);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mExpiryMonth = savedInstanceState.getString(EXPIRY_MONTH);
        mExpiryYear = savedInstanceState.getString(EXPIRY_YEAR);
        super.onRestoreInstanceState(savedInstanceState);
    }

    public String getCardNumber() {
        return mCardNumber;
    }

    public String getCardIdentificationNumber() {
        return this.mCardIdentificationNumber;
    }

    public String getCardHolderName() {
        return mCardHolderName;
    }

    public String getExpiryMonth() {
        return mExpiryMonth;
    }

    public String getExpiryYear() {
        return mExpiryYear;
    }

    public String getSecurityCode() {
        return mSecurityCode;
    }

    public PaymentMethod getCurrentPaymentMethod() {
        return mCurrentPaymentMethod;
    }

    public void saveCardNumber(String number) {
        mCardNumber = number;
    }

    public void saveCardName(String name) {
        mCardHolderName = name;
    }

    public void saveCardExpiryMonth(String month) {
        mExpiryMonth = month;
    }

    public void saveCardExpiryYear(String year) {
        mExpiryYear = year;
    }

    public void saveCardSecurityCode(String code) {
        mSecurityCode = code;
    }

    public void saveCardIdentificationNumber(String number) {
        this.mCardIdentificationNumber = number;
    }

    public String getErrorState() {
        return mErrorState;
    }

    public void saveErrorState(String state) {
        this.mErrorState = state;
    }

    @Override
    public int getCardImage(PaymentMethod paymentMethod) {
        String imageName = CARD_IMAGE_PREFIX + paymentMethod.getId().toLowerCase();
        return getResources().getIdentifier(imageName, "drawable", getPackageName());
    }

    @Override
    public int getCardColor(PaymentMethod paymentMethod) {
        String colorName = CARD_COLOR_PREFIX + paymentMethod.getId().toLowerCase();
        return getResources().getIdentifier(colorName, "color", getPackageName());
    }

    @Override
    public int getCardFontColor(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            return getResources().getColor(CardInterface.FULL_TEXT_VIEW_COLOR);
        }
        String colorName = CARD_FONT_PREFIX + paymentMethod.getId().toLowerCase();
        return getResources().getIdentifier(colorName, "color", getPackageName());
    }

}
