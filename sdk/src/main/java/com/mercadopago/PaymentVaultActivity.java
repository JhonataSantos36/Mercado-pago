package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mercadopago.adapters.PaymentMethodSearchItemAdapter;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.MPTextView;

import java.math.BigDecimal;
import java.util.List;

public class PaymentVaultActivity extends MercadoPagoActivity {

    // Local vars
    protected MercadoPago mMercadoPago;
    protected PaymentMethod mSelectedPaymentMethod;
    protected Token mToken;
    protected Issuer mSelectedIssuer;
    protected PayerCost mSelectedPayerCost;
    protected Site mSite;

    // Controls
    protected RecyclerView mSearchItemsRecyclerView;
    protected AppBarLayout mAppBar;
    protected MPTextView mActivityTitle;

    // Current values
    protected PaymentMethodSearch mPaymentMethodSearch;

    // Activity parameters
    protected String mMerchantPublicKey;
    protected BigDecimal mAmount;
    protected boolean mShowBankDeals;
    protected boolean mCardGuessingEnabled;
    protected PaymentMethodSearchItem mSelectedSearchItem;
    protected PaymentPreference mPaymentPreference;

    @Override
    protected void setContentView() {
        setContentView(R.layout.mpsdk_activity_payment_vault);
    }

    @Override
    protected void getActivityParameters() {
        mMerchantPublicKey = getIntent().getStringExtra("merchantPublicKey");

        if(getIntent().getStringExtra("paymentPreference") != null) {
            mPaymentPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("paymentPreference"), PaymentPreference.class);
        }

