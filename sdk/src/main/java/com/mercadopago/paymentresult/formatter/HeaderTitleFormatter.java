package com.mercadopago.paymentresult.formatter;

import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;

import com.mercadopago.lite.util.CurrenciesUtil;

import java.math.BigDecimal;

public class HeaderTitleFormatter extends AmountFormat {

    private String paymentMethodName;

    public HeaderTitleFormatter(String currencyId, BigDecimal amount, @Nullable String paymentMethodName) {
        super(currencyId, amount);
        this.paymentMethodName = paymentMethodName;
    }

    public CharSequence formatTextWithAmount(String text) {
        if (paymentMethodName == null) {
            return formatTextWithOnlyAmount(text);
        } else {
            return formatTextWithAmountAndName(text);
        }
    }

    private CharSequence formatTextWithOnlyAmount(String text) {
        SpannableStringBuilder spannableAmount = CurrenciesUtil.getSpannableAmountWithSymbolWithoutZeroDecimals(currencyId, amount);
        return insertSpannedAmountInText(text, spannableAmount);
    }


    private CharSequence formatTextWithAmountAndName(String text) {
        String formattedAmount = CurrenciesUtil.getLocalizedAmountWithCurrencySymbol(amount, currencyId, true);
        String formattedText = String.format(text, paymentMethodName, formattedAmount);
        return formatTextWithOnlyAmount(formattedText);
    }
}
