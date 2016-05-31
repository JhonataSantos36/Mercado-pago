package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.exceptions.CheckoutPreferenceException;
import com.mercadopago.exceptions.ExceptionHandler;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.fragments.ShoppingCartFragment;
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

import java.util.Calendar;

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
    protected boolean mActiveActivity;

    protected PaymentMethodSearch mPaymentMethodSearch;

    protected Long mTransactionId;
    protected PaymentMethod mSelectedPaymentMethod;
    protected Issuer mSelectedIssuer;
    protected PayerCost mSelectedPayerCost;
    protected Token mCreatedToken;
    protected Payment mCreatedPayment;

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
    protected Boolean mBackPressedOnce;
    protected Snackbar mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        initializeToolbar();
        getActivityParameters();
        mBackPressedOnce = false;
        mActiveActivity = true;
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
                if(mActiveActivity) {
                    try {
                        validatePreference();
                        initializeCheckout();
                    } catch (CheckoutPreferenceException e) {
                        String errorMessage = ExceptionHandler.getErrorMessage(mActivity, e);
                        ErrorUtil.startErrorActivity(mActivity, errorMessage, false);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if(mActiveActivity) {
                    ApiUtil.showApiExceptionError(mActivity, error);
                    failureRecovery = new FailureRecovery() {
                        @Override
                        public void recover() {
                            getCheckoutPreference();
                        }
                    };
                }
            }
        });
    }

    private void validatePreference() throws CheckoutPreferenceException {
        mCheckoutPreference.validate();
        if(!mCheckoutPreference.getId().equals(mCheckoutPreferenceId)) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.PREF_ID_NOT_MATCHING_REQUESTED);
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
                .commitAllowingStateLoss();
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
        if(isEmpty(mMerchantPublicKey)) {
            throw new IllegalStateException("public key not set");
        }
        else if (isEmpty(mCheckoutPreferenceId)) {
            throw new IllegalStateException("preference id not set");
        }
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
        mTotalAmountTextView = (MPTextView) findViewById(R.id.totalAmountText);
        mContentView = findViewById(R.id.contentLayout);
        mPaymentMethodLayout = (RelativeLayout) findViewById(R.id.paymentMethodLayout);
        mPayerCostLayout = (RelativeLayout) findViewById(R.id.payerCostLayout);
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

        showProgress();
        mMercadoPago.getPaymentMethodSearch(mCheckoutPreference.getAmount(), mCheckoutPreference.getExcludedPaymentTypes(), mCheckoutPreference.getExcludedPaymentMethods(), new Callback<PaymentMethodSearch>() {
            @Override
            public void success(PaymentMethodSearch paymentMethodSearch, Response response) {
                mPaymentMethodSearch = paymentMethodSearch;
                if (mActiveActivity) {
                    startPaymentVaultActivity();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (mActiveActivity) {
                    failureRecovery = new FailureRecovery() {
                        @Override
                        public void recover() {
                            getPaymentMethodSearch();
                        }
                    };
                    ApiUtil.showApiExceptionError(mActivity, error);
                }
            }
        });
    }

    protected void startPaymentVaultActivity() {

        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mMerchantPublicKey)
                .setCurrency(mCheckoutPreference.getItems().get(0).getCurrencyId())
                .setAmount(mCheckoutPreference.getAmount())
                .setPaymentMethodSearch(mPaymentMethodSearch)
                .setPaymentPreference(mCheckoutPreference.getPaymentPreference())
                .startPaymentVaultActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MercadoPago.PAYMENT_VAULT_REQUEST_CODE) {
            resolvePaymentVaultRequest(resultCode, data);
        }
        else if (requestCode == MercadoPago.INSTRUCTIONS_REQUEST_CODE) {
            finishWithPaymentResult();
        }
        else if (requestCode == MercadoPago.CONGRATS_REQUEST_CODE) {
            resolveCongratsRequest(resultCode, data);
        }
        else if (requestCode == MercadoPago.INSTALLMENTS_REQUEST_CODE) {
            resolveInstallmentsRequest(resultCode, data);
        }
        else if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            resolveErrorRequest(resultCode, data);
        }
    }


    private void resolvePaymentVaultRequest(int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            mSelectedIssuer = (Issuer) data.getSerializableExtra("issuer");
            mSelectedPayerCost = (PayerCost) data.getSerializableExtra("payerCost");
            mCreatedToken = (Token) data.getSerializableExtra("token");
            mSelectedPaymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");
            showReviewAndConfirm();
            showRegularLayout();
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

    private void resolveCongratsRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED && data != null) {
            if (data.getBooleanExtra("selectOther", false)) {
                startPaymentVaultActivity();
            } else if (data.getBooleanExtra("retry", false)) {
                //TODO mandar a ingrese de nuevo el código de seguridad
                startPaymentVaultActivity();
            }
        }
        else {
            finishWithPaymentResult();
        }
    }

    private void resolveErrorRequest(int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            recoverFromFailure();
        }
        else if(noUserInteractionReached()) {
            setResult(RESULT_CANCELED, data);
            finish();
        }
        else {
            showRegularLayout();
        }
    }
    private boolean noUserInteractionReached() {
        return mSelectedPaymentMethod == null;
    }

    protected void resolveInstallmentsRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            mSelectedPayerCost = (PayerCost) bundle.getSerializable("payerCost");
            drawPayerCostRow();
            setAmountLabel();
        } else if (resultCode == RESULT_CANCELED) {
            finish();
        }
        overridePendingTransition(R.anim.slide_left_to_right_in, R.anim.slide_left_to_right_out);
    }

    private void finishWithPaymentResult() {
        Intent paymentResultIntent = new Intent();
        paymentResultIntent.putExtra("payment", mCreatedPayment);
        setResult(RESULT_OK, paymentResultIntent);
        finish();
    }

    private void showReviewAndConfirm() {
        drawPaymentMethodRow();
        drawPayerCostRow();
        drawTermsAndConditionsText();
        setAmountLabel();
    }

    private void drawPaymentMethodRow() {
        mPaymentMethodLayout.removeAllViews();
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
                    overridePendingTransition(R.anim.slide_right_to_left_in, R.anim.slide_right_to_left_out);
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

        mPayerCostLayout.removeAllViews();

        if(mSelectedPayerCost != null && mCheckoutPreference != null) {
            mPaymentMethodRow.showSeparator();

            mPayerCostRow = ViewControllerFactory.getPayerCostEditionViewController(this, mCheckoutPreference.getItems().get(0).getCurrencyId());
            mPayerCostRow.inflateInParent(mPayerCostLayout, true);
            mPayerCostRow.initializeControls();
            mPayerCostRow.drawPayerCost(mSelectedPayerCost);
            mPayerCostRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startInstallmentsActivity();
                }
            });
        }
    }

    public void startInstallmentsActivity() {
        new MercadoPago.StartActivityBuilder()
                .setActivity(mActivity)
                .setPublicKey(mMerchantPublicKey)
                .setPaymentMethod(mSelectedPaymentMethod)
                .setAmount(mCheckoutPreference.getAmount())
                .setToken(mCreatedToken)
                .setIssuer(mSelectedIssuer)
                .setCurrency(mCheckoutPreference.getItems().get(0).getCurrencyId())
                .startCardInstallmentsActivity();
        overridePendingTransition(R.anim.slide_right_to_left_in, R.anim.slide_right_to_left_out);
    }

    private void setAmountLabel() {
        mTotalAmountTextView.setText(getAmountLabel());
    }

    private Spanned getAmountLabel() {
        BigDecimal totalAmount = getTotalAmount();
        String currencyId = mCheckoutPreference.getItems().get(0).getCurrencyId();
        String amountText = CurrenciesUtil.formatNumber(totalAmount, currencyId);

        StringBuilder totalAmountTextBuilder = new StringBuilder();
        totalAmountTextBuilder.append(getString(R.string.mpsdk_payment_amount_to_pay));
        totalAmountTextBuilder.append(" ");
        totalAmountTextBuilder.append(amountText);

        return CurrenciesUtil.formatCurrencyInText(totalAmount, currencyId, totalAmountTextBuilder.toString(), true, true);
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
        overridePendingTransition(R.anim.slide_left_to_right_in, R.anim.slide_left_to_right_out);
    }

    private boolean isUniquePaymentMethod() {
        return mPaymentMethodSearch != null && mPaymentMethodSearch.getGroups().size() == 1
                && mPaymentMethodSearch.getGroups().get(0).isPaymentMethod();
    }

    @Override
    protected void onResume() {
        mActiveActivity = true;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mActiveActivity = false;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mActiveActivity = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        mActiveActivity = false;
        super.onStop();
    }

    protected void createPayment() {
        LayoutUtil.showProgressLayout(mActivity);

        PaymentIntent paymentIntent = createPaymentIntent();

        mMercadoPago.createPayment(paymentIntent, new Callback<Payment>() {
            @Override
            public void success(Payment payment, Response response) {
                mCreatedPayment = payment;
                if (MercadoPagoUtil.isCardPaymentType(mSelectedPaymentMethod.getPaymentTypeId())) {
                    startCongratsActivity();
                } else {
                    startInstructionsActivity();
                }
                cleanTransactionId();
            }

            @Override
            public void failure(RetrofitError error) {
                resolvePaymentFailure(error);
            }
        });
    }

    private void startInstructionsActivity() {
        new MercadoPago.StartActivityBuilder()
                .setPublicKey(mMerchantPublicKey)
                .setActivity(mActivity)
                .setPayment(mCreatedPayment)
                .setPaymentMethod(mSelectedPaymentMethod)
                .startInstructionsActivity();
    }


    private PaymentIntent createPaymentIntent() {
        PaymentIntent paymentIntent = new PaymentIntent();
        paymentIntent.setPrefId(mCheckoutPreference.getId());
        paymentIntent.setPublicKey(mMerchantPublicKey);
        paymentIntent.setPaymentMethodId(mSelectedPaymentMethod.getId());
        paymentIntent.setEmail(mCheckoutPreference.getPayer().getEmail());

        if(mCreatedToken != null) {
            paymentIntent.setTokenId(mCreatedToken.getId());
        }
        if(mSelectedPayerCost != null)  {
            paymentIntent.setInstallments(mSelectedPayerCost.getInstallments());
        }
        if(mSelectedIssuer != null) {
            paymentIntent.setIssuerId(mSelectedIssuer.getId());
        }

        if(!existsTransactionId() || !MercadoPagoUtil.isCardPaymentType(mSelectedPaymentMethod.getPaymentTypeId())) {
            mTransactionId = createNewTransactionId();
        }

        paymentIntent.setTransactionId(mTransactionId);
        return paymentIntent;
    }

    private void startCongratsActivity(){
        new MercadoPago.StartActivityBuilder()
            .setPublicKey(mMerchantPublicKey)
            .setActivity(mActivity)
            .setPayment(mCreatedPayment)
            .setPaymentMethod(mSelectedPaymentMethod)
            .startCongratsActivity();
    }

    private Long createNewTransactionId() {
        return Calendar.getInstance().getTimeInMillis() + Math.round(Math.random()) * Math.round(Math.random());
    }

    private boolean existsTransactionId() {
        return mTransactionId != null;
    }

    private void cleanTransactionId() {
        mTransactionId = null;
    }

    private void resolvePaymentFailure(RetrofitError error) {
//        //TODO analizar y ordenar
//        ApiException apiException = ApiUtil.getApiException(error);
//
//        if(error.getResponse() != null && error.getResponse().getStatus() == 408) {
//            //Request timeout
//            ApiUtil.showApiExceptionError(this, error);
//            failureRecovery = new FailureRecovery() {
//                @Override
//                public void recover() {
//                    createPayment();
//                }
//            };
//        }
//        else if(apiException != null && apiException.getStatus() == 503) {
//            //Payment in process
//            startPaymentInProcessActivity();
//            cleanTransactionId();
//        }
//        else if(apiException != null) {
//            MPException mpException = new MPException(apiException);
//            ErrorUtil.startErrorActivity(this, mpException);
//        }
        Toast.makeText(this, "Payments API Exception: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        showRegularLayout();
    }

    private void startPaymentInProcessActivity() {
        //TODO start in process activity
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
            onCancelClicked();
        }
        else if(mBackPressedOnce){
            mSnackbar.dismiss();
            mPaymentMethodEditionRequested = false;
            startPaymentVaultActivity();
            animateBackToPaymentVault();
        }
        else {
            mSnackbar = Snackbar.make(mPaymentMethodLayout, getString(R.string.mpsdk_press_again_confirm), Snackbar.LENGTH_LONG);
            mSnackbar.show();
            mBackPressedOnce = true;
            resetBackPressedOnceIn(4000);
        }
    }

    private void animateBackToPaymentVault() {
        overridePendingTransition(R.anim.slide_left_to_right_in, R.anim.slide_left_to_right_out);
    }

    private void showProgress() {
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        LayoutUtil.showProgressLayout(this);
    }

    private void showRegularLayout() {
        if(getSupportActionBar() != null) {
            getSupportActionBar().show();
        }
        LayoutUtil.showRegularLayout(this);
    }

    private void recoverFromFailure() {
        if (failureRecovery != null) {
            failureRecovery.recover();
        }
    }

    private void resetBackPressedOnceIn(final int mills) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(mills);
                    mBackPressedOnce = false;
                } catch (InterruptedException e) {
                    //Do nothing
                }
            }
        }).start();
    }
}
