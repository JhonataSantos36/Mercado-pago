package com.mercadopago;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.widget.EditText;

import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodRow;
import com.mercadopago.test.ActivityResult;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mreverter on 21/12/15.
 */
public class VaultActivityWithGuessingNewCardTest extends BaseTest<VaultActivity> {

    public VaultActivityWithGuessingNewCardTest() {
        super(VaultActivity.class);
    }

    // Scenario
    // * With all correct parameters
    // * select other payment method
    // * guessing cards form shows up
    // * fill the cards form
    // * enter a security code
    // * generate a cards token

    public void testHappyPath(){

        final VaultActivity activity = prepareActivity(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY, StaticMock.DUMMY_MERCHANT_BASE_URL,
                StaticMock.DUMMY_MERCHANT_GET_CUSTOMER_URI, StaticMock.DUMMY_MERCHANT_ACCESS_TOKEN, new BigDecimal("100"), null);

        // Wait for get customer cards and installments api call
        sleepThread();  // customer cards
        sleepThread();  // installments

        // Mock up an ActivityResult with select other payment method
        Intent returnIntent = new Intent();

        PaymentMethodRow paymentMethodRow = new PaymentMethodRow(null, "label", 0);
        returnIntent.putExtra("paymentMethodRow", paymentMethodRow);
        Instrumentation.ActivityResult customerCardsMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        returnIntent = new Intent();
        returnIntent.putExtra("cardToken", StaticMock.getCardToken(getApplicationContext(), "_issuer_required"));
        returnIntent.putExtra("paymentMethod", StaticMock.getPaymentMethod(getApplicationContext(), "_issuer_required"));
        returnIntent.putExtra("issuer", StaticMock.getIssuer(getApplicationContext()));
        Instrumentation.ActivityResult guessingCardFormMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        returnIntent = new Intent();
        returnIntent.putExtra("payerCost", StaticMock.getPayerCosts(getApplicationContext()).get(2));
        Instrumentation.ActivityResult installmentsMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        Instrumentation.ActivityMonitor guessingCardFormMonitor = getInstrumentation().addMonitor(GuessingNewCardActivity.class.getName(), guessingCardFormMockedResult, true);
        Instrumentation.ActivityMonitor installmentsActivityMonitor = getInstrumentation().addMonitor(InstallmentsActivity.class.getName(), installmentsMockedResult, true);
        Instrumentation.ActivityMonitor customerCardsActivityMonitor = getInstrumentation().addMonitor(CustomerCardsActivity.class.getName(), customerCardsMockedResult , true);

        // Simulate customer cards selection
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                activity.onCustomerMethodsClick(null);
            }
        });

        getInstrumentation().waitForMonitorWithTimeout(customerCardsActivityMonitor, 5);
        getInstrumentation().waitForMonitorWithTimeout(guessingCardFormMonitor, 5);

        // Wait for installments api call
        sleepThread();

        // Simulate installment selection
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                activity.onInstallmentsClick(null);
            }
        });

        getInstrumentation().waitForMonitorWithTimeout(installmentsActivityMonitor, 5);

        // Complete security code
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                EditText securityCodeText = (EditText) activity.findViewById(R.id.securityCode);
                securityCodeText.setText(StaticMock.DUMMY_SECURITY_CODE);
            }
        });

        // Simulate button click
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                activity.submitForm(null);
            }
        });

        // Wait for create token api call
        sleepThread();

        // Validate activity result
        try {
            ActivityResult activityResult = getActivityResult(activity);
            assertTrue(!activityResult.getExtras().getString("token").equals(""));
            PaymentMethod paymentMethod = (PaymentMethod) activityResult.getExtras().getSerializable("paymentMethod");
            assertTrue(paymentMethod.getId().equals("master"));
            assertTrue(activityResult.getExtras().getString("installments").equals("6"));
            assertTrue(activityResult.getExtras().getString("issuerId").equals("692"));
        } catch (Exception ex) {
            fail("Regular start test failed, cause: " + ex.getMessage());
        }
    }

    private VaultActivity prepareActivity(String merchantPublicKey, String merchantBaseUrl,
                                          String merchantGetCustomerUri, String merchantAccessToken,
                                          BigDecimal amount, List<String> supportedPaymentTypes) {

        Intent intent = new Intent();
        if (merchantPublicKey != null) {
            intent.putExtra("merchantPublicKey", merchantPublicKey);
        }
        if (merchantBaseUrl != null) {
            intent.putExtra("merchantBaseUrl", merchantBaseUrl);
        }
        if (merchantGetCustomerUri != null) {
            intent.putExtra("merchantGetCustomerUri", merchantGetCustomerUri);
        }
        if (merchantAccessToken != null) {
            intent.putExtra("merchantAccessToken", merchantAccessToken);
        }
        if (amount != null) {
            intent.putExtra("amount", amount.toString());
        }

        intent.putExtra("cardGuessingEnabled", true);

        putListExtra(intent, "supportedPaymentTypes", supportedPaymentTypes);
        setActivityIntent(intent);
        return getActivity();
    }
}
