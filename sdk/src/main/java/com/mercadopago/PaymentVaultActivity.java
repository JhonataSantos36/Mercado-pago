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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.PaymentMethodSearchItemAdapter;
import com.mercadopago.callbacks.PaymentMethodSearchCallback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.fragments.ShoppingCartFragment;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.PaymentType;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.MPTextView;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PaymentVaultActivity extends AppCompatActivity {

    // Local vars
    protected Activity mActivity;
    protected String mExceptionOnMethod;
    protected MercadoPago mMercadoPago;
    protected PaymentMethod mSelectedPaymentMethod;
    protected CardToken mCardToken;
    protected Issuer mSelectedIssuer;
    protected PayerCost mSelectedPayerCost;
    protected List<PaymentMethod> mPaymentMethods;
    protected Boolean mEditing;

    // Controls
    protected RecyclerView mSearchItemsRecyclerView;
    protected ImageView mShoppingCartIcon;
    protected View mContentView;
    protected MPTextView mActivityTitle;
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
    protected Integer mDefaultInstallments;
    protected Integer mMaxInstallments;
    protected List<String> mExcludedPaymentMethodIds;
    protected List<String> mExcludedPaymentTypes;
    protected String mDefaultPaymentMethodId;
    protected Boolean mSupportMPApp;
    protected PaymentMethodSearchItem mSelectedSearchItem;
    protected String mPurchaseTitle;
    protected String mItemImageUri;
    protected String mCurrencyId;
    protected ShoppingCartFragment mShoppingCartFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_vault);
        getActivityParameters();
        boolean validParameters = true;
        try {
            validateActivityParameters();
        } catch (IllegalStateException e) {
            validParameters = false;
            finishWithIllegalStateException(e.getMessage());
        }
        if(validParameters) {
            mMercadoPago = new MercadoPago.Builder()
                    .setPublicKey(mMerchantPublicKey)
                    .setContext(this)
                    .build();

            initializeControls();
            setActivity();

            if (!isItemSelectedStart()) {
                initPaymentMethodSearch();
            } else {
                showSelectedItemChildren();
            }
            initializeToolbar();
        }
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

    protected void getActivityParameters() {

        if (isItemSelectedStart()) {
            mSelectedSearchItem = (PaymentMethodSearchItem) this.getIntent().getSerializableExtra("selectedSearchItem");
            if(this.getIntent().getSerializableExtra("paymentMethodSearch") != null) {
                mPaymentMethodSearch = (PaymentMethodSearch) this.getIntent().getSerializableExtra("paymentMethodSearch");
            }
        }

        try {
            mAmount = new BigDecimal(this.getIntent().getStringExtra("amount"));
        } catch (Exception ex) {
            mAmount = null;
        }
        mCurrencyId = this.getIntent().getStringExtra("currencyId");
        mItemImageUri = this.getIntent().getStringExtra("itemImageUri");
        mPurchaseTitle = this.getIntent().getStringExtra("purchaseTitle");

        mSupportMPApp = this.getIntent().getBooleanExtra("supportMPApp", false);

        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");

        mMerchantBaseUrl = this.getIntent().getStringExtra("merchantBaseUrl");
        mMerchantGetCustomerUri = this.getIntent().getStringExtra("merchantGetCustomerUri");
        mMerchantAccessToken = this.getIntent().getStringExtra("merchantAccessToken");
        mCardGuessingEnabled = this.getIntent().getBooleanExtra("cardGuessingEnabled", false);
        mShowBankDeals = this.getIntent().getBooleanExtra("showBankDeals", true);

        if (this.getIntent().getStringExtra("excludedPaymentMethodIds") != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>(){}.getType();
            mExcludedPaymentMethodIds = gson.fromJson(this.getIntent().getStringExtra("excludedPaymentMethodIds"), listType);
        }
        if (this.getIntent().getStringExtra("excludedPaymentTypes") != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>(){}.getType();
            mExcludedPaymentTypes = gson.fromJson(this.getIntent().getStringExtra("excludedPaymentTypes"), listType);
        }
        mDefaultPaymentMethodId = this.getIntent().getStringExtra("defaultPaymentMethodId");

        if(this.getIntent().getStringExtra("maxInstallments") != null) {
            mMaxInstallments = Integer.valueOf(this.getIntent().getStringExtra("maxInstallments"));
        }
        if(this.getIntent().getStringExtra("defaultInstallments") != null) {
            mDefaultInstallments = Integer.valueOf(this.getIntent().getStringExtra("defaultInstallments"));
        }
        mEditing = this.getIntent().getBooleanExtra("editing", false);
    }

    private void validateActivityParameters() {

        if (!isAmountValid()) {
            throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_amount));
        }
        else if(!isCurrencyIdValid()){
            throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_currency));
        }
        else if (!isPurchaseTitleValid()){
            throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_title));
        }
        else if (!isMerchantPublicKeyValid()){
            throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_merchant));
        }
        else if (!validInstallmentsPreferences()){
            throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_installments));
        }
        else if (!validExcludedPaymentTypes()){
            throw new IllegalStateException(getString(R.string.mpsdk_error_message_excluded_all_payment_type));
        }
    }

    private boolean validExcludedPaymentTypes() {
        boolean valid = true;
        if(mExcludedPaymentTypes != null && mExcludedPaymentTypes.size() >= PaymentType.getAllPaymentTypes().size()) {
            valid = false;
        }
        return valid;
    }

    private boolean validInstallmentsPreferences() {

        boolean isValid = true;
        if(mDefaultInstallments != null && mDefaultInstallments <= 0
                || mMaxInstallments != null && mMaxInstallments <= 0) {
            isValid = false;
        }
        return isValid;
    }

    private boolean isAmountValid() {
        return mAmount != null && mAmount.compareTo(BigDecimal.ZERO) >= 0;
    }

    private boolean isMerchantPublicKeyValid() {
        return mMerchantPublicKey != null;
    }

    private boolean isPurchaseTitleValid() {
        return mPurchaseTitle != null;
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

    protected void initializeControls() {
        initializeGroupRecyclerView();
        mShoppingCartIcon = (ImageView) findViewById(R.id.shoppingCartIcon);
        mActivityTitle = (MPTextView) findViewById(R.id.title);
        mContentView = findViewById(R.id.contentLayout);
        mShoppingCartFragment = ShoppingCartFragment.newInstance(mItemImageUri, mPurchaseTitle, mAmount, mCurrencyId);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.shoppingCartFragment, mShoppingCartFragment)
                .hide(mShoppingCartFragment)
                .commit();
        mShoppingCartFragment.setToggler(mShoppingCartIcon);
        mShoppingCartFragment.setViewBelow(mContentView);
        mAppBar = (AppBarLayout) findViewById(R.id.appBar);
    }

    protected void initializeGroupRecyclerView() {
        mSearchItemsRecyclerView = (RecyclerView) findViewById(R.id.groupsList);
        mSearchItemsRecyclerView.setHasFixedSize(true);
        mSearchItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initPaymentMethodSearch() {
        String initialTitle = getString(R.string.mpsdk_title_activity_payment_vault);
        setActivityTitle(initialTitle);
        showProgress();
        getPaymentMethodSearch();
    }

    protected void setActivity() {
        this.mActivity = this;
    }

    protected boolean isItemSelectedStart() {
        return this.getIntent().getSerializableExtra("selectedSearchItem") != null;
    }

    protected void getPaymentMethodSearch() {
        mMercadoPago.getPaymentMethodSearch(mAmount, mExcludedPaymentTypes, mExcludedPaymentMethodIds, new Callback<PaymentMethodSearch>() {
            @Override
            public void success(PaymentMethodSearch paymentMethodSearch, Response response) {
                if (!paymentMethodSearch.hasSearchItems()) {
                    finishWithEmptyPaymentMethodSearch();
                } else {
                    mPaymentMethodSearch = paymentMethodSearch;
                    setSearchLayout();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ApiUtil.finishWithApiException(mActivity, error);
            }
        });
    }

    protected void setSearchLayout() {
        showRegularLayout();
        populateSearchList(mPaymentMethodSearch.getGroups());
    }

    private void showProgress() {
        mAppBar.setVisibility(View.INVISIBLE);
        LayoutUtil.showProgressLayout(this);
    }

    private void showRegularLayout() {
        mAppBar.setVisibility(View.VISIBLE);
        LayoutUtil.showRegularLayout(mActivity);
    }

    protected void populateSearchList(List<PaymentMethodSearchItem> items) {
        PaymentMethodSearchItemAdapter groupsAdapter = new PaymentMethodSearchItemAdapter(this, items, getPaymentMethodSearchCallback());
        mSearchItemsRecyclerView.setAdapter(groupsAdapter);

        //TODO update to rv 23 and replace by wrap content
        int recyclerViewSize = groupsAdapter.getHeightForItems();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, recyclerViewSize);
        mSearchItemsRecyclerView.setLayoutParams(layoutParams);
    }

    private void startActivityForSelectedItem(PaymentMethodSearchItem groupIem) {
        Intent intent = new Intent(this, PaymentVaultActivity.class);
        intent.putExtra("selectedSearchItem", groupIem);
        intent.putExtra("merchantPublicKey", mMerchantPublicKey);
        intent.putExtra("currencyId", mCurrencyId);
        intent.putExtra("amount", mAmount.toString());
        intent.putExtra("purchaseTitle", mPurchaseTitle);
        intent.putExtra("itemImageUri", mItemImageUri);
        intent.putExtra("paymentMethodSearch", mPaymentMethodSearch);
        startActivityForResult(intent, MercadoPago.PAYMENT_VAULT_REQUEST_CODE);
        overridePendingTransition(R.anim.slide_right_to_left_in, R.anim.slide_right_to_left_out);
    }

    protected void showSelectedItemChildren() {
        setActivityTitle(mSelectedSearchItem.getChildrenHeader());
        populateSearchList(mSelectedSearchItem.getChildren());
    }

    protected void startNextStepForPaymentType(String paymentTypeId) {

        MercadoPago.StartActivityBuilder builder = new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mMerchantPublicKey)
                .setExcludedPaymentMethodIds(mExcludedPaymentMethodIds)
                .setPaymentTypeId(paymentTypeId);

        if(MercadoPagoUtil.isCardPaymentType(paymentTypeId)){
            builder.startGuessingCardActivity();
        }
        else {
            builder.startPaymentMethodsActivity();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == MercadoPago.GUESSING_CARD_REQUEST_CODE) {
            resolveGuessingCardRequest(resultCode, data);
        } else if (requestCode == MercadoPago.PAYMENT_METHODS_REQUEST_CODE) {
            resolvePaymentMethodsRequest(resultCode, data);
        }
        else if (requestCode == MercadoPago.PAYMENT_VAULT_REQUEST_CODE) {
            resolvePaymentVaultRequest(resultCode, data);
        }
    }

    private void resolvePaymentVaultRequest(int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
        else if(resultCode == RESULT_CANCELED) {
            if(data.hasExtra("error") || data.hasExtra("canceled")) {
                setResult(RESULT_CANCELED, data);
                finish();
            }
        }
    }

    protected void resolveGuessingCardRequest(int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            mSelectedPaymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");
            mCardToken = (CardToken) data.getSerializableExtra("cardToken");
            mSelectedIssuer = (Issuer) data.getSerializableExtra("issuer");
            mSelectedPayerCost = (PayerCost) data.getSerializableExtra("payerCost");

            LayoutUtil.showProgressLayout(mActivity);
            mMercadoPago.createToken(mCardToken, getCreateTokenCallback());


        } else if ((data != null) && (data.getSerializableExtra("apiException") != null)) {
            finishWithApiException(data);
        }
    }

    protected void resolvePaymentMethodsRequest(int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            PaymentMethod paymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");
            finishWithPaymentMethodResult(paymentMethod, "", "");

        } else {
            if ((data != null) && (data.getSerializableExtra("apiException") != null)) {
                finishWithApiException(data);
            }
        }
    }

    protected PaymentMethodSearchCallback getPaymentMethodSearchCallback() {
        return new PaymentMethodSearchCallback() {
            @Override
            public void onGroupItemClicked(PaymentMethodSearchItem groupIem) {
                startActivityForSelectedItem(groupIem);
            }

            @Override
            public void onPaymentTypeItemClicked(PaymentMethodSearchItem paymentTypeItem) {
                if(paymentTypeItem.hasChildren()){
                    startActivityForSelectedItem(paymentTypeItem);
                }
                else {
                    startNextStepForPaymentType(paymentTypeItem.getId());
                }
            }

            @Override
            public void onPaymentMethodItemClicked(final PaymentMethodSearchItem paymentMethodItem) {

                PaymentMethod selectedPaymentMethod = mPaymentMethodSearch.getPaymentMethodBySearchItem(paymentMethodItem);

                if(selectedPaymentMethod == null) {
                    finishWithMismatchingPaymentMethod();
                } else {
                    finishWithPaymentMethodResult(selectedPaymentMethod, paymentMethodItem.getComment(), paymentMethodItem.getDescription());
                }
            }
        };
    }

    protected Callback<Token> getCreateTokenCallback() {

        return new Callback<Token>() {
            @Override
            public void success(Token token, Response response) {

                finishWithTokenResult(token);
            }

            @Override
            public void failure(RetrofitError error) {

                mExceptionOnMethod = "getCreateTokenCallback";
                ApiUtil.finishWithApiException(mActivity, error);
            }
        };
    }

    private void finishWithEmptyPaymentMethodSearch() {
        //TODO modificar
        Toast.makeText(mActivity, "No hay medios de pago disponibles", Toast.LENGTH_SHORT).show();
        finish();
    }

    protected void finishWithIllegalStateException(String message) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("error", message);
        this.setResult(Activity.RESULT_CANCELED, returnIntent);
        this.finish();
    }

    protected void finishWithPaymentMethodResult(PaymentMethod paymentMethod, String paymentMethodComment, String paymentMethodDescription) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("paymentMethod", paymentMethod);
        returnIntent.putExtra("paymentMethodComment", paymentMethodComment);
        returnIntent.putExtra("paymentMethodDescription", paymentMethodDescription);
        this.setResult(Activity.RESULT_OK, returnIntent);
        this.finish();
        animatePaymentMethodSelection();
    }

    protected void finishWithTokenResult(Token token) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("token", token);
        if (mSelectedIssuer != null) {
            returnIntent.putExtra("issuer", mSelectedIssuer);
        }
        returnIntent.putExtra("payerCost", mSelectedPayerCost);
        returnIntent.putExtra("paymentMethod", mSelectedPaymentMethod);
        this.setResult(Activity.RESULT_OK, returnIntent);
        this.finish();
        animatePaymentMethodSelection();
    }

    protected void finishWithApiException(Intent data) {
        setResult(Activity.RESULT_CANCELED, data);
        this.finish();
        animatePaymentMethodSelection();
    }

    private void finishWithMismatchingPaymentMethod() {
        Intent canceledIntent = new Intent();
        canceledIntent.putExtra("error", "Mismatching payment method");
        setResult(RESULT_CANCELED, canceledIntent);
        finish();
    }

    private void animatePaymentMethodSelection() {
        overridePendingTransition(R.anim.slide_right_to_left_in, R.anim.slide_right_to_left_out);
    }

    protected void setActivityTitle(String title) {
        mActivityTitle.setText(title);
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();

        if(isItemSelectedStart()) {
            overridePendingTransition(R.anim.slide_left_to_right_in, R.anim.silde_left_to_right_out);
        }
    }
}
