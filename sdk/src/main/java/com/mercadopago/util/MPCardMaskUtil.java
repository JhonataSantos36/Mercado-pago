package com.mercadopago.util;

import com.mercadopago.model.IdentificationType;

/**
 * Created by vaserber on 8/3/16.
 */
public class MPCardMaskUtil {

    public static final String BASE_FRONT_SECURITY_CODE = "••••";
    public static final int CPF_SEPARATOR_AMOUNT = 3;
    public static final int CNPJ_SEPARATOR_AMOUNT = 4;
    public static final int LAST_DIGITS_LENGTH = 4;
    public static final char HIDDEN_NUMBER_CHAR = "•".charAt(0);
    public static final int ORIGINAL_SPACE_DIGIT = 4;

    public static final int CARD_NUMBER_MAX_LENGTH = 16;
    public static final int CARD_NUMBER_AMEX_LENGTH = 15;
    public static final int CARD_NUMBER_DINERS_LENGTH = 14;
    private static final int CARD_NUMBER_MAESTRO_SETTING_1_LENGTH = 18;
    private static final int CARD_NUMBER_MAESTRO_SETTING_2_LENGTH = 19;

    protected MPCardMaskUtil() {

    }

    public static String getCardNumberHidden(int cardNumberLength, String lastFourDigits) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cardNumberLength - LAST_DIGITS_LENGTH; i++) {
            sb.append(HIDDEN_NUMBER_CHAR);
        }
        sb.append(lastFourDigits);
        return buildNumberWithMask(cardNumberLength, sb.toString());
    }

    public static String buildNumberWithMask(int cardLength, String number) {
        String result = "";
        if (cardLength == CARD_NUMBER_AMEX_LENGTH) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 4; i++) {
                char c = getCharOfCard(number, i);
                sb.append(c);
            }
            sb.append(" ");
            for (int i = 4; i < 10; i++) {
                char c = getCharOfCard(number, i);
                sb.append(c);
            }
            sb.append(" ");
            for (int i = 10; i < CARD_NUMBER_AMEX_LENGTH; i++) {
                char c = getCharOfCard(number, i);
                sb.append(c);
            }
            result = sb.toString();
        } else if (cardLength == CARD_NUMBER_DINERS_LENGTH) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 4; i++) {
                char c = getCharOfCard(number, i);
                sb.append(c);
            }
            sb.append(" ");
            for (int i = 4; i < 10; i++) {
                char c = getCharOfCard(number, i);
                sb.append(c);
            }
            sb.append(" ");
            for (int i = 10; i < CARD_NUMBER_DINERS_LENGTH; i++) {
                char c = getCharOfCard(number, i);
                sb.append(c);
            }
            result = sb.toString();

        } else if (cardLength == CARD_NUMBER_MAESTRO_SETTING_1_LENGTH) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 10; i++) {
                char c = getCharOfCard(number, i);
                sb.append(c);
            }
            sb.append(" ");
            for (int i = 10; i < 15; i++) {
                char c = getCharOfCard(number, i);
                sb.append(c);
            }
            sb.append(" ");
            for (int i = 15; i < CARD_NUMBER_MAESTRO_SETTING_1_LENGTH; i++) {
                char c = getCharOfCard(number, i);
                sb.append(c);
            }
            result = sb.toString();

        } else if (cardLength == CARD_NUMBER_MAESTRO_SETTING_2_LENGTH) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 9; i++) {
                char c = getCharOfCard(number, i);
                sb.append(c);
            }
            sb.append(" ");
            for (int i = 9; i < CARD_NUMBER_MAESTRO_SETTING_2_LENGTH; i++) {
                char c = getCharOfCard(number, i);
                sb.append(c);
            }
            result = sb.toString();

        } else {
            StringBuffer sb = new StringBuffer();
            for (int i = 1; i <= cardLength; i++) {
                sb.append(getCharOfCard(number, i - 1));
                if (i % 4 == 0) {
                    sb.append(" ");
                }
            }
            result = sb.toString();
        }
        return result;
    }


    public static char getCharOfCard(String number, int i) {
        if (i < number.length()) {
            return number.charAt(i);
        }
        return "•".charAt(0);
    }

    public static String buildIdentificationNumberWithMask(CharSequence number, IdentificationType identificationType) {
        if (identificationType != null && identificationType.getId() != null) {
            String type = identificationType.getId();
            if (type.equals("CPF")) {
                return buildIdentificationNumberOfTypeCPF(number, identificationType.getMaxLength());
            } else if (type.equals("CNPJ")) {
                return buildIdentificationNumberOfTypeCNPJ(number, identificationType.getMaxLength());
            }
        }
        return buildIdentificationNumberWithDecimalSeparator(number);
    }

    public static String buildIdentificationNumberWithDecimalSeparator(CharSequence number) {
        if (number.length() == 0) {
            return number.toString();
        }
        return getMaskedNumberWithDecimalSymbols(number.toString());
    }

    private static String getMaskedNumberWithDecimalSymbols(String idNumber) {
        StringBuilder maskBuilder = new StringBuilder();

        String nonNumericRegex = "(\\D(.*))";
        String decimalsSymbolRegex = "(\\d)(?=(\\d{3})+$)";

        String onlyNumbers = idNumber.replaceAll(nonNumericRegex, "");

        //Add decimal symbol to numbers
        maskBuilder.append(onlyNumbers.replaceAll(decimalsSymbolRegex, "$1."));

        //Append anything after numeric prefix
        maskBuilder.append(idNumber.replace(onlyNumbers, ""));

        return maskBuilder.toString();
    }

    public static String buildIdentificationNumberOfTypeCPF(CharSequence number, int maxLength) {
        String result = "";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < (maxLength + CPF_SEPARATOR_AMOUNT) && i < number.length(); i++) {
            if (i == 3 || i == 6) {
                sb.append(".");
            } else if (i == 9) {
                sb.append("-");
            }
            sb.append(number.charAt(i));
        }
        result = sb.toString();
        return result;
    }

    public static String buildIdentificationNumberOfTypeCNPJ(CharSequence number, int maxLength) {
        String result = "";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < (maxLength + CNPJ_SEPARATOR_AMOUNT) && i < number.length(); i++) {
            if (i == 2 || i == 5) {
                sb.append(".");
            } else if (i == 8) {
                sb.append("/");
            } else if (i == 12) {
                sb.append("-");
            }
            sb.append(number.charAt(i));
        }
        result = sb.toString();
        return result;
    }

    public static String buildSecurityCode(int securityCodeLength, String s) {
        StringBuffer sb = new StringBuffer();
        if (s == null || s.length() == 0) {
            return BASE_FRONT_SECURITY_CODE;
        }
        for (int i = 0; i < securityCodeLength; i++) {
            char c = getCharOfCard(s, i);
            sb.append(c);
        }
        return sb.toString();
    }

    public static boolean needsMask(CharSequence currentNumber, int cardNumberLength) {

        if (cardNumberLength == CARD_NUMBER_MAESTRO_SETTING_1_LENGTH) {

            return currentNumber.length() == 10 || currentNumber.length() == 16;
        } else if (cardNumberLength == CARD_NUMBER_MAESTRO_SETTING_2_LENGTH) {

            return currentNumber.length() == 9;
        } else if (cardNumberLength == CARD_NUMBER_AMEX_LENGTH || cardNumberLength == CARD_NUMBER_DINERS_LENGTH) {
            return currentNumber.length() == 4 || currentNumber.length() == 11;
        } else {
            return currentNumber.length() == 4 || currentNumber.length() == 9 || currentNumber.length() == 14;
        }

    }

    public static StringBuilder getCardNumberReset(String currentCardNumber) {
        StringBuilder cardNumberReset = new StringBuilder();
        cardNumberReset.append(currentCardNumber, 0, ORIGINAL_SPACE_DIGIT);
        cardNumberReset.append(" ");
        cardNumberReset.append(currentCardNumber.charAt(ORIGINAL_SPACE_DIGIT));
        return cardNumberReset;
    }

    public static boolean isDefaultSpaceErasable(int currentNumberLength) {
        return currentNumberLength < 6;
    }

}
