package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.RelativeLayout;

import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.exceptions.CheckoutPreferenceException;
import com.mercadopago.exceptions.ExceptionHandler;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.fragments.ShoppingCartFragment;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.CheckoutPreference;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Item;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.Token;
import com.mercadopago.uicontrollers.ViewControllerFactory;
import com.mercadopago.uicontrollers.payercosts.PayerCostViewController;
import com.mercadopago.uicontrollers.paymentmethods.PaymentMethodViewController;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.MPButton;
import com.mercadopago.views.MPTextView;

import java.math.BigDecimal;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.text.TextUtils.isEmpty;

public class CheckoutActivity extends AppCompatActivity {

    //Parameters
    protected String mCheckoutPreferenceId;
    protected CheckoutPreference mCheckoutPreference;
    protected String mMerchantPublicKey;
    protected Boolean mShowBankDeals;
    protected PaymentPreference mPaymentPreference;

    //Local vars
    protected MercadoPago mMercadoPago;
    protected Activity mActivity;

    protected PaymentMethodSearch mPaymentMethodSearch;

    protected PaymentMethod mSelectedPaymentMethod;
    protected Issuer mSelectedIssuer;
    protected PayerCost mSelectedPayerCost;
    protected Token mCreatedToken;
    private Payment mCreatedPayment;

    protected String mPurchaseTitle;
    protected ShoppingCartFragment mShoppingCartFragment;
    protected String mErrorMessage;
    protected boolean mPaymentMethodEditionRequested;

    protected PaymentMethodViewController mPaymentMethodRow;
    protected PayerCostViewController mPayerCostRow;
    protected FailureRecovery failureRecovery;

    //Controls
    protected MPTextView mTermsAndConditionsTextView;
    protected MPTextView mCancelTextView;
    protected MPTextView mTotalAmountTextView;
    protected MPButton mPayButton;
    protected View mContentView;
    protected RelativeLayout mPaymentMethodLayout;
    protected RelativeLayout mPayerCostLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        initializeToolbar();
        getActivityParameters();

