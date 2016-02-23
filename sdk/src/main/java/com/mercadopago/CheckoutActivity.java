package com.mercadopago;

import android.content.Intent;
import android.os.Bundle;
<<<<<<< HEAD
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mercadopago.controllers.ShoppingCartController;
=======
import android.view.View;

import com.mercadopago.adapters.CustomerCardsAdapter;
>>>>>>> parent of df039a5... matched preference to vault settings, added payment methods search classes
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.model.CheckoutPreference;
<<<<<<< HEAD
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Item;
import com.mercadopago.model.MerchantPayment;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentIntent;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;

import java.math.BigDecimal;
=======
import com.mercadopago.model.Customer;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentIntent;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodRow;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;
>>>>>>> parent of df039a5... matched preference to vault settings, added payment methods search classes

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CheckoutActivity extends VaultActivity {

    protected CheckoutPreference mCheckoutPreference;
    protected Payment mPayment;
    protected boolean mSupportMPApp = true;
<<<<<<< HEAD
    protected PaymentMethod mSelectedPaymentMethod;
    protected Issuer mSelectedIssuer;
    protected PayerCost mSelectedPayerCost;
    protected Token mCreatedToken;

    protected String mPurchaseTitle;

    protected ShoppingCartController mShoppingCartController;

    //Controls
    protected TextView mPaymentMethodCommentTextView;
    protected ImageView mPaymentMethodImageView;
    protected ImageView mEditPaymentMethodImageView;
    protected Button mPayButton;
    protected TextView mTextTermsAndConditions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        setTitle(Html.fromHtml("<b><i><small>" + getString(R.string.mpsdk_title_activity_checkout) + "</small></i></b>"));

        mCheckoutPreference = (CheckoutPreference) this.getIntent().getSerializableExtra("checkoutPreference");
        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");
        mShowBankDeals = this.getIntent().getBooleanExtra("showBankDeals", true);

        getApplicationContext();
        initializeActivityControls();
        if(validParameters())
        {
            setActivity();

            mMercadoPago = new MercadoPago.Builder()
                    .setContext(this)
                    .setPublicKey(mMerchantPublicKey)
                    .build();

            startPaymentVaultActivity();
        }
        else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("message", "Invalid parameters");
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mActivity.getMenuInflater().inflate(R.menu.shopping_cart_menu, menu);
        mShoppingCartController = new ShoppingCartController(this, menu.findItem(R.id.shopping_cart), mCheckoutPreference.getItems().get(0).getPictureUrl(), mPurchaseTitle,
                mCheckoutPreference.getAmount(), mCheckoutPreference.getItems().get(0).getCurrencyId(), true, findViewById(R.id.scrollLayout));
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

    private void initializeActivityControls() {
        mPaymentMethodCommentTextView = (TextView) findViewById(R.id.payment_method_comment);
        mPaymentMethodImageView = (ImageView) findViewById(R.id.payment_method_image);
        mEditPaymentMethodImageView = (ImageView) findViewById(R.id.imageEdit);
        mEditPaymentMethodImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPaymentVaultActivity();
            }
        });
        mTextTermsAndConditions = (TextView) findViewById(R.id.termsAndConditions);
        mTextTermsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTermsAndConditionsActivity();
            }
        });

        mPayButton = (Button) findViewById(R.id.payButton);
        mPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPayment();
            }
        });
        mPurchaseTitle = getPurchaseTitle();
    }

    protected void startTermsAndConditionsActivity() {
        Intent termsAndConditionsIntent = new Intent(this, TermsAndConditionsActivity.class);
        startActivity(termsAndConditionsIntent);
=======

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Set checkout preference
        mCheckoutPreference = (CheckoutPreference) this.getIntent().getSerializableExtra("checkoutPreference");

        super.onCreate(savedInstanceState);
>>>>>>> parent of df039a5... matched preference to vault settings, added payment methods search classes
    }

    @Override
    protected void setContentView() {

        setContentView(R.layout.activity_checkout);
    }

