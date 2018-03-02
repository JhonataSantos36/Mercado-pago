package com.mercadopago.review_and_confirm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.MercadoPagoBaseActivity;
import com.mercadopago.R;
import com.mercadopago.components.Action;
import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.ComponentManager;
import com.mercadopago.review_and_confirm.components.actions.ChangePaymentMethodAction;
import com.mercadopago.review_and_confirm.components.ReviewAndConfirmContainer;
import com.mercadopago.review_and_confirm.models.PaymentModel;
import com.mercadopago.review_and_confirm.models.TermsAndConditionsModel;
import com.mercadopago.uicontrollers.FontCache;

public class ReviewAndConfirmActivity extends MercadoPagoBaseActivity implements ActionDispatcher {

    public static final int RESULT_CANCEL_PAYMENT = 4;
    public static final int RESULT_CHANGE_PAYMENT_METHOD = 3;

    private static final String EXTRA_TERMS_AND_CONDITIONS = "extra_terms_and_conditions";
    private static final String EXTRA_PAYMENT_MODEL = "payment_model";

    public static void start(final Context context,
                             final TermsAndConditionsModel termsAndConditions,
                             final PaymentModel paymentModel) {

        Intent intent = new Intent(context, ReviewAndConfirmActivity.class);
        intent.putExtra(EXTRA_TERMS_AND_CONDITIONS, termsAndConditions);
        intent.putExtra(EXTRA_PAYMENT_MODEL, paymentModel);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO tracking init screen?
        setContentView(R.layout.mpsdk_view_container_review_and_confirm);
        initToolbar();
        initContent();
    }

    private void initContent() {
        ViewGroup mainContent = findViewById(R.id.mpsdkReviewScrollView);
        ReviewAndConfirmContainer.Props props = getActivityParameters();
        final ComponentManager manager = new ComponentManager(this);
        final ReviewAndConfirmContainer container = new ReviewAndConfirmContainer(props);
        container.setDispatcher(this);
        manager.render(container, mainContent);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayShowTitleEnabled(false);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelPayment();
            }
        });
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getString(R.string.mpsdk_activity_checkout_title));
        if (FontCache.hasTypeface(FontCache.CUSTOM_REGULAR_FONT)) {
            collapsingToolbarLayout.setCollapsedTitleTypeface(FontCache.getTypeface(FontCache.CUSTOM_REGULAR_FONT));
            collapsingToolbarLayout.setExpandedTitleTypeface(FontCache.getTypeface(FontCache.CUSTOM_REGULAR_FONT));
        }
    }

    private void confirmPayment() {
        // TODO trackCheckoutConfirmed();
        setResult(RESULT_OK);
        finish();
    }

    private void cancelPayment() {
        //TODO tracking finish screen?
        setResult(RESULT_CANCEL_PAYMENT);
        super.onBackPressed();
    }

    private void changePaymentMethod() {
        //TODO tracking finish screen?
        setResult(RESULT_CHANGE_PAYMENT_METHOD);
        finish();
    }

    private ReviewAndConfirmContainer.Props getActivityParameters() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        TermsAndConditionsModel termsAndConditionsModel = null;
        PaymentModel paymentModel = null;
        if (extras != null) {
            termsAndConditionsModel = extras.getParcelable(EXTRA_TERMS_AND_CONDITIONS);
            paymentModel = extras.getParcelable(EXTRA_PAYMENT_MODEL);
        }
        return new ReviewAndConfirmContainer.Props(termsAndConditionsModel, paymentModel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void dispatch(Action action) {
        if (action instanceof ChangePaymentMethodAction) {
            changePaymentMethod();
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
