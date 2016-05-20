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
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.PaymentMethodSearchItemAdapter;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.fragments.ShoppingCartFragment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentType;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.MPTextView;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.text.TextUtils.isEmpty;

public class PaymentVaultActivity extends AppCompatActivity {

    // Local vars
    protected Activity mActivity;
    protected MercadoPago mMercadoPago;
    protected PaymentMethod mSelectedPaymentMethod;
    protected Token mToken;
    protected Issuer mSelectedIssuer;
    protected PayerCost mSelectedPayerCost;
    protected FailureRecovery failureRecovery;

    // Controls
    protected RecyclerView mSearchItemsRecyclerView;
    protected ImageView mShoppingCartIcon;
    protected View mContentView;
    protected AppBarLayout mAppBar;

    // Current values
    protected PaymentMethodSearch mPaymentMethodSearch;

    // Activity parameters
    protected String mMerchantPublicKey;
    protected BigDecimal mAmount;
    protected String mMerchantAccessToken;
    protected String mMerchantBaseUrl;
    protected String mMerchantGetCustomerUri;
    protected boolean mShowBankDeals;
    protected boolean mCardGuessingEnabled;
    protected PaymentMethodSearchItem mSelectedSearchItem;
    protected String mPurchaseTitle;
    protected String mItemImageUri;
    protected String mCurrencyId;
    protected ShoppingCartFragment mShoppingCartFragment;
    protected PaymentPreference mPaymentPreference;
    protected MPTextView mActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment_vault);
        getActivityParameters();
        setActivity();

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

            if(isShoppingCartDataAvailable()) {
                initializeShoppingCartFragment();
            }
            else {
                mShoppingCartIcon.setVisibility(View.GONE);
            }

            if (isItemSelected()) {
                showSelectedItemChildren();
            } else {
                initPaymentMethodSearch();
            }
        }
    }

    private boolean isShoppingCartDataAvailable() {
        return isPurchaseTitleValid() && isCurrencyIdValid();
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
        mItemImageUri = this.getIntent().getStringExtra("itemImageUri");
        mPurchaseTitle = this.getIntent().getStringExtra("purchaseTitle");

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

    private boolean isPurchaseTitleValid() {
        return !isEmpty(mPurchaseTitle);
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
        mContentView = findViewById(R.id.contentLayout);
        mShoppingCartIcon = (ImageView) findViewById(R.id.shoppingCartIcon);
        mAppBar = (AppBarLayout) findViewById(R.id.appBar);
    }

    protected void initializeGroupRecyclerView() {
        mSearchItemsRecyclerView = (RecyclerView) findViewById(R.id.groupsList);
        mSearchItemsRecyclerView.setHasFixedSize(true);
        mSearchItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initializeShoppingCartFragment() {
        mShoppingCartFragment = ShoppingCartFragment.newInstance(mItemImageUri, mPurchaseTitle, mAmount, mCurrencyId);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.shoppingCartFragment, mShoppingCartFragment)
                .hide(mShoppingCartFragment)
                .commit();
        mShoppingCartFragment.setToggler(mShoppingCartIcon);
        mShoppingCartFragment.setViewBelow(mContentView);
    }

    protected void setActivity() {
        this.mActivity = this;
    }

    private void initPaymentMethodSearch() {
        String initialTitle = getString(R.string.mpsdk_title_activity_payment_vault);
        setActivityTitle(initialTitle);
        showProgress();
        getPaymentMethodSearch();
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

        mMercadoPago.getPaymentMethodSearch(mAmount, excludedPaymentTypes, excludedPaymentMethodIds, new Callback<PaymentMethodSearch>() {

            @Override
            public void success(PaymentMethodSearch paymentMethodSearch, Response response) {
                if (!paymentMethodSearch.hasSearchItems()) {
                    showEmptyPaymentMethodsError();
                } else {
                    mPaymentMethodSearch = paymentMethodSearch;
                    setSearchLayout();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ApiUtil.showApiExceptionError(mActivity, error);
                failureRecovery = new FailureRecovery() {
                    @Override
                    public void recover() {
                        getPaymentMethodSearch();
                    }
                };
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
        mSearchItemsRecyclerView.setAdapter(groupsAdapter);

        //TODO update to rv 23 and replace by wrap content
        int recyclerViewSize = groupsAdapter.getHeightForItems();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, recyclerViewSize);
        mSearchItemsRecyclerView.setLayoutParams(layoutParams);
    }

    protected OnSelectedCallback getPaymentMethodSearchItemSelectionCallback() {
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

        if(getIntent().getSerializableExtra("paymentMethodSearch") == null) {
            getIntent().putExtra("paymentMethodSearch", mPaymentMethodSearch);
        }

        Intent intent = new Intent(this, PaymentVaultActivity.class);
        intent.putExtra("selectedSearchItem", groupIem);
        intent.putExtras(this.getIntent());

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
            builder.startGuessingCardActivity();
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

        if(requestCode == MercadoPago.GUESSING_CARD_REQUEST_CODE) {
            resolveGuessingCardRequest(resultCode, data);
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

    protected void resolveGuessingCardRequest(int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            mSelectedPaymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");
            mToken = (Token) data.getSerializableExtra("token");
            mSelectedIssuer = (Issuer) data.getSerializableExtra("issuer");
            mSelectedPayerCost = (PayerCost) data.getSerializableExtra("payerCost");
            finishWithTokenResult();

        } else if (isUniqueSelectionAvailable()||
                ((data != null) && (data.getSerializableExtra("mpException") != null))){

            setResult(Activity.RESULT_CANCELED, data);
            this.finish();
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

    protected void finishWithTokenResult() {
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
        if(failureRecovery != null) {
            failureRecovery.recover();
        }
    }
}
