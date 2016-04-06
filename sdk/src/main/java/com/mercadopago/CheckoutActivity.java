package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;

import com.mercadopago.controllers.ShoppingCartViewController;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.exceptions.CheckoutPreferenceException;
import com.mercadopago.exceptions.ExceptionHandler;
import com.mercadopago.model.CheckoutPreference;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Item;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.MPButton;
import com.mercadopago.views.MPTextView;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CheckoutActivity extends AppCompatActivity {

    protected static final Integer PURCHASE_TITLE_MAX_LENGTH = 50;

    //Parameters
    protected CheckoutPreference mCheckoutPreference;
    protected String mMerchantPublicKey;
    protected Boolean mShowBankDeals;

    //Local vars
    protected MercadoPago mMercadoPago;
    protected Activity mActivity;
    protected boolean mSupportMPApp = true;
    protected PaymentMethod mSelectedPaymentMethod;
    protected Issuer mSelectedIssuer;
    protected PayerCost mSelectedPayerCost;
    protected Token mCreatedToken;
    protected String mPurchaseTitle;
    protected ShoppingCartViewController mShoppingCartController;
    protected String mErrorMessage;
    protected boolean mPaymentMethodEditionRequested;

    //Controls
    protected MPTextView mPaymentMethodCommentTextView;
    protected MPTextView mTermsAndConditionsTextView;
    protected MPTextView mTotalAmountTextView;
    protected ImageView mPaymentMethodImageView;
    protected ImageView mEditPaymentMethodImageView;
    protected MPButton mPayButton;
    protected ImageView mShoppingCartIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        initializeToolbar();

        mCheckoutPreference = (CheckoutPreference) this.getIntent().getSerializableExtra("checkoutPreference");
        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");
        mShowBankDeals = this.getIntent().getBooleanExtra("showBankDeals", true);

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
            startPaymentVaultActivity();
        }
        else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("error", mErrorMessage);
            setResult(RESULT_CANCELED, returnIntent);
            finish();
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
                finish();
            }
        });
    }

    private void validateParameters() throws Exception {
        if(!validParameters()) {
            throw new IllegalStateException("Invalid parameters");
        }
        else {
            mCheckoutPreference.validate();
        }
    }

    private void initializeActivityControls() {
        mPaymentMethodCommentTextView = (MPTextView) findViewById(R.id.payment_method_comment);
        mPaymentMethodImageView = (ImageView) findViewById(R.id.payment_method_image);
        mEditPaymentMethodImageView = (ImageView) findViewById(R.id.imageEdit);
        mEditPaymentMethodImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPaymentMethodEditionRequested = true;
                startPaymentVaultActivity();
                animateBack();
            }
        });
        mTermsAndConditionsTextView = (MPTextView) findViewById(R.id.termsAndConditions);
        mTermsAndConditionsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTermsAndConditionsActivity();
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
        mShoppingCartIcon = (ImageView) findViewById(R.id.shoppingCartIcon);

        mShoppingCartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShoppingCartController.toggle(true);
            }
        });

        mPurchaseTitle = getPurchaseTitle();
        mShoppingCartController = new ShoppingCartViewController(this, mShoppingCartIcon, mCheckoutPreference.getItems().get(0).getPictureUrl(), mPurchaseTitle, PURCHASE_TITLE_MAX_LENGTH,
                mCheckoutPreference.getAmount(), mCheckoutPreference.getItems().get(0).getCurrencyId(), true, findViewById(R.id.contentLayout));
    }

    protected void startTermsAndConditionsActivity() {
        Intent termsAndConditionsIntent = new Intent(this, TermsAndConditionsActivity.class);
        startActivity(termsAndConditionsIntent);
    }

    private String getPurchaseTitle() {
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

    protected void startPaymentVaultActivity() {

        MercadoPago.StartActivityBuilder builder = new MercadoPago.StartActivityBuilder();
        builder.setActivity(this);
        builder.setPublicKey(mMerchantPublicKey);
        builder.setSupportMPApp(mSupportMPApp);
        builder.setItemImageUri(mCheckoutPreference.getItems().get(0).getPictureUrl());
        builder.setPurchaseTitle(mPurchaseTitle);
        builder.setCurrency(mCheckoutPreference.getItems().get(0).getCurrencyId());
        builder.setAmount(mCheckoutPreference.getAmount());
        builder.setShowBankDeals(mShowBankDeals);
        builder.setDefaultPaymentMethodId(mCheckoutPreference.getDefaultPaymentMethodId());
        builder.setExcludedPaymentMethodIds(mCheckoutPreference.getExcludedPaymentMethods());
        builder.setExcludedPaymentTypes(mCheckoutPreference.getExcludedPaymentTypes());
        builder.setDefaultInstallments(mCheckoutPreference.getDefaultInstallments());
        builder.setMaxInstallments(mCheckoutPreference.getMaxInstallments());
        builder.startPaymentVaultActivity();
    }

    private Spanned getAmountLabel() {
        String currencyId = mCheckoutPreference.getItems().get(0).getCurrencyId();
        return CurrenciesUtil.formatNumber(mCheckoutPreference.getAmount(), currencyId, true, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MercadoPago.PAYMENT_VAULT_REQUEST_CODE) {

            if(resultCode == RESULT_OK) {

                mSelectedIssuer = (Issuer) data.getSerializableExtra("issuer");

                mSelectedPayerCost = (PayerCost) data.getSerializableExtra("payerCost");

                mCreatedToken = (Token) data.getSerializableExtra("token");

                mSelectedPaymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");

                showReviewAndConfirm(data.getStringExtra("paymentMethodInfo"));

            }
            else if (resultCode == RESULT_CANCELED) {
                if(!mPaymentMethodEditionRequested) {
                    Intent returnIntent = new Intent();
                    setResult(RESULT_CANCELED, returnIntent);
                    finish();
                }
                else {
                    overridePendingTransition(R.anim.slide_right_to_left_in, R.anim.slide_right_to_left_out);
                }
            }
        }
        else if (requestCode == MercadoPago.INSTRUCTIONS_REQUEST_CODE) {
            finish();
        }
    }

    private void showReviewAndConfirm(String paymentMethodInfo) {
        drawPaymentMethodRow(paymentMethodInfo);
        drawTermsAndConditionsText();
        setAmountLabel();
    }

    private void setAmountLabel() {
        mTotalAmountTextView.setText(getAmountLabel());
    }

    private void drawTermsAndConditionsText() {
        StringBuilder termsAndConditionsText = new StringBuilder();
        termsAndConditionsText.append(getString(R.string.mpsdk_text_terms_and_conditions_start) + " ");
        termsAndConditionsText.append("<font color='#0066CC'>" + getString(R.string.mpsdk_text_terms_and_conditions_linked) +"</font>");
        termsAndConditionsText.append(" " + getString(R.string.mpsdk_text_terms_and_conditions_end));
        mTermsAndConditionsTextView.setText(Html.fromHtml(termsAndConditionsText.toString()));
    }

    private void drawPaymentMethodRow(String paymentMethodInfo) {
        mPaymentMethodCommentTextView.setText(paymentMethodInfo);
        int resourceId = MercadoPagoUtil.getPaymentMethodSearchItemIcon(this, mSelectedPaymentMethod.getId());
        mPaymentMethodImageView.setImageResource(resourceId);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected boolean validParameters() {

        return (mMerchantPublicKey != null) && (mCheckoutPreference != null);
    }

    protected void createPayment() {
        LayoutUtil.showProgressLayout(mActivity);
        mMercadoPago.createPayment(mCheckoutPreference.getId(), mCheckoutPreference.getPayer().getEmail(), mSelectedPaymentMethod.getId(), null, null, null, new Callback<Payment>() {
            @Override
            public void success(Payment payment, Response response) {
                if (MercadoPagoUtil.isCardPaymentType(mSelectedPaymentMethod.getPaymentTypeId())) {
                    new MercadoPago.StartActivityBuilder()
                            .setActivity(mActivity)
                            .setPayment(payment)
                            .setPaymentMethod(mSelectedPaymentMethod)
                            .startCongratsActivity();
                } else {
                    new MercadoPago.StartActivityBuilder()
                            .setPublicKey(mMerchantPublicKey)
                            .setActivity(mActivity)
                            .setPayment(payment)
                            .setPaymentMethod(mSelectedPaymentMethod)
                            .startInstructionsActivity();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ApiUtil.finishWithApiException(mActivity, error);
            }
        });
    }

    @Override
    public void onBackPressed() {
        mPaymentMethodEditionRequested = false;
        startPaymentVaultActivity();
        animateBack();
    }

    private void animateBack() {
        overridePendingTransition(R.anim.slide_left_to_right_in, R.anim.silde_left_to_right_out);
    }

}
