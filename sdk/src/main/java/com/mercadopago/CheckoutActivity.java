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
import com.mercadopago.model.PaymentIntent;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.Token;
import com.mercadopago.model.TransactionManager;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.MPButton;
import com.mercadopago.views.MPTextView;
import com.mercadopago.uicontrollers.PaymentMethodViewController;
import com.mercadopago.uicontrollers.ViewControllerFactory;

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

    protected FailureRecovery failureRecovery;

    //Controls
    protected MPTextView mTermsAndConditionsTextView;
    protected MPTextView mCancelTextView;
    protected MPTextView mTotalAmountTextView;
    protected MPButton mPayButton;
    protected View mContentView;
    protected RelativeLayout mPaymentMethodLayout;

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
            ErrorUtil.startErrorActivity(this, mErrorMessage, false);
        }
    }

    private void finishWithErrorMessage() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("error", mErrorMessage);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
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
            finishWithErrorMessage();
        }
    }

    private void initializeCheckout() {
        initializeShoppingCart();
        getPaymentMethodSearch();
    }

    private void initializeShoppingCart() {
        mPurchaseTitle = getPurchaseTitleFromPreference();
        mShoppingCartFragment = ShoppingCartFragment.newInstance(mCheckoutPreference.getItems().get(0).getPictureUrl(), mPurchaseTitle, mCheckoutPreference.getAmount(), mCheckoutPreference.getItems().get(0).getCurrencyId());
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
        else {
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

    }

    protected void startTermsAndConditionsActivity() {
        Intent termsAndConditionsIntent = new Intent(this, TermsAndConditionsActivity.class);
        termsAndConditionsIntent.putExtra("siteId", mCheckoutPreference.getSiteId());
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

    private Spanned getAmountLabel() {
        String currencyId = mCheckoutPreference.getItems().get(0).getCurrencyId();
        return CurrenciesUtil.formatNumber(mCheckoutPreference.getAmount(), currencyId, true, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MercadoPago.PAYMENT_VAULT_REQUEST_CODE) {

            if(resultCode == RESULT_OK) {

                showRegularLayout();

                mSelectedIssuer = (Issuer) data.getSerializableExtra("issuer");

                mSelectedPayerCost = (PayerCost) data.getSerializableExtra("payerCost");

                mCreatedToken = (Token) data.getSerializableExtra("token");

                mSelectedPaymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");

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
        drawTermsAndConditionsText();
        setAmountLabel();
    }

    private void setAmountLabel() {
        mTotalAmountTextView.setText(getAmountLabel());
    }

    private void animateBackFromPaymentEdition() {
        overridePendingTransition(R.anim.slide_right_to_left_in, R.anim.slide_right_to_left_out);
    }

    private void drawTermsAndConditionsText() {
        StringBuilder termsAndConditionsText = new StringBuilder();
        termsAndConditionsText.append(getString(R.string.mpsdk_text_terms_and_conditions_start) + " ");
        termsAndConditionsText.append("<font color='#0066CC'>" + getString(R.string.mpsdk_text_terms_and_conditions_linked) + "</font>");
        termsAndConditionsText.append(" " + getString(R.string.mpsdk_text_terms_and_conditions_end));
        mTermsAndConditionsTextView.setText(Html.fromHtml(termsAndConditionsText.toString()));
    }

    private void drawPaymentMethodRow() {
        mPaymentMethodLayout.removeAllViewsInLayout();
        PaymentMethodSearchItem item = mPaymentMethodSearch.getSearchItemByPaymentMethod(mSelectedPaymentMethod);

        PaymentMethodViewController paymentMethodViewController = ViewControllerFactory.getPaymentMethodEditionViewController(this);

        paymentMethodViewController.inflateInParent(mPaymentMethodLayout, true);
        paymentMethodViewController.initializeControls();

        if(item != null) {
            paymentMethodViewController.drawPaymentMethod(item);
        }
        else {
            paymentMethodViewController.drawPaymentMethod(mSelectedPaymentMethod);
        }
        if(!isUniquePaymentMethod()) {
            paymentMethodViewController.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPaymentMethodEditionRequested = true;
                    startPaymentVaultActivity();
                    animateBackToPaymentVault();
                }
            });
        }
    }

    private boolean isUniquePaymentMethod() {
        return mPaymentMethodSearch.getGroups().size() == 1
                && mPaymentMethodSearch.getGroups().get(0).isPaymentMethod();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void createPayment() {
        LayoutUtil.showProgressLayout(mActivity);

        PaymentIntent paymentIntent = new PaymentIntent();
        paymentIntent.setPrefId(mCheckoutPreference.getId());
        paymentIntent.setPublicKey(mMerchantPublicKey);
        paymentIntent.setPaymentMethodId(mSelectedPaymentMethod.getId());
        paymentIntent.setEmail(mCheckoutPreference.getPayer().getEmail());
        paymentIntent.setTransactionId(TransactionManager.getInstance().getTransactionId());

        mMercadoPago.createPayment(paymentIntent, new Callback<Payment>() {
            @Override
            public void success(Payment payment, Response response) {
                mCreatedPayment = payment;
                if (MercadoPagoUtil.isCardPaymentType(mSelectedPaymentMethod.getPaymentTypeId())) {
                    new MercadoPago.StartActivityBuilder()
                            .setActivity(mActivity)
                            .setPayment(mCreatedPayment)
                            .setPaymentMethod(mSelectedPaymentMethod)
                            .startCongratsActivity();
                } else {
                    new MercadoPago.StartActivityBuilder()
                            .setPublicKey(mMerchantPublicKey)
                            .setActivity(mActivity)
                            .setPayment(mCreatedPayment)
                            .setPaymentMethod(mSelectedPaymentMethod)
                            .startInstructionsActivity();
                }

                TransactionManager.getInstance().releaseTransaction();
            }

            @Override
            public void failure(RetrofitError error) {
                resolvePaymentFailure(error);
            }
        });
    }

    private void resolvePaymentFailure(RetrofitError error) {
        //TODO analizar y ordenar
        ApiException apiException = ApiUtil.getApiException(error);

        if(error.getResponse().getStatus() == 408) {
            //Request timeout
            ApiUtil.showApiExceptionError(this, error);
            failureRecovery = new FailureRecovery() {
                @Override
                public void recover() {
                    createPayment();
                }
            };
        }
        else if(apiException != null && apiException.getStatus() == 503) {
            startPaymentInProcessActivity();
            TransactionManager.getInstance().releaseTransaction();
        }
        else if(apiException != null) { //Any other failure from wrapper
            //Request timeout
            ApiUtil.showApiExceptionError(this, error);
            failureRecovery = new FailureRecovery() {
                @Override
                public void recover() {
                    createPayment();
                }
            };
        }
    }

    private void startPaymentInProcessActivity() {
        //TODO start in process activity
    }

    private void animateBackToPaymentVault() {
        overridePendingTransition(R.anim.slide_left_to_right_in, R.anim.silde_left_to_right_out);
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

        if(isUniquePaymentMethod()) {
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
