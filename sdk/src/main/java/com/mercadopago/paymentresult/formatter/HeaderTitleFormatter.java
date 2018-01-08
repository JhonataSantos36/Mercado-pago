package com.mercadopago.paymentresult.formatter;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;

import com.mercadopago.util.SuperscriptSpanAdjuster;

import java.math.BigDecimal;

/**
 * Created by vaserber on 11/9/17.
 */

public class HeaderTitleFormatter extends AmountFormat {

    public HeaderTitleFormatter(String currencyId, BigDecimal amount) {
        super(currencyId, amount);
    }

    public HeaderTitleFormatter(String currencyId, BigDecimal amount, String paymentMethodName) {
        super(currencyId, amount, paymentMethodName);
    }

    public CharSequence formatTextWithAmount(String text) {
        if (paymentMethodName == null) {
            return formatTextWithOnlyAmount(text);
        } else {
            return formatTextWithAmountAndName(text);
        }
    }

    private CharSequence formatTextWithOnlyAmount(String text) {
        String formattedAmount = formatNumber(false);
        String decimals = getDecimals(formattedAmount);
        String wholeNumber = getWholeNumber(formattedAmount);

        int fromDecimals = 0;
        int toDecimals = 0;

        //Hide decimals if necessary
        StringBuilder amountWithDecimals = new StringBuilder();
        amountWithDecimals.append(wholeNumber);

        if (decimals != null && !decimals.equals("00")) {
            int length = wholeNumber.length();
            fromDecimals = length;
            toDecimals = length + decimals.length();
            amountWithDecimals.append(decimals);
        }

        //Position symbol higher
        int symbolLength = getSymbolLength();
        SpannableStringBuilder spannableAmount = new SpannableStringBuilder(amountWithDecimals);
        spannableAmount.setSpan(new RelativeSizeSpan(0.5f), 0, symbolLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableAmount.setSpan(new SuperscriptSpanAdjuster(0.65f), 0, symbolLength, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        //Position decimals higher
        if (fromDecimals != 0 && toDecimals != 0) {
            spannableAmount.setSpan(new RelativeSizeSpan(0.5f), fromDecimals, toDecimals, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableAmount.setSpan(new SuperscriptSpanAdjuster(0.7f), fromDecimals, toDecimals, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return insertSpannedAmountInText(text, spannableAmount);
    }


    private CharSequence formatTextWithAmountAndName(String text) {
        String formattedAmount = formatNumber(true);
        String formattedText = String.format(text, paymentMethodName, formattedAmount);
        return formatTextWithOnlyAmount(formattedText);
    }
}
