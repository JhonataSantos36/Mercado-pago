package com.mercadopago.controllers;

import com.mercadopago.lite.model.PaymentTypes;
import com.mercadopago.lite.model.CardInformation;
import com.mercadopago.lite.model.PaymentMethod;
import com.mercadopago.lite.model.Setting;
import com.mercadopago.util.MercadoPagoUtil;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodGuessingController {

    private String mSavedBin;
    private final List<PaymentMethod> mAllPaymentMethods;
    private List<PaymentMethod> mGuessedPaymentMethods;
    private final List<String> mExcludedPaymentTypes;
    private final String mPaymentTypeId;

    public PaymentMethodGuessingController(List<PaymentMethod> paymentMethods,
                                           String paymentTypeId, List<String> excludedPaymentTypes) {
        mAllPaymentMethods = paymentMethods;
        mExcludedPaymentTypes = excludedPaymentTypes;
        mPaymentTypeId = paymentTypeId;
        mSavedBin = "";
    }

    public String getPaymentTypeId() {
        return mPaymentTypeId;
    }

    public List<PaymentMethod> getGuessedPaymentMethods() {
        return mGuessedPaymentMethods;
    }

    public List<PaymentMethod> getAllSupportedPaymentMethods() {
        List<PaymentMethod> supportedPaymentMethods = new ArrayList<>();
        for (PaymentMethod paymentMethod : mAllPaymentMethods) {
            if (isCardPaymentType(paymentMethod) &&
                    ((mPaymentTypeId == null) || (mPaymentTypeId.equals(paymentMethod.getPaymentTypeId())))) {
                supportedPaymentMethods.add(paymentMethod);
            }
        }
        return supportedPaymentMethods;
    }

    private boolean isCardPaymentType(PaymentMethod paymentMethod) {
        String paymentTypeId = paymentMethod.getPaymentTypeId();
        return paymentTypeId.equals(PaymentTypes.CREDIT_CARD) ||
                paymentTypeId.equals(PaymentTypes.DEBIT_CARD) ||
                paymentTypeId.equals(PaymentTypes.PREPAID_CARD);
    }

    public List<PaymentMethod> guessPaymentMethodsByBin(String bin) {
        if (mSavedBin.equals(bin) && mGuessedPaymentMethods != null) {
            return mGuessedPaymentMethods;
        }
        saveBin(bin);
        mGuessedPaymentMethods = MercadoPagoUtil
                .getValidPaymentMethodsForBin(mSavedBin, mAllPaymentMethods);
        mGuessedPaymentMethods = getValidPaymentMethodForType(mPaymentTypeId, mGuessedPaymentMethods);
        if (mGuessedPaymentMethods.size() > 1) {
            mGuessedPaymentMethods = filterByPaymentType(mExcludedPaymentTypes, mGuessedPaymentMethods);
        }
        return mGuessedPaymentMethods;
    }

    public void saveBin(String bin) {
        mSavedBin = bin;
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
        Setting setting = null;
        if (bin == null) {
            if (paymentMethod.getSettings() != null && !paymentMethod.getSettings().isEmpty()) {
                setting = paymentMethod.getSettings().get(0);
            }
        } else {
            List<Setting> settings = paymentMethod.getSettings();
            setting = Setting.getSettingByBin(settings, bin);
        }
        return setting;
    }

    public static Integer getCardNumberLength(PaymentMethod paymentMethod, String bin) {

        if (paymentMethod == null || bin == null) {
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