        boolean validState = true;
        try{
            validateParameters();
        } catch (Exception e) {
            mErrorMessage = e.getMessage();
            validState = false;
        }
        if(validState) {
            getApplicationContext();
            initializeActivityControls();

            setActivity();

            mMercadoPago = new MercadoPago.Builder()
                    .setContext(this)
                    .setPublicKey(mMerchantPublicKey)
                    .build();

            getCheckoutPreference();
        }
        else {
            showError();
        }
    }

    private void getCheckoutPreference() {
        showProgress();
        mMercadoPago.getPreference(mCheckoutPreferenceId, new Callback<CheckoutPreference>() {
            @Override
            public void success(CheckoutPreference checkoutPreference, Response response) {
                mCheckoutPreference = checkoutPreference;
                validatePreference();
                initializeCheckout();
            }

            @Override
            public void failure(RetrofitError error) {
                ApiUtil.showApiExceptionError(mActivity, error);
                failureRecovery = new FailureRecovery() {
                    @Override
                    public void recover() {
                        getCheckoutPreference();
                    }
                };
            }
        });
    }

    private void validatePreference() {
        try {
            mCheckoutPreference.validate();
            if(!mCheckoutPreference.getId().equals(mCheckoutPreferenceId)) {
                throw new CheckoutPreferenceException(CheckoutPreferenceException.PREF_ID_NOT_MATCHING_REQUESTED);
            }
        }
        catch(CheckoutPreferenceException e) {
            mErrorMessage = ExceptionHandler.getErrorMessage(this, e);
            showError();
        }
    }

    private void showError() {
        MPException mpException = new MPException(mErrorMessage, false);
        ErrorUtil.startErrorActivity(this, mpException);
    }

    private void initializeCheckout() {
        initializeShoppingCart();
        getPaymentMethodSearch();
    }

    private void initializeShoppingCart() {
        mPurchaseTitle = getPurchaseTitleFromPreference();
        String currencyId = mCheckoutPreference.getItems().get(0).getCurrencyId();
        String pictureUrl = mCheckoutPreference.getItems().get(0).getPictureUrl();

        mShoppingCartFragment = ShoppingCartFragment.newInstance(pictureUrl, mPurchaseTitle, mCheckoutPreference.getAmount(), currencyId);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.shoppingCartFragment, mShoppingCartFragment)
                .show(mShoppingCartFragment)
                .commit();
    }

    private String getPurchaseTitleFromPreference() {
        StringBuilder purchaseTitle = new StringBuilder();
        int itemListSize = mCheckoutPreference.getItems().size();

        if(itemListSize == 1) {
            purchaseTitle.append(mCheckoutPreference.getItems().get(0).getTitle());
        }
        else if (itemListSize > 1){
            for(Item item : mCheckoutPreference.getItems()){
                purchaseTitle.append(item.getTitle());
                if(!item.equals(mCheckoutPreference.getItems().get(itemListSize-1))) {
                    purchaseTitle.append(", ");
                }
            }
        }
        return purchaseTitle.toString();
    }

    private void getActivityParameters() {
        mCheckoutPreferenceId = this.getIntent().getStringExtra("checkoutPreferenceId");
        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");
        mShowBankDeals = this.getIntent().getBooleanExtra("showBankDeals", true);
    }

    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void validateParameters() {
        if(!validParameters()) {
            throw new IllegalStateException("Invalid parameters");
        }
    }

    protected boolean validParameters() {
        return (mMerchantPublicKey != null) && (!isEmpty(mCheckoutPreferenceId));
    }

    private void initializeActivityControls() {

        mTermsAndConditionsTextView = (MPTextView) findViewById(R.id.termsAndConditions);
        mTermsAndConditionsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTermsAndConditionsActivity();
            }
        });

        mCancelTextView = (MPTextView) findViewById(R.id.cancelTextView);
        mCancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClicked();
            }
        });
        mPayButton = (MPButton) findViewById(R.id.payButton);
        mPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPayment();
            }
        });
        mTotalAmountTextView = (MPTextView) findViewById(R.id.totalAmount);
        mContentView = findViewById(R.id.contentLayout);
        mPaymentMethodLayout = (RelativeLayout) findViewById(R.id.paymentMethodLayout);
        mPayerCostLayout = (RelativeLayout) findViewById(R.id.payerCostLayout);
    }

    protected void startTermsAndConditionsActivity() {
        Intent termsAndConditionsIntent = new Intent(this, TermsAndConditionsActivity.class);
        startActivity(termsAndConditionsIntent);
    }

    protected void setActivity() {
        this.mActivity = this;
    }

    protected void getPaymentMethodSearch() {

        mMercadoPago.getPaymentMethodSearch(mCheckoutPreference.getAmount(), mCheckoutPreference.getExcludedPaymentTypes(), mCheckoutPreference.getExcludedPaymentMethods(), new Callback<PaymentMethodSearch>() {
            @Override
            public void success(PaymentMethodSearch paymentMethodSearch, Response response) {
                mPaymentMethodSearch = paymentMethodSearch;
                startPaymentVaultActivity();
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

    protected void startPaymentVaultActivity() {

        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mMerchantPublicKey)
                .setItemImageUri(mCheckoutPreference.getItems().get(0).getPictureUrl())
                .setPurchaseTitle(mPurchaseTitle)
                .setCurrency(mCheckoutPreference.getItems().get(0).getCurrencyId())
                .setAmount(mCheckoutPreference.getAmount())
                .setShowBankDeals(mShowBankDeals)
                .setPaymentMethodSearch(mPaymentMethodSearch)
                .setPaymentPreference(mCheckoutPreference.getPaymentPreference())
                .startPaymentVaultActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MercadoPago.PAYMENT_VAULT_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                mSelectedIssuer = (Issuer) data.getSerializableExtra("issuer");
                mSelectedPayerCost = (PayerCost) data.getSerializableExtra("payerCost");
                mCreatedToken = (Token) data.getSerializableExtra("token");
                mSelectedPaymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");

                showRegularLayout();
                showReviewAndConfirm();
            }
            else if (resultCode == RESULT_CANCELED) {
                if(!mPaymentMethodEditionRequested) {
                    Intent returnIntent = new Intent();
                    setResult(RESULT_CANCELED, returnIntent);
                    finish();
                }
                else {
                    animateBackFromPaymentEdition();
                }
            }
        }
        else if (requestCode == MercadoPago.INSTRUCTIONS_REQUEST_CODE) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("payment", mCreatedPayment);
            setResult(RESULT_OK, returnIntent);
            finish();
        }
        else if (requestCode == MercadoPago.CONGRATS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("payment", mCreatedPayment);
                setResult(RESULT_OK, returnIntent);
                finish();
            } else if (resultCode == RESULT_CANCELED) {
                if (data.getBooleanExtra("selectOther", false)) {
                    startPaymentVaultActivity();
                } else if (data.getBooleanExtra("retry", false)) {
                    //TODO mandar a ingrese de nuevo el código de seguridad
                }
            }
        }
        else if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                recoverFromFailure();
            }
            else if(noUserInteractionReached()) {
                setResult(RESULT_OK, data);
                finish();
            }
            else {
                showRegularLayout();
            }
        }
    }

    private boolean noUserInteractionReached() {
        return mSelectedPaymentMethod == null;
    }

    private void showReviewAndConfirm() {
        drawPaymentMethodRow();
        drawPayerCostRow();
        drawTermsAndConditionsText();
        setAmountLabel();
    }

    private void drawPaymentMethodRow() {
        mPaymentMethodLayout.removeAllViewsInLayout();

        setPaymentMethodRowController();

        mPaymentMethodRow.inflateInParent(mPaymentMethodLayout, true);
        mPaymentMethodRow.initializeControls();
        mPaymentMethodRow.drawPaymentMethod();

        if(!isUniquePaymentMethod()) {
            mPaymentMethodRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPaymentMethodEditionRequested = true;
                    startPaymentVaultActivity();
                    animateBackToPaymentVault();
                }
            });
        }
    }

    private void setPaymentMethodRowController() {
        if(MercadoPagoUtil.isCardPaymentType(mSelectedPaymentMethod.getPaymentTypeId())) {
            mPaymentMethodRow = ViewControllerFactory.getPaymentMethodOnEditionViewController(this, mSelectedPaymentMethod, mCreatedToken);
        }
        else {
            PaymentMethodSearchItem item = mPaymentMethodSearch.getSearchItemByPaymentMethod(mSelectedPaymentMethod);
            if(item != null) {
                mPaymentMethodRow = ViewControllerFactory.getPaymentMethodOffEditionViewController(this, item);
            }
            else {
                mPaymentMethodRow = ViewControllerFactory.getPaymentMethodOffEditionViewController(this, mSelectedPaymentMethod);
            }
        }
    }

    private void drawPayerCostRow() {

        mPayerCostLayout.removeAllViewsInLayout();

        if(mSelectedPayerCost != null) {
            mPaymentMethodRow.showSeparator();


            mPayerCostRow = ViewControllerFactory.getPayerCostEditionViewController(this, mCheckoutPreference.getItems().get(0).getCurrencyId());
            mPayerCostRow.inflateInParent(mPayerCostLayout, true);
            mPayerCostRow.initializeControls();
            mPayerCostRow.drawPayerCost(mSelectedPayerCost);
            mPayerCostRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO start payer costs activity y en el onactivityresult hacer:
                    //drawPayerCostRow();
                    //setAmountLabel();

                }
            });
        }
    }

    private void setAmountLabel() {
        mTotalAmountTextView.setText(getAmountLabel());
    }

    private Spanned getAmountLabel() {
        BigDecimal totalAmount = getTotalAmount();
        String currencyId = mCheckoutPreference.getItems().get(0).getCurrencyId();
        return CurrenciesUtil.formatNumber(totalAmount, currencyId, true, true);
    }

    private BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal(0);
        if(mSelectedPayerCost != null)
        {
            amount = amount.add(mSelectedPayerCost.getTotalAmount());
        }
        else {
            amount = mCheckoutPreference.getAmount();
        }
        return amount;
    }

    private void drawTermsAndConditionsText() {
        StringBuilder termsAndConditionsText = new StringBuilder();
        termsAndConditionsText.append(getString(R.string.mpsdk_text_terms_and_conditions_start) + " ");
        termsAndConditionsText.append("<font color='#0066CC'>" + getString(R.string.mpsdk_text_terms_and_conditions_linked) + "</font>");
        termsAndConditionsText.append(" " + getString(R.string.mpsdk_text_terms_and_conditions_end));
        mTermsAndConditionsTextView.setText(Html.fromHtml(termsAndConditionsText.toString()));
    }

    private void animateBackFromPaymentEdition() {
        overridePendingTransition(R.anim.slide_right_to_left_in, R.anim.slide_right_to_left_out);
    }

    private boolean isUniquePaymentMethod() {
        return mPaymentMethodSearch != null && mPaymentMethodSearch.getGroups().size() == 1
                && mPaymentMethodSearch.getGroups().get(0).isPaymentMethod();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void createPayment() {
        LayoutUtil.showProgressLayout(mActivity);
        mMercadoPago.createPayment(mCheckoutPreference.getId(), mCheckoutPreference.getPayer().getEmail(), mSelectedPaymentMethod.getId(), null, null, null, new Callback<Payment>() {
            @Override
            public void success(Payment payment, Response response) {
                /*mCreatedPayment = payment;
                if (MercadoPagoUtil.isCardPaymentType(mSelectedPaymentMethod.getPaymentTypeId())) {
                    startCongratActivity(payment);
                } else {
                    new MercadoPago.StartActivityBuilder()
                            .setPublicKey(mMerchantPublicKey)
                            .setActivity(mActivity)
                            .setPayment(mCreatedPayment)
                            .setPaymentMethod(mSelectedPaymentMethod)
                            .startInstructionsActivity();
                }*/

                ApiUtil.showApiExceptionError(mActivity, null);
            }

            @Override
            public void failure(RetrofitError error) {
                resolvePaymentFailure(error);
            }
        });
    }

    private void startCongratActivity(Payment payment) {
        MercadoPago.StartActivityBuilder builder = new MercadoPago.StartActivityBuilder()
                .setPublicKey(mMerchantPublicKey)
                .setActivity(mActivity)
                .setPayment(payment)
                .setPaymentMethod(mSelectedPaymentMethod);

        builder.startCongratsActivity();
    }

    private void resolvePaymentFailure(RetrofitError error) {
        if(error.getKind() == RetrofitError.Kind.NETWORK) {
            ApiUtil.showApiExceptionError(this, error);
            failureRecovery = new FailureRecovery() {
                @Override
                public void recover() {
                    createPayment();
                }
            };
        }
        else {
            ApiException apiException = ApiUtil.getApiException(error);
            if(apiException.getStatus() == 503) {
                startPaymentInProcessActivity();
            }
        }
    }

    private void startPaymentInProcessActivity() {
        //TODO ver que hacer
    }

    private void animateBackToPaymentVault() {
        overridePendingTransition(R.anim.slide_left_to_right_in, R.anim.slide_left_to_right_out);
    }

    protected void finishWithApiException(Intent data) {
        setResult(RESULT_CANCELED, data);
        finish();
    }

    public void onCancelClicked() {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {

        if(mPaymentMethodSearch == null || isUniquePaymentMethod()) {
            super.onBackPressed();
        }
        else {
            mPaymentMethodEditionRequested = false;
            startPaymentVaultActivity();
        }
        animateBackToPaymentVault();
    }

    private void showProgress() {
        getSupportActionBar().hide();
        LayoutUtil.showProgressLayout(this);
    }

    private void showRegularLayout() {
        getSupportActionBar().show();
        LayoutUtil.showRegularLayout(this);
    }

    private void recoverFromFailure() {
        if(failureRecovery != null) {
            failureRecovery.recover();
        }
    }

}
