package com.mercadopago.review_and_confirm.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mercadopago.R;
import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.review_and_confirm.components.actions.CancelPaymentAction;
import com.mercadopago.review_and_confirm.components.actions.ChangePaymentMethodAction;
import com.mercadopago.review_and_confirm.components.actions.ConfirmPaymentAction;
import com.mercadopago.review_and_confirm.components.payment_method.PaymentMethodComponent;
import com.mercadopago.review_and_confirm.models.PaymentModel;

public class ReviewAndConfirmRenderer extends Renderer<ReviewAndConfirmContainer> {

    @Override
    protected View render(@NonNull final ReviewAndConfirmContainer component,
                          @NonNull final Context context,
                          @Nullable final ViewGroup parent) {

        LinearLayout linearLayout = createMainLayout(context);

        //TODO add view summary - add view custom

        if (component.hasItemsEnabled()) {
            addReviewItems(component, linearLayout);
        }

        if (component.props.preferences.hasCustomTopView()) {
            Renderer renderer = RendererFactory.create(context, component.props.preferences.getTopComponent());
            linearLayout.addView(renderer.render(parent));
        }

        addPaymentMethod(component.props.paymentModel, component.getDispatcher(), linearLayout);

        if (component.props.preferences.hasCustomBottomView()) {
            Renderer renderer = RendererFactory.create(context, component.props.preferences.getBottomComponent());
            linearLayout.addView(renderer.render(parent));
        }

        if (component.props.termsAndConditionsModel.isActive()) {
            addTermsAndConditions(component, linearLayout);
        }


        addFooter(component, linearLayout);

        parent.addView(linearLayout);

        return parent;
    }

    @NonNull
    private LinearLayout createMainLayout(final @NonNull Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return linearLayout;
    }

    private void addFooter(final @NonNull ReviewAndConfirmContainer component, final LinearLayout linearLayout) {
        View footer = inflate(R.layout.mpsdk_view_confirm_cancel_buttons, linearLayout);
        View confirm = footer.findViewById(R.id.mpsdkFloatingConfirmText);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                component.getDispatcher().dispatch(new ConfirmPaymentAction());
            }
        });

        View cancel = footer.findViewById(R.id.mpsdkCancelText);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                component.getDispatcher().dispatch(new CancelPaymentAction());
            }
        });
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

    private void addReviewItems(final @NonNull ReviewAndConfirmContainer component, final ViewGroup parent) {
        Renderer renderer = RendererFactory.create(parent.getContext(),
                new ReviewItems(
                        new ReviewItems.Props(
                                component.props.itemsModel,
                                component.props.preferences.getCollectorIcon(),
                                component.props.preferences.getQuantityLabel(),
                                component.props.preferences.getUnitPriceLabel())));
        renderer.render(parent);
    }

    private void addTermsAndConditions(final @NonNull ReviewAndConfirmContainer component,
                                       final ViewGroup container) {
        Renderer termsAndConditions = RendererFactory.create(container.getContext(),
                new TermsAndCondition(component.props.termsAndConditionsModel, component.getDispatcher()));
        termsAndConditions.render(container);
    }

}