<<<<<<< HEAD
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

        if(payerHasEmail())
        {
            builder.setMerchantBaseUrl("https://mp-android-sdk.herokuapp.com/");
            builder.setMerchantGetCustomerUri("customers?preference_id=" + mCheckoutPreference.getId());
=======
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (!onVaultActivityResult(requestCode, resultCode, data)) {

            // Set checkout result
            Intent checkoutResult = null;
            if (requestCode == MercadoPago.INSTALL_APP_REQUEST_CODE) {

                if(data != null) {
                    if (!data.getBooleanExtra("backButtonPressed", false)) {
                        checkoutResult = data;
                    } else {
                        return;
                    }
                }

            } else if (requestCode == MercadoPago.CONGRATS_REQUEST_CODE) {

                // from SDK
                checkoutResult = new Intent();
                checkoutResult.putExtra("externalReference", mPayment.getExternalReference());
                checkoutResult.putExtra("paymentId", mPayment.getId());
                checkoutResult.putExtra("paymentStatus", mPayment.getStatus());
                checkoutResult.putExtra("paymentType", mPayment.getPaymentTypeId());
                checkoutResult.putExtra("preferenceId", mCheckoutPreference.getId());
            }

            // Return checkout result
            setResult(RESULT_OK, checkoutResult);
            finish();
>>>>>>> parent of df039a5... matched preference to vault settings, added payment methods search classes
        }
    }

    @Override
    protected void setAmount() {

<<<<<<< HEAD
        builder.startPaymentVaultActivity();
    }

    private String getAmountLabel() {
        String currencyId = mCheckoutPreference.getItems().get(0).getCurrencyId();
        return CurrenciesUtil.formatNumber(mCheckoutPreference.getAmount(), currencyId);
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
                if(!item.equals(mCheckoutPreference.getItems().get(itemListSize))) {
                    purchaseTitle.append(", ");
                }
                purchaseTitle.append(item.getTitle());
            }
        }
        return purchaseTitle.toString();
=======
        if (mCheckoutPreference != null) {
            mAmount = mCheckoutPreference.getAmount();
        }
>>>>>>> parent of df039a5... matched preference to vault settings, added payment methods search classes
    }

    @Override
    protected boolean validParameters() {

        if ((mMerchantPublicKey != null) && (mCheckoutPreference != null)) {
            return true;
        }
        return false;
    }

    @Override
    protected void setActivity() {

        mActivity = this;
        mActivity.setTitle(getString(R.string.mpsdk_title_activity_checkout));
    }

    @Override
    protected void initPaymentFlow() {

        // Show payment method selection or go for customer's cards
        if ((mCheckoutPreference.getPayer() != null) &&
                (mCheckoutPreference.getPayer().getEmail() != null) && (!mCheckoutPreference.getPayer().getEmail().equals(""))) {
            getCustomerCardsAsync();
        } else {
            startPaymentMethodsActivity();
        }
    }

