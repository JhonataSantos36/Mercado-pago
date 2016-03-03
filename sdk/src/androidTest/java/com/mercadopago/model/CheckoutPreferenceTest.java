package com.mercadopago.model;

import com.mercadopago.exceptions.CheckoutPreferenceException;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by mromar on 2/24/16.
 */
public class CheckoutPreferenceTest extends TestCase {

    ///////////////////CURRENCY tests///////////////////
    public void testWhenValidatePreferenceWithTwoItemsWithOneCurrencyNullReturnFalse() {
        CheckoutPreference preference = getPreferenceWithTwoItemsOneHasCurrencyNull();
        Assert.assertFalse(preference.itemsValid());
    }

    public void testWhenValidatePreferenceWithTwoItemsWithDifferentCurrencyReturnFalse() {
        CheckoutPreference preference = getPreferenceWithTwoItemsWithDifferrentCurrency();
        Assert.assertFalse(preference.itemsValid());
    }


    ///////////////////PAYMENTS_TYPES tests///////////////////
    public void testWhenValidatePreferenceReturnFalseIfAllPaymentsTypesAreExcluded() {
        CheckoutPreference preference = getPreferenceWithAllPaymentTypesExcluded();
        Assert.assertFalse(preference.isExcludedPaymentTypesValid());
    }

    public void testWhenValidatePreferenceReturnTrueIfSomePaymentsTypesAreExcluded() {
        CheckoutPreference preference = getPreferenceWithSomePaymentTypesExcluded();
        Assert.assertTrue(preference.isExcludedPaymentTypesValid());
    }

    ///////////////////INSTALLMENTS tests///////////////////
    public void testWhenValidatePreferenceReturnFalseIfPositiveInstallmentsDefaultNumberAndNegativeInstallmentDefaultNumber() {
        CheckoutPreference preference = getPreferenceWithPositiveDefaultInstallmentsNumberAndNegativeInstallmentsNumber();
        Assert.assertFalse(preference.isInstallmentsValid());
    }

    public void testWhenValidatePreferenceReturnFalseIfPositiveInstallmentsNumberAndNegativeInstallmentDefaultNumber() {
        CheckoutPreference preference = getPreferenceWithPositiveInstallmentsNumberAndNegativeDefaultInstallmentsNumber();
        Assert.assertFalse(preference.isInstallmentsValid());
    }

    public void testWhenValidatePreferenceReturnTrueIfNumberInstallmentsValid() {
        CheckoutPreference preference = getPreferenceWithValidNumberInstallments();
        Assert.assertTrue(preference.isInstallmentsValid());
    }

    public void testWhenValidatePreferenceReturnFalseIfIsNegativeNumberInstallments() {
        CheckoutPreference preference = getPreferenceWithNegativeNumberInstallments();
        Assert.assertFalse(preference.isInstallmentsValid());
    }

    ///////////////////EXCEPTIONS tests///////////////////
    public void testWhenValidatePreferenceReturnTrueIfNoThrowException() {
        CheckoutPreference preference = getPreferenceWithOneItemValidActiveAndSomePaymentTypesExcluded();
        Boolean valid = true;

        try {
            preference.validate();
        } catch (CheckoutPreferenceException e) {
            valid = false;
        }
        finally{
            assertTrue(valid);
        }
    }

    public void testWhenValidatePreferenceReturnFalseIfAllPaymentTypesAreExcludedThrowException() {
        CheckoutPreference preference = getPreferenceWithOneItemValidActiveButAllPaymentTypesExcluded();

        try {
            preference.validate();
        } catch (CheckoutPreferenceException e) {
            assertTrue(e.getErrorCode() == CheckoutPreferenceException.EXCLUDED_ALL_PAYMENTTYPES);
        }
    }

