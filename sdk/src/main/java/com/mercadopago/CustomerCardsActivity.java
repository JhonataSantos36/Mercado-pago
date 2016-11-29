package com.mercadopago;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.core.MercadoPagoUI;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.Customer;
import com.mercadopago.uicontrollers.savedcards.SavedCardsListView;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;

import java.lang.reflect.Type;
import java.util.List;

public class CustomerCardsActivity extends MercadoPagoActivity {


    protected Toolbar mToolbar;
    protected TextView mTitle;
    protected ViewGroup mSavedCardsContainer;

    private List<Card> mCards;
    private String mCustomTitle;
    private String mSelectionConfirmPromptText;
    private String mCustomFooterMessage;
    private Integer mSelectionImageDrawableResId;
    private String mMerchantBaseUrl;
    private String mMerchantGetCustomerUri;
    private String mMerchantAccessToken;

    @Override
    protected void getActivityParameters() {
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Card>>() {
            }.getType();
            mCards = gson.fromJson(this.getIntent().getStringExtra("cards"), listType);
        } catch (Exception ex) {
            mCards = null;
        }
        mCustomTitle = this.getIntent().getStringExtra("title");
        mSelectionConfirmPromptText = this.getIntent().getStringExtra("selectionConfirmPromptText");
        mSelectionImageDrawableResId = this.getIntent().getIntExtra("selectionImageResId", 0);
        mCustomFooterMessage = this.getIntent().getStringExtra("footerText");
        mMerchantBaseUrl = this.getIntent().getStringExtra("merchantBaseUrl");
        mMerchantGetCustomerUri = this.getIntent().getStringExtra("merchantGetCustomerUri");
        mMerchantAccessToken = this.getIntent().getStringExtra("merchantAccessToken");

        if (mDecorationPreference != null) {
            super.decorate(mToolbar);
            super.decorateFont(mTitle);
        }
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
        if (this.mCards == null && (TextUtils.isEmpty(mMerchantBaseUrl)
                || TextUtils.isEmpty(mMerchantGetCustomerUri)
                || TextUtils.isEmpty(mMerchantAccessToken))) {
            throw new IllegalStateException("cards or merchant server info required");
        }
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.mpsdk_activity_customer_cards);
    }

    @Override
    protected void initializeControls() {
        initializeToolbar();
        mSavedCardsContainer = (ViewGroup) findViewById(R.id.mpsdkRegularLayout);
    }

    @Override
    protected void onValidStart() {
        if(mCards == null) {
            getCustomerAsync();
        } else {
            fillData();
        }

    }

    private void getCustomerAsync() {
        showProgress();
        MerchantServer.getCustomer(this, mMerchantBaseUrl, mMerchantGetCustomerUri, mMerchantAccessToken, new Callback<Customer>() {
            @Override
            public void success(Customer customer) {
                mCards = customer.getCards();
                hideProgress();
                fillData();
            }

            @Override
            public void failure(ApiException apiException) {
                ApiUtil.showApiExceptionError(getActivity(), apiException);
                hideProgress();
                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        getCustomerAsync();
                    }
                });
            }
        });
    }

    @Override
    protected void onInvalidStart(String message) {
        ErrorUtil.startErrorActivity(this, message, false);
    }

    private void initializeToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.mpsdkToolbar);
        mTitle = (TextView) findViewById(R.id.mpsdkToolbarTitle);
        if (!TextUtils.isEmpty(mCustomTitle)) {
            mTitle.setText(mCustomTitle);
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (isCustomColorSet()) {
            mToolbar.setBackgroundColor(getCustomBaseColor());
        }
        if (isDarkFontEnabled()) {
            mTitle.setTextColor(getDarkFontColor());
            Drawable upArrow = mToolbar.getNavigationIcon();
            upArrow.setColorFilter(getDarkFontColor(), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
    }

    private void fillData() {
        SavedCardsListView savedCardsView = new MercadoPagoUI.Views.SavedCardsListViewBuilder()
                .setContext(this)
                .setCards(mCards)
                .setFooter(mCustomFooterMessage)
                .setOnSelectedCallback(getOnSelectedCallback())
                .setSelectionImage(mSelectionImageDrawableResId)
                .build();

        savedCardsView.drawInParent(mSavedCardsContainer);
    }

    private OnSelectedCallback<Card> getOnSelectedCallback() {
        return new OnSelectedCallback<Card>() {
            @Override
            public void onSelected(Card card) {
                if (card != null) {
                    resolveCardResponse(card);
                }
            }
        };
    }

    private void resolveCardResponse(final Card card) {

        if (isConfirmPromptEnabled()) {

            String dialogTitle = new StringBuilder().append(getString(R.string.mpsdk_last_digits_label)).append(" ").append(card.getLastFourDigits()).toString();

            int resourceId = MercadoPagoUtil.getPaymentMethodIcon(this, card.getPaymentMethod().getId());
            if (resourceId == 0) {
                resourceId = android.R.drawable.ic_dialog_alert;
            }

            new AlertDialog.Builder(this)
                    .setIcon(resourceId)
                    .setTitle(dialogTitle)
                    .setMessage(mSelectionConfirmPromptText)
                    .setPositiveButton(getString(R.string.mpsdk_confirm_prompt_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishWithCardResult(card);
                        }

                    })
                    .setNegativeButton(getString(R.string.mpsdk_confirm_prompt_no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    })
                    .show();
        } else {
            finishWithCardResult(card);
        }
    }

    private boolean isConfirmPromptEnabled() {
        return !TextUtils.isEmpty(mSelectionConfirmPromptText);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                recoverFromFailure();
            } else {
                setResult(RESULT_CANCELED, data);
                finish();
            }
        }
    }

    private void finishWithCardResult(Card card) {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        returnIntent.putExtra("card", JsonUtil.getInstance().toJson(card));
        finish();
    }

    public void onOtherPaymentMethodClicked(View view) {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    public void hideProgress() {
        LayoutUtil.showRegularLayout(this);
    }

    public void showProgress() {
        LayoutUtil.showProgressLayout(this);
    }
}
