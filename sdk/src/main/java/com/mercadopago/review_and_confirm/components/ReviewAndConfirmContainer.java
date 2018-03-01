package com.mercadopago.review_and_confirm.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.review_and_confirm.models.TermsAndConditionsModel;

public class ReviewAndConfirmContainer extends Component<ReviewAndConfirmContainer.Props, Void> {

    static {
        RendererFactory.register(ReviewAndConfirmContainer.class, ReviewAndConfirmRenderer.class);
    }

    public ReviewAndConfirmContainer(@NonNull Props props) {
        super(props);
    }

    public ReviewAndConfirmContainer(@NonNull Props props, @NonNull ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public Component createTermsAndConditions() {
        return new TermsAndCondition(props.termsAndConditionsModel, getDispatcher());
    }

    public static class Props {
        final TermsAndConditionsModel termsAndConditionsModel;

        public Props(final TermsAndConditionsModel termsAndConditionsModel) {
            this.termsAndConditionsModel = termsAndConditionsModel;
        }
    }


}
