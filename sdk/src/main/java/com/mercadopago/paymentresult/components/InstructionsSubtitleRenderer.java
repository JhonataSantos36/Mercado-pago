package com.mercadopago.paymentresult.components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.customviews.MPTextView;

/**
 * Created by vaserber on 11/13/17.
 */

public class InstructionsSubtitleRenderer extends Renderer<InstructionsSubtitle> {


    @Override
    public View render(final InstructionsSubtitle component, final Context context, final ViewGroup parent) {
        final View instructionsView = inflate(R.layout.mpsdk_payment_result_instructions_subtitle, parent);

        final MPTextView subtitleTextView = instructionsView.findViewById(R.id.msdpkInstructionsSubtitle);
        subtitleTextView.setText(component.props.subtitle);

        return instructionsView;
    }
}
