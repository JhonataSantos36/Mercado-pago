package com.mercadopago.controllers;

import android.app.Activity;
import android.text.InputFilter;

import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.SecurityCode;
import com.mercadopago.views.MPEditText;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodGuessingController {

    public static int CARD_NUMBER_MAX_LENGTH = 16;
    public static int CARD_DEFAULT_SECURITY_CODE_LENGTH = 4;

    private Activity mActivity;
    private String mSavedBin;
    private List<PaymentMethod> mAllPaymentMethods;
    private List<PaymentMethod> mGuessedPaymentMethods;
    private List<String> mExcludedPaymentTypes;
    private String mPaymentTypeId;
    private Issuer mIssuer;
    private int mCardNumberLength;
    private boolean mIsSecurityCodeRequired;
    private int mCardSecurityCodeLength;
    private String mSecurityCodeLocation;
    private boolean mCardNumberBlocked;

    public PaymentMethodGuessingController(Activity activity, List<PaymentMethod> paymentMethods,
                                           String paymentTypeId, List<String> excludedPaymentTypes){
        this.mActivity = activity;
        this.mAllPaymentMethods = paymentMethods;
        this.mExcludedPaymentTypes = excludedPaymentTypes;
        this.mPaymentTypeId = paymentTypeId;
        this.mSavedBin = "";
        this.mIsSecurityCodeRequired = true;
        this.mCardNumberLength = CARD_NUMBER_MAX_LENGTH;
        this.mCardSecurityCodeLength = CARD_DEFAULT_SECURITY_CODE_LENGTH;
    }

    public void setSecurityCodeRequired(boolean required) {
        this.mIsSecurityCodeRequired = required;
    }

    public void setSecurityCodeLength(int length) {
        this.mCardSecurityCodeLength = length;
    }

    public void setSecurityCodeLocation(String location) {
        this.mSecurityCodeLocation = location;
    }

    public int getSecurityCodeLength() {
        return mCardSecurityCodeLength;
    }

    public String getSecurityCodeLocation() {
        return mSecurityCodeLocation;
    }

    public String getPaymentTypeId() {
        return mPaymentTypeId;
    }

    public void setSecurityCodeRestrictions(boolean isRequired, SecurityCode securityCode) {
        setSecurityCodeRequired(isRequired);
        if (securityCode == null) {
            setSecurityCodeLocation(null);
            setSecurityCodeLength(CARD_DEFAULT_SECURITY_CODE_LENGTH);
            return;
        }
        setSecurityCodeLocation(securityCode.getCardLocation());
        setSecurityCodeLength(securityCode.getLength());
    }

    public boolean isSecurityCodeRequired() {
        return mIsSecurityCodeRequired;
    }

    public void setCardNumberLength(int maxLength) {
        this.mCardNumberLength = maxLength;
    }

    public int getCardNumberLength() {
        return mCardNumberLength;
    }

    public void setIssuer(Issuer issuer) {
        this.mIssuer = issuer;
    }

    public Issuer getIssuer() {
        return mIssuer;
    }

    public List<PaymentMethod> guessPaymentMethodsByBin(String bin) {
        if (mSavedBin.equals(bin) && mGuessedPaymentMethods != null) {
            return mGuessedPaymentMethods;
        }
        mSavedBin = bin;
        mGuessedPaymentMethods = MercadoPago
                .getValidPaymentMethodsForBin(mSavedBin, this.mAllPaymentMethods);
        mGuessedPaymentMethods = getValidPaymentMethodForType(mPaymentTypeId, mGuessedPaymentMethods);
        if (mGuessedPaymentMethods.size() > 1) {
            mGuessedPaymentMethods = filterByPaymentType(mExcludedPaymentTypes, mGuessedPaymentMethods);
        }
        imprimir();
        return mGuessedPaymentMethods;
    }

    private void imprimir() {
        Log.d("size", String.valueOf(mGuessedPaymentMethods.size()));
        if (mGuessedPaymentMethods.size() > 0) {
            Log.d("first", mGuessedPaymentMethods.get(0).getName());
        }
    }

    public String getSavedBin(){
        return mSavedBin;
    }

    private List<PaymentMethod> getValidPaymentMethodForType(String paymentTypeId,
                                                             List<PaymentMethod> paymentMethods) {
        if(paymentTypeId == null) {
            return paymentMethods;
        }
        else {
            List<PaymentMethod> validPaymentMethodsForType = new ArrayList<>();
            for(PaymentMethod pm : paymentMethods)
            {
                if(pm.getPaymentTypeId().equals(paymentTypeId))
                    validPaymentMethodsForType.add(pm);
            }
            return validPaymentMethodsForType;
        }
    }

    public List<PaymentMethod> filterByPaymentType(List<String> excludedPaymentTypes,
                                                   List<PaymentMethod> guessingPaymentMethods) {
        if (excludedPaymentTypes == null) {
            return guessingPaymentMethods;
        }

        List<PaymentMethod> ans = new ArrayList<>();
        for (PaymentMethod p : guessingPaymentMethods) {
            for (String paymentType : excludedPaymentTypes) {
                if (!paymentType.equals(p.getPaymentTypeId())) {
                    ans.add(p);
                }
            }
        }
        return ans;
    }

    public void blockCardNumbersInput(MPEditText text) {
        int maxLength = MercadoPago.BIN_LENGTH;
        setInputMaxLength(text, maxLength);
        mCardNumberBlocked = true;
    }

    public void unBlockCardNumbersInput(MPEditText text){
        int maxLength = 16;
        setInputMaxLength(text, maxLength);
        mCardNumberBlocked = false;
    }

    public void setInputMaxLength(MPEditText text, int maxLength) {
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        text.setFilters(fArray);
    }

}
