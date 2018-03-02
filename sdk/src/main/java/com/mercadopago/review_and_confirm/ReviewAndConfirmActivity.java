package com.mercadopago.review_and_confirm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.mercadopago.MercadoPagoBaseActivity;
import com.mercadopago.R;
import com.mercadopago.components.Action;
import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.ComponentManager;
import com.mercadopago.core.MercadoPagoComponents;
import com.mercadopago.review_and_confirm.components.ReviewAndConfirmContainer;
import com.mercadopago.review_and_confirm.components.actions.CancelPaymentAction;
import com.mercadopago.review_and_confirm.components.actions.ChangePaymentMethodAction;
import com.mercadopago.review_and_confirm.components.actions.ConfirmPaymentAction;
import com.mercadopago.review_and_confirm.models.PaymentModel;
import com.mercadopago.review_and_confirm.models.TermsAndConditionsModel;
import com.mercadopago.uicontrollers.FontCache;

public class ReviewAndConfirmActivity extends MercadoPagoBaseActivity implements ActionDispatcher {

    public static final int RESULT_CANCEL_PAYMENT = 4;
    public static final int RESULT_CHANGE_PAYMENT_METHOD = 3;

    private static final String EXTRA_TERMS_AND_CONDITIONS = "extra_terms_and_conditions";
    private static final String EXTRA_PAYMENT_MODEL = "payment_model";
    private View floatingConfirmLayout;

    public static void start(final Activity activity,
                             final TermsAndConditionsModel termsAndConditions,
                             final PaymentModel paymentModel) {
        //TODO result code should be changed by the outside.
        Intent intent = new Intent(activity, ReviewAndConfirmActivity.class);
        intent.putExtra(EXTRA_TERMS_AND_CONDITIONS, termsAndConditions);
        intent.putExtra(EXTRA_PAYMENT_MODEL, paymentModel);
        activity.startActivityForResult(intent, MercadoPagoComponents.Activities.REVIEW_AND_CONFIRM_REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO tracking init screen?
        setContentView(R.layout.mpsdk_view_container_review_and_confirm);
        initializeViews();
    }

    private void initializeViews() {
        initToolbar();
        NestedScrollView mainContent = findViewById(R.id.scroll_view);
        initContent(mainContent);
        initFloatingButton(mainContent);
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

    private void initFloatingButton(final NestedScrollView scrollView) {
        floatingConfirmLayout = findViewById(R.id.floating_confirm_layout);
        View confirmButton = findViewById(R.id.floating_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                confirmPayment();
            }
        });
        resolveFloatingButtonVisibility(scrollView);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                resolveFloatingButtonVisibility(scrollView);
            }
        });
    }

    private void resolveFloatingButtonVisibility(final NestedScrollView scrollView) {
        ViewGroup group = (ViewGroup) scrollView.getChildAt(0);
        int positionY = scrollView.getScrollY();
        int totalHeight = scrollView.getMeasuredHeight();
        //Last child AKA footer
        int lastChildHeight = group.getChildAt(group.getChildCount() - 1)
                .getMeasuredHeight();

        if ((totalHeight - lastChildHeight) < positionY) {
            setFloatingVisibility(true);
        } else {
            setFloatingVisibility(false);
        }
    }

    private void initContent(final ViewGroup mainContent) {
        ReviewAndConfirmContainer.Props props = getActivityParameters();
        final ComponentManager manager = new ComponentManager(this);
        final ReviewAndConfirmContainer container = new ReviewAndConfirmContainer(props);
        container.setDispatcher(this);
        manager.render(container, mainContent);
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

    private void setFloatingVisibility(boolean visible) {
        floatingConfirmLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
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

    @Override
    public void dispatch(Action action) {
        if (action instanceof ChangePaymentMethodAction) {
            changePaymentMethod();
        } else if (action instanceof CancelPaymentAction) {
            cancelPayment();
        } else if (action instanceof ConfirmPaymentAction) {
            confirmPayment();
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
