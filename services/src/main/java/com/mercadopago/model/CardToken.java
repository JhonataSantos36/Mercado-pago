package com.mercadopago.model;

import android.content.Context;
import android.text.TextUtils;

import com.mercadopago.lite.exceptions.CardTokenException;

import java.util.Calendar;
import java.util.Locale;

public class CardToken {

    public static final int MIN_LENGTH_NUMBER = 10;
    public static final int MAX_LENGTH_NUMBER = 19;
    private static final Calendar now = Calendar.getInstance();

    private Cardholder cardholder;
    private String cardNumber;
    private Device device;
    private Integer expirationMonth;
    private Integer expirationYear;
    private String securityCode;

    public CardToken(String cardNumber, Integer expirationMonth, Integer expirationYear,
                     String securityCode, String cardholderName, String identificationType, String identificationNumber) {
        this.cardNumber = normalizeCardNumber(cardNumber);
        this.expirationMonth = expirationMonth;
        this.expirationYear = normalizeYear(expirationYear);
        this.securityCode = securityCode;
        cardholder = new Cardholder();
        cardholder.setName(cardholderName);
        Identification identification = new Identification();
        identification.setNumber(identificationNumber);
        identification.setType(identificationType);
        cardholder.setIdentification(identification);
    }

    public static boolean validateSecurityCode(String securityCode) {

        return securityCode == null || (!TextUtils.isEmpty(securityCode) && securityCode.length() >= 3 && securityCode.length() <= 4);
    }

    public static void validateSecurityCode(String securityCode, PaymentMethod paymentMethod, String bin) throws CardTokenException {

        if (paymentMethod != null) {
            Setting setting = Setting.getSettingByBin(paymentMethod.getSettings(), bin);

            // Validate security code length
            if (setting != null) {
                int cvvLength = setting.getSecurityCode().getLength();
                if ((securityCode == null) || ((cvvLength != 0) && (securityCode.trim().length() != cvvLength))) {
                    throw new CardTokenException(CardTokenException.INVALID_CVV_LENGTH, String.valueOf(cvvLength));
                }
            } else {
                throw new CardTokenException(CardTokenException.INVALID_FIELD);
            }
        }
    }

    public static boolean validateExpiryDate(Integer month, Integer year) {

        if (!validateExpMonth(month)) {
            return false;
        }
        if (!validateExpYear(year)) {
            return false;
        }
        return !hasMonthPassed(month, year);
    }

    public static boolean validateExpMonth(Integer month) {

        return month != null && (month >= 1 && month <= 12);
    }

    public static boolean validateExpYear(Integer year) {

        return year != null && !hasYearPassed(year);
    }

    public static boolean checkLuhn(String cardNumber) {

        int sum = 0;
        boolean alternate = false;
        if ((cardNumber == null) || (cardNumber.length() == 0)) {
            return false;
        }
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    private static boolean hasYearPassed(int year) {
        int normalized = normalizeYear(year);
        return normalized < now.get(Calendar.YEAR);
    }

    private static boolean hasMonthPassed(int month, int year) {
        return hasYearPassed(year) || normalizeYear(year) == now.get(Calendar.YEAR) && month < (now.get(Calendar.MONTH) + 1);
    }

    private static Integer normalizeYear(Integer year) {
        Integer normalizedYear = year;

        if ((year != null) && (year < 100 && year >= 0)) {
            String currentYear = String.valueOf(now.get(Calendar.YEAR));
            String prefix = currentYear.substring(0, currentYear.length() - 2);
            normalizedYear = Integer.parseInt(String.format(Locale.US, "%s%02d", prefix, year));
        }
        return normalizedYear;
    }

    public Cardholder getCardholder() {
        return cardholder;
    }

    public void setCardholder(Cardholder cardholder) {
        this.cardholder = cardholder;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Context context) {
        device = new Device(context);
    }

    public Integer getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(Integer expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public Integer getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(Integer expirationYear) {
        this.expirationYear = CardToken.normalizeYear(expirationYear);
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public boolean validate(boolean includeSecurityCode) {
        boolean result = validateCardNumber() && validateExpiryDate() && validateIdentification() && validateCardholderName();
        if (includeSecurityCode) {
            result = result && validateSecurityCode();
        }
        return result;
    }

    public boolean validateCardNumber() {
        return !TextUtils.isEmpty(cardNumber) && (cardNumber.length() > MIN_LENGTH_NUMBER) && (cardNumber.length() < MAX_LENGTH_NUMBER);
    }

    public void validateCardNumber(PaymentMethod paymentMethod) throws CardTokenException {

        // Empty field

        if (cardNumber == null || cardNumber.isEmpty()) {
            throw new CardTokenException(CardTokenException.INVALID_EMPTY_CARD);
        }

        Setting setting = Setting.getSettingByBin(paymentMethod.getSettings(), (cardNumber.length()
                >= Bin.BIN_LENGTH ? cardNumber.substring(0, Bin.BIN_LENGTH) : ""));

        if (setting == null) {

            // Invalid bin
            throw new CardTokenException(CardTokenException.INVALID_CARD_BIN);

        } else {

            // Validate cards length
            int cardLength = setting.getCardNumber().getLength();
            if (cardNumber.trim().length() != cardLength) {

                throw new CardTokenException(CardTokenException.INVALID_CARD_LENGTH, String.valueOf(cardLength));
            }

            // Validate luhn
            String luhnAlgorithm = setting.getCardNumber().getValidation();
            if (("standard".equals(luhnAlgorithm)) && (!checkLuhn(cardNumber))) {
                throw new CardTokenException(CardTokenException.INVALID_CARD_LUHN);
            }
        }
    }

    public boolean validateSecurityCode() {

        return validateSecurityCode(securityCode);
    }

    public void validateSecurityCode(PaymentMethod paymentMethod) throws CardTokenException {
        validateSecurityCode(securityCode, paymentMethod, (((cardNumber != null) ?
                cardNumber.length() : 0) >= Bin.BIN_LENGTH ?
                cardNumber.substring(0, Bin.BIN_LENGTH) : ""));
    }

    public boolean validateIdentificationNumber(IdentificationType identificationType) {
        if (identificationType != null) {
            if ((cardholder != null) &&
                    (cardholder.getIdentification() != null) &&
                    (cardholder.getIdentification().getNumber() != null)) {
                int len = cardholder.getIdentification().getNumber().length();
                Integer min = identificationType.getMinLength();
                Integer max = identificationType.getMaxLength();
                if ((min != null) && (max != null)) {
                    return ((len <= max) && (len >= min));
                } else {
                    return validateIdentificationNumber();
                }
            } else {
                return false;
            }
        } else {
            return validateIdentificationNumber();
        }
    }

    public boolean validateExpiryDate() {

        return validateExpiryDate(expirationMonth, expirationYear);
    }

    public boolean validateIdentification() {
        return validateIdentificationType() && validateIdentificationNumber();
    }

    public boolean validateIdentificationType() {
        return cardholder != null &&
                (cardholder.getIdentification() != null && !TextUtils.isEmpty(cardholder.getIdentification().getType()));
    }

    public boolean validateIdentificationNumber() {
        return cardholder != null && (cardholder.getIdentification() != null &&
                (validateIdentificationType() && !TextUtils.isEmpty(cardholder.getIdentification().getNumber())));
    }

    public boolean validateCardholderName() {
        return cardholder != null && cardholder.getName() != null && !cardholder.getName().isEmpty();
    }

    private String normalizeCardNumber(String number) {
        return number.trim().replaceAll("\\s+|-", "");
    }
}
