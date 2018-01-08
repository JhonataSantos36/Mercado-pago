package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.paymentresult.components.InstructionsInfo;

import java.util.List;

/**
 * Created by vaserber on 11/13/17.
 */

public class InstructionsInfoRenderer extends Renderer<InstructionsInfo> {

    @Override
    public View render() {
        final View infoView = LayoutInflater.from(context).inflate(R.layout.mpsdk_payment_result_instructions_info, null, false);
        final MPTextView infoTitle = infoView.findViewById(R.id.mpsdkInstructionsInfoTitle);
        final MPTextView infoContent = infoView.findViewById(R.id.mpsdkInstructionsInfoContent);
        final View bottomDivider = infoView.findViewById(R.id.mpsdkInstructionsInfoDividerBottom);

        setText(infoTitle, component.props.infoTitle);

        renderInfoContent(infoContent);

        renderDivider(bottomDivider);

        return infoView;
    }

    private void renderDivider(@NonNull final View bottomDividerView) {
        if (component.props.bottomDivider) {
            bottomDividerView.setVisibility(View.VISIBLE);
        } else {
            bottomDividerView.setVisibility(View.GONE);
        }
    }

    private void renderInfoContent(@NonNull final MPTextView infoContentTextView) {
        if (component.props.infoContent == null || component.props.infoContent.isEmpty()) {
            infoContentTextView.setVisibility(View.GONE);
        } else {
            infoContentTextView.setText(getInfoText(component.props.infoContent));
            infoContentTextView.setVisibility(View.VISIBLE);
        }
    }

    private String getInfoText(List<String> info) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < info.size(); i++) {
            stringBuilder.append(info.get(i));
            if ( i != info.size() - 1) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
