import android.support.test.InstrumentationRegistry;

import com.mercadopago.model.CardToken;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.PaymentMethod;

import com.mercadopago.lite.test.StaticMock;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class CardTokenTest {

    @Test
    public void testConstructor() {
        CardToken cardToken = StaticMock.getCardToken();

        assertTrue(cardToken.getCardNumber().equals(StaticMock.DUMMY_CARD_NUMBER));
        assertTrue(cardToken.getExpirationMonth() == StaticMock.DUMMY_EXPIRATION_MONTH);
        assertTrue(cardToken.getExpirationYear() == StaticMock.DUMMY_EXPIRATION_YEAR_LONG);
        assertTrue(cardToken.getSecurityCode().equals(StaticMock.DUMMY_SECURITY_CODE));
        assertTrue(cardToken.getCardholder().getName().equals(StaticMock.DUMMY_CARDHOLDER_NAME));
        assertTrue(cardToken.getCardholder().getIdentification().getType().equals(StaticMock.DUMMY_IDENTIFICATION_TYPE));
        assertTrue(cardToken.getCardholder().getIdentification().getNumber().equals(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
    }

    @Test
    public void testValidateNoSecurityCode() {
        CardToken cardToken = StaticMock.getCardToken();
        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        assertTrue(cardToken.validateSecurityCode(paymentMethod));
    }

    // * Card number
    @Test
    public void testCardNumber() {
        CardToken cardToken = StaticMock.getCardToken();
        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        assertTrue(cardToken.validateCardNumber(paymentMethod));
    }

    @Test
    public void testCardNumberEmpty() {
        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("");

        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        assertTrue(!cardToken.validateCardNumber(paymentMethod));
    }

    @Test
    public void testCardNumberMinLength() {
        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("4444");

        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        assertTrue(!cardToken.validateCardNumber(paymentMethod));
    }

    @Test
    public void testCardNumberMaxLength() {
        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("44440000444400004444");

        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        assertTrue(!cardToken.validateCardNumber(paymentMethod));
    }

    @Test
    public void testCardNumberWithPaymentMethodInvalidBin() {
        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("5300888800009999");

        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        assertFalse(cardToken.validateCardNumber(paymentMethod));
    }

    @Test
    public void testCardNumberWithPaymentMethodInvalidLength() {
        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("466057001125");

        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        assertFalse(cardToken.validateCardNumber(paymentMethod));
    }

    @Test
    public void testCardNumberWithPaymentMethodInvalidLuhn() {
        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("4660888888888888");

        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        assertFalse(cardToken.validateCardNumber(paymentMethod));
    }

    // * Security code
    @Test
    public void testSecurityCode() {
        CardToken cardToken = StaticMock.getCardToken();
        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        assertTrue(cardToken.validateSecurityCode(paymentMethod));
    }

    @Test
    public void testSecurityCodeEmpty() {
        CardToken cardToken = StaticMock.getCardToken();
        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        cardToken.setSecurityCode("");

        assertTrue(!cardToken.validateSecurityCode(paymentMethod));
    }

    @Test
    public void testSecurityCodeMinLength() {
        CardToken cardToken = StaticMock.getCardToken();
        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        cardToken.setSecurityCode("4");

        assertTrue(!cardToken.validateSecurityCode(paymentMethod));
    }

    @Test
    public void testSecurityCodeMaxLength() {
        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setSecurityCode("44444");

        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        assertTrue(!cardToken.validateSecurityCode(paymentMethod));
    }

    @Test
    public void testSecurityCodeWithPaymentMethod() {
        CardToken cardToken = StaticMock.getCardToken();
        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        assertTrue(cardToken.validateSecurityCode(paymentMethod));
    }

    @Test
    public void testSecurityCodeWithPaymentMethodInvalidBin() {
        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("5300888800009999");

        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        assertFalse(cardToken.validateSecurityCode(paymentMethod));
    }

    @Test
    public void testSecurityCodeWithPaymentMethodInvalidLength() {
        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setSecurityCode("4444");

        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        assertFalse(cardToken.validateSecurityCode(paymentMethod));
    }

    // TODO: test cvv not required

    // * Expiry date
    @Test
    public void testExpiryDate() {
        CardToken cardToken = StaticMock.getCardToken();

        Integer month = 3;
        Integer year = 2029;

        assertTrue(cardToken.validateExpiryDate(month, year));
    }

    @Test
    public void testExpiryDateNullMonth() {
        CardToken cardToken = StaticMock.getCardToken();

        Integer month = null;
        Integer year = 2020;

        assertFalse(cardToken.validateExpiryDate(month, year));
    }

    @Test
    public void testExpiryDateWrongMonth() {
        CardToken cardToken = StaticMock.getCardToken();

        Integer month = 13;
        Integer year = 2020;

        assertFalse(cardToken.validateExpiryDate(month, year));
    }

    @Test
    public void testExpiryDateNullYear() {
        CardToken cardToken = StaticMock.getCardToken();

        Integer month = 13;
        Integer year = null;

        assertFalse(cardToken.validateExpiryDate(month, year));
    }

    @Test
    public void testExpiryDateWrongYear() {
        CardToken cardToken = StaticMock.getCardToken();

        Integer month = 13;
        Integer year = 2000;

        assertFalse(cardToken.validateExpiryDate(month, year));
    }

    @Test
    public void testExpiryDateWrongShortYear() {
        CardToken cardToken = StaticMock.getCardToken();

        Integer month = 13;
        Integer year = 00;

        assertFalse(cardToken.validateExpiryDate(month, year));
    }

    // * Identification
    @Test
    public void testIdentification() {
        CardToken cardToken = StaticMock.getCardToken();

        assertTrue(cardToken.validateIdentificationNumber());
    }

    @Test
    public void testIdentificationNullCardholder() {
        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardholder(null);

        assertFalse(cardToken.validateIdentificationNumber());
    }

    @Test
    public void testIdentificationNullIdentification() {
        CardToken cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().setIdentification(null);

        assertFalse(cardToken.validateIdentificationNumber());
    }

    @Test
    public void testIdentificationEmptyType() {
        CardToken cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().getIdentification().setType("");

        assertFalse(cardToken.validateIdentificationNumber());
    }

    @Test
    public void testIdentificationEmptyNumber() {
        CardToken cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().getIdentification().setNumber("");

        assertFalse(cardToken.validateIdentificationNumber());
    }

    @Test
    public void testIdentificationNumber() {
        CardToken cardToken = StaticMock.getCardToken();
        IdentificationType type = StaticMock.getIdentificationType();

        assertTrue(cardToken.validateIdentificationNumber(type));
    }

    @Test
    public void testIdentificationNumberWrongLength() {
        CardToken cardToken;
        IdentificationType type;

        cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().getIdentification().setNumber("123456");
        type = StaticMock.getIdentificationType();
        assertFalse(cardToken.validateIdentificationNumber(type));

        cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().getIdentification().setNumber("12345678901234567890");
        type = StaticMock.getIdentificationType();
        assertFalse(cardToken.validateIdentificationNumber(type));
    }

    @Test
    public void testIdentificationNumberNullIdType() {
        CardToken cardToken = StaticMock.getCardToken();

        assertTrue(cardToken.validateIdentificationNumber(null));
    }

    @Test
    public void testIdentificationNumberNullCardholderValues() {
        CardToken cardToken;
        IdentificationType type;

        cardToken = StaticMock.getCardToken();
        cardToken.setCardholder(null);
        type = StaticMock.getIdentificationType();
        assertFalse(cardToken.validateIdentificationNumber(type));

        cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().setIdentification(null);
        type = StaticMock.getIdentificationType();
        assertFalse(cardToken.validateIdentificationNumber(type));

        cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().getIdentification().setNumber(null);
        type = StaticMock.getIdentificationType();
        assertFalse(cardToken.validateIdentificationNumber(type));
    }

    @Test
    public void testIdentificationNumberNullMinMaxLength() {
        CardToken cardToken;
        IdentificationType type;

        cardToken = StaticMock.getCardToken();
        type = StaticMock.getIdentificationType();
        type.setMinLength(null);
        assertTrue(cardToken.validateIdentificationNumber(type));

        cardToken = StaticMock.getCardToken();
        type = StaticMock.getIdentificationType();
        type.setMaxLength(null);
        assertTrue(cardToken.validateIdentificationNumber(type));
    }

    // * Cardholder name
    @Test
    public void testCardholderName() {
        CardToken cardToken = StaticMock.getCardToken();

        assertTrue(cardToken.validateCardholderName());
    }

    @Test
    public void testCardholderNameEmpty() {
        CardToken cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().setName("");

        assertFalse(cardToken.validateCardholderName());
    }

    @Test
    public void testCardholderNameNull() {
        CardToken cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().setName(null);

        assertFalse(cardToken.validateCardholderName());
    }

    @Test
    public void testCardholderNameCardholderNull() {
        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardholder(null);

        assertFalse(cardToken.validateCardholderName());
    }
}
