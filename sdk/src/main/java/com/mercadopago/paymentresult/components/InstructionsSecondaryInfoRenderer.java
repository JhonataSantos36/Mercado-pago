package com.mercadopago.paymentresult.components;

import android.view.LayoutInflater;
import android.view.View;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.paymentresult.components.InstructionsSecondaryInfo;

import java.util.List;

/**
 * Created by vaserber on 11/14/17.
 */

public class InstructionsSecondaryInfoRenderer extends Renderer<InstructionsSecondaryInfo> {

    @Override
    public View render() {
        final View secondaryInfoView = LayoutInflater.from(context).inflate(R.layout.mpsdk_payment_result_instructions_secondary_info, null, false);
        final MPTextView secondaryInfoTextView = secondaryInfoView.findViewById(R.id.msdpkSecondaryInfo);

        setText(secondaryInfoTextView, getSecondaryInfoText(component.props.secondaryInfo));
        return secondaryInfoView;
    }

    private String getSecondaryInfoText(List<String> secondaryInfo) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < secondaryInfo.size(); i++) {
            stringBuilder.append(secondaryInfo.get(i));
            if ( i != secondaryInfo.size() - 1) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
