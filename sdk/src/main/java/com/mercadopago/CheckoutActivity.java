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
import android.widget.TextView;

import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.exceptions.CheckoutPreferenceException;
import com.mercadopago.exceptions.ExceptionHandler;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.fragments.ShoppingCartFragment;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.CheckoutPreference;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Item;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentIntent;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.mptracker.delegate.MPTrackerDelegate;
import com.mercadopago.mptracker.informer.MPPaymentTrackInformer;
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
    protected Site mSite;

    protected String mPurchaseTitle;
    protected ShoppingCartFragment mShoppingCartFragment;
    protected String mErrorMessage;

    protected boolean mPaymentMethodEditionRequested;

    protected PaymentMethodViewController mPaymentMethodRow;
    protected PayerCostViewController mPayerCostRow;
    protected FailureRecovery failureRecovery;

    //Controls
    protected Toolbar mToolbar;
    protected MPTextView mTermsAndConditionsTextView;
    protected MPTextView mCancelTextView;
    protected MPTextView mTotalAmountTextView;
    protected MPButton mPayButton;
    protected View mContentView;
    protected RelativeLayout mPaymentMethodLayout;
    protected RelativeLayout mPayerCostLayout;
    protected Boolean mBackPressedOnce;
    protected Snackbar mSnackbar;

    protected MPTrackerDelegate mTrackerDelegate;
    protected DecorationPreference mDecorationPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityParameters();
        if(mDecorationPreference != null && mDecorationPreference.hasColors()) {
            setTheme(R.style.Theme_MercadoPagoTheme_NoActionBar);
        }
        setContentView(R.layout.activity_checkout);
        initializeToolbar();
        mBackPressedOnce = false;
        mActiveActivity = true;
        boolean validState = true;

        //TODO validate
        createMPTrackerDelegate();
        MPTracker.getInstance().trackEvent("CHECKOUT", "INIT_CHECKOUT", "3", mTrackerDelegate, this);

        try{
            validateParameters();
        } catch (Exception e) {
            mErrorMessage = e.getMessage();
            validState = false;
        }
        if(validState) {
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


    private void createMPTrackerDelegate() {
        mTrackerDelegate = new MPTrackerDelegate() {
            @Override
            public String getPublicKey() {
                return mMerchantPublicKey;
            }

            @Override
            public String getSdkVersion() {
                //TODO que sea constante
                return "1.0";
            }

            @Override
            public String getSite() {
                //TODO que sea contante
                return "MLA";
            }
        };
    }

    private void getCheckoutPreference() {
        showProgress();
        mMercadoPago.getPreference(mCheckoutPreferenceId, new Callback<CheckoutPreference>() {
            @Override
            public void success(CheckoutPreference checkoutPreference) {
                mCheckoutPreference = checkoutPreference;
                if (mActiveActivity) {
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
            public void failure(ApiException apiException) {
                if (mActiveActivity) {
                    ApiUtil.showApiExceptionError(mActivity, apiException);
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
        mSite = new Site(mCheckoutPreference.getSiteId(), mCheckoutPreference.getItems().get(0).getCurrencyId());
        initializeShoppingCart();
        getPaymentMethodSearch();
    }

    private void initializeShoppingCart() {
        mPurchaseTitle = getPurchaseTitleFromPreference();
        String currencyId = mCheckoutPreference.getItems().get(0).getCurrencyId();
        String pictureUrl = mCheckoutPreference.getItems().get(0).getPictureUrl();

        if(mDecorationPreference != null && mDecorationPreference.hasColors()) {
            mShoppingCartFragment = ShoppingCartFragment.newInstance(pictureUrl, mPurchaseTitle, mCheckoutPreference.getAmount(), currencyId, mDecorationPreference);
        }
        else {
            mShoppingCartFragment = ShoppingCartFragment.newInstance(pictureUrl, mPurchaseTitle, mCheckoutPreference.getAmount(), currencyId);
        }
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
        if(this.getIntent().getSerializableExtra("decorationPreference") != null) {
            mDecorationPreference = (DecorationPreference) this.getIntent().getSerializableExtra("decorationPreference");
        }
    }

    private void initializeToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if(mDecorationPreference != null) {
            if(mDecorationPreference.hasColors()) {
                mToolbar.setBackgroundColor(mDecorationPreference.getBaseColor());
            }
            if(mDecorationPreference.isDarkFontEnabled()) {
                TextView title = (TextView) findViewById(R.id.title);
                title.setTextColor(mDecorationPreference.getDarkFontColor(this));
            }
        }
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

        if(mDecorationPreference != null) {
            if(mDecorationPreference.hasColors()) {
                mPayButton.setBackgroundColor(mDecorationPreference.getBaseColor());
            }
            if(mDecorationPreference.isDarkFontEnabled()) {
                mPayButton.setTextColor(mDecorationPreference.getDarkFontColor(this));
            }
        }

        mTotalAmountTextView = (MPTextView) findViewById(R.id.totalAmountText);
        mContentView = findViewById(R.id.contentLayout);
        mPaymentMethodLayout = (RelativeLayout) findViewById(R.id.paymentMethodLayout);
        mPayerCostLayout = (RelativeLayout) findViewById(R.id.payerCostLayout);
    }

    protected void startTermsAndConditionsActivity() {
        Intent termsAndConditionsIntent = new Intent(this, TermsAndConditionsActivity.class);
        termsAndConditionsIntent.putExtra("siteId", mCheckoutPreference.getSiteId());
        termsAndConditionsIntent.putExtra("decorationPreference", mDecorationPreference);
        //TODO validate
        MPTracker.getInstance().trackEvent("CHECKOUT","TERMS_AND_CONDITIONS","3",mTrackerDelegate,this);

        startActivity(termsAndConditionsIntent);
    }

    protected void setActivity() {
        this.mActivity = this;
    }

    protected void getPaymentMethodSearch() {

        showProgress();
        mMercadoPago.getPaymentMethodSearch(mCheckoutPreference.getAmount(), mCheckoutPreference.getExcludedPaymentTypes(), mCheckoutPreference.getExcludedPaymentMethods(), new Callback<PaymentMethodSearch>() {
            @Override
            public void success(PaymentMethodSearch paymentMethodSearch) {
                mPaymentMethodSearch = paymentMethodSearch;

                //TODO validate
                MPTracker.getInstance().trackEvent("CHECKOUT", "GET_PAYMENT_METHOD_SEARCH", "SUCCESS", "3", mTrackerDelegate, mActivity);

                if (mActiveActivity) {
                    startPaymentVaultActivity();
                }
            }

            @Override
            public void failure(ApiException apiException) {
                if (mActiveActivity) {
                    failureRecovery = new FailureRecovery() {
                        @Override
                        public void recover() {
                            getPaymentMethodSearch();
                        }
                    };
                    ApiUtil.showApiExceptionError(mActivity, apiException);
                }
            }
        });
    }

    protected void startPaymentVaultActivity() {

        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mMerchantPublicKey)
                .setSite(mSite)
                .setAmount(mCheckoutPreference.getAmount())
                .setPaymentMethodSearch(mPaymentMethodSearch)
                .setPaymentPreference(mCheckoutPreference.getPaymentPreference())
                .setDecorationPreference(mDecorationPreference)
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

            //TODO Tracke, delete TODO
            if(mCreatedToken != null) {
                MPTracker.getInstance().trackToken(mCreatedToken.getId(), "3", mTrackerDelegate, this);
            }
            showReviewAndConfirm();
            showRegularLayout();
        }
        else if (resultCode == RESULT_CANCELED) {
            if(!mPaymentMethodEditionRequested) {
                Intent returnIntent = new Intent();

                //TODO validate
                MPTracker.getInstance().trackEvent("CHECKOUT","CANCELED","3",mTrackerDelegate,this);

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
                .setSite(mSite)
                .setDecorationPreference(mDecorationPreference)
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
            public void success(Payment payment) {
                mCreatedPayment = payment;

                //TODO validate
                MPTracker.getInstance().trackPayment("CHECKOUT", "CREATE_PAYMENT", "3", mTrackerDelegate, createMPTrackerInformer(), mActivity);

                if (MercadoPagoUtil.isCardPaymentType(mSelectedPaymentMethod.getPaymentTypeId())) {
                    startCongratsActivity();
                } else {
                    startInstructionsActivity();
                }
                cleanTransactionId();
            }

            @Override
            public void failure(ApiException apiException) {
                resolvePaymentFailure(apiException);
            }
        });
    }

    private MPPaymentTrackInformer createMPTrackerInformer(){
        return new MPPaymentTrackInformer() {
            @Override
            public String getPaymentMethodId() {
                return mCreatedPayment.getPaymentMethodId();
            }

            @Override
            public String getStatus() {
                return mCreatedPayment.getStatus();
            }

            @Override
            public String getStatusDetail() {
                return mCreatedPayment.getStatusDetail();
            }

            @Override
            public String getTypeId() {
                return mCreatedPayment.getPaymentTypeId();
            }

            @Override
            public Integer getInstallments() {
                return mCreatedPayment.getInstallments();
            }

            @Override
            public Integer getIssuerId() {
                return mCreatedPayment.getIssuerId();
            }
        };
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

    private void resolvePaymentFailure(ApiException apiException) {
        //TODO REVISAR
        if(apiException.getStatus() != null) {
            String serverErrorFirstDigit = String.valueOf(ApiUtil.StatusCodes.INTERNAL_SERVER_ERROR).substring(0, 1);
            if (String.valueOf(apiException.getStatus()).startsWith(serverErrorFirstDigit)
                    && apiException.getStatus() != ApiUtil.StatusCodes.PROCESSING) {
                ApiUtil.showApiExceptionError(this, apiException);
                failureRecovery = new FailureRecovery() {
                    @Override
                    public void recover() {
                        createPayment();
                    }
                };
            } else if (apiException.getStatus() == ApiUtil.StatusCodes.PROCESSING) {
                startPaymentInProcessActivity();
                cleanTransactionId();
            }
            else if (apiException.getStatus() == ApiUtil.StatusCodes.BAD_REQUEST) {
                MPException mpException = new MPException(apiException);
                ErrorUtil.startErrorActivity(this, mpException);
            }
            else {
                ApiUtil.showApiExceptionError(this, apiException);
            }
        }
        else {
            ApiUtil.showApiExceptionError(this, apiException);
        }
        showRegularLayout();
    }

    private void startPaymentInProcessActivity() {
        mCreatedPayment = new Payment();
        mCreatedPayment.setStatus(Payment.StatusCodes.STATUS_IN_PROCESS);
        mCreatedPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_PENDING_CONTINGENCY);
        startCongratsActivity();
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
        else if(mBackPressedOnce) {
            //TODO validate
            MPTracker.getInstance().trackEvent("CHECKOUT","BACK_PRESSED","3",mTrackerDelegate,this);

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
