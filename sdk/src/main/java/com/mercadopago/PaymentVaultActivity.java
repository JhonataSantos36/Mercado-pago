package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.PaymentMethodSearchItemAdapter;
import com.mercadopago.callbacks.PaymentMethodSearchCallback;
import com.mercadopago.controllers.ShoppingCartController;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.decorations.DividerItemDecoration;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.PaymentType;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PaymentVaultActivity extends AppCompatActivity {

    private static final int PURCHASE_TITLE_MAX_LENGTH = 50;
    // Local vars
    protected Activity mActivity;
    protected String mExceptionOnMethod;
    protected MercadoPago mMercadoPago;
    protected PaymentMethod mSelectedPaymentMethod;
    protected CardToken mCardToken;
    protected Issuer mSelectedIssuer;
    protected PayerCost mSelectedPayerCost;
    protected ShoppingCartController mShoppingCartController;

    // Controls
    protected RecyclerView mSearchItemsRecyclerView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_vault);
        setTitle(Html.fromHtml("<b><i><small>" + getString(R.string.mpsdk_title_activity_payment_vault) + "</small></i></b>"));

        getActivityParameters();

        try {
            validateActivityParameters();
        } catch (IllegalStateException e) {
            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        mMercadoPago = new MercadoPago.Builder()
                .setPublicKey(mMerchantPublicKey)
                .setContext(this)
                .build();

        initializeControls();
        setActivity();


        if(isItemSelected()) {
            showItemChildren(mSelectedSearchItem);
        }
        else {
            LayoutUtil.showProgressLayout(this);
            getPaymentMethodSearch();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    private void validateActivityParameters() {

        if (!isAmountValid()){
            throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_amount));
        }
        else if(!isCurrencyIdValid()){
            throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_currency));
        }
        else if (!isPurchaseTitleValid()){
            throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_title));
        }
        else if (!isMerchantPublicKey()){
            throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_merchant));
        }
        else if (!isInstallmentValid()){
            throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_installments));
        }
        else if (!isPaymentTypesValid()){
            throw new IllegalStateException(getString(R.string.mpsdk_error_message_excluded_all_payment_type));
        }
    }

    private boolean isPaymentTypesValid() {
        return mExcludedPaymentTypes.size() < PaymentType.getAllPaymentTypes().size();
    }

    private boolean isInstallmentValid() {
        return mDefaultInstallments >=0 && mMaxInstallments >= 0;
    }

    private boolean isAmountValid() {
        return mAmount != null && mAmount.compareTo(BigDecimal.ZERO) >= 0;
    }

    private boolean isMerchantPublicKey() {
        return mMerchantPublicKey != null;
    }

    private boolean isPurchaseTitleValid() {
        return mPurchaseTitle != null;
    }

    private boolean isCurrencyIdValid() {
        return mCurrencyId != null;
    }

    ///////////////////////////////////////////////////////////////////////////
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
        mPurchaseTitle = getFormatedPurchaseTitle();

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



    }

    protected String getFormatedPurchaseTitle() {
        if(this.getIntent().getStringExtra("purchaseTitle") != null) {
            String purchaseTitle = this.getIntent().getStringExtra("purchaseTitle");
            if (purchaseTitle.length() > PURCHASE_TITLE_MAX_LENGTH) {
                purchaseTitle = purchaseTitle.substring(0, PURCHASE_TITLE_MAX_LENGTH);
                purchaseTitle = purchaseTitle + "…";
            }
            return purchaseTitle;
        }
        else return null;
    }

    protected void initializeControls() {
        initializeGroupRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shopping_cart_menu, menu);
        mShoppingCartController = new ShoppingCartController(this, menu.findItem(R.id.shopping_cart), mItemImageUri, mPurchaseTitle,
                mAmount, mCurrencyId, false, findViewById(R.id.scrollLayout));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.shopping_cart) {
            mShoppingCartController.toggle(true);
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initializeGroupRecyclerView() {
        mSearchItemsRecyclerView = (RecyclerView) findViewById(R.id.groupsList);
        mSearchItemsRecyclerView.setHasFixedSize(true);
        mSearchItemsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mSearchItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    protected void setActivity() {
        this.mActivity = this;
    }

    protected boolean isItemSelected() {
        return mSelectedSearchItem != null;
    }

    protected void getPaymentMethodSearch() {
        mMercadoPago.getPaymentMethodSearch(mAmount, mExcludedPaymentTypes, mExcludedPaymentMethodIds, new Callback<PaymentMethodSearch>() {
            @Override
            public void success(PaymentMethodSearch paymentMethodSearch, Response response) {
                LayoutUtil.showRegularLayout(mActivity);
                if(paymentMethodSearch.getGroups().isEmpty()) {
                    finishWithEmptyPaymentMethodSearch();
                }
                else {
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

    //////////////////////////////////////////////////////////////////////
    private void finishWithEmptyPaymentMethodSearch() {
        Toast.makeText(mActivity, "Se excluyeron todos los paymentMethods", Toast.LENGTH_SHORT).show();
        finish();
    }
    //////////////////////////////////////////////////////////////////////

    protected void setSearchLayout() {
        String initialTitle = getString(R.string.mpsdk_title_activity_payment_vault);
        setTitle(Html.fromHtml("<b><i><small>" + initialTitle + "</small></i></b>"));
        if(mPaymentMethodSearch.hasPreferred() && mPaymentMethodSearch.hasSearchItems()) {
            //showTitles
            populateSearchList(mPaymentMethodSearch.getGroups());
            //populatePreferredList(paymentMethodSearch.getPreferred());
        }
        else if(mPaymentMethodSearch.hasSearchItems()) {
            populateSearchList(mPaymentMethodSearch.getGroups());
        }
        else if(mPaymentMethodSearch.hasPreferred()) {
            //populatePreferredList(paymentMethodSearch.getPreferred());
        }
    }

    protected void populateSearchList(List<PaymentMethodSearchItem> items) {
        PaymentMethodSearchItemAdapter groupsAdapter = new PaymentMethodSearchItemAdapter(this, items, getPaymentMethodSearchCallback());
        mSearchItemsRecyclerView.setAdapter(groupsAdapter);
    }

    protected PaymentMethodSearchCallback getPaymentMethodSearchCallback() {
        return new PaymentMethodSearchCallback() {
            @Override
            public void onGroupItemClicked(PaymentMethodSearchItem groupIem) {
                startActivityForItem(groupIem);
            }

            @Override
            public void onPaymentTypeItemClicked(PaymentMethodSearchItem paymentTypeItem) {
                if(paymentTypeItem.hasChildren()){
                    startActivityForItem(paymentTypeItem);
                }
                else {
                    PaymentType paymentType = new PaymentType();
                    paymentType.setId(paymentTypeItem.getId());

                    startNextStepForPaymentType(paymentType);
                }
            }

            @Override
            public void onPaymentMethodItemClicked(PaymentMethodSearchItem paymentMethodItem) {
                if(paymentMethodItem.getId().equals(getResources().getString(R.string.mpsdk_mp_app_id))) {
                    //TODO account money
                }
                else {
                    PaymentMethod paymentMethod = new PaymentMethod();
                    paymentMethod.setId(paymentMethodItem.getId());

                    finishWithPaymentMethodResult(paymentMethod, paymentMethodItem.getComment());
                }
            }
        };
    }

    private void startActivityForItem(PaymentMethodSearchItem groupIem) {
        Intent intent = new Intent(this, PaymentVaultActivity.class);
        intent.putExtra("selectedSearchItem", groupIem);
        intent.putExtra("merchantPublicKey", mMerchantPublicKey);
        intent.putExtra("currencyId", mCurrencyId);
        intent.putExtra("amount", mAmount.toString());
        intent.putExtra("purchaseTitle", mPurchaseTitle);
        intent.putExtra("itemImageUri", mItemImageUri);
        startActivityForResult(intent, MercadoPago.PAYMENT_VAULT_REQUEST_CODE);
        overridePendingTransition(R.anim.slide_right_to_left_in, R.anim.slide_right_to_left_out);
    }

    protected void showItemChildren(PaymentMethodSearchItem item) {
        setTitle(Html.fromHtml("<b><i><small>" + item.getChildrenHeader() + "</small></i></b>"));
        populateSearchList(item.getChildren());
    }

    protected void startNextStepForPaymentType(PaymentType paymentType) {

        MercadoPago.StartActivityBuilder builder = new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mMerchantPublicKey)
                .setExcludedPaymentMethodIds(mExcludedPaymentMethodIds)
                .setPaymentType(paymentType);

        if(MercadoPagoUtil.isCardPaymentType(paymentType.getId())){
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
        }
        else if (requestCode == MercadoPago.PAYMENT_METHODS_REQUEST_CODE) {
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
            finishWithPaymentMethodResult(paymentMethod, "");

        } else {
            if ((data != null) && (data.getSerializableExtra("apiException") != null)) {
                finishWithApiException(data);
            }
        }
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

    protected void finishWithPaymentMethodResult(PaymentMethod paymentMethod, String paymentMethodInfo) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("paymentMethod", paymentMethod);
        returnIntent.putExtra("paymentMethodInfo", paymentMethodInfo);
        this.setResult(Activity.RESULT_OK, returnIntent);
        this.finish();
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
    }

    protected void finishWithApiException(Intent data) {
        setResult(Activity.RESULT_CANCELED, data);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
        if(isItemSelected()) {
            overridePendingTransition(R.anim.slide_left_to_right_in, R.anim.silde_left_to_right_out);
        }
    }


}
