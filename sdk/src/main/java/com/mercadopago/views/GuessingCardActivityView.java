package com.mercadopago.views;

import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.lite.exceptions.CardTokenException;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.lite.exceptions.ApiException;
import com.mercadopago.model.Discount;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;
import com.mercadopago.mvp.MvpView;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vaserber on 10/13/16.
 */

public interface GuessingCardActivityView extends MvpView {
    void onValidStart();

    void onInvalidStart(String message);

    void initializeTimer();

    void showError(MercadoPagoError error, String requestOrigin);

    void setCardNumberListeners(PaymentMethodGuessingController controller);

    void showInputContainer();

    void showApiExceptionError(ApiException exception, String requestOrigin);

    void setCardNumberInputMaxLength(int length);

    void setSecurityCodeInputMaxLength(int length);

    void setSecurityCodeViewLocation(String location);

    void initializeIdentificationTypes(List<IdentificationType> identificationTypes);

    void setNextButtonListeners();

    void setBackButtonListeners();

    void setErrorContainerListener();

    void setContainerAnimationListeners();

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

    void setErrorView(String message);

    void setErrorView(CardTokenException exception);

    void setErrorCardNumber();

    void setErrorCardholderName();

    void setErrorExpiryDate();

    void setErrorSecurityCode();

    void setErrorIdentificationNumber();

    void clearErrorIdentificationNumber();

    void initializeTitle();

    void setCardholderName(String cardholderName);

    void setIdentificationNumber(String identificationNumber);

    void setSoftInputMode();

    void showDiscountRow(BigDecimal transactionAmount);

    void finishCardFlow(PaymentMethod paymentMethod, Token token, Discount discount, Boolean directDiscountEnabled, Boolean discountEnabled, List<Issuer> issuers);

    void finishCardFlow(PaymentMethod paymentMethod, Token token, Discount discount, Boolean directDiscountEnabled, Boolean discountEnabled, Issuer issuer, List<PayerCost> payerCosts);

    void finishCardFlow(PaymentMethod paymentMethod, Token token, Discount discount, Boolean directDiscountEnabled, Boolean discountEnabled, Issuer issuer, PayerCost payerCost);

    void startDiscountActivity(BigDecimal transactionAmount);

    void hideProgress();

    void setExclusionWithOneElementInfoView(PaymentMethod supportedPaymentMethod, boolean withAnimation);

    void hideExclusionWithOneElementInfoView();

    void setInvalidCardOnePaymentMethodErrorView();

    void setInvalidCardMultipleErrorView();

    void resolvePaymentMethodSet(PaymentMethod paymentMethod);

    void clearSecurityCodeEditText();

    void checkClearCardView();

    void hideRedErrorContainerView(boolean withAnimation);

    void restoreBlackInfoContainerView();

    void clearCardNumberInputLength();

    void clearCardNumberEditTextMask();

    void askForPaymentType();

    void showFinishCardFlow();

    void setPaymentMethod(PaymentMethod paymentMethod);
}
