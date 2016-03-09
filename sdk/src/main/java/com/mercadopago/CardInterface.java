package com.mercadopago;

import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;

public interface CardInterface {

    public static int NEUTRAL_CARD_COLOR = R.color.mpsdk_white;
    public static int EMPTY_TEXT_VIEW_COLOR = R.color.mpsdk_base_text;
    public static int FULL_TEXT_VIEW_COLOR = R.color.mpsdk_base_text;
    public static int ERROR_TEXT_VIEW_COLOR = R.color.mpsdk_color_red_error;

    public static String CARD_SIDE_FRONT = "front";
    public static String CARD_SIDE_BACK = "back";
    public static String CARD_IDENTIFICATION = "identification";
    public static String ERROR_STATE = "textview_error";
    public static String NORMAL_STATE = "textview_normal";

    public static String CARD_NUMBER_INPUT = "cardNumber";
    public static String CARDHOLDER_NAME_INPUT = "cardHolderName";
    public static String CARD_EXPIRYDATE_INPUT = "cardExpiryDate";
    public static String CARD_SECURITYCODE_INPUT = "cardSecurityCode";
    public static String CARD_IDENTIFICATION_INPUT = "cardIdentification";
    public static String CARD_INPUT_FINISH = "card_input_finish";

    public void saveCardSecurityCode(String securityCode);

    public String getSecurityCode();

    public String getSecurityCodeState();

    public String getSecurityCodeLocation();

    public void saveSecurityCodeState(String state);

    public void saveCardExpiryMonth(String expiryMonth);

    public void saveCardName(String cardName);

    public String getCardNumber();

    public void saveCardNumber(String cardNumber);

    public String getCardNumberState();

    public void saveCardNumberState(String state);

    public String buildNumberWithMask(CharSequence s);

    public String buildIdentificationNumberWithMask(CharSequence s);

    public String getCardHolderName();

    public String getCardHolderNameState();

    public void saveCardHolderNameState(String state);

    public String getExpiryDateState();

    public void saveExpiryDateState(String state);

    public String getExpiryMonth();

    public String getExpiryYear();

    public String getCardIdentificationNumber();

    public void saveCardExpiryYear(String year);

    public PaymentMethod getCurrentPaymentMethod();

    public int getCardImage(PaymentMethod paymentMethod);

    public int getCardColor(PaymentMethod paymentMethod);

    public int getCardFontColor(PaymentMethod paymentMethod);

    public void checkFocusOnSecurityCode();

    public boolean hasToFlipCard();

    public void saveCardIdentificationNumber(String number);

    public String getCardIdentificationNumberState();

    public boolean isSecurityCodeRequired();

}
