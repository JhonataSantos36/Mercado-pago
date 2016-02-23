package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mercadopago.adapters.PaymentMethodsAdapter;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.test.ActivityResult;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodsActivityTest extends BaseTest<PaymentMethodsActivity> {

    public PaymentMethodsActivityTest() {
        super(PaymentMethodsActivity.class);
    }

    public void testGetPaymentMethod() {

        Activity activity = prepareActivity(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY, null, null, null);

        sleepThread();

        RecyclerView list = (RecyclerView) activity.findViewById(R.id.payment_methods_list);
        if ((list != null) && (list.getAdapter() != null)) {
            assertTrue(list.getAdapter().getItemCount() > 0);
        } else {
            fail("Get payment method test failed, no items found");
        }

        // Simulate click on first item
        PaymentMethodsAdapter paymentMethodsAdapter = (PaymentMethodsAdapter) list.getAdapter();
        View row = new TextView(getApplicationContext());
        row.setTag(paymentMethodsAdapter.getItem(0));
        paymentMethodsAdapter.getListener().onClick(row);

        getApplicationContext();
        try {
            ActivityResult activityResult = getActivityResult(activity);
            PaymentMethod paymentMethod = (PaymentMethod) activityResult.getExtras().getSerializable("paymentMethod");
            assertTrue(activityResult.getResultCode() == Activity.RESULT_OK);
            assertTrue(paymentMethod.getId().equals("visa"));
        } catch (Exception ex) {
            fail("Get payment method test failed, cause: " + ex.getMessage());
        }
    }

    public void testWrongMerchantPublicKey() {

        Activity activity = prepareActivity("wrong_public_key", null, null, null);

        sleepThread();

        try {
            ActivityResult activityResult = getActivityResult(activity);
            ApiException apiException = (ApiException) activityResult.getExtras().getSerializable("apiException");
            assertTrue(activityResult.getResultCode() == Activity.RESULT_CANCELED);
            assertTrue(apiException.getStatus() == 404);
        } catch (Exception ex) {
            fail("Wrong merchant public key test failed, cause: " + ex.getMessage());
        }
    }

    public void testSupportedPaymentTypesFilter() {

        List<String> supportedPaymentTypes = new ArrayList<String>(){{
            add("credit_card");
        }};
        Activity activity = prepareActivity(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY, supportedPaymentTypes, null, null);

        sleepThread();

        RecyclerView list = (RecyclerView) activity.findViewById(R.id.payment_methods_list);
        PaymentMethodsAdapter adapter = (PaymentMethodsAdapter) list.getAdapter();
        if (adapter != null) {
            assertTrue(adapter.getItemCount() > 0);
            boolean incorrectPaymentTypeFound = false;
            for (int i = 0; i < adapter.getItemCount(); i++) {
                if (!adapter.getItem(i).getPaymentTypeId().equals("credit_card")) {
                    incorrectPaymentTypeFound = true;
                    break;
                }
            }
            assertTrue(!incorrectPaymentTypeFound);
        } else {
            fail("Supported payment types filter test failed, no items found");
        }
    }

    public void testExcludedPaymentTypesFilter() {

        List<String> excludedPaymentTypes = new ArrayList<String>(){{
            add("credit_card");
        }};
        Activity activity = prepareActivity(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY, null, excludedPaymentTypes, null);

        sleepThread();

        RecyclerView list = (RecyclerView) activity.findViewById(R.id.payment_methods_list);
        PaymentMethodsAdapter adapter = (PaymentMethodsAdapter) list.getAdapter();
        if (adapter != null) {
            assertTrue(adapter.getItemCount() > 0);
            boolean incorrectPaymentTypeFound = false;
            for (int i = 0; i < adapter.getItemCount(); i++) {
                if (adapter.getItem(i).getPaymentTypeId().equals("credit_card")) {
                    incorrectPaymentTypeFound = true;
                    break;
                }
            }
            assertTrue(!incorrectPaymentTypeFound);
        } else {
            fail("Excluded payment types filter test failed, no items found");
        }
    }
    public void testExcludedPaymentMethodIdsFilter() {

        List<String> excludedPaymentMethodIds = new ArrayList<String>(){{
            add("visa");
        }};
        Activity activity = prepareActivity(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY, null, null, excludedPaymentMethodIds);

        sleepThread();

        RecyclerView list = (RecyclerView) activity.findViewById(R.id.payment_methods_list);
        PaymentMethodsAdapter adapter = (PaymentMethodsAdapter) list.getAdapter();
        if (adapter != null) {
            assertTrue(adapter.getItemCount() > 0);
            boolean incorrectPaymentMethodId = false;
            for (int i = 0; i < adapter.getItemCount(); i++) {
                if (adapter.getItem(i).getId().equals("visa")) {
                    incorrectPaymentMethodId = true;
                    break;
                }
            }
            assertTrue(!incorrectPaymentMethodId);
        } else {
            fail("Excluded payment types filter test failed, no items found");
        }
    }

    public void testBackPressed() {

        Activity activity = prepareActivity(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY, null, null, null);
        activity.onBackPressed();
        assertFinishCalledWithResult(activity, Activity.RESULT_CANCELED);
    }

<<<<<<< HEAD
    private PaymentMethodsActivity prepareActivity(String merchantPublicKey, List<String> excludedPaymentTypes, List<String> excludedPaymentMethodIds) {
=======
    private Activity prepareActivity(String merchantPublicKey, List<String> supportedPaymentTypes, List<String> excludedPaymentTypes, List<String> excludedPaymentMethodIds) {
>>>>>>> parent of df039a5... matched preference to vault settings, added payment methods search classes

        Intent intent = new Intent();
        if (merchantPublicKey != null) {
            intent.putExtra("merchantPublicKey", merchantPublicKey);
        }
        putListExtra(intent, "supportedPaymentTypes", supportedPaymentTypes);
        putListExtra(intent, "excludedPaymentTypes", excludedPaymentTypes);
        putListExtra(intent, "excludedPaymentMethodIds", excludedPaymentMethodIds);

        setActivityIntent(intent);
        return getActivity();
    }
}
