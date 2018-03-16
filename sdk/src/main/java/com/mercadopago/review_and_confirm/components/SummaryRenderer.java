package com.mercadopago.review_and_confirm.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.core.CheckoutStore;
import com.mercadopago.review_and_confirm.models.ReviewAndConfirmPreferences;

/**
 * Created by mromar on 2/28/18.
 */

public class SummaryRenderer extends Renderer<SummaryComponent> {

    @Override
    public View render(@NonNull final SummaryComponent component, @NonNull final Context context, final ViewGroup parent) {
        final View summaryView = inflate(R.layout.mpsdk_summary_component, parent);
        final LinearLayout summaryContainer = summaryView.findViewById(R.id.mpsdkSummaryContainer);

        if ((component.props.summaryModel.hasMultipleInstallments())
                || component.props.summaryModel.hasCoupon()
                || component.props.reviewAndConfirmPreferences.hasExtrasAmount()) {
            final Renderer fullSummaryRenderer = RendererFactory.create(context, component.getFullSummary());
            final View fullSummaryView = fullSummaryRenderer.render();
            summaryContainer.addView(fullSummaryView);
        } else {
            final Renderer compactSummaryRenderer = RendererFactory.create(context, component.getCompactSummary());
            final View compactSummaryView = compactSummaryRenderer.render();
            summaryContainer.addView(compactSummaryView);
        }

        return summaryView;
    }
}
