package com.mercadopago.utils;

import com.mercadopago.model.DummyCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaserber on 7/22/16.
 */
public class CardTestUtils {

    // * Card
    public final static String DUMMY_SECURITY_CODE = "123";
    public final static String DUMMY_SECURITY_CODE_LONG = "1234";


    public static DummyCard getPaymentMethodOnWithFrontSecurityCode() {
        return new DummyCard("amex", "371180303257522", DUMMY_SECURITY_CODE_LONG);
    }

    public static DummyCard getPaymentMethodOnWithBackSecurityCode() {
        return new DummyCard("visa", "4170068810108020", DUMMY_SECURITY_CODE);
    }

    public static DummyCard getPaymentMethodOnWithoutRequiredSecurityCode() {
        return new DummyCard("tarshop", "2799519076121", null);
    }

    public static DummyCard getPaymentMethodWithMultipleIssuers() {
        return new DummyCard("master", "5156883002652543", DUMMY_SECURITY_CODE);
    }

    public static List<String> getInvalidCardNumbers() {
        List<String> cardNumbers = new ArrayList<>();
        cardNumbers.add("");
        cardNumbers.add("1234");
        cardNumbers.add("417006");
        cardNumbers.add("4170061111111111");
        return cardNumbers;
    }

    public static List<String> getInvalidExpiryDates() {
        List<String> expiryDates = new ArrayList<>();
        expiryDates.add("12");
        expiryDates.add("1419");
        expiryDates.add("1215");
        expiryDates.add("1415");
        expiryDates.add("125");
        return expiryDates;
    }

    public static List<String> getInvalidSecurityCodes() {
        List<String> securityCodes = new ArrayList<>();
        securityCodes.add("1");
        securityCodes.add("12");
        return securityCodes;
    }

    public static List<String> getInvalidIdentificationNumber() {
        List<String> identificationNumbers = new ArrayList<>();
        identificationNumbers.add("1");
        identificationNumbers.add("1234");
        return identificationNumbers;
    }

    public static String getCardNumber(String paymentMethod) {
        switch (paymentMethod) {
            case "visa":
                return "4170068810108020";
            case "master":
                return "5031755734530604";
            case "cordial":
                return "5221352856430472";
            default:
                return null;
        }
    }

    public static List<DummyCard> getAllCards() {
        List<DummyCard> cardTokens = new ArrayList<>();
        cardTokens.add(new DummyCard("visa", "4170068810108020", DUMMY_SECURITY_CODE));
        cardTokens.add(new DummyCard("master", "5031755734530604", DUMMY_SECURITY_CODE));
        cardTokens.add(new DummyCard("amex", "371180303257522", DUMMY_SECURITY_CODE_LONG));

        return cardTokens;
    }

    public static String getMockedBinInFront(String cardNumber) {
        StringBuffer contained = new StringBuffer();
        contained.append(cardNumber.substring(0,4));
        contained.append(" ");
        contained.append(cardNumber.substring(4,6));
        return contained.toString();
    }

}
