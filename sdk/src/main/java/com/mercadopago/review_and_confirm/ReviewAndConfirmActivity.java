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
import com.mercadopago.review_and_confirm.components.ReviewAndConfirmContainer;
import com.mercadopago.review_and_confirm.models.TermsAndConditionsModel;
import com.mercadopago.uicontrollers.FontCache;

public class ReviewAndConfirmActivity extends MercadoPagoBaseActivity implements ActionDispatcher {

    private static final String EXTRA_TERMS_AND_CONDITIONS = "extra_terms_and_conditions";

    public static void start(final Context context, final TermsAndConditionsModel termsAndConditions) {
        Intent intent = new Intent(context, ReviewAndConfirmActivity.class);
        intent.putExtra(EXTRA_TERMS_AND_CONDITIONS, termsAndConditions);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                onBackPressed();
            }
        });
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getString(R.string.mpsdk_activity_checkout_title));
        if (FontCache.hasTypeface(FontCache.CUSTOM_REGULAR_FONT)) {
            collapsingToolbarLayout.setCollapsedTitleTypeface(FontCache.getTypeface(FontCache.CUSTOM_REGULAR_FONT));
            collapsingToolbarLayout.setExpandedTitleTypeface(FontCache.getTypeface(FontCache.CUSTOM_REGULAR_FONT));
        }
    }

    private ReviewAndConfirmContainer.Props getActivityParameters() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        TermsAndConditionsModel termsAndConditionsModel = null;
        if (extras != null) {
            termsAndConditionsModel = extras.getParcelable(EXTRA_TERMS_AND_CONDITIONS);
        }
        return new ReviewAndConfirmContainer.Props(termsAndConditionsModel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void dispatch(Action action) {
        throw new UnsupportedOperationException();
    }
}
