package com.mercadopago.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.BankDealsActivity;
import com.mercadopago.CallForAuthorizeActivity;
import com.mercadopago.CardVaultActivity;
import com.mercadopago.CheckoutActivity;
import com.mercadopago.CongratsActivity;
import com.mercadopago.CustomerCardsActivity;
import com.mercadopago.DiscountsActivity;
import com.mercadopago.constants.ProcessingModes;
import com.mercadopago.GuessingCardActivity;
import com.mercadopago.InstallmentsActivity;
import com.mercadopago.InstructionsActivity;
import com.mercadopago.IssuersActivity;
import com.mercadopago.PaymentMethodsActivity;
import com.mercadopago.PaymentResultActivity;
import com.mercadopago.PaymentTypesActivity;
import com.mercadopago.PaymentVaultActivity;
import com.mercadopago.PendingActivity;
import com.mercadopago.RejectionActivity;
import com.mercadopago.SecurityCodeActivity;
import com.mercadopago.VaultActivity;
import com.mercadopago.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.model.BankDeal;
import com.mercadopago.model.Campaign;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Discount;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Payer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PayerIntent;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentBody;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.Instructions;
import com.mercadopago.model.PaymentType;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.SecurityCodeIntent;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.preferences.PaymentPreference;
import com.mercadopago.services.BankDealService;
import com.mercadopago.services.CheckoutService;
import com.mercadopago.services.DiscountService;
import com.mercadopago.services.GatewayService;
import com.mercadopago.services.IdentificationService;
import com.mercadopago.services.PaymentService;
import com.mercadopago.util.HttpClientUtil;
import com.mercadopago.util.JsonUtil;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Deprecated
public class MercadoPago {

    public static final String KEY_TYPE_PUBLIC = "public_key";
    public static final String KEY_TYPE_PRIVATE = "private_key";

    public static final int CUSTOMER_CARDS_REQUEST_CODE = 0;
    public static final int PAYMENT_METHODS_REQUEST_CODE = 1;
    public static final int INSTALLMENTS_REQUEST_CODE = 2;
    public static final int ISSUERS_REQUEST_CODE = 3;
    public static final int NEW_CARD_REQUEST_CODE = 4;
    public static final int PAYMENT_RESULT_REQUEST_CODE = 5;
    public static final int VAULT_REQUEST_CODE = 6;
    public static final int CALL_FOR_AUTHORIZE_REQUEST_CODE = 7;
    public static final int PENDING_REQUEST_CODE = 8;
    public static final int REJECTION_REQUEST_CODE = 9;
    public static final int PAYMENT_VAULT_REQUEST_CODE = 10;
    public static final int BANK_DEALS_REQUEST_CODE = 11;
    public static final int CHECKOUT_REQUEST_CODE = 12;
    public static final int GUESSING_CARD_REQUEST_CODE = 13;
    public static final int INSTRUCTIONS_REQUEST_CODE = 14;
    public static final int CARD_VAULT_REQUEST_CODE = 15;
    public static final int CONGRATS_REQUEST_CODE = 16;
    public static final int PAYMENT_TYPES_REQUEST_CODE = 17;
    public static final int SECURITY_CODE_REQUEST_CODE = 18;
    public static final int DISCOUNTS_REQUEST_CODE = 19;
    public static final int REVIEW_AND_CONFIRM_REQUEST_CODE = 20;

    public static final int BIN_LENGTH = 6;

    private static final String MP_API_BASE_URL = "https://api.mercadopago.com";

    private static final String PAYMENT_RESULT_API_VERSION = "1.3.x";
    private static final String PAYMENT_METHODS_OPTIONS_API_VERSION = "1.3.x";

    private String mprocessingMode = ProcessingModes.AGGREGATOR;
    private String mKey = null;
    private String mKeyType = null;
    private Context mContext = null;

    Retrofit mRetrofit;

    private MercadoPago(Builder builder) {

        this.mContext = builder.mContext;
        this.mKey = builder.mKey;
        this.mKeyType = builder.mKeyType;

        System.setProperty("http.keepAlive", "false");

        mRetrofit = new Retrofit.Builder()
                .baseUrl(MP_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(JsonUtil.getInstance().getGson()))
                .client(HttpClientUtil.getClient(this.mContext, 10, 20, 20))
                .addCallAdapterFactory(new ErrorHandlingCallAdapter.ErrorHandlingCallAdapterFactory())
                .build();
    }

