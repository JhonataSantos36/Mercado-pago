package com.mercadopago;

import android.content.Intent;

import com.mercadopago.model.PaymentPreference;
import com.mercadopago.test.BaseTest;
import com.mercadopago.util.JsonUtil;

public class PaymentMethodsActivityTest extends BaseTest<PaymentMethodsActivity> {

    public PaymentMethodsActivityTest() {
        super(PaymentMethodsActivity.class);
    }
//TODO espresso
//    public void testGetPaymentMethod() {
//
//        Activity activity = prepareActivity("6c0d81bc-99c1-4de8-9976-c8d1d62cd4f2", null);
//
//        RecyclerView list = (RecyclerView) activity.findViewById(R.id.mpsdkPaymentMethodsList);
//        if ((list != null) && (list.getAdapter() != null)) {
//            assertTrue(list.getAdapter().getItemCount() > 0);
//        } else {
//            fail("Get payment method test failed, no items found");
//        }
//
//        // Simulate click on first item
//        PaymentMethodsAdapter paymentMethodsAdapter = (PaymentMethodsAdapter) list.getAdapter();
//        View row = new MPTextView(getApplicationContext());
//        row.setTag(paymentMethodsAdapter.getItem(0));
//        paymentMethodsAdapter.getListener().onClick(row);
//
//        getApplicationContext();
//        try {
//            ActivityResult activityResult = getActivityResult(activity);
//            PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(activityResult.getExtras().getString("paymentMethod"), PaymentMethod.class);
//            assertTrue(activityResult.getResultCode() == Activity.RESULT_OK);
//            assertTrue(paymentMethod.getId().equals("bancomer"));
//        } catch (Exception ex) {
//            fail("Get payment method test failed, cause: " + ex.getMessage());
//        }
//    }
//
//    public void testWrongMerchantPublicKey() {
//
//        Activity activity = prepareActivity("wrong_public_key", null);
//
//        sleepThread();
//
//        try {
//            ActivityResult activityResult = getActivityResult(activity);
//            MPException mpException = JsonUtil.getInstance().fromJson(activityResult.getExtras().getString("mpException"), MPException.class);
//            assertTrue(activityResult.getResultCode() == Activity.RESULT_CANCELED);
//            assertTrue(mpException.getApiException().getStatus() == 404);
//        } catch (Exception ex) {
//            fail("Wrong merchant public key test failed, cause: " + ex.getMessage());
//        }
//    }
//
//    public void testExcludedPaymentTypesFilter() {
//
//        PaymentPreference paymentPreference = new PaymentPreference();
//        paymentPreference.setExcludedPaymentTypeIds(new ArrayList<String>() {{ add("ticket"); }});
//
//        Activity activity = prepareActivity(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY, paymentPreference);
//
//        sleepThread();
//
//        RecyclerView list = (RecyclerView) activity.findViewById(R.id.mpsdkPaymentMethodsList);
//        PaymentMethodsAdapter adapter = (PaymentMethodsAdapter) list.getAdapter();
//        if (adapter != null) {
//            assertTrue(adapter.getItemCount() > 0);
//            boolean incorrectPaymentTypeFound = false;
//            for (int i = 0; i < adapter.getItemCount(); i++) {
//                if (adapter.getItem(i).getPaymentTypeId().equals("ticket")) {
//                    incorrectPaymentTypeFound = true;
//                    break;
//                }
//            }
//            assertTrue(!incorrectPaymentTypeFound);
//        } else {
//            fail("Excluded payment types filter test failed, no items found");
//        }
//    }
//    public void testExcludedPaymentMethodIdsFilter() {
//
//        PaymentPreference paymentPreference = new PaymentPreference();
//        paymentPreference.setExcludedPaymentMethodIds(new ArrayList<String>(){{
//            add("visa");
//        }});
//        Activity activity = prepareActivity(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY, paymentPreference);
//
//        sleepThread();
//
//        RecyclerView list = (RecyclerView) activity.findViewById(R.id.mpsdkPaymentMethodsList);
//        PaymentMethodsAdapter adapter = (PaymentMethodsAdapter) list.getAdapter();
//        if (adapter != null) {
//            assertTrue(adapter.getItemCount() > 0);
//            boolean incorrectPaymentMethodIdFound = false;
//            for (int i = 0; i < adapter.getItemCount(); i++) {
//                if (adapter.getItem(i).getId().equals("visa")) {
//                    incorrectPaymentMethodIdFound = true;
//                    break;
//                }
//            }
//            assertTrue(!incorrectPaymentMethodIdFound);
//        } else {
//            fail("Excluded payment types filter test failed, no items found");
//        }
//    }
//
//    public void testBackPressed() {
//        Activity activity = prepareActivity(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY, null);
//        activity.onBackPressed();
//        assertFinishCalledWithResult(activity, Activity.RESULT_CANCELED);
//    }

    private PaymentMethodsActivity prepareActivity(String merchantPublicKey, PaymentPreference paymentPreference) {

        Intent intent = new Intent();
        if (merchantPublicKey != null) {
            intent.putExtra("merchantPublicKey", merchantPublicKey);
        }
        if (paymentPreference != null) {
            intent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));
        }

        setActivityIntent(intent);
        return getActivity();
    }
}
