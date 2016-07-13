package com.mercadopago;

import com.mercadopago.test.BaseTest;

public class BankDealsActivityTest extends BaseTest<BankDealsActivity> {

    public BankDealsActivityTest() {

        super(BankDealsActivity.class);
    }

    public void testIfBankDealsReceivedShowThemInRecyclerView() {

//        //Create expected data for test
//        List<BankDeal> bankDeals = new ArrayList<>();
//        bankDeals.add(new BankDeal());
//
//        // Set activity
//        BankDealsActivity activity = prepareActivity(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY);
//
//        // Validate view
//        RecyclerView list = (RecyclerView) activity.findViewById(R.id.mpsdkBankDealsList);
//        if ((list != null) && (list.getAdapter() != null)) {
//            assertTrue(list.getAdapter().getItemCount() > 0);
//        } else {
//            fail("Bank Deals test failed, no items found");
//        }
    }

//    private BankDealsActivity prepareActivity(String merchantPublicKey) {
//
//        Intent intent = new Intent();
//        if (merchantPublicKey != null) {
//            intent.putExtra("merchantPublicKey", merchantPublicKey);
//        }
//        setActivityIntent(intent);
//        return getActivity();
//    }
}
