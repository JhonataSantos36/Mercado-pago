package com.mercadopago.review_and_confirm.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.TermsAndConditionsActivity;
import com.mercadopago.components.Renderer;

public class TermsAndConditionRenderer extends Renderer<TermsAndCondition> {
    @Override
    protected View render(@NonNull final TermsAndCondition component,
                          @NonNull final Context context,
                          @Nullable final ViewGroup parent) {

        final View termsAndConditionsView = inflate(R.layout.mpsdk_view_terms_and_condition, parent);
        termsAndConditionsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                TermsAndConditionsActivity.start(termsAndConditionsView.getContext(), component.props.getSiteId());
            }
        });

        return termsAndConditionsView;
    }
}
