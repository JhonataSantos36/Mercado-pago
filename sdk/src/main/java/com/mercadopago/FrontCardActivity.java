package com.mercadopago;

import android.support.v7.app.AppCompatActivity;

import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.model.PaymentMethod;

public abstract class FrontCardActivity extends AppCompatActivity implements CardInterface {

    protected String mCardNumber;
    protected String mCardHolderName;
    protected String mExpiryMonth;
    protected String mExpiryYear;
    protected String mCardIdentificationNumber;
    protected String mCardNumberState;
    protected String mCardNameState;
    protected String mExpiryDateState;
    protected String mSecurityCodeState;
    protected String mCardIdentificationNumberState;

    protected String mSecurityCode = "";
    protected PaymentMethod mCurrentPaymentMethod;
    protected PaymentMethodGuessingController mPaymentMethodGuessingController;

    public String buildNumberWithMask(CharSequence s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i <= s.length(); i++) {
            sb.append(s.charAt(i-1));
            if (i % 4 == 0) {
                sb.append("  ");
            }
        }
        return sb.toString();
    }

    public String buildIdentificationNumberWithMask(CharSequence s) {
        return s.toString();
    }

    public String getSecurityCodeState() {
        return mSecurityCodeState;
    }

    public String getCardNumber() {
        return mCardNumber;
    }

    public String getCardIdentificationNumber() {
        return this.mCardIdentificationNumber;
    }

    public String getCardNumberState() {
        return mCardNumberState;
    }

    public String getCardHolderName() {
        return mCardHolderName;
    }

    public String getCardHolderNameState() {
        return mCardNameState;
    }

    public String getExpiryMonth() {
        return mExpiryMonth;
    }

    public String getExpiryDateState() {
        return mExpiryDateState;
    }

    public String getExpiryYear() {
        return mExpiryYear;
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

    public void saveCardExpiryYear(String year)  {
        mExpiryYear = year;
    }

    public void saveCardSecurityCode(String code) {
        mSecurityCode = code;
    }

    public void saveCardIdentificationNumber(String number) {
        this.mCardIdentificationNumber = number;
    }

    public String getCardIdentificationNumberState() {
        return mCardIdentificationNumberState;
    }

    public void saveCardIdentificationNumberState(String state) {
        this.mCardIdentificationNumberState = state;
    }

    public int getCardImage(PaymentMethod paymentMethod) {
        String imageName = "ico_card_" + paymentMethod.getId().toLowerCase();
        return getResources().getIdentifier(imageName, "drawable", getPackageName());
    }

    @Override
    public int getCardColor(PaymentMethod paymentMethod) {
        String colorName = "mpsdk_" + paymentMethod.getId().toLowerCase();
        return getResources().getIdentifier(colorName, "color", getPackageName());
    }

    @Override
    public int getCardFontColor(PaymentMethod paymentMethod) {
        String colorName = "mpsdk_font_" + paymentMethod.getId().toLowerCase();
        int color = getResources().getIdentifier(colorName, "color", getPackageName());
        return color;
    }

    public String getSecurityCode() {
        return mSecurityCode;
    }

    @Override
    public void saveCardHolderNameState(String state) {
        this.mCardNameState = state;
    }

    @Override
    public void saveCardNumberState(String state) {
        this.mCardNumberState = state;
    }

    @Override
    public void saveSecurityCodeState(String state) {
        this.mSecurityCodeState = state;
    }

    @Override
    public void saveExpiryDateState(String state) {
        this.mExpiryDateState = state;
    }
}
