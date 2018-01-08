package com.mercadopago.paymentresult.components;

import android.view.LayoutInflater;
import android.view.View;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.customviews.MPTextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by vaserber on 04/12/2017.
 */

public class ReceiptRenderer extends Renderer<Receipt> {

    @Override
    public View render() {
        final View receiptView = LayoutInflater.from(context).inflate(R.layout.mpsdk_payment_receipt_component, null, false);
        final MPTextView descriptionTextView = receiptView.findViewById(R.id.mpsdkReceiptDescription);
        final MPTextView dateTextView = receiptView.findViewById(R.id.mpsdkReceiptDate);

        renderDate(dateTextView);
        setText(descriptionTextView, component.getDescription());

        return receiptView;
    }

    private void renderDate(final MPTextView dateTextView) {
        final String divider = context.getResources().getString(R.string.mpsdk_date_divider);
        final Calendar calendar = Calendar.getInstance();
        final Locale locale = context.getResources().getConfiguration().locale;

        final String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        final String month = new SimpleDateFormat("MMMM", locale).format(calendar.getTime());
        final String year = String.valueOf(calendar.get(Calendar.YEAR));

        final StringBuilder builder = new StringBuilder()
                .append(day).append(" ").append(divider).append(" ").append(month).append(" ")
                .append(divider).append(" ").append(year);

        setText(dateTextView, builder.toString());
    }
}