    public void testWhenValidatePreferenceReturnFalseIfPreferenceInstallmentsDefaultAndInstallmentsAreNegativeThrowException() {
        CheckoutPreference preference = getPreferenceWithOneItemValidButInstallmenstsDefaultAndInstallmentNegative();

        try {
            preference.validate();
        } catch (CheckoutPreferenceException e) {
            assertTrue(e.getErrorCode() == CheckoutPreferenceException.INVALID_INSTALLMENTS);
        }
    }

    public void testWhenValidatePreferenceReturnTrueIfPreferenceIsNotActiveThrowException() {
        CheckoutPreference preference = getPreferenceWithOneItemValidButInactive();

        try {
            preference.validate();
        } catch (CheckoutPreferenceException e) {
            assertTrue(e.getErrorCode() == CheckoutPreferenceException.INACTIVE_PREFERENCE);
        }
    }

    public void testWhenValidatePreferenceReturnTrueIfPreferenceIsExpiredThrowException() {
        CheckoutPreference preference = getPreferenceWithOneItemValidButExpired();

        try {
            preference.validate();
        } catch (CheckoutPreferenceException e) {
            assertTrue(e.getErrorCode() == CheckoutPreferenceException.EXPIRED_PREFERENCE);
        }
    }


    public void testWhenValidatePreferenceReturnTrueItemsIfItemsIsInvalidThrowException() {
        CheckoutPreference preference = getPreferenceWithEmptyItems();

        try {
            preference.validate();
        } catch (CheckoutPreferenceException e) {
            assertTrue(e.getErrorCode() == CheckoutPreferenceException.INVALID_ITEM);
        }
    }


    ///////////////////ITEMS tests///////////////////
    public void testWhenValidatePreferenceWithTwoItemsReturnTrue() {
        CheckoutPreference preference = getPreferenceWithTwoItems();
        Assert.assertTrue(preference.itemsValid());
    }

    public void testWhenValidatePreferenceWithTwoItemsWithoutUnitPriceReturnFalse() {
        CheckoutPreference preference = getPreferenceWithTwoItemsWithoutUnitPrice();
        Assert.assertFalse(preference.itemsValid());
    }

    public void testWhenValidatePreferenceItemReturnFalseIfItemUnitPriceIsNegative() {
        CheckoutPreference preference = getPreferenceWithNegativeUnitPriceItem();
        Assert.assertFalse(preference.itemsValid());
    }

    public void testWhenValidatePreferenceItemReturnFalseIfItemUnitPriceIsNull() {
        CheckoutPreference preference = getPreferenceWithNullUnitPriceItem();
        Assert.assertFalse(preference.itemsValid());
    }

    public void testWhenValidatePreferenceItemReturnFalseIfItemQuantityIsZero() {
        CheckoutPreference preference = getPreferenceWithZeroQuantityItem();
        Assert.assertFalse(preference.itemsValid());
    }

    public void testWhenValidatePreferenceItemReturnFalseIfItemQuantityIsNegative() {
        CheckoutPreference preference = getPreferenceWithNegativeQuantityItem();
        Assert.assertFalse(preference.itemsValid());
    }

    public void testWhenValidatePreferenceItemReturnFalseIfItemQuantityIsNull() {
        CheckoutPreference preference = getPreferenceWithNullQuantityItem();
        Assert.assertFalse(preference.itemsValid());
    }

    public void testWhenValidatePreferenceItemReturnFalseIfItemIdIsNull() {
        CheckoutPreference preference = getPreferenceWithNullIdItem();
        Assert.assertFalse(preference.itemsValid());
    }

    public void testWenValidatePreferenceItemsReturnFalseIfItemsListIsNull() {
        CheckoutPreference preference = getPreferenceWithNullItems();
        Assert.assertFalse(preference.itemsValid());
    }

    public void testWhenValidatePreferenceItemsReturnFalseIfItemsListIsEmpty() {
        CheckoutPreference preference = getPreferenceWithEmptyItems();
        Assert.assertFalse(preference.itemsValid());
    }

