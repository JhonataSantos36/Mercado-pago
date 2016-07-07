package com.mercadopago;

import android.app.Activity;
import android.content.Intent;

import com.mercadopago.model.Card;
import com.mercadopago.test.BaseTest;

import java.util.List;

public class CustomerCardsActivityTests extends BaseTest<CustomerCardsActivity> {

    public CustomerCardsActivityTests() {
        super(CustomerCardsActivity.class);
    }

    //TODO Espresso
//    public void testGetCard() {
//
//        // Set activity
//        Activity activity = prepareActivity(StaticMock.getCards(getApplicationContext()));
//
//        sleepThread();
//
//        // Validate list
//        RecyclerView list = (RecyclerView) activity.findViewById(R.id.mpsdkCustomerCardsList);
//        if ((list != null) && (list.getAdapter() != null)) {
//            assertTrue(list.getAdapter().getItemCount() > 0);
//        } else {
//            fail("Get card test failed, no items found");
//        }
//
//        // Simulate click on first item
//        CustomerCardsAdapter adapter = (CustomerCardsAdapter) list.getAdapter();
//        View row = new MPTextView(getApplicationContext());
//        row.setTag(adapter.getItem(0));
//        adapter.getListener().onClick(row);
//
//        // Validate result
//        try {
//            ActivityResult activityResult = getActivityResult(activity);
//            PaymentMethodRow pmRow = (PaymentMethodRow) activityResult.getExtras().getSerializable("paymentMethodRow");
//            assertTrue(activityResult.getResultCode() == Activity.RESULT_OK);
//            assertTrue(pmRow.getCard().getPaymentMethod().getId().equals("master"));
//        } catch (Exception ex) {
//            fail("Get card test failed, cause: " + ex.getMessage());
//        }
//    }

//    public void testNullCards() {
//
//        Activity activity = prepareActivity(null);
//        assertFinishCalledWithResult(activity, Activity.RESULT_CANCELED);
//    }
//
//    public void testWrongCards() {
//
//        Activity activity = prepareActivityWrongCards();
//        assertFinishCalledWithResult(activity, Activity.RESULT_CANCELED);
//    }

    private Activity prepareActivity(List<Card> cards) {

        Intent intent = new Intent();
        putListExtra(intent, "cards", cards);
        setActivityIntent(intent);
        return getActivity();
    }

    private Activity prepareActivityWrongCards() {

        Intent intent = new Intent();
        intent.putExtra("cards", "1234");
        setActivityIntent(intent);
        return getActivity();
    }
}
