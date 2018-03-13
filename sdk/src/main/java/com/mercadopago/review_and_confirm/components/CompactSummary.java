package com.mercadopago.review_and_confirm.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.Component;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.review_and_confirm.models.SummaryModel;

import java.math.BigDecimal;

/**
 * Created by mromar on 2/28/18.
 */

public class CompactSummary extends Component<SummaryModel, Void> {

    static {
        RendererFactory.register(CompactSummary.class, CompactSummaryRenderer.class);
    }

    public CompactSummary(@NonNull final SummaryModel props) {
        super(props);
    }
}