    ///////////////////DATES tests///////////////////
    public void testWhenPreferenceIsActiveReturnIsActiveTrue() {
        CheckoutPreference preference = getActivePreference();
        Assert.assertTrue(preference.isActive());
    }

    public void testWhenPreferenceIsNotActiveReturnIsActiveFalse() {
        CheckoutPreference preference = getInactivePreference();
        Assert.assertFalse(preference.isActive());
    }

    public void testWhenPreferenceIsNotExpiredReturnExpiredFalse() {
        CheckoutPreference preference = getNotExpiredPreference();
        Assert.assertFalse(preference.isExpired());
    }

    public void testWhenPreferenceIsExpiredReturnExpiredTrue() {
        CheckoutPreference preference = getExpiredPreference();

        Assert.assertTrue(preference.isExpired());
    }

    public void testWhenValidatePreferenceExpirationDateToReturnFalseIfExpirationDateToIsNull() {
        CheckoutPreference preference = getPreferenceWithNullExpirationDateTo();
        Assert.assertFalse(preference.isExpired());
    }

    public void testWhenValidatePreferenceExpirationDateFromReturnFalseIfExpirationDateFromIsNull() {
        CheckoutPreference preference = getPreferenceWithNullExpirationDateFrom();
        Assert.assertFalse(preference.isActive());
    }

    ///////////////////Getters preferences with different DATES///////////////////
    private CheckoutPreference getActivePreference() {
        CheckoutPreference preference = new CheckoutPreference();
        Date pastDate = new Date();
        pastDate.setTime((new Date().getTime()) - 1000 * 60 * 60);
        preference.setActiveFrom(pastDate);
        return preference;
    }

    private CheckoutPreference getInactivePreference() {
        CheckoutPreference preference = new CheckoutPreference();

        GregorianCalendar calendar = new GregorianCalendar(2100, 3, 3); //Date should be after that today
        Date date = calendar.getTime();
        preference.setActiveFrom(date);
        return preference;
    }

    private CheckoutPreference getNotExpiredPreference() {
        CheckoutPreference preference = new CheckoutPreference();

        GregorianCalendar calendar = new GregorianCalendar(2100, 7, 3); //Date should be after that today
        Date date = calendar.getTime();
        preference.setExpirationDate(date);
        return preference;
    }

    private CheckoutPreference getExpiredPreference() {
        CheckoutPreference preference = new CheckoutPreference();

        GregorianCalendar calendar = new GregorianCalendar(2015, 3, 3); //Date should be before that today
        Date date = calendar.getTime();
        preference.setExpirationDate(date);
        return preference;
    }

    private CheckoutPreference getPreferenceWithNullExpirationDateFrom() {
        CheckoutPreference preference = new CheckoutPreference();
        preference.setActiveFrom(null);
        return preference;
    }

    private CheckoutPreference getPreferenceWithNullExpirationDateTo() {
        CheckoutPreference preference = new CheckoutPreference();
        preference.setExpirationDate(null);
        return preference;
    }

    ///////////////////Getters preferences with different CURRENCY///////////////////
    private CheckoutPreference getPreferenceWithTwoItemsOneHasCurrencyNull() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item itemA = new Item("123", 1);
        Item itemB = new Item("456", 1);

        itemA.setUnitPrice(new BigDecimal(2));
        itemB.setUnitPrice(new BigDecimal(5));

        itemA.setCurrencyId("USD");

