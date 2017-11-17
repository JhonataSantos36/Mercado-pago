package com.mercadopago.preferences;

import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mreverter on 29/12/15.
 */
public class PaymentPreferenceTest {

    public void testIfDefaultInstallmentsSetAndExistsInListReturnIt() {
        List<PayerCost> payerCosts = getPayerCosts();

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultInstallments(3);
        PayerCost defaultPayerCost = paymentPreference.getDefaultInstallments(payerCosts);

        Assert.assertTrue(defaultPayerCost.getInstallments() == 3);
    }

    public void testIfDefaultInstallmentNotSetReturnNull() {
        List<PayerCost> payerCosts = getPayerCosts();

        PaymentPreference paymentPreference = new PaymentPreference();

        Assert.assertTrue(paymentPreference.getDefaultInstallments(payerCosts) == null);

    }

    public void testIfDefaultInstallmentSetButDoesNotExistReturnNull() {

        List<PayerCost> payerCosts = getPayerCosts();

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultInstallments(4);
        PayerCost defaultPayerCost = paymentPreference.getDefaultInstallments(payerCosts);

        Assert.assertTrue(defaultPayerCost == null);
    }

    public void testIfMaxInstallmentsSetRemoveInstallmentsAboveMax() {
        List<PayerCost> originalPayerCosts = getPayerCosts();

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setMaxAcceptedInstallments(6);

        List<PayerCost> filteredPayerCosts = paymentPreference.getInstallmentsBelowMax(originalPayerCosts);

        Assert.assertTrue(getMaxPayerCost(originalPayerCosts) > 6);
        Assert.assertTrue(getMaxPayerCost(filteredPayerCosts) <= 6);
    }

    public void testIfMaxInstallmentsNotSetReturnOriginalPayerCostsList() {
        List<PayerCost> originalPayerCosts = getPayerCosts();

        PaymentPreference paymentPreference = new PaymentPreference();

        List<PayerCost> filteredPayerCosts = paymentPreference.getInstallmentsBelowMax(originalPayerCosts);

        Assert.assertTrue(filteredPayerCosts.equals(originalPayerCosts));
    }


    public void testWhenPaymentMethodTypeIsExcludedReturnNotSupported() {

        List<String> excludedPaymentMethods = new ArrayList<String>() {{
            add("debit_card");
        }};

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentTypeId("debit_card");
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentTypeIds(excludedPaymentMethods);

        Assert.assertTrue(!paymentPreference.isPaymentMethodSupported(paymentMethod));
    }

    public void testIfNeitherExcludedOrSupportedSetSupportAnyType() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentTypeId("credit_card");
        PaymentPreference paymentPreference = new PaymentPreference();

