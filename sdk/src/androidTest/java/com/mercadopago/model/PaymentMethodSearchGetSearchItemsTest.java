package com.mercadopago.model;

import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by mreverter on 27/4/16.
 */

@RunWith(Parameterized.class)
public class PaymentMethodSearchGetSearchItemsTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"oxxo", "ticket", "oxxo"},
                {"bancomer", "bank_transfer", "bancomer_bank_transfer"},
                {"bancomer", "ticket", "bancomer_ticket"},
                {"banamex", "bank_transfer", "banamex_bank_transfer"},
                {"banamex", "ticket", "banamex_ticket"},
                {"serfin", "bank_transfer", "serfin_bank_transfer"},
                {"serfin", "ticket", "serfin_ticket"},
                {"invalid_pm", "", "null"}
        });
    }

    public PaymentMethodSearch paymentMethodSearch;

    public String mItemId;

    public String mPaymentMethodId;

    public String mPaymentTypeId;


    public PaymentMethodSearchGetSearchItemsTest(String paymentMethodId, String paymentTypeId, String itemId) {
        this.paymentMethodSearch = getPaymentMethodSearch();
        this.mItemId = itemId;
        this.mPaymentMethodId = paymentMethodId;
        this.mPaymentTypeId = paymentTypeId;
    }

    @Test
    public void testGetItemByPaymentMethod() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(mPaymentMethodId);
        paymentMethod.setPaymentTypeId(mPaymentTypeId);

        PaymentMethodSearchItem item = paymentMethodSearch.getSearchItemByPaymentMethod(paymentMethod);

        if (item != null) {
            Assert.assertEquals(mItemId, item.getId());
        } else {
            Assert.assertEquals(mItemId, "null");
        }
    }


    private PaymentMethodSearch getPaymentMethodSearch() {
        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        return JsonUtil.getInstance().fromJson(paymentMethodSearchJson, PaymentMethodSearch.class);

    }
}