        if (this.getIntent().getStringExtra("selectedSearchItem") != null) {
            mSelectedSearchItem = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("selectedSearchItem"), PaymentMethodSearchItem.class);
        }

        try {
            mAmount = new BigDecimal(this.getIntent().getStringExtra("amount"));
        } catch (Exception ex) {
            mAmount = null;
        }

        mSite = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("site"), Site.class);

        mCardGuessingEnabled = this.getIntent().getBooleanExtra("cardGuessingEnabled", false);

        mShowBankDeals = this.getIntent().getBooleanExtra("showBankDeals", true);

        if(this.getIntent().getStringExtra("paymentMethodSearch") != null) {
            mPaymentMethodSearch = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentMethodSearch"), PaymentMethodSearch.class);
        }
    }

    @Override
    protected void validateActivityParameters() {

        if(mPaymentPreference != null) {
            if (!mPaymentPreference.validMaxInstallments()) {
                throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_max_installments));
            } else if (!mPaymentPreference.validDefaultInstallments()) {
                throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_default_installments));
            } else if (!mPaymentPreference.excludedPaymentTypesValid()) {
                throw new IllegalStateException(getString(R.string.mpsdk_error_message_excluded_all_payment_type));
            }
        }
        if (!isAmountValid()) {
            throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_amount));
        }
        else if (!isCurrencyIdValid()) {
            throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_currency));
        }
        else if (!isMerchantPublicKeyValid()){
            throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_merchant));
        }
    }

    @Override
    protected void initializeControls() {
        initializeGroupRecyclerView();
        mActivityTitle = (MPTextView) findViewById(R.id.mpsdkTitle);
        mAppBar = (AppBarLayout) findViewById(R.id.mpsdkAppBar);
        initializeToolbar();
    }

    @Override
    protected void onInvalidStart(String message) {
        ErrorUtil.startErrorActivity(this, message, false);
    }

    @Override
    protected void onValidStart() {

        mMercadoPago = new MercadoPago.Builder()
                .setPublicKey(mMerchantPublicKey)
                .setContext(this)
                .build();

        MPTracker.getInstance().trackScreen("PAYMENT_METHOD_SEARCH", "2", mMerchantPublicKey, "MLA", "1.0", this);
        MPTracker.getInstance().trackEvent("PAYMENT_METHOD_SEARCH", "INIT_PAYMENT_VAULT", "2", mMerchantPublicKey, mSite.getId(), "1.0", this);

        if (isItemSelected()) {
            showSelectedItemChildren();
        } else {
            initPaymentMethodSearch();
        }
    }

    private boolean isAmountValid() {
        return mAmount != null && mAmount.compareTo(BigDecimal.ZERO) >= 0;
    }

    private boolean isMerchantPublicKeyValid() {
        return mMerchantPublicKey != null;
    }

    private boolean isCurrencyIdValid() {
        boolean isValid = true;

        if(mSite.getCurrencyId() == null) {
            isValid = false;
        }
        else if(!CurrenciesUtil.isValidCurrency(mSite.getCurrencyId())){
            isValid = false;
        }
        return isValid;
    }

    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.mpsdkToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView toolbarTitle = (TextView) findViewById(R.id.mpsdkTitle);

        decorate(toolbar);
        decorate(toolbarTitle);
    }

    protected void initializeGroupRecyclerView() {
        mSearchItemsRecyclerView = (RecyclerView) findViewById(R.id.mpsdkGroupsList);
        mSearchItemsRecyclerView.setHasFixedSize(true);
        mSearchItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initPaymentMethodSearch() {
        String initialTitle = getString(R.string.mpsdk_title_activity_payment_vault);
        setActivityTitle(initialTitle);

        if(mPaymentMethodSearch != null) {
            setSearchLayout();
        }
        else {
            getPaymentMethodSearch();
        }
    }

    protected void setActivityTitle(String title) {
        mActivityTitle.setText(title);
    }

    protected boolean isItemSelected() {
        return mSelectedSearchItem != null;
    }

    protected void getPaymentMethodSearch() {

        List<String> excludedPaymentTypes = mPaymentPreference != null ? mPaymentPreference.getExcludedPaymentTypes() : null;
        List<String> excludedPaymentMethodIds = mPaymentPreference != null ? mPaymentPreference.getExcludedPaymentMethodIds() : null;

        showProgress();
        mMercadoPago.getPaymentMethodSearch(mAmount, excludedPaymentTypes, excludedPaymentMethodIds, new Callback<PaymentMethodSearch>() {

            @Override
            public void success(PaymentMethodSearch paymentMethodSearch) {
                MPTracker.getInstance().trackEvent("PAYMENT_METHOD_SEARCH","GET_PAYMENT_METHOD_SEARCH_RESPONSE", "SUCCESS","2", mMerchantPublicKey, mSite.getId(), "1.0", getActivity());
                if(isActivityActive()) {
                    if (!paymentMethodSearch.hasSearchItems()) {
                        showEmptyPaymentMethodsError();
                    } else {
                        mPaymentMethodSearch = paymentMethodSearch;
                        setSearchLayout();
                    }
                }
            }

            @Override
            public void failure(ApiException apiException) {
                MPTracker.getInstance().trackEvent("PAYMENT_METHOD_SEARCH","GET_PAYMENT_METHOD_SEARCH_RESPONSE", "FAIL","2", mMerchantPublicKey, mSite.getId(), "1.0", getActivity());
                if (isActivityActive()) {
                    ApiUtil.showApiExceptionError(getActivity(), apiException);
                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            getPaymentMethodSearch();
                        }
                    });
                }
            }
        });
    }

    protected void setSearchLayout() {
        if(!isUniqueSelectionAvailable()) {
            populateSearchList(mPaymentMethodSearch.getGroups());
            showRegularLayout();
        }
        else {
            PaymentMethodSearchItem uniqueItem = mPaymentMethodSearch.getGroups().get(0);
            if(MercadoPagoUtil.isCardPaymentType(uniqueItem.getId())) {
                startNextStepForPaymentType(uniqueItem);
            }
            else if(uniqueItem.isPaymentMethod()) {
                resolvePaymentMethodSelection(uniqueItem);
            }
        }
    }

    private boolean isUniqueSelectionAvailable() {
        return mPaymentMethodSearch.getGroups().size() == 1;
    }

    protected void populateSearchList(List<PaymentMethodSearchItem> items) {
        PaymentMethodSearchItemAdapter groupsAdapter = new PaymentMethodSearchItemAdapter(this, items, getPaymentMethodSearchItemSelectionCallback(), mDecorationPreference);
        mSearchItemsRecyclerView.setAdapter(groupsAdapter);
    }

    protected OnSelectedCallback<PaymentMethodSearchItem> getPaymentMethodSearchItemSelectionCallback() {
        return new OnSelectedCallback<PaymentMethodSearchItem>() {
            @Override
            public void onSelected(PaymentMethodSearchItem item) {
                if (item.hasChildren()) {
                    restartWithSelectedItem(item);
                }
                else if (item.isPaymentType()) {
                    startNextStepForPaymentType(item);
                }
                else if (item.isPaymentMethod()) {
                    resolvePaymentMethodSelection(item);
                }
            }
        };
    }

    private void resolvePaymentMethodSelection(PaymentMethodSearchItem item) {
        PaymentMethod selectedPaymentMethod = mPaymentMethodSearch.getPaymentMethodBySearchItem(item);
        if (selectedPaymentMethod == null) {
            showMismatchingPaymentMethodError();
        }
        else {
            finishWithPaymentMethodResult(selectedPaymentMethod);
        }
    }

    private void restartWithSelectedItem(PaymentMethodSearchItem groupIem) {
        Intent intent = new Intent(this, PaymentVaultActivity.class);
        intent.putExtras(this.getIntent());
        intent.putExtra("selectedSearchItem", JsonUtil.getInstance().toJson(groupIem));
        intent.putExtra("paymentMethodSearch", JsonUtil.getInstance().toJson(mPaymentMethodSearch));

        startActivityForResult(intent, MercadoPago.PAYMENT_VAULT_REQUEST_CODE);
        overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
    }

    protected void showSelectedItemChildren() {
        setActivityTitle(mSelectedSearchItem.getChildrenHeader());
        populateSearchList(mSelectedSearchItem.getChildren());
    }

    protected void startNextStepForPaymentType(PaymentMethodSearchItem item) {

        if(mPaymentPreference == null) {
            mPaymentPreference = new PaymentPreference();
        }
        mPaymentPreference.setDefaultPaymentTypeId(item.getId());

        MercadoPago.StartActivityBuilder builder = new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mMerchantPublicKey)
                .setPaymentPreference(mPaymentPreference)
                .setDecorationPreference(mDecorationPreference);

        if(MercadoPagoUtil.isCardPaymentType(item.getId())){
            builder.setAmount(mAmount);
            builder.setSite(mSite);
            builder.setSupportedPaymentMethods(mPaymentMethodSearch.getPaymentMethods());
            builder.startCardVaultActivity();
            animatePaymentMethodSelection();
        }
        else {
            builder.startPaymentMethodsActivity();
        }
    }

    private void showProgress() {
        mAppBar.setVisibility(View.INVISIBLE);
        LayoutUtil.showProgressLayout(this);
    }

    private void showRegularLayout() {
        mAppBar.setVisibility(View.VISIBLE);
        LayoutUtil.showRegularLayout(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == MercadoPago.CARD_VAULT_REQUEST_CODE) {
            resolveCardRequest(resultCode, data);
        }
        else if (requestCode == MercadoPago.PAYMENT_METHODS_REQUEST_CODE) {
            resolvePaymentMethodsRequest(resultCode, data);
        }
        else if (requestCode == MercadoPago.PAYMENT_VAULT_REQUEST_CODE) {
            resolvePaymentVaultRequest(resultCode, data);
        }
        else if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            resolveErrorRequest(resultCode, data);
        }
    }

    private void resolveErrorRequest(int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            recoverFromFailure();
        }
        else if(noUserInteraction()){
            setResult(resultCode, data);
            finish();
        }
        else {
            showRegularLayout();
        }
    }

    private boolean noUserInteraction() {
        return mSelectedSearchItem == null;
    }

    private void resolvePaymentVaultRequest(int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
        else if(resultCode == RESULT_CANCELED && data != null && data.hasExtra("mpException")) {
            MPTracker.getInstance().trackEvent("PAYMENT_VAULT","CANCELED","2", mMerchantPublicKey, mSite.getId(), "1.0",this);

            setResult(Activity.RESULT_CANCELED, data);
            this.finish();
        }
    }

    protected void resolveCardRequest(int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            mSelectedPaymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
            mToken = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);
            mSelectedIssuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
            mSelectedPayerCost = JsonUtil.getInstance().fromJson(data.getStringExtra("payerCost"), PayerCost.class);
            finishWithCardResult();

        } else if (isUniqueSelectionAvailable()||
                ((data != null) && (data.getStringExtra("mpException") != null))){

            //TODO validate
            MPTracker.getInstance().trackEvent("PAYMENT_VAULT","CANCELED","2", mMerchantPublicKey, mSite.getId(), "1.0",this);

            setResult(Activity.RESULT_CANCELED, data);
            this.finish();
        }
        else {
            overridePendingTransition(R.anim.mpsdk_slide_left_to_right_in, R.anim.mpsdk_slide_left_to_right_out);
        }
    }

    protected void resolvePaymentMethodsRequest(int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
            finishWithPaymentMethodResult(paymentMethod);
        }
        else {
            if ((data != null) && (data.getStringExtra("apiException") != null)) {
                //TODO validate
                MPTracker.getInstance().trackEvent("PAYMENT_VAULT","CANCELED","2", mMerchantPublicKey, mSite.getId(), "1.0",this);

                setResult(Activity.RESULT_CANCELED, data);
                this.finish();
            }
        }
    }

    protected void finishWithPaymentMethodResult(PaymentMethod paymentMethod) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        this.setResult(Activity.RESULT_OK, returnIntent);
        this.finish();
        animatePaymentMethodSelection();
    }

    protected void finishWithCardResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("token", JsonUtil.getInstance().toJson(mToken));
        if (mSelectedIssuer != null) {
            returnIntent.putExtra("issuer", JsonUtil.getInstance().toJson(mSelectedIssuer));
        }
        returnIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(mSelectedPayerCost));
        returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mSelectedPaymentMethod));
        this.setResult(Activity.RESULT_OK, returnIntent);
        this.finish();
        animatePaymentMethodSelection();
    }

    private void animatePaymentMethodSelection() {
        overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
    }

    @Override
    public void onBackPressed() {
        MPTracker.getInstance().trackEvent("PAYMENT_METHOD_SEARCH","BACK_PRESSED","2", mMerchantPublicKey, mSite.getId(), "1.0",this);

        setResult(Activity.RESULT_CANCELED);
        finish();

        if(isItemSelected()) {
            overridePendingTransition(R.anim.mpsdk_slide_left_to_right_in, R.anim.mpsdk_slide_left_to_right_out);
        }
    }

    private void showMismatchingPaymentMethodError() {
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), "Payment method in search not found", false);
    }

    private void showEmptyPaymentMethodsError() {
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_no_payment_methods_found), false);
    }
}
