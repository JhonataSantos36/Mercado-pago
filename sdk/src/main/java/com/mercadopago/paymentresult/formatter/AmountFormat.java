package com.mercadopago.paymentresult.formatter;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.mercadopago.lite.util.CurrenciesUtil;

import java.math.BigDecimal;


public class AmountFormat {

    protected String currencyId;
    protected BigDecimal amount;

    public AmountFormat(final String currencyId,
                        final BigDecimal amount) {
        this.currencyId = currencyId;
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    protected CharSequence insertSpannedAmountInText(final String title, final SpannableStringBuilder spannedAmount) {
        String formattedAmount = CurrenciesUtil.getLocalizedAmountWithCurrencySymbol(amount, currencyId, true);
        CharSequence result = title;

        if (title.contains(formattedAmount)) {
            String formattedText = title.replace(formattedAmount, "*");
            int index = formattedText.indexOf("*");
            String firstSubstring = formattedText.substring(0, index);
            String secondSubstring = formattedText.substring(index + 1, formattedText.length());

            CharSequence auxSubstring1 = TextUtils.concat(firstSubstring, "\n");
            CharSequence auxSubstring2 = TextUtils.concat(auxSubstring1, spannedAmount);
            result = TextUtils.concat(auxSubstring2, secondSubstring);
        }

        return result;
    }

}
