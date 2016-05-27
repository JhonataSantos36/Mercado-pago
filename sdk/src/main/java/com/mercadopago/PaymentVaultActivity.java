package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mercadopago.adapters.PaymentMethodSearchItemAdapter;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.MPTextView;

import java.math.BigDecimal;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PaymentVaultActivity extends AppCompatActivity {

    // Local vars
    protected Activity mActivity;
    protected MercadoPago mMercadoPago;
    protected PaymentMethod mSelectedPaymentMethod;
    protected Token mToken;
    protected Issuer mSelectedIssuer;
    protected PayerCost mSelectedPayerCost;
    protected FailureRecovery mFailureRecovery;

    // Controls
    protected RecyclerView mSearchItemsRecyclerView;
    protected AppBarLayout mAppBar;

    // Current values
    protected PaymentMethodSearch mPaymentMethodSearch;
    protected boolean mActivityActive;

    // Activity parameters
    protected String mMerchantPublicKey;
    protected BigDecimal mAmount;
    protected String mMerchantAccessToken;
    protected String mMerchantBaseUrl;
    protected String mMerchantGetCustomerUri;
    protected boolean mShowBankDeals;
    protected boolean mCardGuessingEnabled;
    protected PaymentMethodSearchItem mSelectedSearchItem;
    protected String mCurrencyId;
    protected PaymentPreference mPaymentPreference;
    protected MPTextView mActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment_vault);
        getActivityParameters();
        setActivity();
        mActivityActive = true;
        boolean validParameters = true;

        try {
            validateActivityParameters();
        } catch (IllegalStateException e) {
            validParameters = false;
            ErrorUtil.startErrorActivity(this, e.getMessage(), false);
        }
        if(validParameters) {

            mMercadoPago = new MercadoPago.Builder()
                    .setPublicKey(mMerchantPublicKey)
                    .setContext(this)
                    .build();

            initializeToolbar();
            initializeControls();

            if (isItemSelected()) {
                showSelectedItemChildren();
            } else {
                initPaymentMethodSearch();
            }
        }
    }

    protected void getActivityParameters() {

        if (this.getIntent().getSerializableExtra("selectedSearchItem") != null) {
            mSelectedSearchItem = (PaymentMethodSearchItem) this.getIntent().getSerializableExtra("selectedSearchItem");
        }
        try {
            mAmount = new BigDecimal(this.getIntent().getStringExtra("amount"));
        } catch (Exception ex) {
            mAmount = null;
        }
        mCurrencyId = this.getIntent().getStringExtra("currencyId");

        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");

        mMerchantBaseUrl = this.getIntent().getStringExtra("merchantBaseUrl");
        mMerchantGetCustomerUri = this.getIntent().getStringExtra("merchantGetCustomerUri");
        mMerchantAccessToken = this.getIntent().getStringExtra("merchantAccessToken");
        mCardGuessingEnabled = this.getIntent().getBooleanExtra("cardGuessingEnabled", false);
        mShowBankDeals = this.getIntent().getBooleanExtra("showBankDeals", true);

        if (this.getIntent().getSerializableExtra("paymentPreference") != null) {
            mPaymentPreference = (PaymentPreference) this.getIntent().getSerializableExtra("paymentPreference");
        }

        if(this.getIntent().getSerializableExtra("paymentMethodSearch") != null) {
            mPaymentMethodSearch = (PaymentMethodSearch) this.getIntent().getSerializableExtra("paymentMethodSearch");
        }
    }

    private void validateActivityParameters() {

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

    private boolean isAmountValid() {
        return mAmount != null && mAmount.compareTo(BigDecimal.ZERO) >= 0;
    }

    private boolean isMerchantPublicKeyValid() {
        return mMerchantPublicKey != null;
    }

    private boolean isCurrencyIdValid() {

        boolean isValid = true;

        if(mCurrencyId == null) {
            isValid = false;
        }
        else if(!CurrenciesUtil.isValidCurrency(mCurrencyId)){
            isValid = false;
        }
        return isValid;
    }

    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

    }

    protected void initializeControls() {
        initializeGroupRecyclerView();
        mActivityTitle = (MPTextView) findViewById(R.id.title);
        mAppBar = (AppBarLayout) findViewById(R.id.appBar);
    }

    protected void initializeGroupRecyclerView() {
        mSearchItemsRecyclerView = (RecyclerView) findViewById(R.id.groupsList);
        mSearchItemsRecyclerView.setHasFixedSize(true);
        mSearchItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    protected void setActivity() {
        this.mActivity = this;
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
            public void success(PaymentMethodSearch paymentMethodSearch, Response response) {
                if(mActivityActive) {
                    if (!paymentMethodSearch.hasSearchItems()) {
                        showEmptyPaymentMethodsError();
                    } else {
                        mPaymentMethodSearch = paymentMethodSearch;
                        setSearchLayout();
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (mActivityActive) {
                    ApiUtil.showApiExceptionError(mActivity, error);
                    mFailureRecovery = new FailureRecovery() {
                        @Override
                        public void recover() {
                            getPaymentMethodSearch();
                        }
                    };
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
        PaymentMethodSearchItemAdapter groupsAdapter = new PaymentMethodSearchItemAdapter(this, items, getPaymentMethodSearchItemSelectionCallback());
        mSearchItemsRecyclerView.setAdapter(groupsAdapter);;
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
        intent.putExtra("selectedSearchItem", groupIem);
        intent.putExtra("paymentMethodSearch", mPaymentMethodSearch);

        startActivityForResult(intent, MercadoPago.PAYMENT_VAULT_REQUEST_CODE);
        overridePendingTransition(R.anim.slide_right_to_left_in, R.anim.slide_right_to_left_out);
    }

    protected void showSelectedItemChildren() {
        setActivityTitle(mSelectedSearchItem.getChildrenHeader());
        populateSearchList(mSelectedSearchItem.getChildren());
    }

    protected void startNextStepForPaymentType(PaymentMethodSearchItem item) {

        MercadoPago.StartActivityBuilder builder = new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mMerchantPublicKey)
                .setPaymentPreference(mPaymentPreference);

        if(MercadoPagoUtil.isCardPaymentType(item.getId())){
            builder.setAmount(mAmount);
            builder.setCurrency(mCurrencyId);
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
        LayoutUtil.showRegularLayout(mActivity);
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
            setResult(Activity.RESULT_CANCELED, data);
            this.finish();
        }
    }

    protected void resolveCardRequest(int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            mSelectedPaymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");
            mToken = (Token) data.getSerializableExtra("token");
            mSelectedIssuer = (Issuer) data.getSerializableExtra("issuer");
            mSelectedPayerCost = (PayerCost) data.getSerializableExtra("payerCost");
            finishWithCardResult();

        } else if (isUniqueSelectionAvailable()||
                ((data != null) && (data.getSerializableExtra("mpException") != null))){

            setResult(Activity.RESULT_CANCELED, data);
            this.finish();
        }
        else {
            overridePendingTransition(R.anim.slide_left_to_right_in, R.anim.slide_left_to_right_out);
        }
    }

    protected void resolvePaymentMethodsRequest(int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            PaymentMethod paymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");
            finishWithPaymentMethodResult(paymentMethod);
        }
        else {
            if ((data != null) && (data.getSerializableExtra("apiException") != null)) {
                setResult(Activity.RESULT_CANCELED, data);
                this.finish();
            }
        }
    }

    protected void finishWithPaymentMethodResult(PaymentMethod paymentMethod) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("paymentMethod", paymentMethod);
        this.setResult(Activity.RESULT_OK, returnIntent);
        this.finish();
        animatePaymentMethodSelection();
    }

    protected void finishWithCardResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("token", mToken);
        if (mSelectedIssuer != null) {
            returnIntent.putExtra("issuer", mSelectedIssuer);
        }
        returnIntent.putExtra("payerCost", mSelectedPayerCost);
        returnIntent.putExtra("paymentMethod", mSelectedPaymentMethod);
        this.setResult(Activity.RESULT_OK, returnIntent);
        this.finish();
        animatePaymentMethodSelection();
    }

    private void animatePaymentMethodSelection() {
        overridePendingTransition(R.anim.slide_right_to_left_in, R.anim.slide_right_to_left_out);
    }

    @Override
    protected void onResume() {
        mActivityActive = true;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mActivityActive = false;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mActivityActive = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        mActivityActive = false;
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();

        if(isItemSelected()) {
            overridePendingTransition(R.anim.slide_left_to_right_in, R.anim.slide_left_to_right_out);
        }
    }

    private void showMismatchingPaymentMethodError() {
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), "Payment method in search not found", false);
    }

    private void showEmptyPaymentMethodsError() {
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_no_payment_methods_found), false);
    }

    private void recoverFromFailure() {
        if(mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }
}
