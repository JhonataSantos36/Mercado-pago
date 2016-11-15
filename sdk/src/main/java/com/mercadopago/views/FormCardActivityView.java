package com.mercadopago.views;

import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.IdentificationType;

import java.util.List;

/**
 * Created by vaserber on 10/13/16.
 */

public interface FormCardActivityView {
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
    void hideSecurityCodeInput();
    void hideIdentificationInput();
    void showIdentificationInput();
    void showSecurityCodeInput();
    void setCardholderNameListeners();
    void setExpiryDateListeners();
}
