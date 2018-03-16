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
import com.mercadopago.core.CheckoutStore;
import com.mercadopago.core.MercadoPagoComponents;
import com.mercadopago.review_and_confirm.components.ReviewAndConfirmContainer;
import com.mercadopago.review_and_confirm.components.actions.CancelPaymentAction;
import com.mercadopago.review_and_confirm.components.actions.ChangePaymentMethodAction;
import com.mercadopago.review_and_confirm.components.actions.ConfirmPaymentAction;
import com.mercadopago.review_and_confirm.models.ItemsModel;
import com.mercadopago.review_and_confirm.models.PaymentModel;
import com.mercadopago.review_and_confirm.models.ReviewAndConfirmPreferences;
import com.mercadopago.review_and_confirm.models.SummaryModel;
import com.mercadopago.review_and_confirm.models.TermsAndConditionsModel;
import com.mercadopago.tracker.Tracker;
import com.mercadopago.uicontrollers.FontCache;

public final class ReviewAndConfirmActivity extends MercadoPagoBaseActivity implements ActionDispatcher {

    public static final int RESULT_CANCEL_PAYMENT = 4;
    public static final int RESULT_CHANGE_PAYMENT_METHOD = 3;

    private static final String EXTRA_TERMS_AND_CONDITIONS = "extra_terms_and_conditions";
    private static final String EXTRA_PAYMENT_MODEL = "extra_payment_model";
    private static final String EXTRA_SUMMARY_MODEL = "extra_summary_model";
    private static final String EXTRA_PUBLIC_KEY = "extra_public_key";
    private static final String EXTRA_ITEMS = "extra_items";
    private View floatingConfirmLayout;

    public static void start(final Activity activity,
                             final String merchantPublicKey,
                             final TermsAndConditionsModel termsAndConditions,
                             final PaymentModel paymentModel,
                             final SummaryModel summaryModel,
                             final ItemsModel itemsModel) {
        //TODO result code should be changed by the outside.
        Intent intent = new Intent(activity, ReviewAndConfirmActivity.class);
        intent.putExtra(EXTRA_PUBLIC_KEY, merchantPublicKey);
        intent.putExtra(EXTRA_TERMS_AND_CONDITIONS, termsAndConditions);
        intent.putExtra(EXTRA_PAYMENT_MODEL, paymentModel);
        intent.putExtra(EXTRA_SUMMARY_MODEL, summaryModel);
        intent.putExtra(EXTRA_ITEMS, itemsModel);
        activity.startActivityForResult(intent, MercadoPagoComponents.Activities.REVIEW_AND_CONFIRM_REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        final View confirmButton = findViewById(R.id.floating_confirm);
        floatingConfirmLayout = findViewById(R.id.floating_confirm_layout);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                confirmPayment();
            }
        });

        ViewTreeObserver viewTreeObserver = scrollView.getViewTreeObserver();

        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                resolveFloatingButtonVisibility(scrollView);
            }
        });

        viewTreeObserver.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                resolveFloatingButtonVisibility(scrollView);
            }
        });
    }

    private void resolveFloatingButtonVisibility(final NestedScrollView scrollView) {
        ViewGroup content = (ViewGroup) scrollView.getChildAt(0);
        int containerHeight = content.getHeight();
        // get footer/last child
        View footer = content.getChildAt(content.getChildCount() - 1);
        // This footer has two buttons, to avoid mesure cancel button we devide by 2 the footer height
        float finalSize = containerHeight - scrollView.getHeight() - (footer.getHeight() / 2);
        setFloatingVisibility(scrollView.getScrollY() < finalSize);
    }

    private void initContent(final ViewGroup mainContent) {
        ReviewAndConfirmContainer.Props props = getActivityParameters();
        final ComponentManager manager = new ComponentManager(this);
        final ReviewAndConfirmContainer container = new ReviewAndConfirmContainer(props, this, new SummaryProviderImpl(this));
        container.setDispatcher(this);
        manager.render(container, mainContent);
    }

    private ReviewAndConfirmContainer.Props getActivityParameters() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        TermsAndConditionsModel termsAndConditionsModel = null;
        PaymentModel paymentModel = null;
        SummaryModel summaryModel = null;

        ItemsModel itemsModel = null;
        if (extras != null) {
            termsAndConditionsModel = extras.getParcelable(EXTRA_TERMS_AND_CONDITIONS);
            paymentModel = extras.getParcelable(EXTRA_PAYMENT_MODEL);
            summaryModel = extras.getParcelable(EXTRA_SUMMARY_MODEL);
            itemsModel = extras.getParcelable(EXTRA_ITEMS);
            Tracker.trackReviewAndConfirmScreen(this, getIntent().getStringExtra(EXTRA_PUBLIC_KEY), paymentModel);
        }

        ReviewAndConfirmPreferences reviewAndConfirmPreferences = CheckoutStore.getInstance().getReviewAndConfirmPreferences();
        return new ReviewAndConfirmContainer.Props(termsAndConditionsModel, paymentModel, summaryModel, reviewAndConfirmPreferences, itemsModel);
    }

    private void setFloatingVisibility(boolean visible) {
        floatingConfirmLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void confirmPayment() {
        Tracker.trackCheckoutConfirm(this, getIntent().getStringExtra(EXTRA_PUBLIC_KEY));
        setResult(RESULT_OK);
        finish();
    }

    private void cancelPayment() {
        setResult(RESULT_CANCEL_PAYMENT);
        super.onBackPressed();
    }

    private void changePaymentMethod() {
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
