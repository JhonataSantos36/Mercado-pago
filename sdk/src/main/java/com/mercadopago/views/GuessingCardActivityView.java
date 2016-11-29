package com.mercadopago.views;

import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.IdentificationType;

import java.util.List;

/**
 * Created by vaserber on 10/13/16.
 */

public interface GuessingCardActivityView {
    void onValidStart();
    void onInvalidStart(String message);
    void setCardNumberListeners(PaymentMethodGuessingController controller);
    void showInputContainer();
    void showApiExceptionError(ApiException exception);
    void setCardNumberInputMaxLength(int length);
    void setSecurityCodeInputMaxLength(int length);
    void setSecurityCodeViewLocation(String location);
    void startErrorView(String message, String errorDetail);
    void initializeIdentificationTypes(List<IdentificationType> identificationTypes);
    void setNextButtonListeners();
    void setBackButtonListeners();
    void setIdentificationTypeListeners();
    void setIdentificationNumberListeners();
    void hideSecurityCodeInput();
    void hideIdentificationInput();
    void showIdentificationInput();
    void showSecurityCodeInput();
    void setCardholderNameListeners();
    void setExpiryDateListeners();
    void setSecurityCodeListeners();
    void setIdentificationNumberRestrictions(String type);
    void hideBankDeals();
    void showBankDeals();
    void clearErrorView();
    void setErrorView(String mErrorState);
    void setErrorCardNumber();
    void setErrorCardholderName();
    void setErrorExpiryDate();
    void setErrorSecurityCode();
    void setErrorIdentificationNumber();
    void clearErrorIdentificationNumber();
    void initializeTitle();
    void setCardholderName(String cardholderName);
    void setIdentificationNumber(String identificationNumber);
}
