package com.mercadopago;

import android.support.test.runner.AndroidJUnit4;
import android.text.Spanned;

import com.mercadopago.util.CurrenciesUtil;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

/**
 * Created by mreverter on 6/8/17.
 */

@RunWith(AndroidJUnit4.class)
public class CurrenciesUtilTest {

    @Test
    public void whenCurrencyDoesNotHaveDecimalDividerIndexThenDoNotFormatDecimals() {
        Spanned formattedAmount = CurrenciesUtil.formatNumber(new BigDecimal("3000.54"), CurrenciesUtil.CURRENCY_COLOMBIA, true, true);
        Assert.assertFalse(formattedAmount == null);
        Assert.assertFalse(formattedAmount.toString().contains("54"));
    }
}