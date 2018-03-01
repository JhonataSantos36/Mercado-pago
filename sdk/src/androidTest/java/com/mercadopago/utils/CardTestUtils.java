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

    public static DummyCard getDummyCard(String paymentMethodId) {
        switch (paymentMethodId) {
            case "master":
                return new DummyCard("master", "5156883002652543", DUMMY_SECURITY_CODE, "5156 8830 0265 2543");
            case "amex":
                return new DummyCard("amex", "371180303257522", DUMMY_SECURITY_CODE_LONG, "3711 803032 57522");
            case "visa":
                return new DummyCard("visa", "4170068810108020", DUMMY_SECURITY_CODE, "4170 0688 1010 8020");
            case "tarshop":
                return new DummyCard("tarshop", "2799519076121", null, "2799 5190 7612 1");
            case "cordial":
                return new DummyCard("cordial", "5221352856430472", DUMMY_SECURITY_CODE, "5221 3528 5643 0472");
            case "naranja":
                return new DummyCard("naranja", "5895627823453005", DUMMY_SECURITY_CODE, "5895 6278 2345 3005");
            case "cencosud":
                return new DummyCard("cencosud", "6034937272862830", DUMMY_SECURITY_CODE, "6034 9372 7286 2830");
            case "argencard":
                return new DummyCard("argencard", "5011054211206753", DUMMY_SECURITY_CODE, "5011 0542 1120 6753");
            case "cabal":
                return new DummyCard("cabal", "6035227716427021", DUMMY_SECURITY_CODE, "6035 2277 1642 7021");
            case "nativa":
                return new DummyCard("nativa", "5465532683840176", DUMMY_SECURITY_CODE, "5465 5326 8384 0176");
            case "diners":
                return new DummyCard("diners", "30238030180020", DUMMY_SECURITY_CODE, "3023 803018 0020");
            case "cordobesa":
                return new DummyCard("cordobesa", "5500732058068364", DUMMY_SECURITY_CODE, "5500 7320 5806 8364");
            case "cmr":
                return new DummyCard("cmr", "5570390633007137", DUMMY_SECURITY_CODE, "5570 3906 3300 7137");
            case "mercadopago_cc":
                return new DummyCard("mercadopago_cc", "5150730431208304", DUMMY_SECURITY_CODE, "5150 7304 3120 8304");
            case "master_mlm":
                return new DummyCard("master", "5031755734530604", DUMMY_SECURITY_CODE, "5031 7557 3453 0604");
            default:
                return null;
        }
    }


    public static DummyCard getPaymentMethodOnWithFrontSecurityCode() {
        return getDummyCard("amex");
    }

    public static DummyCard getPaymentMethodOnWithBackSecurityCode() {
        return getDummyCard("visa");
    }

    public static DummyCard getPaymentMethodOnWithoutRequiredSecurityCode() {
        return getDummyCard("tarshop");
    }

    public static DummyCard getPaymentMethodWithMultipleIssuers() {
        return getDummyCard("master");
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

    public static List<DummyCard> getSomeCards() {
        List<DummyCard> cardTokens = new ArrayList<>();
        cardTokens.add(getDummyCard("visa"));
        cardTokens.add(getDummyCard("master"));
        cardTokens.add(getDummyCard("amex"));

        return cardTokens;
    }

    public static String getMockedBinInFront(String cardNumber) {
        StringBuffer contained = new StringBuffer();
        contained.append(cardNumber.substring(0, 4));
        contained.append(" ");
        contained.append(cardNumber.substring(4, 6));
        return contained.toString();
    }

}