    public void getPreference(String checkoutPreferenceId, Callback<CheckoutPreference> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            CheckoutService service = mRetrofit.create(CheckoutService.class);
            service.getPreference(checkoutPreferenceId, this.mKey).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void createPayment(final PaymentBody paymentBody, final Callback<Payment> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            Retrofit paymentsRetrofitAdapter = new Retrofit.Builder()
                    .baseUrl(MP_API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(JsonUtil.getInstance().getGson()))
                    .client(HttpClientUtil.getClient(this.mContext, 10, 40, 40))
                    .addCallAdapterFactory(new ErrorHandlingCallAdapter.ErrorHandlingCallAdapterFactory())
                    .build();

            CheckoutService service = paymentsRetrofitAdapter.create(CheckoutService.class);
            service.createPayment(paymentBody.getTransactionId(), paymentBody).enqueue(callback);

        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void createToken(final SavedCardToken savedCardToken, final Callback<Token> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    savedCardToken.setDevice(mContext);
                    GatewayService service = mRetrofit.create(GatewayService.class);
                    service.getToken(mKey, "", savedCardToken).enqueue(callback);
                }
            }).start();

        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void createToken(final CardToken cardToken, final Callback<Token> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    cardToken.setDevice(mContext);
                    GatewayService service = mRetrofit.create(GatewayService.class);
                    service.getToken(mKey, "", cardToken).enqueue(callback);
                }
            }).start();
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void cloneToken(final String tokenId, final Callback<Token> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            GatewayService service = mRetrofit.create(GatewayService.class);
            service.getToken(tokenId, "", this.mKey).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void putSecurityCode(final String tokenId, final SecurityCodeIntent securityCodeIntent, final Callback<Token> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            GatewayService service = mRetrofit.create(GatewayService.class);
            service.getToken(tokenId, this.mKey, "", securityCodeIntent).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getBankDeals(final Callback<List<BankDeal>> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            BankDealService service = mRetrofit.create(BankDealService.class);
            service.getBankDeals(this.mKey, "", mContext.getResources().getConfiguration().locale.toString()).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getDirectDiscount(String amount, String payerEmail, final Callback<Discount> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            DiscountService service = mRetrofit.create(DiscountService.class);
            service.getDirectDiscount(this.mKey, amount, payerEmail).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getCodeDiscount(String amount, String payerEmail, String couponCode, final Callback<Discount> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            DiscountService service = mRetrofit.create(DiscountService.class);
            service.getCodeDiscount(this.mKey, amount, payerEmail, couponCode).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getCampaigns(final Callback<List<Campaign>> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {

            DiscountService service = mRetrofit.create(DiscountService.class);
            service.getCampaigns(this.mKey).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getIdentificationTypes(Callback<List<IdentificationType>> callback) {
        IdentificationService service = mRetrofit.create(IdentificationService.class);
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            service.getIdentificationTypes(this.mKey, null).enqueue(callback);
        } else {
            service.getIdentificationTypes(null, this.mKey).enqueue(callback);
        }
    }

    public void getInstallments(String bin, BigDecimal amount, Long issuerId, String paymentMethodId, Callback<List<Installment>> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            PaymentService service = mRetrofit.create(PaymentService.class);
            service.getInstallments(this.mKey, "", bin, amount, issuerId, paymentMethodId,
                    mContext.getResources().getConfiguration().locale.toString(), mprocessingMode).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getIssuers(String paymentMethodId, String bin, final Callback<List<Issuer>> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            PaymentService service = mRetrofit.create(PaymentService.class);
            service.getIssuers(this.mKey, "", paymentMethodId, bin, mprocessingMode).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getPaymentMethods(final Callback<List<PaymentMethod>> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            PaymentService service = mRetrofit.create(PaymentService.class);
            service.getPaymentMethods(this.mKey, "").enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getPaymentMethodSearch(BigDecimal amount, List<String> excludedPaymentTypes, List<String> excludedPaymentMethods, Payer payer, final Callback<PaymentMethodSearch> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            PayerIntent payerIntent = new PayerIntent(payer);
            CheckoutService service = mRetrofit.create(CheckoutService.class);
            String separator = ",";
            String excludedPaymentTypesAppended = getListAsString(excludedPaymentTypes, separator);
            String excludedPaymentMethodsAppended = getListAsString(excludedPaymentMethods, separator);

            service.getPaymentMethodSearch(mContext.getResources().getConfiguration().locale.getLanguage(), this.mKey, amount, excludedPaymentTypesAppended, excludedPaymentMethodsAppended, payerIntent, "", PAYMENT_METHODS_OPTIONS_API_VERSION, mprocessingMode).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getPaymentResult(Long paymentId, String paymentTypeId, final Callback<Instructions> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            CheckoutService service = mRetrofit.create(CheckoutService.class);
            service.getPaymentResult(mContext.getResources().getConfiguration().locale.getLanguage(), paymentId, this.mKey, "", paymentTypeId, PAYMENT_RESULT_API_VERSION).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public static List<PaymentMethod> getValidPaymentMethodsForBin(String bin, List<PaymentMethod> paymentMethods) {
        if (bin.length() == BIN_LENGTH) {
            List<PaymentMethod> validPaymentMethods = new ArrayList<>();
            for (PaymentMethod pm : paymentMethods) {
                if (pm.isValidForBin(bin)) {
                    validPaymentMethods.add(pm);
                }
            }
            return validPaymentMethods;
        } else
            throw new RuntimeException("Invalid bin: " + BIN_LENGTH + " digits needed, " + bin.length() + " found");
    }

    // * Static methods for StartActivityBuilder implementation

    private static void startBankDealsActivity(Activity activity, String publicKey, List<BankDeal> bankDeals, DecorationPreference decorationPreference) {

        Intent bankDealsIntent = new Intent(activity, BankDealsActivity.class);
        bankDealsIntent.putExtra("merchantPublicKey", publicKey);
        bankDealsIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));
        if (bankDeals != null) {
            bankDealsIntent.putExtra("bankDeals", JsonUtil.getInstance().toJson(bankDeals));
        }
        activity.startActivityForResult(bankDealsIntent, BANK_DEALS_REQUEST_CODE);
    }

    private static void startCheckoutActivity(Activity activity, String merchantPublicKey, String merchantBaseUrl, String merchantGetCustomerUri, String merchantAccessToken,
                                              String checkoutPreferenceId, Boolean showBankDeals, Boolean binaryModeEnabled, Integer congratsDisplay, DecorationPreference decorationPreference,
                                              Discount discount, Boolean discountEnabled, Boolean directDiscountEnabled, Boolean installmentsReviewEnabled, String merchantDiscountBaseUrl, String merchantGetDiscountUri,
                                              Map<String, String> discountAdditionalInfo) {

        Intent checkoutIntent = new Intent(activity, CheckoutActivity.class);
        checkoutIntent.putExtra("merchantPublicKey", merchantPublicKey);
        checkoutIntent.putExtra("checkoutPreferenceId", checkoutPreferenceId);
        checkoutIntent.putExtra("showBankDeals", showBankDeals);
        checkoutIntent.putExtra("merchantBaseUrl", merchantBaseUrl);
        checkoutIntent.putExtra("merchantGetCustomerUri", merchantGetCustomerUri);
        checkoutIntent.putExtra("merchantAccessToken", merchantAccessToken);
        checkoutIntent.putExtra("congratsDisplay", congratsDisplay);
        checkoutIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));
        checkoutIntent.putExtra("binaryModeEnabled", binaryModeEnabled);
        checkoutIntent.putExtra("discount", JsonUtil.getInstance().toJson(discount));
        checkoutIntent.putExtra("discountEnabled", discountEnabled);
        checkoutIntent.putExtra("directDiscountEnabled", directDiscountEnabled);
        checkoutIntent.putExtra("installmentsReviewEnabled", installmentsReviewEnabled);
        checkoutIntent.putExtra("merchantDiscountBaseUrl", merchantDiscountBaseUrl);
        checkoutIntent.putExtra("merchantGetDiscountUri", merchantGetDiscountUri);
        checkoutIntent.putExtra("discountAdditionalInfo", JsonUtil.getInstance().toJson(discountAdditionalInfo));

        activity.startActivityForResult(checkoutIntent, CHECKOUT_REQUEST_CODE);
    }

    private static void startPaymentResultActivity(Activity activity, String merchantPublicKey, Payment payment, PaymentMethod paymentMethod, Discount discount, Boolean discountEnabled,
                                                   Integer congratsDisplay) {

        Intent resultIntent = new Intent(activity, PaymentResultActivity.class);
        resultIntent.putExtra("merchantPublicKey", merchantPublicKey);
        resultIntent.putExtra("payment", JsonUtil.getInstance().toJson(payment));
        resultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        resultIntent.putExtra("discount", JsonUtil.getInstance().toJson(discount));
        resultIntent.putExtra("discountEnabled", discountEnabled);
        resultIntent.putExtra("congratsDisplay", congratsDisplay);

        activity.startActivityForResult(resultIntent, PAYMENT_RESULT_REQUEST_CODE);
    }

    private static void startCongratsActivity(Activity activity, String merchantPublicKey, Payment payment, PaymentMethod paymentMethod, Discount discount, Boolean discountEnabled,
                                              Integer congratsDisplay) {

        Intent congratsIntent = new Intent(activity, CongratsActivity.class);
        congratsIntent.putExtra("merchantPublicKey", merchantPublicKey);
        congratsIntent.putExtra("payment", JsonUtil.getInstance().toJson(payment));
        congratsIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        congratsIntent.putExtra("discount", JsonUtil.getInstance().toJson(discount));
        congratsIntent.putExtra("discountEnabled", discountEnabled);
        congratsIntent.putExtra("congratsDisplay", congratsDisplay);

        activity.startActivityForResult(congratsIntent, CONGRATS_REQUEST_CODE);
    }

    private static void startCallForAuthorizeActivity(Activity activity, String merchantPublicKey, Payment payment, PaymentMethod paymentMethod) {

        Intent callForAuthorizeIntent = new Intent(activity, CallForAuthorizeActivity.class);
        callForAuthorizeIntent.putExtra("merchantPublicKey", merchantPublicKey);
        callForAuthorizeIntent.putExtra("payment", JsonUtil.getInstance().toJson(payment));
        callForAuthorizeIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));

        activity.startActivityForResult(callForAuthorizeIntent, CALL_FOR_AUTHORIZE_REQUEST_CODE);
    }

    private static void startPendingActivity(Activity activity, String merchantPublicKey, Payment payment) {

        Intent pendingIntent = new Intent(activity, PendingActivity.class);
        pendingIntent.putExtra("merchantPublicKey", merchantPublicKey);
        pendingIntent.putExtra("payment", JsonUtil.getInstance().toJson(payment));

        activity.startActivityForResult(pendingIntent, PENDING_REQUEST_CODE);
    }

    private static void startRejectionActivity(Activity activity, String merchantPublicKey, Payment payment, PaymentMethod paymentMethod) {

        Intent rejectionIntent = new Intent(activity, RejectionActivity.class);
        rejectionIntent.putExtra("merchantPublicKey", merchantPublicKey);
        rejectionIntent.putExtra("payment", JsonUtil.getInstance().toJson(payment));
        rejectionIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));

        activity.startActivityForResult(rejectionIntent, REJECTION_REQUEST_CODE);
    }

    private static void startInstructionsActivity(Activity activity, String merchantPublicKey, Payment payment, String paymentTypeId) {

        Intent instructionIntent = new Intent(activity, InstructionsActivity.class);
        instructionIntent.putExtra("merchantPublicKey", merchantPublicKey);
        instructionIntent.putExtra("paymentTypeId", paymentTypeId);
        instructionIntent.putExtra("payment", JsonUtil.getInstance().toJson(payment));

        activity.startActivityForResult(instructionIntent, INSTRUCTIONS_REQUEST_CODE);
    }

    private static void startCustomerCardsActivity(Activity activity, List<Card> cards) {

        if ((activity == null) || (cards == null)) {
            throw new RuntimeException("Invalid parameters");
        }
        Intent paymentMethodsIntent = new Intent(activity, CustomerCardsActivity.class);
        Gson gson = new Gson();
        paymentMethodsIntent.putExtra("cards", gson.toJson(cards));
        activity.startActivityForResult(paymentMethodsIntent, CUSTOMER_CARDS_REQUEST_CODE);
    }

    private static void startInstallmentsActivity(Activity activity, BigDecimal amount, Site site,
                                                  String publicKey, List<PayerCost> payerCosts,
                                                  PaymentPreference paymentPreference, Issuer issuer,
                                                  PaymentMethod paymentMethod, DecorationPreference decorationPreference,
                                                  CardInfo cardInfo, BigDecimal transactionAmount,
                                                  String payerEmail, Discount discount, Boolean discountEnabled, Boolean directDiscountEnabled,
                                                  Boolean installmentsReviewEnabled, String merchantBaseUrl, String merchantDiscountBaseUrl,
                                                  String merchantGetDiscountUri, Map<String, String> discountAdditionalInfo) {

        Intent intent = new Intent(activity, InstallmentsActivity.class);

        if (amount != null) {
            intent.putExtra("amount", amount.toString());
        }

        intent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        intent.putExtra("merchantPublicKey", publicKey);
        intent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        intent.putExtra("site", JsonUtil.getInstance().toJson(site));
        intent.putExtra("payerCosts", JsonUtil.getInstance().toJson(payerCosts));
        intent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));
        intent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));
        intent.putExtra("cardInfo", JsonUtil.getInstance().toJson(cardInfo));
        intent.putExtra("payerEmail", payerEmail);
        intent.putExtra("transactionAmount", JsonUtil.getInstance().toJson(transactionAmount));
        intent.putExtra("discount", JsonUtil.getInstance().toJson(discount));
        intent.putExtra("discountEnabled", discountEnabled);
        intent.putExtra("directDiscountEnabled", directDiscountEnabled);
        intent.putExtra("installmentsReviewEnabled", installmentsReviewEnabled);
        intent.putExtra("merchantBaseUrl", merchantBaseUrl);
        intent.putExtra("merchantDiscountBaseUrl", merchantDiscountBaseUrl);
        intent.putExtra("merchantGetDiscountUri", merchantGetDiscountUri);
        intent.putExtra("discountAdditionalInfo", JsonUtil.getInstance().toJson(discountAdditionalInfo));

        activity.startActivityForResult(intent, INSTALLMENTS_REQUEST_CODE);
    }

    private static void startIssuersActivity(Activity activity, String publicKey,
                                             PaymentMethod paymentMethod,
                                             List<Issuer> issuers, DecorationPreference decorationPreference,
                                             CardInfo cardInfo) {

        Intent intent = new Intent(activity, IssuersActivity.class);
        intent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        intent.putExtra("merchantPublicKey", publicKey);
        intent.putExtra("issuers", JsonUtil.getInstance().toJson(issuers));
        intent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));
        intent.putExtra("cardInfo", JsonUtil.getInstance().toJson(cardInfo));
        activity.startActivityForResult(intent, ISSUERS_REQUEST_CODE);

    }

    private static void startSecurityCodeActivity(Activity activity, String publicKey,
                                                  CardInfo cardInfo, Card card, Token token,
                                                  PaymentMethod paymentMethod,
                                                  DecorationPreference decorationPreference) {

        Intent intent = new Intent(activity, SecurityCodeActivity.class);
        intent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        intent.putExtra("token", JsonUtil.getInstance().toJson(token));
        intent.putExtra("card", JsonUtil.getInstance().toJson(card));
        intent.putExtra("merchantPublicKey", publicKey);
        intent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));
        intent.putExtra("cardInfo", JsonUtil.getInstance().toJson(cardInfo));
        activity.startActivityForResult(intent, SECURITY_CODE_REQUEST_CODE);
    }

    private static void startPaymentTypesActivity(Activity activity, String publicKey,
                                                  List<PaymentMethod> paymentMethods,
                                                  List<PaymentType> paymentTypes,
                                                  CardInfo cardInfo,
                                                  DecorationPreference decorationPreference) {

        Intent intent = new Intent(activity, PaymentTypesActivity.class);
        intent.putExtra("paymentMethods", JsonUtil.getInstance().toJson(paymentMethods));
        intent.putExtra("paymentTypes", JsonUtil.getInstance().toJson(paymentTypes));
        intent.putExtra("merchantPublicKey", publicKey);
        intent.putExtra("cardInfo", JsonUtil.getInstance().toJson(cardInfo));
        intent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));
        activity.startActivityForResult(intent, PAYMENT_TYPES_REQUEST_CODE);

    }

    private static void startGuessingCardActivity(Activity activity, String key, Boolean requireSecurityCode,
                                                  Boolean requireIssuer, Boolean showBankDeals, PaymentPreference paymentPreference,
                                                  DecorationPreference decorationPreference, List<PaymentMethod> paymentMethodList,
                                                  PaymentRecovery paymentRecovery, Card card, BigDecimal transactionAmount,
                                                  String payerEmail, Discount discount, Boolean discountEnabled, Boolean directDiscountEnabled,
                                                  Boolean installmentsEnabled, String merchantBaseUrl, String merchantDiscountBaseUrl,
                                                  String merchantGetDiscountUri, Map<String, String> discountAdditionalInfo) {

        Intent guessingCardIntent = new Intent(activity, GuessingCardActivity.class);
        guessingCardIntent.putExtra("merchantPublicKey", key);

        if (requireSecurityCode != null) {
            guessingCardIntent.putExtra("requireSecurityCode", requireSecurityCode);
        }
        if (requireIssuer != null) {
            guessingCardIntent.putExtra("requireIssuer", requireIssuer);
        }
        if (showBankDeals != null) {
            guessingCardIntent.putExtra("showBankDeals", showBankDeals);
        }

        guessingCardIntent.putExtra("showBankDeals", showBankDeals);

        guessingCardIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));

        guessingCardIntent.putExtra("paymentMethodList", JsonUtil.getInstance().toJson(paymentMethodList));

        guessingCardIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));

        guessingCardIntent.putExtra("paymentRecovery", JsonUtil.getInstance().toJson(paymentRecovery));

        guessingCardIntent.putExtra("card", JsonUtil.getInstance().toJson(card));

        guessingCardIntent.putExtra("payerEmail", payerEmail);

        guessingCardIntent.putExtra("transactionAmount", JsonUtil.getInstance().toJson(transactionAmount));

        guessingCardIntent.putExtra("discount", JsonUtil.getInstance().toJson(discount));

        guessingCardIntent.putExtra("installmentsEnabled", installmentsEnabled);

        guessingCardIntent.putExtra("discountEnabled", discountEnabled);

        guessingCardIntent.putExtra("directDiscountEnabled", directDiscountEnabled);

        guessingCardIntent.putExtra("merchantBaseUrl", merchantBaseUrl);

        guessingCardIntent.putExtra("merchantDiscountBaseUrl", merchantDiscountBaseUrl);

        guessingCardIntent.putExtra("merchantGetDiscountUri", merchantGetDiscountUri);

        guessingCardIntent.putExtra("discountAdditionalInfo", JsonUtil.getInstance().toJson(discountAdditionalInfo));

        activity.startActivityForResult(guessingCardIntent, GUESSING_CARD_REQUEST_CODE);
    }

    private static void startCardVaultActivity(Activity activity, String key, BigDecimal amount, Site site, Boolean installmentsEnabled,
                                               Boolean showBankDeals, PaymentPreference paymentPreference, DecorationPreference decorationPreference,
                                               List<PaymentMethod> paymentMethodList, PaymentRecovery paymentRecovery, Card card,
                                               String payerEmail, Discount discount, Boolean discountEnabled, Boolean directDiscountEnabled, Boolean installmentsReviewEnabled,
                                               String merchantBaseUrl, String merchantDiscountBaseUrl, String merchantGetDiscountUri, Map<String, String> discountAdditionalInfo) {

        Intent cardVaultIntent = new Intent(activity, CardVaultActivity.class);
        cardVaultIntent.putExtra("merchantPublicKey", key);

        if (amount != null) {
            cardVaultIntent.putExtra("amount", amount.toString());
        }

        cardVaultIntent.putExtra("site", JsonUtil.getInstance().toJson(site));
        cardVaultIntent.putExtra("installmentsEnabled", installmentsEnabled);
        cardVaultIntent.putExtra("showBankDeals", showBankDeals);
        cardVaultIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));
        cardVaultIntent.putExtra("paymentMethodList", JsonUtil.getInstance().toJson(paymentMethodList));
        cardVaultIntent.putExtra("paymentRecovery", JsonUtil.getInstance().toJson(paymentRecovery));
        cardVaultIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));
        cardVaultIntent.putExtra("card", JsonUtil.getInstance().toJson(card));
        cardVaultIntent.putExtra("payerEmail", payerEmail);
        cardVaultIntent.putExtra("discount", JsonUtil.getInstance().toJson(discount));
        cardVaultIntent.putExtra("discountEnabled", discountEnabled);
        cardVaultIntent.putExtra("directDiscountEnabled", directDiscountEnabled);
        cardVaultIntent.putExtra("installmentsReviewEnabled", installmentsReviewEnabled);
        cardVaultIntent.putExtra("merchantBaseUrl", merchantBaseUrl);
        cardVaultIntent.putExtra("merchantDiscountBaseUrl", merchantDiscountBaseUrl);
        cardVaultIntent.putExtra("merchantGetDiscountUri", merchantGetDiscountUri);
        cardVaultIntent.putExtra("discountAdditionalInfo", JsonUtil.getInstance().toJson(discountAdditionalInfo));

        activity.startActivityForResult(cardVaultIntent, CARD_VAULT_REQUEST_CODE);
    }

    private static void startDiscountsActivity(Activity activity, String key, BigDecimal amount, String payerEmail, Discount discount,
                                               DecorationPreference decorationPreference, Boolean directDiscountEnabled,
                                               String merchantBaseUrl, String merchantDiscountBaseUrl, String merchantGetDiscountUri,
                                               Map<String, String> discountAdditionalInfo) {

        Intent discountsIntent = new Intent(activity, DiscountsActivity.class);
        discountsIntent.putExtra("merchantPublicKey", key);
        discountsIntent.putExtra("amount", amount.toString());
        discountsIntent.putExtra("payerEmail", payerEmail);
        discountsIntent.putExtra("directDiscountEnabled", directDiscountEnabled);
        discountsIntent.putExtra("discount", JsonUtil.getInstance().toJson(discount));
        discountsIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));
        discountsIntent.putExtra("merchantBaseUrl", merchantBaseUrl);
        discountsIntent.putExtra("merchantDiscountBaseUrl", merchantDiscountBaseUrl);
        discountsIntent.putExtra("merchantGetDiscountUri", merchantGetDiscountUri);
        discountsIntent.putExtra("discountAdditionalInfo", JsonUtil.getInstance().toJson(discountAdditionalInfo));

        activity.startActivityForResult(discountsIntent, DISCOUNTS_REQUEST_CODE);
    }

    private static void startPaymentMethodsActivity(Activity activity, String merchantPublicKey, Boolean showBankDeals, PaymentPreference paymentPreference, DecorationPreference decorationPreference) {

        Intent paymentMethodsIntent = new Intent(activity, PaymentMethodsActivity.class);
        paymentMethodsIntent.putExtra("merchantPublicKey", merchantPublicKey);
        paymentMethodsIntent.putExtra("showBankDeals", showBankDeals);
        paymentMethodsIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));
        paymentMethodsIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));

        activity.startActivityForResult(paymentMethodsIntent, PAYMENT_METHODS_REQUEST_CODE);
    }

    private static void startPaymentVaultActivity(Activity activity, String merchantPublicKey, String merchantBaseUrl,
                                                  String merchantGetCustomerUri, String merchantAccessToken, BigDecimal amount,
                                                  Site site, Boolean installmentsEnabled, Boolean showBankDeals, PaymentPreference paymentPreference,
                                                  DecorationPreference decorationPreference, PaymentMethodSearch paymentMethodSearch, List<Card> cards,
                                                  String payerAccessToken, Boolean accountMoneyEnabled, Integer maxSavedCards, String payerEmail,
                                                  Discount discount, Boolean discountEnabled, Boolean directDiscountEnabled, Boolean installmentsReviewEnabled, String merchantDiscountBaseUrl,
                                                  String merchantGetDiscountUri, Map<String, String> discountAdditionalInfo) {

        Intent vaultIntent = new Intent(activity, PaymentVaultActivity.class);
        vaultIntent.putExtra("merchantPublicKey", merchantPublicKey);
        vaultIntent.putExtra("merchantBaseUrl", merchantBaseUrl);
        vaultIntent.putExtra("merchantGetCustomerUri", merchantGetCustomerUri);
        vaultIntent.putExtra("merchantAccessToken", merchantAccessToken);
        vaultIntent.putExtra("amount", amount.toString());
        vaultIntent.putExtra("site", JsonUtil.getInstance().toJson(site));
        vaultIntent.putExtra("installmentsEnabled", installmentsEnabled);
        vaultIntent.putExtra("installmentsReviewEnabled", installmentsReviewEnabled);
        vaultIntent.putExtra("showBankDeals", showBankDeals);
        vaultIntent.putExtra("merchantBaseUrl", merchantBaseUrl);
        vaultIntent.putExtra("merchantGetCustomerUri", merchantGetCustomerUri);
        vaultIntent.putExtra("merchantAccessToken", merchantAccessToken);
        vaultIntent.putExtra("paymentMethodSearch", JsonUtil.getInstance().toJson(paymentMethodSearch));
        vaultIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));
        Gson gson = new Gson();
        vaultIntent.putExtra("cards", gson.toJson(cards));
        vaultIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));
        vaultIntent.putExtra("payerAccessToken", payerAccessToken);
        vaultIntent.putExtra("accountMoneyEnabled", accountMoneyEnabled);
        vaultIntent.putExtra("payerEmail", payerEmail);
        vaultIntent.putExtra("discount", JsonUtil.getInstance().toJson(discount));
        vaultIntent.putExtra("discountEnabled", discountEnabled);
        vaultIntent.putExtra("directDiscountEnabled", directDiscountEnabled);
        vaultIntent.putExtra("merchantDiscountBaseUrl", merchantDiscountBaseUrl);
        vaultIntent.putExtra("merchantGetDiscountUri", merchantGetDiscountUri);
        vaultIntent.putExtra("maxSavedCards", maxSavedCards);
        vaultIntent.putExtra("discountAdditionalInfo", JsonUtil.getInstance().toJson(discountAdditionalInfo));

        activity.startActivityForResult(vaultIntent, PAYMENT_VAULT_REQUEST_CODE);
    }

    private static void startNewCardActivity(Activity activity, String keyType, String key, PaymentMethod paymentMethod, Boolean requireSecurityCode) {

        Intent newCardIntent = new Intent(activity, com.mercadopago.NewCardActivity.class);
        newCardIntent.putExtra("keyType", keyType);
        newCardIntent.putExtra("key", key);
        newCardIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        if (requireSecurityCode != null) {
            newCardIntent.putExtra("requireSecurityCode", requireSecurityCode);
        }
        activity.startActivityForResult(newCardIntent, NEW_CARD_REQUEST_CODE);
    }

    private static void startVaultActivity(Activity activity, String merchantPublicKey, String merchantBaseUrl, String merchantGetCustomerUri, String merchantAccessToken, BigDecimal amount, Site site, List<String> supportedPaymentTypes, Boolean showBankDeals) {

        Intent vaultIntent = new Intent(activity, VaultActivity.class);
        vaultIntent.putExtra("merchantPublicKey", merchantPublicKey);
        vaultIntent.putExtra("merchantBaseUrl", merchantBaseUrl);
        vaultIntent.putExtra("merchantGetCustomerUri", merchantGetCustomerUri);
        vaultIntent.putExtra("merchantAccessToken", merchantAccessToken);
        vaultIntent.putExtra("site", JsonUtil.getInstance().toJson(site));
        vaultIntent.putExtra("amount", amount.toString());
        putListExtra(vaultIntent, "supportedPaymentTypes", supportedPaymentTypes);
        vaultIntent.putExtra("showBankDeals", showBankDeals);
        activity.startActivityForResult(vaultIntent, VAULT_REQUEST_CODE);
    }

    private static void putListExtra(Intent intent, String listName, List<String> list) {

        if (list != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            intent.putExtra(listName, gson.toJson(list, listType));
        }
    }

    private String getListAsString(List<String> list, String separator) {
        StringBuilder stringBuilder = new StringBuilder();
        if (list != null) {
            for (String typeId : list) {
                stringBuilder.append(typeId);
                if (!typeId.equals(list.get(list.size() - 1))) {
                    stringBuilder.append(separator);
                }
            }
        }
        return stringBuilder.toString();
    }

    public static class Builder {

        private Context mContext;
        private String mKey;
        private String mKeyType;

        public Builder() {

            mContext = null;
            mKey = null;
        }

        public Builder setContext(Context context) {

            if (context == null) throw new IllegalArgumentException("context is null");
            this.mContext = context;
            return this;
        }

        public Builder setKey(String key, String keyType) {

            this.mKey = key;
            this.mKeyType = keyType;
            return this;
        }

        public Builder setPrivateKey(String key) {

            this.mKey = key;
            this.mKeyType = MercadoPago.KEY_TYPE_PRIVATE;
            return this;
        }

        public Builder setPublicKey(String key) {

            this.mKey = key;
            this.mKeyType = MercadoPago.KEY_TYPE_PUBLIC;
            this.mKeyType = MercadoPago.KEY_TYPE_PUBLIC;
            return this;
        }

        public MercadoPago build() {

            if (this.mContext == null) throw new IllegalStateException("context is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");
            if ((!this.mKeyType.equals(MercadoPago.KEY_TYPE_PRIVATE)) &&
                    (!this.mKeyType.equals(MercadoPago.KEY_TYPE_PUBLIC)))
                throw new IllegalArgumentException("invalid key type");
            return new MercadoPago(this);
        }
    }

    public static class StartActivityBuilder {

        private Activity mActivity;
        private BigDecimal mAmount;
        private List<Card> mCards;
        private String mCheckoutPreferenceId;
        private String mPayerEmail;
        private String mKey;
        private String mKeyType;
        private String mMerchantAccessToken;
        private String mMerchantBaseUrl;
        private String mMerchantGetCustomerUri;
        private String mMerchantDiscountBaseUrl;
        private String mMerchantGetDiscountUri;
        private String mPaymentTypeId;
        private Map<String, String> mDiscountAdditionalInfo;
        private List<PayerCost> mPayerCosts;
        private List<Issuer> mIssuers;
        private Payment mPayment;
        private PaymentMethod mPaymentMethod;
        private List<PaymentMethod> mPaymentMethodList;
        private Boolean mRequireIssuer;
        private Boolean mRequireSecurityCode;
        private Boolean mShowBankDeals;
        private Integer mCongratsDisplay;
        private PaymentMethodSearch mPaymentMethodSearch;
        private PaymentPreference mPaymentPreference;
        private PaymentRecovery mPaymentRecovery;
        private PayerCost mPayerCost;
        private Discount mDiscount;

        private Token mToken;
        private Issuer mIssuer;
        private Site mSite;
        private DecorationPreference mDecorationPreference;
        private Boolean mInstallmentsEnabled;
        private List<String> mSupportedPaymentTypes;
        private List<BankDeal> mBankDeals;
        private Card mCard;
        private String mPayerAccessToken;
        private Boolean mAccountMoneyEnabled;
        private Boolean mDirectDiscountEnabled;
        private Boolean mDiscountEnabled;
        private Boolean mInstallmentsReviewEnabled;
        private List<PaymentType> mPaymentTypesList;
        private CardInfo mCardInfo;
        private Integer mMaxSavedCards;
        private Boolean mBinaryModeEnabled;

        public StartActivityBuilder() {

            mActivity = null;
            mKey = null;
            mKeyType = KEY_TYPE_PUBLIC;
        }

        public StartActivityBuilder setActivity(Activity activity) {

            if (activity == null) throw new IllegalArgumentException("context is null");
            this.mActivity = activity;
            return this;
        }

        public StartActivityBuilder setIssuer(Issuer issuer) {
            this.mIssuer = issuer;
            return this;
        }

        public StartActivityBuilder setAmount(BigDecimal amount) {

            this.mAmount = amount;
            return this;
        }

        public StartActivityBuilder setCard(Card card) {
            this.mCard = card;
            return this;
        }

        public StartActivityBuilder setCards(List<Card> cards) {

            this.mCards = cards;
            return this;
        }

        public StartActivityBuilder setCheckoutPreferenceId(String checkoutPreferenceId) {

            this.mCheckoutPreferenceId = checkoutPreferenceId;
            return this;
        }

        public StartActivityBuilder setPublicKey(String key) {

            this.mKey = key;
            this.mKeyType = MercadoPago.KEY_TYPE_PUBLIC;
            return this;
        }

        public StartActivityBuilder setBankDeals(List<BankDeal> bankDeals) {
            this.mBankDeals = bankDeals;
            return this;
        }

        public StartActivityBuilder setMerchantAccessToken(String merchantAccessToken) {

            this.mMerchantAccessToken = merchantAccessToken;
            return this;
        }

        public StartActivityBuilder setMerchantBaseUrl(String merchantBaseUrl) {

            this.mMerchantBaseUrl = merchantBaseUrl;
            return this;
        }

        public StartActivityBuilder setMerchantDiscountBaseUrl(String merchantDiscountBaseUrl) {

            this.mMerchantDiscountBaseUrl = merchantDiscountBaseUrl;
            return this;
        }

        public StartActivityBuilder setDiscountAdditionalInfo(Map<String, String> discountAdditionalInfo) {

            this.mDiscountAdditionalInfo = discountAdditionalInfo;
            return this;
        }

        public StartActivityBuilder setMerchantGetDiscountUri(String merchantGetDiscountUri) {

            this.mMerchantGetDiscountUri = merchantGetDiscountUri;
            return this;
        }

        public StartActivityBuilder setMerchantGetCustomerUri(String merchantGetCustomerUri) {

            this.mMerchantGetCustomerUri = merchantGetCustomerUri;
            return this;
        }

        public StartActivityBuilder setPayerCosts(List<PayerCost> payerCosts) {

            this.mPayerCosts = payerCosts;
            return this;
        }

        public StartActivityBuilder setPayerEmail(String payerEmail) {

            this.mPayerEmail = payerEmail;
            return this;
        }

        public StartActivityBuilder setDiscount(Discount discount) {

            this.mDiscount = discount;
            return this;
        }

        public StartActivityBuilder setDirectDiscountEnabled(Boolean directDiscountEnabled) {

            this.mDirectDiscountEnabled = directDiscountEnabled;
            return this;
        }

        public StartActivityBuilder setDiscountEnabled(Boolean discountEnabled) {

            this.mDiscountEnabled = discountEnabled;
            return this;
        }

        public StartActivityBuilder setIssuers(List<Issuer> issuers) {

            this.mIssuers = issuers;
            return this;
        }

        public StartActivityBuilder setInstallmentsReviewEnabled(Boolean installmentReviewEnabled) {

            this.mInstallmentsReviewEnabled = installmentReviewEnabled;
            return this;
        }

        public StartActivityBuilder setPayment(Payment payment) {

            this.mPayment = payment;
            return this;
        }

        public StartActivityBuilder setPayerCost(PayerCost payerCost) {

            this.mPayerCost = payerCost;
            return this;
        }


        public StartActivityBuilder setCardInfo(CardInfo cardInfo) {

            this.mCardInfo = cardInfo;
            return this;
        }

        public StartActivityBuilder setPaymentRecovery(PaymentRecovery paymentRecovery) {

            this.mPaymentRecovery = paymentRecovery;
            return this;
        }

        public StartActivityBuilder setPaymentMethod(PaymentMethod paymentMethod) {

            this.mPaymentMethod = paymentMethod;
            return this;
        }

        public StartActivityBuilder setPaymentTypeId(String paymentTypeId) {

            this.mPaymentTypeId = paymentTypeId;
            return this;
        }

        public StartActivityBuilder setSupportedPaymentMethods(List<PaymentMethod> paymentMethodList) {
            this.mPaymentMethodList = paymentMethodList;
            return this;
        }

        public StartActivityBuilder setPaymentTypesList(List<PaymentType> paymentTypesList) {
            this.mPaymentTypesList = paymentTypesList;
            return this;
        }

        public StartActivityBuilder setRequireSecurityCode(Boolean requireSecurityCode) {
            this.mRequireSecurityCode = requireSecurityCode;
            return this;
        }

        public StartActivityBuilder setRequireIssuer(Boolean requireIssuer) {

            this.mRequireIssuer = requireIssuer;
            return this;
        }

        public StartActivityBuilder setShowBankDeals(boolean showBankDeals) {

            this.mShowBankDeals = showBankDeals;
            return this;
        }

        public StartActivityBuilder setCongratsDisplay(int seconds) {
            this.mCongratsDisplay = seconds;
            return this;
        }

        public StartActivityBuilder setPaymentMethodSearch(PaymentMethodSearch paymentMethodSearch) {
            this.mPaymentMethodSearch = paymentMethodSearch;
            return this;
        }

        public StartActivityBuilder setPaymentPreference(PaymentPreference paymentPreference) {
            this.mPaymentPreference = paymentPreference;
            return this;
        }

        public StartActivityBuilder setToken(Token token) {
            this.mToken = token;
            return this;
        }

        public StartActivityBuilder setSite(Site site) {
            this.mSite = site;
            return this;
        }

        public StartActivityBuilder setInstallmentsEnabled(Boolean installmentsEnabled) {
            this.mInstallmentsEnabled = installmentsEnabled;
            return this;
        }

        public StartActivityBuilder setDecorationPreference(DecorationPreference decorationPreference) {
            this.mDecorationPreference = decorationPreference;
            return this;
        }

        public StartActivityBuilder setPayerAccessToken(String payerAccessToken) {
            this.mPayerAccessToken = payerAccessToken;
            return this;
        }

        public StartActivityBuilder setAccountMoneyEnabled(boolean accountMoneyEnabled) {
            this.mAccountMoneyEnabled = accountMoneyEnabled;
            return this;
        }

        public StartActivityBuilder setMaxSavedCards(Integer maxSavedCards) {
            this.mMaxSavedCards = maxSavedCards;
            return this;
        }

        public StartActivityBuilder setBinaryModeEnabled(Boolean binaryModeEnabled) {
            this.mBinaryModeEnabled = binaryModeEnabled;
            return this;
        }

        @Deprecated
        public StartActivityBuilder setSupportedPaymentTypes(List<String> supportedPaymentTypes) {
            this.mSupportedPaymentTypes = supportedPaymentTypes;
            return this;
        }

        public void startBankDealsActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPago.startBankDealsActivity(this.mActivity, this.mKey, this.mBankDeals, this.mDecorationPreference);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }
        }

        public void startCheckoutActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mCheckoutPreferenceId == null)
                throw new IllegalStateException("checkout preference id is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPago.startCheckoutActivity(this.mActivity, this.mKey, this.mMerchantBaseUrl, this.mMerchantGetCustomerUri, this.mMerchantAccessToken,
                        this.mCheckoutPreferenceId, this.mShowBankDeals, this.mBinaryModeEnabled, this.mCongratsDisplay, this.mDecorationPreference,
                        this.mDiscount, this.mDiscountEnabled, this.mDirectDiscountEnabled, this.mInstallmentsReviewEnabled, this.mMerchantDiscountBaseUrl, this.mMerchantGetDiscountUri,
                        this.mDiscountAdditionalInfo);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }
        }

        public void startPaymentResultActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mPayment == null) throw new IllegalStateException("payment is null");
            if (this.mPaymentMethod == null)
                throw new IllegalStateException("payment method is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPago.startPaymentResultActivity(this.mActivity, this.mKey, this.mPayment, this.mPaymentMethod, this.mDiscount, this.mDiscountEnabled, this.mCongratsDisplay);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }
        }

        public void startCongratsActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mPayment == null) throw new IllegalStateException("payment is null");
            if (this.mPaymentMethod == null)
                throw new IllegalStateException("payment method is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPago.startCongratsActivity(this.mActivity, this.mKey, this.mPayment, this.mPaymentMethod, this.mDiscount, this.mDiscountEnabled, this.mCongratsDisplay);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }
        }

        public void startCallForAuthorizeActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mPayment == null) throw new IllegalStateException("payment is null");
            if (this.mPaymentMethod == null)
                throw new IllegalStateException("payment method is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPago.startCallForAuthorizeActivity(this.mActivity, this.mKey, this.mPayment, this.mPaymentMethod);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }
        }

        public void startPendingActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mPayment == null) throw new IllegalStateException("payment is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPago.startPendingActivity(this.mActivity, this.mKey, this.mPayment);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }
        }

        public void startRejectionActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mPayment == null) throw new IllegalStateException("payment is null");
            if (this.mPaymentMethod == null)
                throw new IllegalStateException("payment method is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPago.startRejectionActivity(this.mActivity, this.mKey, this.mPayment, this.mPaymentMethod);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }
        }

        public void startInstructionsActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mPayment == null) throw new IllegalStateException("payment is null");
            if (this.mPaymentTypeId == null)
                throw new IllegalStateException("payment type id is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPago.startInstructionsActivity(this.mActivity, this.mKey, this.mPayment, this.mPaymentTypeId);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }

        }

        public void startCustomerCardsActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mCards == null) throw new IllegalStateException("cards is null");

            MercadoPago.startCustomerCardsActivity(this.mActivity, this.mCards);
        }

        public void startInstallmentsActivity() {
            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mSite == null) throw new IllegalStateException("site is null");
            if (this.mAmount == null) throw new IllegalStateException("amount is null");
            if (mPayerCosts == null) {
                if (this.mKey == null) throw new IllegalStateException("key is null");
                if (this.mIssuer == null) throw new IllegalStateException("issuer is null");
                if (this.mPaymentMethod == null)
                    throw new IllegalStateException("payment method is null");
            }

            MercadoPago.startInstallmentsActivity(mActivity, mAmount, mSite,
                    mKey, mPayerCosts, mPaymentPreference, mIssuer, mPaymentMethod, mDecorationPreference,
                    mCardInfo, mAmount, mPayerEmail, mDiscount, mDiscountEnabled, mDirectDiscountEnabled, mInstallmentsReviewEnabled,
                    mMerchantBaseUrl, mMerchantDiscountBaseUrl, mMerchantGetDiscountUri, mDiscountAdditionalInfo);
        }

        public void startIssuersActivity() {
            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mPaymentMethod == null)
                throw new IllegalStateException("payment method is null");

            MercadoPago.startIssuersActivity(this.mActivity, this.mKey, this.mPaymentMethod,
                    this.mIssuers, this.mDecorationPreference, this.mCardInfo);

        }

        public void startSecurityCodeActivity() {
            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mCardInfo == null) throw new IllegalStateException("card info is null");
            if (this.mPaymentMethod == null)
                throw new IllegalStateException("payment method is null");
            if (this.mCard != null && this.mToken != null)
                throw new IllegalStateException("can't start with card and token at the same time");
            if (this.mCard == null && this.mToken == null)
                throw new IllegalStateException("card and token can't both be null");

            MercadoPago.startSecurityCodeActivity(this.mActivity, this.mKey, this.mCardInfo,
                    this.mCard, this.mToken, this.mPaymentMethod, this.mDecorationPreference);

        }

        public void startPaymentTypesActivity() {
            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mPaymentMethodList == null)
                throw new IllegalStateException("payment method list is null");
            if (this.mPaymentTypesList == null)
                throw new IllegalStateException("payment types list is null");

            MercadoPago.startPaymentTypesActivity(mActivity, mKey, mPaymentMethodList, mPaymentTypesList,
                    mCardInfo, mDecorationPreference);
        }

        public void startGuessingCardActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");

            MercadoPago.startGuessingCardActivity(this.mActivity, this.mKey, this.mRequireSecurityCode,
                    this.mRequireIssuer, this.mShowBankDeals, this.mPaymentPreference, this.mDecorationPreference,
                    this.mPaymentMethodList, this.mPaymentRecovery, this.mCard, this.mAmount,
                    this.mPayerEmail, this.mDiscount, this.mDiscountEnabled, this.mDirectDiscountEnabled, this.mInstallmentsEnabled,
                    this.mMerchantBaseUrl, this.mMerchantDiscountBaseUrl, this.mMerchantGetDiscountUri, this.mDiscountAdditionalInfo);
        }

        public void startCardVaultActivity() {
            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mInstallmentsEnabled != null && this.mInstallmentsEnabled) {
                if (this.mAmount == null) throw new IllegalStateException("amount is null");
                if (this.mSite == null) throw new IllegalStateException("site is null");
            }

            MercadoPago.startCardVaultActivity(this.mActivity, this.mKey, this.mAmount, this.mSite, this.mInstallmentsEnabled, this.mShowBankDeals,
                    this.mPaymentPreference, this.mDecorationPreference, this.mPaymentMethodList, this.mPaymentRecovery, this.mCard,
                    this.mPayerEmail, this.mDiscount, this.mDiscountEnabled, this.mDirectDiscountEnabled, this.mInstallmentsReviewEnabled, this.mMerchantBaseUrl, this.mMerchantDiscountBaseUrl,
                    this.mMerchantGetDiscountUri, this.mDiscountAdditionalInfo);
        }

        public void startDiscountsActivity() {
            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mAmount == null) throw new IllegalStateException("amount is null");

            MercadoPago.startDiscountsActivity(this.mActivity, this.mKey, this.mAmount, this.mPayerEmail, this.mDiscount, this.mDecorationPreference,
                    this.mDirectDiscountEnabled, this.mMerchantBaseUrl, this.mMerchantDiscountBaseUrl, this.mMerchantGetDiscountUri,
                    this.mDiscountAdditionalInfo);
        }

        public void startPaymentMethodsActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPago.startPaymentMethodsActivity(this.mActivity, this.mKey,
                        this.mShowBankDeals, this.mPaymentPreference, this.mDecorationPreference);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }
        }

        public void startPaymentVaultActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mAmount == null) throw new IllegalStateException("amount is null");
            if (this.mSite == null) throw new IllegalStateException("site is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPago.startPaymentVaultActivity(this.mActivity, this.mKey, this.mMerchantBaseUrl,
                        this.mMerchantGetCustomerUri, this.mMerchantAccessToken,
                        this.mAmount, this.mSite, this.mInstallmentsEnabled, this.mShowBankDeals,
                        this.mPaymentPreference, this.mDecorationPreference, this.mPaymentMethodSearch,
                        this.mCards, this.mPayerAccessToken, this.mAccountMoneyEnabled, this.mMaxSavedCards,
                        this.mPayerEmail, this.mDiscount, this.mDiscountEnabled, this.mDirectDiscountEnabled,
                        this.mInstallmentsReviewEnabled, this.mMerchantDiscountBaseUrl, this.mMerchantGetDiscountUri,
                        this.mDiscountAdditionalInfo);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }
        }

        @Deprecated
        public void startNewCardActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");
            if (this.mPaymentMethod == null)
                throw new IllegalStateException("payment method is null");

            MercadoPago.startNewCardActivity(this.mActivity, this.mKeyType, this.mKey,
                    this.mPaymentMethod, this.mRequireSecurityCode);
        }

        public void startVaultActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mAmount == null) throw new IllegalStateException("amount is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");
            if (this.mSite == null) throw new IllegalStateException("key type is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPago.startVaultActivity(this.mActivity, this.mKey, this.mMerchantBaseUrl,
                        this.mMerchantGetCustomerUri, this.mMerchantAccessToken,
                        this.mAmount, this.mSite, this.mSupportedPaymentTypes, this.mShowBankDeals);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }
        }
    }
}