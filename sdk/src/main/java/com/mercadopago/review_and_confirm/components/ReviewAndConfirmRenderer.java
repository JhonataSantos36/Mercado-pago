package com.mercadopago.review_and_confirm.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.review_and_confirm.components.actions.ChangePaymentMethodAction;
import com.mercadopago.review_and_confirm.components.payment_method.PaymentMethodComponent;
import com.mercadopago.review_and_confirm.models.PaymentModel;

public class ReviewAndConfirmRenderer extends Renderer<ReviewAndConfirmContainer> {

    @Override
    protected View render(@NonNull final ReviewAndConfirmContainer component,
                          @NonNull final Context context,
                          @Nullable final ViewGroup parent) {

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        //TODO add view summary - add view item - add view custom

        addPaymentMethod(component.props.paymentModel, component.getDispatcher(), linearLayout);

        if (component.props.termsAndConditionsModel.isActive()) {
            addReviewAndConfirm(component, linearLayout);
        }

        parent.addView(linearLayout);
        return parent;
    }

    private void addPaymentMethod(final PaymentModel paymentModel, final ActionDispatcher dispatcher, final ViewGroup parent) {
        PaymentMethodComponent paymentMethodComponent = new PaymentMethodComponent(paymentModel, new PaymentMethodComponent.Actions() {
            @Override
            public void onPaymentMethodChangeClicked() {
                dispatcher.dispatch(new ChangePaymentMethodAction());
            }
        });

        View paymentView = paymentMethodComponent.render(parent);
        parent.addView(paymentView);
    }

    private void addReviewAndConfirm(final @NonNull ReviewAndConfirmContainer component,
                                     final ViewGroup container) {
        Renderer termsAndConditions = RendererFactory.create(container.getContext(), component.createTermsAndConditions());
        termsAndConditions.render(container);
    }
}