<<<<<<< HEAD
        if(requestCode == MercadoPago.PAYMENT_VAULT_REQUEST_CODE) {

            if(resultCode == RESULT_OK) {
                boolean MPAppNeeded = data.getBooleanExtra("MPAppNeeded", false);
                if(MPAppNeeded) {
                    startMPApp();
                }
                else {

                    mSelectedIssuer = (Issuer) data.getSerializableExtra("issuer");

                    mSelectedPayerCost = (PayerCost) data.getSerializableExtra("payerCost");

                    mCreatedToken = (Token) data.getSerializableExtra("token");
=======
    @Override
    protected void resolveCustomerCardsRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            PaymentMethodRow selectedPaymentMethodRow = (PaymentMethodRow) data.getSerializableExtra("paymentMethodRow");

            if (selectedPaymentMethodRow.getCard() != null) {

                // Set selection status
                mPayerCosts = null;
                mCardToken = null;
                mSelectedPaymentMethodRow = selectedPaymentMethodRow;
                mSelectedPayerCost = null;
                mTempPaymentMethod = null;
>>>>>>> parent of df039a5... matched preference to vault settings, added payment methods search classes

                // Set customer method selection
                setCustomerMethodSelection();

<<<<<<< HEAD
                    if(MercadoPagoUtil.isCardPaymentType(mSelectedPaymentMethod.getPaymentTypeId()))
                    {
                        //TODO ver que poner
                        showReviewAndConfirm("Ver que poner");
                    }
                    else {
                        showReviewAndConfirm(data.getStringExtra("paymentMethodInfo"));
                    }


                }
            }
            else if (resultCode == RESULT_CANCELED) {
                if(mSelectedPaymentMethod == null) {
                    Intent returnIntent = new Intent();
                    setResult(RESULT_CANCELED, returnIntent);
                    finish();
=======
            } else {

                if (selectedPaymentMethodRow.getLabel().equals(getResources().getString(R.string.mpsdk_mp_app_name))) {

                    startMPApp();

                } else {

                    startPaymentMethodsActivity();
>>>>>>> parent of df039a5... matched preference to vault settings, added payment methods search classes
                }
            }
        } else {

            if ((data != null) && (data.getStringExtra("apiException") != null)) {
                finishWithApiException(data);
            }
        }
<<<<<<< HEAD

        Intent checkoutResult = null;
        if (requestCode == MercadoPago.INSTALL_APP_REQUEST_CODE) {
            if(data != null && !data.getBooleanExtra("backButtonPressed", false)) {
                checkoutResult = data;
                setResult(RESULT_OK, checkoutResult);
                finish();
            }
=======
    }

    @Override
    protected void resolvePaymentMethodsRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            // Set selection status
            mTempIssuer = null;
            mTempPaymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");

            if (MercadoPagoUtil.isCardPaymentType(mTempPaymentMethod.getPaymentTypeId())) {  // Card-like methods

                if (mTempPaymentMethod.isIssuerRequired()) {

                    // Call issuer activity
                    startIssuersActivity();

                } else {

                    // Call new card activity
                    startNewCardActivity();
                }
            } else if (mTempPaymentMethod.getId().equals(getResources().getString(R.string.mpsdk_mp_app_id))) {
>>>>>>> parent of df039a5... matched preference to vault settings, added payment methods search classes

                // Start MercadoPago App
                startMPApp();

<<<<<<< HEAD
            // from SDK
            checkoutResult = new Intent();
            checkoutResult.putExtra("eAs" +
                    "xternalReference", mPayment.getExternalReference() != null ? mPayment.getExternalReference() : null);
            checkoutResult.putExtra("paymentId", mPayment.getId() != null ? mPayment.getId() : null);
            checkoutResult.putExtra("paymentStatus", mPayment.getStatus() != null ? mPayment.getStatus() : null);
            checkoutResult.putExtra("paymentType", mPayment.getPaymentTypeId() != null ? mPayment.getPaymentTypeId() : null);
            checkoutResult.putExtra("preferenceId", mCheckoutPreference.getId());
            setResult(RESULT_OK, checkoutResult);
            finish();
        }
    }

    private void showReviewAndConfirm(String paymentMethodInfo) {
        mPaymentMethodCommentTextView.setText(paymentMethodInfo);
        int resourceId = MercadoPagoUtil.getPaymentMethodSearchItemIcon(this, mSelectedPaymentMethod.getId());
        mPaymentMethodImageView.setImageResource(resourceId);
        mPayButton.setText(getResources().getString(R.string.mpsdk_pay_button_text) + " " + this.getAmountLabel());
        StringBuilder termsAndConditionsText = new StringBuilder();
        termsAndConditionsText.append(getString(R.string.mpsdk_text_terms_and_conditions_start)+" ");
        termsAndConditionsText.append(" <font color='blue'><u>" + getString(R.string.mpsdk_text_terms_and_conditions_linked) + "</u></font> ");
        termsAndConditionsText.append(" " + getString(R.string.mpsdk_text_terms_and_conditions_end));
        mTextTermsAndConditions.setText(Html.fromHtml(termsAndConditionsText.toString()));
    }

    @Override
    protected void onResume() {
        super.onResume();
=======
            } else {  // Off-line methods

                // Set selection status
                mPayerCosts = null;
                mCardToken = null;
                mSelectedPaymentMethodRow = null;
                mSelectedPayerCost = null;
                mSelectedPaymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");
                mSelectedIssuer = null;

                // Set customer method selection
                mCustomerMethodsText.setText(mSelectedPaymentMethod.getName());
                mCustomerMethodsText.setCompoundDrawablesWithIntrinsicBounds(MercadoPagoUtil.getPaymentMethodIcon(mActivity, mSelectedPaymentMethod.getId()), 0, 0, 0);

                // Set security card visibility
                mSecurityCodeCard.setVisibility(View.GONE);

                // Set installments visibility
                mInstallmentsCard.setVisibility(View.GONE);

                // Set button visibility
                mSubmitButton.setEnabled(true);
            }
        } else {

            if ((data != null) && (data.getStringExtra("apiException") != null)) {
                finishWithApiException(data);
            } else if ((mSelectedPaymentMethodRow == null) && (mCardToken == null)) {
                // if nothing is selected
                finish();
            }
        }
    }

    @Override
    protected void startCustomerCardsActivity() {

        // Now call customer cards activity with MP App support
        new MercadoPago.StartActivityBuilder()
                .setActivity(mActivity)
                .setCards(mCards)
                .setSupportMPApp(mSupportMPApp)
                .startCustomerCardsActivity();
>>>>>>> parent of df039a5... matched preference to vault settings, added payment methods search classes
    }

    @Override
    protected void startPaymentMethodsActivity() {

        // Now call payment methods activity with MP App support
        new MercadoPago.StartActivityBuilder()
                .setActivity(mActivity)
                .setPublicKey(mMerchantPublicKey)
                .setSupportedPaymentTypes(mPaymentMethodPreference.getSupportedPaymentTypes())
                .setShowBankDeals(mShowBankDeals)
                .setSupportMPApp(mSupportMPApp)
                .startPaymentMethodsActivity();
    }

    @Override
    protected void getCustomerCardsAsync() {

        LayoutUtil.showProgressLayout(mActivity);
        mMercadoPago.getCustomer(mCheckoutPreference.getId(), new Callback<Customer>() {
            @Override
            public void success(Customer customer, Response response) {

                mCards = customer.getCards();

                // If the customer has saved cards show the first one, else show the payment methods step
                if ((mCards != null) && (mCards.size() > 0)) {

                    // Set selected payment method row
                    mSelectedPaymentMethodRow = CustomerCardsAdapter.getPaymentMethodRow(mActivity, mCards.get(0));

                    // Set customer method selection
                    setCustomerMethodSelection();

                } else {

                    // Show payment methods step
                    startPaymentMethodsActivity();

                    LayoutUtil.showRegularLayout(mActivity);
                }
            }

            @Override
            public void failure(RetrofitError error) {

                mExceptionOnMethod = "getCustomerCardsAsync";
                ApiUtil.finishWithApiException(mActivity, error);
            }
        });
    }

