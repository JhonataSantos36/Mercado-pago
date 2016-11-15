package com.mercadopago.controllers;

import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.CardInformation;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Setting;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodGuessingController {

    private String mSavedBin;
    private List<PaymentMethod> mAllPaymentMethods;
    private List<PaymentMethod> mGuessedPaymentMethods;
    private List<String> mExcludedPaymentTypes;
    private String mPaymentTypeId;

    public PaymentMethodGuessingController(List<PaymentMethod> paymentMethods,
                                           String paymentTypeId, List<String> excludedPaymentTypes) {
        this.mAllPaymentMethods = paymentMethods;
        this.mExcludedPaymentTypes = excludedPaymentTypes;
        this.mPaymentTypeId = paymentTypeId;
        this.mSavedBin = "";
    }

    public String getPaymentTypeId() {
        return mPaymentTypeId;
    }

    public List<PaymentMethod> getGuessedPaymentMethods() {
        return mGuessedPaymentMethods;
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
        return mGuessedPaymentMethods;
    }

    public String getSavedBin() {
        return mSavedBin;
    }

    private List<PaymentMethod> getValidPaymentMethodForType(String paymentTypeId,
                                                             List<PaymentMethod> paymentMethods) {
        if (paymentTypeId == null) {
            return paymentMethods;
        } else {
            List<PaymentMethod> validPaymentMethodsForType = new ArrayList<>();
            for (PaymentMethod pm : paymentMethods) {
                if (pm.getPaymentTypeId().equals(paymentTypeId))
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

    public Setting getSettingByPaymentMethod(PaymentMethod paymentMethod) {
        List<Setting> settings = paymentMethod.getSettings();
        Setting setting = Setting.getSettingByBin(settings, mSavedBin);
        return setting;
    }

    public static Setting getSettingByPaymentMethodAndBin(PaymentMethod paymentMethod, String bin) {
        List<Setting> settings = paymentMethod.getSettings();
        Setting setting = Setting.getSettingByBin(settings, bin);
        return setting;
    }

    public static Integer getCardNumberLength(PaymentMethod paymentMethod, String bin) {
        if (paymentMethod == null) {
            return CardInformation.CARD_NUMBER_MAX_LENGTH;
        }
        Setting setting = PaymentMethodGuessingController.getSettingByPaymentMethodAndBin(paymentMethod, bin);
        int cardNumberLength = CardInformation.CARD_NUMBER_MAX_LENGTH;
        if (setting != null) {
            cardNumberLength = setting.getCardNumber().getLength();
        }
        return cardNumberLength;
    }

}