        Assert.assertTrue(paymentPreference.isPaymentMethodSupported(paymentMethod));
    }

    public void testIfSupportedAndExcludedPMSetExcludeEvenWhenSupported() {
        List<String> excludedPaymentMethods = new ArrayList<String>() {{
            add("debit_card");
        }};

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentTypeId("debit_card");
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentTypeIds(excludedPaymentMethods);

        Assert.assertTrue(!paymentPreference.isPaymentMethodSupported(paymentMethod));
    }

    public void testIfPaymentMethodIdExcludedReturnSupportedFalse() {
        List<String> excludedPaymentMethodIds = new ArrayList<String>() {{
            add("visa");
        }};

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("visa");
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentMethodIds(excludedPaymentMethodIds);

        Assert.assertTrue(!paymentPreference.isPaymentMethodSupported(paymentMethod));
    }

    public void testIfPaymentMethodIdExcludedReturnSupportedTrue() {
        List<String> excludedPaymentMethodIds = new ArrayList<String>() {{
            add("visa");
        }};

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("master");
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentMethodIds(excludedPaymentMethodIds);

        Assert.assertTrue(paymentPreference.isPaymentMethodSupported(paymentMethod));
    }

    public void testIfPaymentMethodIdNotExcludedButPaymentTypeExcludedReturnSupportedFalse() {
        List<String> excludedPaymentMethodIds = new ArrayList<String>() {{
            add("master");
        }};
        List<String> excludedPaymentMethodTypes = new ArrayList<String>() {{
            add("credit_card");
        }};

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("visa");
        paymentMethod.setPaymentTypeId("credit_card");
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentTypeIds(excludedPaymentMethodTypes);
        paymentPreference.setExcludedPaymentMethodIds(excludedPaymentMethodIds);

        Assert.assertTrue(!paymentPreference.isPaymentMethodSupported(paymentMethod));
    }

    public void testIfExcludedPaymentMethodIdNotSetReturnSupportedTrue() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("master");
        PaymentPreference paymentPreference = new PaymentPreference();

        Assert.assertTrue(paymentPreference.isPaymentMethodSupported(paymentMethod));
    }

    public void testFilterListByExcludedPaymentMethodTypes() {

        List<String> excludedPaymentMethodTypes = new ArrayList<String>() {{
            add("prepaid_card");
            add("credit_card");
        }};

        List<PaymentMethod> originalPaymentMethods = getPaymentMethods();
        PaymentPreference paymentPreference = new PaymentPreference();

        paymentPreference.setExcludedPaymentTypeIds(excludedPaymentMethodTypes);

        List<PaymentMethod> filteredPaymentMethods = paymentPreference.getSupportedPaymentMethods(originalPaymentMethods);

        Assert.assertTrue(filteredPaymentMethods.size() != 0);
        for (PaymentMethod pm : filteredPaymentMethods) {
            Assert.assertTrue(!(pm.getPaymentTypeId().equals("prepaid_card") || pm.getPaymentTypeId().equals("credit_card")));
        }
    }

    public void testFilterListByExcludedPaymentMethodIds() {

        List<String> excludedPaymentMethodIds = new ArrayList<String>() {{
            add("visa");
            add("debvisa");
        }};

        List<PaymentMethod> originalPaymentMethods = getPaymentMethods();
        PaymentPreference paymentPreference = new PaymentPreference();

        paymentPreference.setExcludedPaymentMethodIds(excludedPaymentMethodIds);

        List<PaymentMethod> filteredPaymentMethods = paymentPreference.getSupportedPaymentMethods(originalPaymentMethods);

        Assert.assertTrue(filteredPaymentMethods.size() != 0);
        for (PaymentMethod pm : filteredPaymentMethods) {
            Assert.assertTrue(!(pm.getId().equals("visa") || pm.getId().equals("debvisa")));
        }
    }

    public void testIfDefaultPaymentMethodExistsReturnIt() {

        List<PaymentMethod> paymentMethods = getPaymentMethods();
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultPaymentMethodId("visa");

        PaymentMethod defaultPaymentMethod = paymentPreference.getDefaultPaymentMethod(paymentMethods);

        Assert.assertTrue(defaultPaymentMethod.getId().equals("visa"));
    }

    public void testIfDefaultPaymentMethodDoesNotExistsReturnNull() {

        List<PaymentMethod> paymentMethods = getPaymentMethods();
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultPaymentMethodId("i don't exist");

        PaymentMethod defaultPaymentMethod = paymentPreference.getDefaultPaymentMethod(paymentMethods);

        Assert.assertTrue(defaultPaymentMethod == null);
    }

    public void testIfDefaultPaymentMethodIdNotSetReturnNull() {

        List<PaymentMethod> paymentMethods = getPaymentMethods();
        PaymentPreference paymentPreference = new PaymentPreference();

        PaymentMethod defaultPaymentMethod = paymentPreference.getDefaultPaymentMethod(paymentMethods);

        Assert.assertTrue(defaultPaymentMethod == null);
    }

    private int getMaxPayerCost(List<PayerCost> payerCosts) {
        int max = 0;
        for (PayerCost pc : payerCosts) {
            if (pc.getInstallments() > max)
                max = pc.getInstallments();
        }
        return max;
    }

    private List<PayerCost> getPayerCosts() {

        List<PayerCost> payerCosts = new ArrayList<>();
        PayerCost payerCost1 = new PayerCost();
        payerCost1.setInstallments(3);
        payerCosts.add(payerCost1);

        PayerCost payerCost2 = new PayerCost();
        payerCost2.setInstallments(6);
        payerCosts.add(payerCost2);

        PayerCost payerCost3 = new PayerCost();
        payerCost3.setInstallments(9);
        payerCosts.add(payerCost3);

        PayerCost payerCost4 = new PayerCost();
        payerCost4.setInstallments(12);
        payerCosts.add(payerCost4);

        return payerCosts;
    }

    private List<PaymentMethod> getPaymentMethods() {
        List<PaymentMethod> paymentMethods = new ArrayList<>();

        PaymentMethod paymentMethod1 = new PaymentMethod();
        paymentMethod1.setPaymentTypeId("credit_card");
        paymentMethod1.setId("visa");
        paymentMethods.add(paymentMethod1);

        PaymentMethod paymentMethod2 = new PaymentMethod();
        paymentMethod2.setPaymentTypeId("credit_card");
        paymentMethod2.setId("master");
        paymentMethods.add(paymentMethod2);

        PaymentMethod paymentMethod3 = new PaymentMethod();
        paymentMethod3.setPaymentTypeId("debit_card");
        paymentMethod3.setId("debvisa");
        paymentMethods.add(paymentMethod3);

        PaymentMethod paymentMethod4 = new PaymentMethod();
        paymentMethod4.setPaymentTypeId("prepaid_card");
        paymentMethod4.setId("sube");
        paymentMethods.add(paymentMethod4);

        PaymentMethod paymentMethod5 = new PaymentMethod();
        paymentMethod5.setPaymentTypeId("debit_card");
        paymentMethod5.setId("debmaster");
        paymentMethods.add(paymentMethod5);

        return paymentMethods;
    }
}