<<<<<<< HEAD
    protected void createPayment() {
        PaymentIntent paymentIntent = new PaymentIntent();
=======
    @Override
    protected void resolveCreateTokenSuccess(String token) {

        // Set payment intent
        PaymentIntent paymentIntent = new PaymentIntent();
        paymentIntent.setPrefId(mCheckoutPreference.getId());
        paymentIntent.setToken(token);
        if (mSelectedIssuer != null) {
            paymentIntent.setIssuerId(mSelectedIssuer.getId());
        }
        paymentIntent.setInstallments(mSelectedPayerCost.getInstallments());
        paymentIntent.setPaymentMethodId(mSelectedPaymentMethod.getId());
>>>>>>> parent of df039a5... matched preference to vault settings, added payment methods search classes

        paymentIntent.setPrefId(mCheckoutPreference.getId());
        if(mCreatedToken != null) {
            paymentIntent.setToken(mCreatedToken.getId());
        }
        if (mSelectedIssuer != null) {
            paymentIntent.setIssuerId(mSelectedIssuer.getId());
        }
        if(mSelectedPayerCost != null) {
            paymentIntent.setInstallments(mSelectedPayerCost.getInstallments());
        }

        paymentIntent.setPaymentMethodId(mSelectedPaymentMethod.getId());
        // Create payment
<<<<<<< HEAD
        LayoutUtil.showProgressLayout(this);
=======
        LayoutUtil.showProgressLayout(mActivity);
        mMercadoPago.createPayment(paymentIntent, new Callback<Payment>() {
            @Override
            public void success(Payment payment, Response response) {
>>>>>>> parent of df039a5... matched preference to vault settings, added payment methods search classes

        //TODO ir a nuevo servicio de payment
        createMockPayment(paymentIntent);

    }

    //TODO ir a nuevo servicio de payment
    private void createMockPayment(PaymentIntent paymentIntent) {
        if (paymentIntent.getPaymentMethodId() != null) {

            // Set item
            Item item = new Item("id1", 1,
                    new BigDecimal("100"));

            // Set payment method id
            String paymentMethodId = paymentIntent.getPaymentMethodId();

            // Set campaign id

            // Set merchant payment
            MerchantPayment payment = new MerchantPayment(item, paymentIntent.getInstallments(), paymentIntent.getIssuerId(),
                    paymentIntent.getToken(), paymentMethodId, null, "mlm-cards-data");

            // Create payment
            MerchantServer.createPayment(this, "https://www.mercadopago.com", "/checkout/examples/doPayment", payment, new Callback<Payment>() {
                @Override
                public void success(Payment payment, Response response) {

                    mPayment = payment;

                    if(MercadoPagoUtil.isCardPaymentType(payment.getPaymentTypeId())) {
                        new MercadoPago.StartActivityBuilder()
                                .setActivity(mActivity)
                                .setPayment(mPayment)
                                .setPaymentMethod(mSelectedPaymentMethod)
                                .startCongratsActivity();
                    } else {
                        new MercadoPago.StartActivityBuilder()
                                .setPublicKey(mMerchantPublicKey)
                                .setActivity(mActivity)
                                .setPayment(mPayment)
                                .setPaymentMethod(mSelectedPaymentMethod)
                                .startInstructionsActivity();
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                    LayoutUtil.showRegularLayout(mActivity);
                    Toast.makeText(mActivity, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {

            Toast.makeText(mActivity, "Invalid payment method", Toast.LENGTH_LONG).show();
        }
    }

    protected void startMPApp() {

        if ((mCheckoutPreference != null) && (mCheckoutPreference.getId() != null)) {
            Intent intent = new Intent(this, InstallAppActivity.class);
            intent.putExtra("preferenceId", mCheckoutPreference.getId());
            intent.putExtra("packageName", this.getPackageName());
            intent.putExtra("deepLink", "mercadopago://mpsdk_install_app");
            startActivityForResult(intent, MercadoPago.INSTALL_APP_REQUEST_CODE);
        }
    }
}
