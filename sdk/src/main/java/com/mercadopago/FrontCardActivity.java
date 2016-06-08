package com.mercadopago;

import android.support.v7.app.AppCompatActivity;

import com.mercadopago.model.PaymentMethod;

import java.text.DecimalFormat;
import java.util.Locale;

public abstract class FrontCardActivity extends AppCompatActivity implements CardInterface {

    protected String mCardNumber;
    protected String mCardHolderName;
    protected String mExpiryMonth;
    protected String mExpiryYear;
    protected String mCardIdentificationNumber;
    protected String mErrorState;
    protected String mSecurityCode = "";
    protected PaymentMethod mCurrentPaymentMethod;


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

    public void saveCardExpiryYear(String year)  {
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
        if (paymentMethod == null) {
            return getResources().getColor(CardInterface.FULL_TEXT_VIEW_COLOR);
        }
        String colorName = "mpsdk_font_" + paymentMethod.getId().toLowerCase();
        return getResources().getIdentifier(colorName, "color", getPackageName());
    }

    @Override
    public String buildNumberWithMask(int cardLength, String s) {
        String result = "";
        if (cardLength == CARD_NUMBER_AMEX_LENGTH) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 4 ; i++) {
                char c = getCharOfCard(s, i);
                sb.append(c);
            }
            sb.append(" ");
            for (int i = 4; i < 10; i++) {
                char c = getCharOfCard(s, i);
                sb.append(c);
            }
            sb.append(" ");
            for (int i = 10; i < CARD_NUMBER_AMEX_LENGTH; i++) {
                char c = getCharOfCard(s, i);
                sb.append(c);
            }
            result = sb.toString();
        } else if (cardLength == CARD_NUMBER_DINERS_LENGTH) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 4 ; i++) {
                char c = getCharOfCard(s, i);
                sb.append(c);
            }
            sb.append(" ");
            for (int i = 4; i < 10; i++) {
                char c = getCharOfCard(s, i);
                sb.append(c);
            }
            sb.append(" ");
            for (int i = 10; i < CARD_NUMBER_DINERS_LENGTH; i++) {
                char c = getCharOfCard(s, i);
                sb.append(c);
            }
            result = sb.toString();
        } else {
            StringBuffer sb = new StringBuffer();
            for (int i = 1; i <= cardLength; i++) {
                sb.append(getCharOfCard(s, i-1));
                if (i % 4 == 0) {
                    sb.append(" ");
                }
            }
            result = sb.toString();
        }
        return result;
    }

    private char getCharOfCard(String s, int i) {
        if (i < s.length()) {
            return s.charAt(i);
        } else {
            return "•".charAt(0);
        }
    }

    public String buildIdentificationNumberWithMask(CharSequence s) {
        if (s.length() == 0) {
            return s.toString();
        }
        try {
            Integer value = Integer.valueOf(s.toString());
            Locale.setDefault(Locale.GERMAN);
            return String.format("%,d", value);
        } catch (NumberFormatException e) {
            return "";
        }
    }

}
