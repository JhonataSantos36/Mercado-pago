package com.mercadopago;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import com.mercadopago.model.BankDeal;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.MockedHttpClient;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.HttpClientUtil;

import java.util.ArrayList;
import java.util.List;

public class BankDealsActivityTest extends BaseTest<BankDealsActivity> {

    public BankDealsActivityTest() {

        super(BankDealsActivity.class);
    }

  /*  public void testIfBankDealsReceivedShowThemInRecyclerView() {

        //Create expected data for test
        List<BankDeal> bankDeals = new ArrayList<>();
        bankDeals.add(new BankDeal());

        //Set mocked client to return expected data
        MockedHttpClient client = new MockedHttpClient();
        client.addResponseToQueue(bankDeals, 200, "");
        HttpClientUtil.bindClient(client);

        // Set activity
        BankDealsActivity activity = prepareActivity(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY);

        // Validate view
        RecyclerView list = (RecyclerView) activity.findViewById(R.id.bank_deals_list);
        if ((list != null) && (list.getAdapter() != null)) {
            assertTrue(list.getAdapter().getItemCount() > 0);
        } else {
            fail("Bank Deals test failed, no items found");
        }
        HttpClientUtil.unbindClient();
    }

    private BankDealsActivity prepareActivity(String merchantPublicKey) {

        Intent intent = new Intent();
        if (merchantPublicKey != null) {
            intent.putExtra("merchantPublicKey", merchantPublicKey);
        }
        setActivityIntent(intent);
        return getActivity();
    }*/
}