        items.add(itemA);
        items.add(itemB);
        preference.setItems(items);
        return preference;
    }

    private CheckoutPreference getPreferenceWithTwoItemsWithDifferrentCurrency() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item itemA = new Item("123", 1);
        Item itemB = new Item("456", 1);

        itemA.setUnitPrice(new BigDecimal(2));
        itemB.setUnitPrice(new BigDecimal(5));

        itemA.setCurrencyId("USD");
        itemB.setCurrencyId("PesoARG");

        items.add(itemA);
        items.add(itemB);
        preference.setItems(items);
        return preference;
    }

    ///////////////////Getters preferences with different ITEMS///////////////////
    private CheckoutPreference getPreferenceWithTwoItems() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item itemA = new Item("123", 1);
        Item itemB = new Item("456", 1);

        itemA.setUnitPrice(new BigDecimal(2));
        itemB.setUnitPrice(new BigDecimal(5));

        itemA.setCurrencyId("USD");
        itemB.setCurrencyId("USD");

        items.add(itemA);
        items.add(itemB);
        preference.setItems(items);
        return preference;
    }

    private CheckoutPreference getPreferenceWithTwoItemsWithoutUnitPrice() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item itemA = new Item("123", 1);
        Item itemB = new Item("456", 1);

        items.add(itemA);
        items.add(itemB);
        preference.setItems(items);
        return preference;
    }

    private CheckoutPreference getPreferenceWithNegativeUnitPriceItem() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item item = new Item("123", 1);

        item.setUnitPrice(new BigDecimal(-1));

        items.add(item);
        preference.setItems(items);
        return preference;
    }

    private CheckoutPreference getPreferenceWithNullUnitPriceItem() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item item = new Item("123", 1);

        item.setUnitPrice(null);

        items.add(item);
        preference.setItems(items);
        return preference;
    }

    private CheckoutPreference getPreferenceWithZeroQuantityItem() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item item = new Item("123", 0);

        items.add(item);
        preference.setItems(items);
        return preference;
    }

    private CheckoutPreference getPreferenceWithNegativeQuantityItem() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item item = new Item("123", -1);

        items.add(item);
        preference.setItems(items);
        return preference;
    }

    private CheckoutPreference getPreferenceWithNullQuantityItem() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item item = new Item("123", null);

        items.add(item);
        preference.setItems(items);
        return preference;
    }

    private CheckoutPreference getPreferenceWithNullIdItem() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item item = new Item(null, 1);

        items.add(item);
        preference.setItems(items);
        return preference;
    }

    private CheckoutPreference getPreferenceWithNullItems() {
        CheckoutPreference preference = new CheckoutPreference();
        preference.setItems(null);
        return preference;
    }

    private CheckoutPreference getPreferenceWithEmptyItems() {
        CheckoutPreference preference = new CheckoutPreference();
        preference.setItems(new ArrayList<Item>());
        return preference;
    }

    ///////////////////Getters preferences with different PAYMENTS_TYPES///////////////////
    private CheckoutPreference getPreferenceWithAllPaymentTypesExcluded() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        ArrayList<String> paymentTypes= new ArrayList<>();
        Item itemA = new Item("123", 1);

        String CREDIT_CARD = "credit_card";
        String DEBIT_CARD = "debit_card";
        String PREPAID_CARD = "prepaid_card";
        String TICKET = "ticket";
        String ATM = "atm";
        String DIGITAL_CURRENCY = "digital_currency";

        paymentTypes.add(CREDIT_CARD);
        paymentTypes.add(DEBIT_CARD);
        paymentTypes.add(PREPAID_CARD);
        paymentTypes.add(TICKET);
        paymentTypes.add(ATM);
        paymentTypes.add(DIGITAL_CURRENCY);

        itemA.setUnitPrice(new BigDecimal(2));
        itemA.setCurrencyId("USD");
        items.add(itemA);
        preference.setItems(items);

        PaymentMethodPreference paymentMethodPreference = new PaymentMethodPreference();
        paymentMethodPreference.setExcludedPaymentTypes(paymentTypes);
        preference.setPaymentMethods(paymentMethodPreference);

        return preference;
    }

    private CheckoutPreference getPreferenceWithSomePaymentTypesExcluded() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        ArrayList<String> paymentTypes= new ArrayList<>();
        Item itemA = new Item("123", 1);

        String CREDIT_CARD = "credit_card";
        String DEBIT_CARD = "debit_card";
        String PREPAID_CARD = "prepaid_card";
        String TICKET = "ticket";
        String ATM = "atm";
        String DIGITAL_CURRENCY = "digital_currency";

        paymentTypes.add(CREDIT_CARD);
        paymentTypes.add(DEBIT_CARD);
        paymentTypes.add(PREPAID_CARD);

        itemA.setUnitPrice(new BigDecimal(2));
        itemA.setCurrencyId("USD");
        items.add(itemA);
        preference.setItems(items);

        PaymentMethodPreference paymentMethodPreference = new PaymentMethodPreference();
        paymentMethodPreference.setExcludedPaymentTypes(paymentTypes);
        preference.setPaymentMethods(paymentMethodPreference);

        return preference;
    }

    ///////////////////Getters preferences with different INSTALLMENT///////////////////
    private CheckoutPreference getPreferenceWithPositiveInstallmentsNumberAndNegativeDefaultInstallmentsNumber() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item itemA = new Item("123", 1);

        itemA.setUnitPrice(new BigDecimal(2));
        itemA.setCurrencyId("USD");
        items.add(itemA);
        preference.setItems(items);

        PaymentMethodPreference paymentMethodPreference = new PaymentMethodPreference();
        paymentMethodPreference.setInstallments(1);
        paymentMethodPreference.setDefaultInstallments(-3);
        preference.setPaymentMethods(paymentMethodPreference);

        return preference;
    }

    private CheckoutPreference getPreferenceWithPositiveDefaultInstallmentsNumberAndNegativeInstallmentsNumber() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item itemA = new Item("123", 1);

        itemA.setUnitPrice(new BigDecimal(2));
        itemA.setCurrencyId("USD");
        items.add(itemA);
        preference.setItems(items);

        PaymentMethodPreference paymentMethodPreference = new PaymentMethodPreference();
        paymentMethodPreference.setInstallments(-1);
        paymentMethodPreference.setDefaultInstallments(3);
        preference.setPaymentMethods(paymentMethodPreference);

        return preference;
    }

    private CheckoutPreference getPreferenceWithValidNumberInstallments() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item itemA = new Item("123", 1);

        itemA.setUnitPrice(new BigDecimal(2));
        itemA.setCurrencyId("USD");
        items.add(itemA);
        preference.setItems(items);

        PaymentMethodPreference paymentMethodPreference = new PaymentMethodPreference();
        paymentMethodPreference.setInstallments(1);
        paymentMethodPreference.setDefaultInstallments(3);
        preference.setPaymentMethods(paymentMethodPreference);

        return preference;
    }

    private CheckoutPreference getPreferenceWithNegativeNumberInstallments() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item itemA = new Item("123", 1);

        itemA.setUnitPrice(new BigDecimal(2));
        itemA.setCurrencyId("USD");
        items.add(itemA);
        preference.setItems(items);

        PaymentMethodPreference paymentMethodPreference = new PaymentMethodPreference();
        paymentMethodPreference.setInstallments(-1);
        paymentMethodPreference.setDefaultInstallments(-1);
        preference.setPaymentMethods(paymentMethodPreference);

        return preference;
    }

    ///////////////////Getters preferences with different EXCEPTIONS///////////////////
    private CheckoutPreference getPreferenceWithOneItemValidActiveAndSomePaymentTypesExcluded() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        ArrayList<String> paymentTypes= new ArrayList<>();
        Item itemA = new Item("123", 1);

        itemA.setUnitPrice(new BigDecimal(2));
        itemA.setCurrencyId("USD");
        items.add(itemA);
        preference.setItems(items);

        String CREDIT_CARD = "credit_card";
        String DEBIT_CARD = "debit_card";
        String PREPAID_CARD = "prepaid_card";
        String TICKET = "ticket";
        String ATM = "atm";
        String DIGITAL_CURRENCY = "digital_currency";

        paymentTypes.add(CREDIT_CARD);
        paymentTypes.add(DEBIT_CARD);
        paymentTypes.add(PREPAID_CARD);
        //paymentTypes.add(TICKET);
        //paymentTypes.add(ATM);
        //paymentTypes.add(DIGITAL_CURRENCY);

        Date pastDate = new Date();
        pastDate.setTime((new Date().getTime()) - 1000 * 60 * 60);
        preference.setActiveFrom(pastDate);

        PaymentMethodPreference paymentMethodPreference = new PaymentMethodPreference();
        paymentMethodPreference.setInstallments(0);
        paymentMethodPreference.setDefaultInstallments(0);
        paymentMethodPreference.setExcludedPaymentTypes(paymentTypes);
        preference.setPaymentMethods(paymentMethodPreference);

        return preference;
    }

    private CheckoutPreference getPreferenceWithOneItemValidActiveButAllPaymentTypesExcluded() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        ArrayList<String> paymentTypes= new ArrayList<>();
        Item itemA = new Item("123", 1);

        itemA.setUnitPrice(new BigDecimal(2));
        itemA.setCurrencyId("USD");
        items.add(itemA);
        preference.setItems(items);

        String CREDIT_CARD = "credit_card";
        String DEBIT_CARD = "debit_card";
        String PREPAID_CARD = "prepaid_card";
        String TICKET = "ticket";
        String ATM = "atm";
        String DIGITAL_CURRENCY = "digital_currency";

        paymentTypes.add(CREDIT_CARD);
        paymentTypes.add(DEBIT_CARD);
        paymentTypes.add(PREPAID_CARD);
        paymentTypes.add(TICKET);
        paymentTypes.add(ATM);
        paymentTypes.add(DIGITAL_CURRENCY);

        Date pastDate = new Date();
        pastDate.setTime((new Date().getTime()) - 1000 * 60 * 60);
        preference.setActiveFrom(pastDate);

        PaymentMethodPreference paymentMethodPreference = new PaymentMethodPreference();
        paymentMethodPreference.setInstallments(0);
        paymentMethodPreference.setDefaultInstallments(0);
        paymentMethodPreference.setExcludedPaymentTypes(paymentTypes);
        preference.setPaymentMethods(paymentMethodPreference);

        return preference;
    }


    private CheckoutPreference getPreferenceWithOneItemValidButInstallmenstsDefaultAndInstallmentNegative() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item itemA = new Item("123", 1);

        itemA.setUnitPrice(new BigDecimal(2));
        itemA.setCurrencyId("USD");
        items.add(itemA);
        preference.setItems(items);

        Date pastDate = new Date();
        pastDate.setTime((new Date().getTime()) - 1000 * 60 * 60);
        preference.setActiveFrom(pastDate);

        PaymentMethodPreference paymentMethodPreference = new PaymentMethodPreference();
        paymentMethodPreference.setInstallments(-1);
        paymentMethodPreference.setDefaultInstallments(-3);
        preference.setPaymentMethods(paymentMethodPreference);

        return preference;
    }

    private CheckoutPreference getPreferenceWithOneItemValidButExpired() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item itemA = new Item("123", 1);

        itemA.setUnitPrice(new BigDecimal(2));
        itemA.setCurrencyId("USD");
        items.add(itemA);
        preference.setItems(items);

        GregorianCalendar calendar = new GregorianCalendar(2015, 3, 3); //Date should be before that today
        Date date = calendar.getTime();
        preference.setExpirationDate(date);

        return preference;
    }

    private CheckoutPreference getPreferenceWithOneItemValidButInactive() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item itemA = new Item("123", 1);

        itemA.setUnitPrice(new BigDecimal(2));
        itemA.setCurrencyId("USD");
        items.add(itemA);
        preference.setItems(items);

        return preference;
    }
}