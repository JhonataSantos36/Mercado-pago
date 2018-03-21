package com.mercadopago.review_and_confirm.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.Component;
import com.mercadopago.components.RendererFactory;

public class FullSummary extends Component<SummaryComponent.SummaryProps, Void> {

    static {
        RendererFactory.register(FullSummary.class, FullSummaryRenderer.class);
    }

    FullSummary(@NonNull final SummaryComponent.SummaryProps props) {
        super(props);
    }
}
