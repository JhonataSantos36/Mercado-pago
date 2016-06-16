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
    public void testWhenValidatePreferenceWithTwoItemsWithSameCurrencyIdReturnTrue() {
        CheckoutPreference preference = getPreferenceWithTwoItemsWithSameCurrencyId();
        Assert.assertTrue(preference.itemsValid());
    }

    public void testWhenValidatePreferenceWithTwoItemsWithDifferentCurrencyIdReturnFalse() {
        CheckoutPreference preference = getPreferenceWithTwoItemsWithDifferentCurrencyId();
        Assert.assertFalse(preference.itemsValid());
    }

    public void testWhenValidatePreferenceWithTwoItemsIfOneHasCurrencyNullReturnFalse() {
        CheckoutPreference preference = getPreferenceWithTwoItemsOneHasCurrencyNull();
        Assert.assertFalse(preference.itemsValid());
    }

    public void testWhenValidatePreferenceWithTwoItemsWithIncorrectCurrencyReturnFalse() {
        CheckoutPreference preference = getPreferenceWithTwoItemsWithIncorrectCurrencyId();
        Assert.assertFalse(preference.itemsValid());
    }

    ///////////////////PAYMENTS_TYPES tests///////////////////
    public void testWhenValidatePreferenceWithAllPaymentsTypesExcludedReturnFalse() {
        CheckoutPreference preference = getPreferenceWithAllPaymentTypesExcluded();
        Assert.assertFalse(preference.validPaymentTypeExclusion());
    }

    public void testWhenValidatePreferenceWithSomePaymentsTypesExcludedReturnTrue() {
        CheckoutPreference preference = getPreferenceWithSomePaymentTypesExcluded();
        Assert.assertTrue(preference.validPaymentTypeExclusion());
    }

    ///////////////////INSTALLMENTS tests///////////////////
    public void testWhenValidatePreferenceWithPositiveDefaultInstallmentsNumberAndNegativeMaxInstallmentsNumberReturnFalse() {
        CheckoutPreference preference = getPreferenceWithPositiveDefaultInstallmentsNumberAndNegativeMaxInstallmentsNumber();
        Assert.assertFalse(preference.validInstallmentsPreference());
    }

    public void testWhenValidatePreferenceWithPositiveMaxInstallmentsNumberAndNegativeDefaultInstallmentsNumberReturnFalse() {
        CheckoutPreference preference = getPreferenceWithPositiveMaxInstallmentsNumberAndNegativeDefaultInstallmentsNumber();
        Assert.assertFalse(preference.validInstallmentsPreference());
    }

    public void testWhenValidatePreferenceWithMaxInstallmentsNumberPositiveReturnTrue() {
        CheckoutPreference preference = getPreferenceWithPositiveInstallmentsNumber();
        Assert.assertTrue(preference.validInstallmentsPreference());
    }

    public void testWhenValidatePreferenceWithNegativeMaxInstallmentsNumberReturnFalse() {
        CheckoutPreference preference = getPreferenceWithNegativeInstallmentsNumbers();
        Assert.assertFalse(preference.validInstallmentsPreference());
    }

    ///////////////////EXCEPTIONS tests///////////////////
    public void testWhenValidatePreferenceValidNoThrowExceptionReturnTrue() {
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

    public void testWhenValidatePreferenceWithAllPaymentTypesExcludedThrowExceptionReturnTrue() {
        CheckoutPreference preference = getPreferenceWithOneItemValidActiveButAllPaymentTypesExcluded();

        try {
            preference.validate();
        } catch (CheckoutPreferenceException e) {
            assertTrue(e.getErrorCode() == CheckoutPreferenceException.EXCLUDED_ALL_PAYMENT_TYPES);
        }
    }

    public void testWhenValidatePreferenceWithInstallmentsDefaultNumberAndInstallmentsNumberNegativeThrowExceptionReturnTrue() {
        CheckoutPreference preference = getPreferenceWithOneItemValidButInstallmenstsDefaultNumberAndInstallmentsNumberNegative();

        try {
            preference.validate();
        } catch (CheckoutPreferenceException e) {
            assertTrue(e.getErrorCode() == CheckoutPreferenceException.INVALID_INSTALLMENTS);
        }
    }

    public void testWhenValidatePreferenceWithPreferenceExpiredThrowExceptionReturnTrue() {
        CheckoutPreference preference = getPreferenceWithOneItemValidButPreferenceExpired();

        try {
            preference.validate();
        } catch (CheckoutPreferenceException e) {
            assertTrue(e.getErrorCode() == CheckoutPreferenceException.EXPIRED_PREFERENCE);
        }
    }


    public void testWhenValidatePreferenceWithItemsInvalidThrowExceptionReturnTrue() {
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

    public void testWhenValidatePreferenceWithNegativeUnitPriceItemReturnFalse() {
        CheckoutPreference preference = getPreferenceWithNegativeUnitPriceItem();
        Assert.assertFalse(preference.itemsValid());
    }

    public void testWhenValidatePreferenceWithNullUnitPriceItemReturnFalse() {
        CheckoutPreference preference = getPreferenceWithNullUnitPriceItem();
        Assert.assertFalse(preference.itemsValid());
    }

    public void testWhenValidatePreferenceWithZeroItemQuantityReturnFalse() {
        CheckoutPreference preference = getPreferenceWithZeroItemQuantity();
        Assert.assertFalse(preference.itemsValid());
    }

    public void testWhenValidatePreferenceWithNegativeItemQuantityReturnFalse() {
        CheckoutPreference preference = getPreferenceWithNegativeItemQuantity();
        Assert.assertFalse(preference.itemsValid());
    }

    public void testWhenValidatePreferenceWithNullItemQuantityReturnFalse() {
        CheckoutPreference preference = getPreferenceWithNullItemQuantity();
        Assert.assertFalse(preference.itemsValid());
    }

    public void testWhenValidatePreferenceWithNullItemIdReturnFalse() {
        CheckoutPreference preference = getPreferenceWithNullItemId();
        Assert.assertFalse(preference.itemsValid());
    }

    public void testWenValidatePreferenceWithNullItemsListReturnFalse() {
        CheckoutPreference preference = getPreferenceWithNullItems();
        Assert.assertFalse(preference.itemsValid());
    }

    public void testWhenValidatePreferenceWithEmptyItemsListReturnFalse() {
        CheckoutPreference preference = getPreferenceWithEmptyItems();
        Assert.assertFalse(preference.itemsValid());
    }

    ///////////////////DATES tests///////////////////
    public void testWhenPreferenceIsActiveReturnTrue() {
        CheckoutPreference preference = getActivePreference();
        Assert.assertTrue(preference.isActive());
    }

    public void testWhenPreferenceIsNotActiveReturnFalse() {
        CheckoutPreference preference = getInactivePreference();
        Assert.assertFalse(preference.isActive());
    }

    public void testWhenPreferenceIsNotExpiredReturnFalse() {
        CheckoutPreference preference = getNotExpiredPreference();
        Assert.assertFalse(preference.isExpired());
    }

    public void testWhenPreferenceIsExpiredReturnTrue() {
        CheckoutPreference preference = getExpiredPreference();

        Assert.assertTrue(preference.isExpired());
    }

    public void testWhenValidatePreferenceWithNullExpirationDateToReturnFalse() {
        CheckoutPreference preference = getPreferenceWithNullExpirationDateTo();
        Assert.assertFalse(preference.isExpired());
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
    private CheckoutPreference getPreferenceWithTwoItemsWithSameCurrencyId() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item itemA = new Item("123", 1);
        Item itemB = new Item("456", 1);

        itemA.setUnitPrice(new BigDecimal(2));
        itemB.setUnitPrice(new BigDecimal(5));

        itemA.setCurrencyId("ARS");
        itemB.setCurrencyId("ARS");


        items.add(itemA);
        items.add(itemB);
        preference.setItems(items);
        return preference;
    }

    private CheckoutPreference getPreferenceWithTwoItemsWithDifferentCurrencyId() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item itemA = new Item("123", 1);
        Item itemB = new Item("456", 1);

        itemA.setUnitPrice(new BigDecimal(2));
        itemB.setUnitPrice(new BigDecimal(5));

        itemA.setCurrencyId("US$");
        itemB.setCurrencyId("ARS");


        items.add(itemA);
        items.add(itemB);
        preference.setItems(items);
        return preference;
    }

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

    private CheckoutPreference getPreferenceWithTwoItemsWithIncorrectCurrencyId() {
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

    private CheckoutPreference getPreferenceWithZeroItemQuantity() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item item = new Item("123", 0);

        items.add(item);
        preference.setItems(items);
        return preference;
    }

    private CheckoutPreference getPreferenceWithNegativeItemQuantity() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item item = new Item("123", -1);

        items.add(item);
        preference.setItems(items);
        return preference;
    }

    private CheckoutPreference getPreferenceWithNullItemQuantity() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item item = new Item("123", null);

        items.add(item);
        preference.setItems(items);
        return preference;
    }

    private CheckoutPreference getPreferenceWithNullItemId() {
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

        paymentTypes.addAll(PaymentType.getAllPaymentTypes());

        itemA.setUnitPrice(new BigDecimal(2));
        itemA.setCurrencyId("USD");
        items.add(itemA);
        preference.setItems(items);

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentTypeIds(paymentTypes);
        preference.setPaymentPreference(paymentPreference);

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

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentTypeIds(paymentTypes);
        preference.setPaymentPreference(paymentPreference);

        return preference;
    }

    ///////////////////Getters preferences with different INSTALLMENT///////////////////
    private CheckoutPreference getPreferenceWithPositiveMaxInstallmentsNumberAndNegativeDefaultInstallmentsNumber() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item itemA = new Item("123", 1);

        itemA.setUnitPrice(new BigDecimal(2));
        itemA.setCurrencyId("USD");
        items.add(itemA);
        preference.setItems(items);

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setMaxAcceptedInstallments(1);
        paymentPreference.setDefaultInstallments(-3);
        preference.setPaymentPreference(paymentPreference);

        return preference;
    }

    private CheckoutPreference getPreferenceWithPositiveDefaultInstallmentsNumberAndNegativeMaxInstallmentsNumber() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item itemA = new Item("123", 1);

        itemA.setUnitPrice(new BigDecimal(2));
        itemA.setCurrencyId("USD");
        items.add(itemA);
        preference.setItems(items);

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setMaxAcceptedInstallments(-1);
        paymentPreference.setDefaultInstallments(3);
        preference.setPaymentPreference(paymentPreference);

        return preference;
    }

    private CheckoutPreference getPreferenceWithPositiveInstallmentsNumber() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item itemA = new Item("123", 1);

        itemA.setUnitPrice(new BigDecimal(2));
        itemA.setCurrencyId("USD");
        items.add(itemA);
        preference.setItems(items);

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setMaxAcceptedInstallments(1);
        paymentPreference.setDefaultInstallments(3);
        preference.setPaymentPreference(paymentPreference);

        return preference;
    }

    private CheckoutPreference getPreferenceWithNegativeInstallmentsNumbers() {
        CheckoutPreference preference = new CheckoutPreference();
        List<Item> items = new ArrayList<>();
        Item itemA = new Item("123", 1);

        itemA.setUnitPrice(new BigDecimal(2));
        itemA.setCurrencyId("USD");
        items.add(itemA);
        preference.setItems(items);

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setMaxAcceptedInstallments(-1);
        paymentPreference.setDefaultInstallments(-1);
        preference.setPaymentPreference(paymentPreference);

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

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setMaxAcceptedInstallments(1);
        paymentPreference.setDefaultInstallments(1);
        paymentPreference.setExcludedPaymentTypeIds(paymentTypes);
        preference.setPaymentPreference(paymentPreference);

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

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setMaxAcceptedInstallments(1);
        paymentPreference.setDefaultInstallments(1);
        paymentPreference.setExcludedPaymentTypeIds(paymentTypes);
        preference.setPaymentPreference(paymentPreference);

        return preference;
    }


    private CheckoutPreference getPreferenceWithOneItemValidButInstallmenstsDefaultNumberAndInstallmentsNumberNegative() {
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

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setMaxAcceptedInstallments(-1);
        paymentPreference.setDefaultInstallments(-3);
        preference.setPaymentPreference(paymentPreference);

        return preference;
    }

    private CheckoutPreference getPreferenceWithOneItemValidButPreferenceExpired() {
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

    private CheckoutPreference getPreferenceWithOneItemValidButPreferenceInactive() {
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