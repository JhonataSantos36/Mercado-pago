package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mercadopago.core.MercadoPago;
import com.mercadopago.exceptions.CheckoutPreferenceException;
import com.mercadopago.exceptions.ExceptionHandler;
import com.mercadopago.fragments.ShoppingCartFragment;
import com.mercadopago.model.CheckoutPreference;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Item;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodPreference;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.MPButton;
import com.mercadopago.views.MPTextView;

import java.math.BigDecimal;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CheckoutActivity extends AppCompatActivity {

    //Parameters
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

    //Controls
    protected AppBarLayout mAppBar;
    protected MPTextView mPaymentMethodDescriptionTextView;
    protected MPTextView mPaymentMethodCommentTextView;
    protected MPTextView mTermsAndConditionsTextView;
    protected MPTextView mCancelTextView;
    protected MPTextView mTotalAmountTextView;
    protected ImageView mPaymentMethodImageView;
    protected ImageView mEditPaymentMethodImageView;
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
        }
        catch(CheckoutPreferenceException e){
            mErrorMessage = ExceptionHandler.getErrorMessage(this, e);
            validState = false;
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

            getPaymentMethodSearch();
        }
        else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("error", mErrorMessage);
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    private void getActivityParameters() {
        mCheckoutPreference = (CheckoutPreference) this.getIntent().getSerializableExtra("checkoutPreference");
        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");
        mShowBankDeals = this.getIntent().getBooleanExtra("showBankDeals", true);
    }

    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void validateParameters() throws Exception {
        if(!validParameters()) {
            throw new IllegalStateException("Invalid parameters");
        }
        else {
            mCheckoutPreference.validate();
        }
    }

    protected boolean validParameters() {

        return (mMerchantPublicKey != null) && (mCheckoutPreference != null);
    }

    private void initializeActivityControls() {
        mPaymentMethodDescriptionTextView = (MPTextView) findViewById(R.id.paymentMethodDescription);
        mPaymentMethodCommentTextView = (MPTextView) findViewById(R.id.paymentMethodComment);
        mPaymentMethodImageView = (ImageView) findViewById(R.id.paymentMethodImage);
        mEditPaymentMethodImageView = (ImageView) findViewById(R.id.imageEdit);
        mEditPaymentMethodImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPaymentMethodEditionRequested = true;
                startPaymentVaultActivity();
                animateBackToPaymentVault();
            }
        });
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
        mPurchaseTitle = getPurchaseTitleFromPreference();
        mContentView = findViewById(R.id.contentLayout);
        mAppBar = (AppBarLayout) findViewById(R.id.appBar);
        mPaymentMethodLayout = (RelativeLayout) findViewById(R.id.paymentMethodLayout);

        mShoppingCartFragment = ShoppingCartFragment.newInstance(mCheckoutPreference.getItems().get(0).getPictureUrl(), mPurchaseTitle, mCheckoutPreference.getAmount(), mCheckoutPreference.getItems().get(0).getCurrencyId());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.shoppingCartFragment, mShoppingCartFragment)
                .show(mShoppingCartFragment)
                .commit();
    }

    protected void startTermsAndConditionsActivity() {
        Intent termsAndConditionsIntent = new Intent(this, TermsAndConditionsActivity.class);
        startActivity(termsAndConditionsIntent);
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

    protected void setActivity() {
        this.mActivity = this;
    }

    protected void getPaymentMethodSearch() {

        showProgress();
        mMercadoPago.getPaymentMethodSearch(mCheckoutPreference.getAmount(), mCheckoutPreference.getExcludedPaymentTypes(), mCheckoutPreference.getExcludedPaymentMethods(), new Callback<PaymentMethodSearch>() {
            @Override
            public void success(PaymentMethodSearch paymentMethodSearch, Response response) {
                mPaymentMethodSearch = paymentMethodSearch;
                startPaymentVaultActivity();
            }

            @Override
            public void failure(RetrofitError error) {
                ApiUtil.finishWithApiException(mActivity, error);
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
                .setDefaultPaymentMethodId(mCheckoutPreference.getDefaultPaymentMethodId())
                .setExcludedPaymentMethodIds(mCheckoutPreference.getExcludedPaymentMethods())
                .setExcludedPaymentTypes(mCheckoutPreference.getExcludedPaymentTypes())
                .setDefaultInstallments(mCheckoutPreference.getDefaultInstallments())
                .setMaxInstallments(mCheckoutPreference.getMaxInstallments())
                .setPaymentMethodSearch(mPaymentMethodSearch)
                .startPaymentVaultActivity();
    }

    private Spanned getAmountLabel() {
        String currencyId = mCheckoutPreference.getItems().get(0).getCurrencyId();
        return CurrenciesUtil.formatNumber(mCheckoutPreference.getAmount(), currencyId, true, true);
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
        PaymentMethodSearchItem item = mPaymentMethodSearch.getSearchItemByPaymentMethod(mSelectedPaymentMethod);

        if(item != null ) {
            String paymentMethodComment;
            if (item.hasComment()) {
                paymentMethodComment = item.getComment();
            } else {
                paymentMethodComment = MercadoPagoUtil.getAccreditationTimeMessage(mSelectedPaymentMethod.getAccreditationTime(), this);
            }
            mPaymentMethodCommentTextView.setText(paymentMethodComment);
            if (item.hasDescription()) {
                mPaymentMethodDescriptionTextView.setText(item.getDescription());
            } else {
                mPaymentMethodDescriptionTextView.setText("");
            }
            if (item.isIconRecommended()) {
                int resourceId = MercadoPagoUtil.getPaymentMethodSearchItemIcon(this, item.getId());
                if (resourceId != 0) {
                    mPaymentMethodImageView.setImageResource(resourceId);
                }
            }
        }
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
            }

            @Override
            public void failure(RetrofitError error) {
                //TODO cambiar esto cuando vuelva a andar el servicio de payments ApiUtil.finishWithApiException(mActivity, error);
                Payment payment = new Payment();
                payment.setId((long) 919191);
                payment.setCurrencyId("ARS");
                payment.setTransactionAmount(new BigDecimal("1000"));
                new MercadoPago.StartActivityBuilder()
                        .setPublicKey(mMerchantPublicKey)
                        .setActivity(mActivity)
                        .setPayment(payment)
                        .setPaymentMethod(mSelectedPaymentMethod)
                        .startInstructionsActivity();
            }
        });
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
        mPaymentMethodEditionRequested = false;
        startPaymentVaultActivity();
        animateBackToPaymentVault();
    }

}
