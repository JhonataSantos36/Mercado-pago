package com.mercadopago.review_and_confirm.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.Component;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.review_and_confirm.SummaryProvider;
import com.mercadopago.review_and_confirm.models.ReviewAndConfirmPreferences;
import com.mercadopago.review_and_confirm.models.SummaryModel;

/**
 * Created by mromar on 2/28/18.
 */

public class SummaryComponent extends Component<SummaryComponent.SummaryProps, Void> {

    static class SummaryProps {
        final SummaryModel summaryModel;
        final ReviewAndConfirmPreferences reviewAndConfirmPreferences;

        public SummaryProps(final SummaryModel summaryModel, final ReviewAndConfirmPreferences reviewAndConfirmPreferences) {
            this.summaryModel = summaryModel;
            this.reviewAndConfirmPreferences = reviewAndConfirmPreferences;
        }

        public static SummaryProps createFrom(SummaryModel summaryModel, ReviewAndConfirmPreferences reviewAndConfirmPreferences){
            return new SummaryProps(summaryModel, reviewAndConfirmPreferences);
        }
    }

    private SummaryProvider provider;

    public static final String CFT = "CFT ";

    static {
        RendererFactory.register(SummaryComponent.class, SummaryRenderer.class);
    }

    public SummaryComponent(@NonNull final SummaryComponent.SummaryProps props,
                            @NonNull final SummaryProvider provider) {
        super(props);
        this.provider = provider;
    }

    public FullSummary getFullSummary() {
        return new FullSummary(props, provider);
    }

    public CompactSummary getCompactSummary() {
        return new CompactSummary(props.summaryModel);
    }
}
