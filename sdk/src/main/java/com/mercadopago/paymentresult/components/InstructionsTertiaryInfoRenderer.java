package com.mercadopago.paymentresult.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.customviews.MPTextView;

import java.util.List;

/**
 * Created by vaserber on 11/14/17.
 */

public class InstructionsTertiaryInfoRenderer extends Renderer<InstructionsTertiaryInfo> {

    @Override
    public View render(final InstructionsTertiaryInfo component, final Context context) {
        final View secondaryInfoView = LayoutInflater.from(context).inflate(R.layout.mpsdk_payment_result_instructions_tertiary_info, null, false);
        final MPTextView secondaryInfoTextView = secondaryInfoView.findViewById(R.id.msdpkTertiaryInfo);

        setText(secondaryInfoTextView, getTertiaryInfoText(component.props.tertiaryInfo));
        return secondaryInfoView;
    }

    private String getTertiaryInfoText(List<String> tertiaryInfo) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < tertiaryInfo.size(); i++) {
            stringBuilder.append(tertiaryInfo.get(i));
            if ( i != tertiaryInfo.size() - 1) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
