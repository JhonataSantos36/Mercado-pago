package com.mercadopago.model;

import com.mercadopago.lite.model.PaymentMethod;
import com.mercadopago.lite.model.PaymentMethodSearch;
import com.mercadopago.lite.model.PaymentMethodSearchItem;
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
public class PaymentMethodSearchGetPaymentMethodTest {

    private PaymentMethodSearch paymentMethodSearch;
    private String mItemId;
    private String mPaymentMethodId;
    private String mPaymentTypeId;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"oxxo", "oxxo", "ticket"},
                {"bancomer_bank_transfer", "bancomer", "bank_transfer"},
                {"bancomer.ticket", "bancomer", "ticket"},
                {"banamex.bank_transfer", "banamex", "bank_transfer"},
                {"banamex_ticket", "banamex", "ticket"},
                {"serfin_bank_transfer", "serfin", "bank_transfer"},
                {"serfin.ticket", "serfin", "ticket"},
                {"invalid_item", "invalid_item", ""}
        });
    }

    public PaymentMethodSearchGetPaymentMethodTest(String itemId, String paymentMethodId, String paymentTypeId) {
        this.paymentMethodSearch = getPaymentMethodSearch();
        this.mItemId = itemId;
        this.mPaymentMethodId = paymentMethodId;
        this.mPaymentTypeId = paymentTypeId;
    }

    @Test
    public void testGetPaymentMethodByItem() {
        PaymentMethodSearchItem item = new PaymentMethodSearchItem();
        item.setId(mItemId);

        PaymentMethod paymentMethod = paymentMethodSearch.getPaymentMethodBySearchItem(item);

        if (paymentMethod != null) {
            Assert.assertEquals(mPaymentMethodId, paymentMethod.getId());
            Assert.assertEquals(mPaymentTypeId, paymentMethod.getPaymentTypeId());
        } else {
            Assert.assertEquals(mPaymentMethodId, "invalid_item");
        }
    }

    private PaymentMethodSearch getPaymentMethodSearch() {
        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        return JsonUtil.getInstance().fromJson(paymentMethodSearchJson, PaymentMethodSearch.class);
    }
}
