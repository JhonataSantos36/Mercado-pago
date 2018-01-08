package com.mercadopago.paymentresult.components;

import android.view.LayoutInflater;
import android.view.View;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.paymentresult.components.InstructionsSubtitle;

/**
 * Created by vaserber on 11/13/17.
 */

public class InstructionsSubtitleRenderer extends Renderer<InstructionsSubtitle> {


    @Override
    public View render() {
        final View instructionsView = LayoutInflater.from(context).inflate(R.layout.mpsdk_payment_result_instructions_subtitle, null, false);

        final MPTextView subtitleTextView = instructionsView.findViewById(R.id.msdpkInstructionsSubtitle);
        subtitleTextView.setText(component.props.subtitle);

        return instructionsView;
    }
}
