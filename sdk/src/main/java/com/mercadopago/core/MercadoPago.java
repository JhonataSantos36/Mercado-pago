package com.mercadopago.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.mercadopago.BankDealsActivity;
import com.mercadopago.CardInstallmentsActivity;
import com.mercadopago.CardIssuersActivity;
import com.mercadopago.CardVaultActivity;
import com.mercadopago.CheckoutActivity;
import com.mercadopago.CongratsActivity;
import com.mercadopago.GuessingNewCardActivity;
import com.mercadopago.InstructionsActivity;
import com.mercadopago.CustomerCardsActivity;
import com.mercadopago.InstallmentsActivity;
import com.mercadopago.IssuersActivity;
import com.mercadopago.PaymentMethodsActivity;
import com.mercadopago.PaymentVaultActivity;
import com.mercadopago.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.model.BankDeal;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.CheckoutPreference;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Instruction;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentIntent;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.services.BankDealService;
import com.mercadopago.services.GatewayService;
import com.mercadopago.services.IdentificationService;
import com.mercadopago.services.PaymentService;
import com.mercadopago.util.HttpClientUtil;
import com.mercadopago.util.JsonUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MercadoPago {

    public static final String KEY_TYPE_PUBLIC = "public_key";
    public static final String KEY_TYPE_PRIVATE = "private_key";

    public static final int CUSTOMER_CARDS_REQUEST_CODE = 0;
    public static final int PAYMENT_METHODS_REQUEST_CODE = 1;
    public static final int INSTALLMENTS_REQUEST_CODE = 2;
    public static final int ISSUERS_REQUEST_CODE = 3;
    public static final int NEW_CARD_REQUEST_CODE = 4;
    public static final int CONGRATS_REQUEST_CODE = 5;
    public static final int PAYMENT_VAULT_REQUEST_CODE = 6;
    public static final int BANK_DEALS_REQUEST_CODE = 7;
    public static final int CHECKOUT_REQUEST_CODE = 8;
    public static final int GUESSING_CARD_REQUEST_CODE = 10;
    public static final int INSTRUCTIONS_REQUEST_CODE = 11;
    public static final int CARD_VAULT_REQUEST_CODE = 12;

    public static final int BIN_LENGTH = 6;

    private static final String MP_API_BASE_URL = "https://api.mercadopago.com";
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
            MPTracker.getInstance().trackEvent("NO_SCREEN", "GET_PREFERENCE", "1", mKeyType, "MLA", "1.0", mContext);
            PaymentService service = mRetrofit.create(PaymentService.class);
            service.getPreference(checkoutPreferenceId, this.mKey).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void createPayment(final PaymentIntent paymentIntent, final Callback<Payment> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN", "CREATE_PAYMENT", "1", mKeyType, "MLA", "1.0", mContext);
            Retrofit paymentsRetrofitAdapter = new Retrofit.Builder()
                    .baseUrl(MP_API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(JsonUtil.getInstance().getGson()))
                    .client(HttpClientUtil.getClient(this.mContext, 10, 40, 40))
                    .addCallAdapterFactory(new ErrorHandlingCallAdapter.ErrorHandlingCallAdapterFactory())
                    .build();

            PaymentService service = paymentsRetrofitAdapter.create(PaymentService.class);
            service.createPayment(String.valueOf(paymentIntent.getTransactionId()), paymentIntent).enqueue(callback);

        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void createToken(final SavedCardToken savedCardToken, final Callback<Token> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN","CREATE_SAVED_TOKEN","1", mKeyType, "MLA", "1.0", mContext);

            savedCardToken.setDevice(mContext);
            GatewayService service = mRetrofit.create(GatewayService.class);
            service.getToken(this.mKey, savedCardToken).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void createToken(final CardToken cardToken, final Callback<Token> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN","CREATE_CARD_TOKEN","1", mKeyType, "MLA", "1.0", mContext);

            cardToken.setDevice(mContext);
            GatewayService service = mRetrofit.create(GatewayService.class);
            service.getToken(this.mKey, cardToken).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getBankDeals(final Callback<List<BankDeal>> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN","GET_BANK_DEALS","1", mKeyType, "MLA", "1.0", mContext);
            BankDealService service = mRetrofit.create(BankDealService.class);
            service.getBankDeals(this.mKey, mContext.getResources().getConfiguration().locale.toString()).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }


    public void getIdentificationTypes(Callback<List<IdentificationType>> callback) {
        IdentificationService service = mRetrofit.create(IdentificationService.class);
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN","GET_IDENTIFICATION_TYPES","1", mKeyType, "MLA", "1.0", mContext);
            service.getIdentificationTypes(this.mKey, null).enqueue(callback);
        } else {
            service.getIdentificationTypes(null, this.mKey).enqueue(callback);
        }
    }

    public void getInstallments(String bin, BigDecimal amount, Long issuerId, String paymentMethodId, Callback<List<Installment>> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN","GET_INSTALLMENTS","1", mKeyType, "MLA", "1.0", mContext);
            PaymentService service = mRetrofit.create(PaymentService.class);
            service.getInstallments(this.mKey, bin, amount, issuerId, paymentMethodId,
                    mContext.getResources().getConfiguration().locale.toString()).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getIssuers(String paymentMethodId, String bin, final Callback<List<Issuer>> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN","GET_ISSUERS","1", mKeyType, "MLA", "1.0", mContext);
            PaymentService service = mRetrofit.create(PaymentService.class);
            service.getIssuers(this.mKey, paymentMethodId, bin).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getPaymentMethods(final Callback<List<PaymentMethod>> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN","GET_PAYMENT_METHODS","1", mKeyType, "MLA", "1.0", mContext);
            PaymentService service = mRetrofit.create(PaymentService.class);
            service.getPaymentMethods(this.mKey).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getPaymentMethodSearch(BigDecimal amount, List<String> excludedPaymentTypes, List<String> excludedPaymentMethods, final Callback<PaymentMethodSearch> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN","GET_PAYMENT_METHOD_SEARCH","1", mKeyType, "MLA", "1.0", mContext);

            PaymentService service = mRetrofit.create(PaymentService.class);

            StringBuilder stringBuilder = new StringBuilder();
            if(excludedPaymentTypes != null) {

                for (String typeId : excludedPaymentTypes) {
                    stringBuilder.append(typeId);
                    if (!typeId.equals(excludedPaymentTypes.get(excludedPaymentTypes.size() - 1))) {
                        stringBuilder.append(",");
                    }
                }
            }
            String excludedPaymentTypesAppended = stringBuilder.toString();

            stringBuilder = new StringBuilder();
            if(excludedPaymentMethods != null) {
                for(String paymentMethodId : excludedPaymentMethods) {
                    stringBuilder.append(paymentMethodId);
                    if (!paymentMethodId.equals(excludedPaymentMethods.get(excludedPaymentMethods.size() - 1))) {
                        stringBuilder.append(",");
                    }
                }
            }
            String excludedPaymentMethodsAppended = stringBuilder.toString();

            service.getPaymentMethodSearch(this.mKey, amount, excludedPaymentTypesAppended, excludedPaymentMethodsAppended).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getInstructions(Long paymentId, String paymentTypeId, final Callback<Instruction> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN","GET_INSTRUCTIONS","1", mKeyType, "MLA", "1.0", mContext);

            PaymentService service = mRetrofit.create(PaymentService.class);
            service.getInstruction(paymentId, this.mKey, paymentTypeId).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public static List<PaymentMethod> getValidPaymentMethodsForBin(String bin, List<PaymentMethod> paymentMethods){
        if(bin.length() == BIN_LENGTH) {
            List<PaymentMethod> validPaymentMethods = new ArrayList<>();
            for (PaymentMethod pm : paymentMethods) {
                if (pm.isValidForBin(bin)) {
                    validPaymentMethods.add(pm);
                }
            }
            return validPaymentMethods;
        }
        else
            throw new RuntimeException("Invalid bin: " + BIN_LENGTH + " digits needed, " + bin.length() + " found");
    }

    // * Static methods for StartActivityBuilder implementation

    private static void startBankDealsActivity(Activity activity, String publicKey, DecorationPreference decorationPreference) {

        Intent bankDealsIntent = new Intent(activity, BankDealsActivity.class);
        bankDealsIntent.putExtra("publicKey", publicKey);
        bankDealsIntent.putExtra("decorationPreference", decorationPreference);
        activity.startActivityForResult(bankDealsIntent, BANK_DEALS_REQUEST_CODE);
    }

    private static void startCheckoutActivity(Activity activity, String merchantPublicKey, String checkoutPreferenceId, Boolean showBankDeals, DecorationPreference decorationPreference) {

        Intent checkoutIntent = new Intent(activity, CheckoutActivity.class);
        checkoutIntent.putExtra("merchantPublicKey", merchantPublicKey);
        checkoutIntent.putExtra("checkoutPreferenceId", checkoutPreferenceId);
        checkoutIntent.putExtra("showBankDeals", showBankDeals);
        checkoutIntent.putExtra("decorationPreference", decorationPreference);
        activity.startActivityForResult(checkoutIntent, CHECKOUT_REQUEST_CODE);
    }

    private static void startCongratsActivity(Activity activity, Payment payment, PaymentMethod paymentMethod) {

        Intent congratsIntent = new Intent(activity, CongratsActivity.class);
        congratsIntent.putExtra("payment", payment);
        congratsIntent.putExtra("paymentMethod", paymentMethod);

        activity.startActivityForResult(congratsIntent, CONGRATS_REQUEST_CODE);
    }

    private static void startInstructionsActivity(Activity activity, String merchantPublicKey, Payment payment, PaymentMethod paymentMethod) {

        Intent congratsIntent = new Intent(activity, InstructionsActivity.class);
        congratsIntent.putExtra("merchantPublicKey", merchantPublicKey);
        congratsIntent.putExtra("payment", payment);
        congratsIntent.putExtra("paymentMethod", paymentMethod);

        activity.startActivityForResult(congratsIntent, INSTRUCTIONS_REQUEST_CODE);
    }

    private static void startCustomerCardsActivity(Activity activity, List<Card> cards, Boolean supportMPApp) {

        if ((activity == null) || (cards == null)) {
            throw new RuntimeException("Invalid parameters");
        }
        Intent paymentMethodsIntent = new Intent(activity, CustomerCardsActivity.class);
        Gson gson = new Gson();
        paymentMethodsIntent.putExtra("cards", gson.toJson(cards));
        paymentMethodsIntent.putExtra("supportMPApp", supportMPApp);
        activity.startActivityForResult(paymentMethodsIntent, CUSTOMER_CARDS_REQUEST_CODE);
    }

    private static void startInstallmentsActivity(Activity activity, List<PayerCost> payerCosts) {

        Intent installmentsIntent = new Intent(activity, InstallmentsActivity.class);
        Gson gson = new Gson();
        installmentsIntent.putExtra("payerCosts", gson.toJson(payerCosts));
        activity.startActivityForResult(installmentsIntent, INSTALLMENTS_REQUEST_CODE);
    }

    private static void startCardInstallmentsActivity(Activity activity, BigDecimal amount, Site site,
                                                      Token token, String publicKey,
                                                      List<PayerCost> payerCosts,
                                                      PaymentPreference paymentPreference,
                                                      Issuer issuer, PaymentMethod paymentMethod, DecorationPreference decorationPreference) {
        Intent intent = new Intent(activity, CardInstallmentsActivity.class);
        intent.putExtra("amount", amount.toString());
        intent.putExtra("site", site);
        intent.putExtra("paymentMethod",  JsonUtil.getInstance().toJson(paymentMethod));
        intent.putExtra("token", JsonUtil.getInstance().toJson(token));
        intent.putExtra("publicKey", publicKey);
        intent.putExtra("payerCosts", (ArrayList<PayerCost>) payerCosts);
        intent.putExtra("paymentPreference", paymentPreference);
        intent.putExtra("issuer", issuer);
        intent.putExtra("decorationPreference", decorationPreference);

        activity.startActivityForResult(intent, INSTALLMENTS_REQUEST_CODE);
    }

    private static void startIssuersActivity(Activity activity, String merchantPublicKey, PaymentMethod paymentMethod) {

        Intent issuersIntent = new Intent(activity, IssuersActivity.class);
        issuersIntent.putExtra("merchantPublicKey", merchantPublicKey);
        issuersIntent.putExtra("paymentMethod", paymentMethod);
        activity.startActivityForResult(issuersIntent, ISSUERS_REQUEST_CODE);
    }

    private static void startCardIssuersActivity(Activity activity, String publicKey,
                                             PaymentMethod paymentMethod, Token token,
                                             List<Issuer> issuers, DecorationPreference decorationPreference) {

        Intent intent = new Intent(activity, CardIssuersActivity.class);
        intent.putExtra("paymentMethod",  JsonUtil.getInstance().toJson(paymentMethod));
        intent.putExtra("token", JsonUtil.getInstance().toJson(token));
        intent.putExtra("publicKey", publicKey);
        intent.putExtra("issuers", (ArrayList<Issuer>) issuers);
        intent.putExtra("decorationPreference", decorationPreference);
        activity.startActivityForResult(intent, ISSUERS_REQUEST_CODE);

    }

    private static void startGuessingCardActivity(Activity activity, String key,
                                                  Boolean requireSecurityCode,
                                                  Boolean requireIssuer,
                                                  Boolean showBankDeals,
                                                  PaymentPreference paymentPreference,
                                                  DecorationPreference decorationPreference,
                                                  Token token,
                                                  List<PaymentMethod> paymentMethodList) {

        Intent guessingCardIntent = new Intent(activity, GuessingNewCardActivity.class);
        guessingCardIntent.putExtra("publicKey", key);

        if (requireSecurityCode != null) {
            guessingCardIntent.putExtra("requireSecurityCode", requireSecurityCode);
        }
        if (requireIssuer != null) {
            guessingCardIntent.putExtra("requireIssuer", requireIssuer);
        }
        if(showBankDeals != null){
            guessingCardIntent.putExtra("showBankDeals", showBankDeals);
        }
        guessingCardIntent.putExtra("showBankDeals", showBankDeals);

        guessingCardIntent.putExtra("paymentPreference", paymentPreference);

        guessingCardIntent.putExtra("token", token);

        guessingCardIntent.putExtra("paymentMethodList", (ArrayList<PaymentMethod>) paymentMethodList);

        guessingCardIntent.putExtra("decorationPreference", decorationPreference);

        activity.startActivityForResult(guessingCardIntent, GUESSING_CARD_REQUEST_CODE);
    }

    private static void startCardVaultActivity(Activity activity,
                                               String key,
                                               BigDecimal amount,
                                               Site site,
                                               PaymentPreference paymentPreference,
                                               DecorationPreference decorationPreference,
                                               Token token,
                                               List<PaymentMethod> paymentMethodList) {

        Intent cardVaultIntent = new Intent(activity, CardVaultActivity.class);
        cardVaultIntent.putExtra("publicKey", key);

        cardVaultIntent.putExtra("amount", amount.toString());

        cardVaultIntent.putExtra("site", site);

        cardVaultIntent.putExtra("paymentPreference", paymentPreference);

        cardVaultIntent.putExtra("token", token);

        cardVaultIntent.putExtra("paymentMethodList", (ArrayList<PaymentMethod>) paymentMethodList);

        cardVaultIntent.putExtra("decorationPreference", decorationPreference);

        activity.startActivityForResult(cardVaultIntent, CARD_VAULT_REQUEST_CODE);
    }


    private static void startPaymentMethodsActivity(Activity activity, String merchantPublicKey, Boolean showBankDeals, PaymentPreference paymentPreference, DecorationPreference decorationPreference) {

        Intent paymentMethodsIntent = new Intent(activity, PaymentMethodsActivity.class);
        paymentMethodsIntent.putExtra("merchantPublicKey", merchantPublicKey);
        paymentMethodsIntent.putExtra("showBankDeals", showBankDeals);
        paymentMethodsIntent.putExtra("paymentPreference", paymentPreference);
        paymentMethodsIntent.putExtra("decorationPreference", decorationPreference);

        activity.startActivityForResult(paymentMethodsIntent, PAYMENT_METHODS_REQUEST_CODE);
    }

    private static void startPaymentVaultActivity(Activity activity,
                                                  String merchantPublicKey,
                                                  String merchantBaseUrl,
                                                  String merchantGetCustomerUri,
                                                  String merchantAccessToken,
                                                  BigDecimal amount,
                                                  Site site,
                                                  Boolean showBankDeals,
                                                  PaymentPreference paymentPreference,
                                                  DecorationPreference decorationPreference,
                                                  PaymentMethodSearch paymentMethodSearch) {

        Intent vaultIntent = new Intent(activity, PaymentVaultActivity.class);
        vaultIntent.putExtra("merchantPublicKey", merchantPublicKey);
        vaultIntent.putExtra("merchantBaseUrl", merchantBaseUrl);
        vaultIntent.putExtra("merchantGetCustomerUri", merchantGetCustomerUri);
        vaultIntent.putExtra("merchantAccessToken", merchantAccessToken);
        vaultIntent.putExtra("amount", amount.toString());
        vaultIntent.putExtra("site", site);
        vaultIntent.putExtra("showBankDeals", showBankDeals);
        vaultIntent.putExtra("paymentMethodSearch", paymentMethodSearch);
        vaultIntent.putExtra("paymentPreference", paymentPreference);
        vaultIntent.putExtra("decorationPreference", decorationPreference);

        activity.startActivityForResult(vaultIntent, PAYMENT_VAULT_REQUEST_CODE);
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
                    (!this.mKeyType.equals(MercadoPago.KEY_TYPE_PUBLIC))) throw new IllegalArgumentException("invalid key type");
            return new MercadoPago(this);
        }
    }

    public static class StartActivityBuilder {

        private Activity mActivity;
        private BigDecimal mAmount;
        private List<Card> mCards;
        private String mCheckoutPreferenceId;
        private String mKey;
        private String mKeyType;
        private String mMerchantAccessToken;
        private String mMerchantBaseUrl;
        private String mMerchantGetCustomerUri;
        private List<PayerCost> mPayerCosts;
        private List<Issuer> mIssuers;
        private Payment mPayment;
        private PaymentMethod mPaymentMethod;
        private List<PaymentMethod> mPaymentMethodList;
        private Boolean mRequireIssuer;
        private Boolean mRequireSecurityCode;
        private Boolean mShowBankDeals;
        private Boolean mSupportMPApp;
        private PaymentMethodSearch mPaymentMethodSearch;
        private PaymentPreference mPaymentPreference;
        private Token mToken;
        private Issuer mIssuer;
        private Site mSite;
        private DecorationPreference mDecorationPreference;

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

        public StartActivityBuilder setCards(List<Card> cards) {

            this.mCards = cards;
            return this;
        }

        public StartActivityBuilder setCheckoutPreferenceId(String checkoutPreferenceId) {

            this.mCheckoutPreferenceId = checkoutPreferenceId;
            return this;
        }

        public StartActivityBuilder setKey(String key, String keyType) {

            this.mKey = key;
            this.mKeyType = keyType;
            return this;
        }

        public StartActivityBuilder setPrivateKey(String key) {

            this.mKey = key;
            this.mKeyType = MercadoPago.KEY_TYPE_PRIVATE;
            return this;
        }

        public StartActivityBuilder setPublicKey(String key) {

            this.mKey = key;
            this.mKeyType = MercadoPago.KEY_TYPE_PUBLIC;
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

        public StartActivityBuilder setMerchantGetCustomerUri(String merchantGetCustomerUri) {

            this.mMerchantGetCustomerUri = merchantGetCustomerUri;
            return this;
        }

        public StartActivityBuilder setPayerCosts(List<PayerCost> payerCosts) {

            this.mPayerCosts = payerCosts;
            return this;
        }

        public StartActivityBuilder setIssuers(List<Issuer> issuers) {

            this.mIssuers = issuers;
            return this;
        }

        public StartActivityBuilder setPayment(Payment payment) {

            this.mPayment = payment;
            return this;
        }

        public StartActivityBuilder setPaymentMethod(PaymentMethod paymentMethod) {

            this.mPaymentMethod = paymentMethod;
            return this;
        }

        public StartActivityBuilder setSupportedPaymentMethods(List<PaymentMethod> paymentMethodList) {

            this.mPaymentMethodList = paymentMethodList;
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

        public StartActivityBuilder setSupportMPApp(boolean supportMPApp) {

            this.mSupportMPApp = supportMPApp;
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

        public StartActivityBuilder setDecorationPreference(DecorationPreference decorationPreference) {
            this.mDecorationPreference = decorationPreference;
            return this;
        }

        public void startBankDealsActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPago.startBankDealsActivity(this.mActivity, this.mKey, this.mDecorationPreference);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }
        }

        public void startCheckoutActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mCheckoutPreferenceId == null) throw new IllegalStateException("checkout preference id is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPago.startCheckoutActivity(this.mActivity, this.mKey,
                        this.mCheckoutPreferenceId, this.mShowBankDeals, this.mDecorationPreference);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }
        }

        public void startCongratsActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mPayment == null) throw new IllegalStateException("payment is null");
            if (this.mPaymentMethod == null) throw new IllegalStateException("payment method is null");

            MercadoPago.startCongratsActivity(this.mActivity, this.mPayment, this.mPaymentMethod);
        }


        public void startInstructionsActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mPayment == null) throw new IllegalStateException("payment is null");
            if (this.mPaymentMethod == null) throw new IllegalStateException("payment method is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPago.startInstructionsActivity(this.mActivity, this.mKey, this.mPayment, this.mPaymentMethod);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }

        }

        public void startCustomerCardsActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mCards == null) throw new IllegalStateException("cards is null");

            MercadoPago.startCustomerCardsActivity(this.mActivity, this.mCards, this.mSupportMPApp);
        }

        public void startInstallmentsActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mPayerCosts == null) throw new IllegalStateException("payer costs are null");

            MercadoPago.startInstallmentsActivity(this.mActivity, this.mPayerCosts);
        }

        public void startCardInstallmentsActivity() {
            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mSite == null) throw new IllegalStateException("site is null");
            if (this.mAmount == null) throw new IllegalStateException("amount is null");
            if (this.mIssuer == null) throw new IllegalStateException("issuer is null");
            if (this.mPaymentMethod == null) throw new IllegalStateException("payment method is null");

            MercadoPago.startCardInstallmentsActivity(mActivity, mAmount, mSite, mToken,
                    mKey, mPayerCosts, mPaymentPreference, mIssuer, mPaymentMethod, mDecorationPreference);
        }

        public void startCardIssuersActivity() {
            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mPaymentMethod == null) throw new IllegalStateException("payment method is null");

            MercadoPago.startCardIssuersActivity(this.mActivity, this.mKey, this.mPaymentMethod,
                    this.mToken, this.mIssuers, this.mDecorationPreference);

        }

        public void startIssuersActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");
            if (this.mPaymentMethod == null) throw new IllegalStateException("payment method is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPago.startIssuersActivity(this.mActivity,
                        this.mKey, this.mPaymentMethod);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }
        }

        public void startGuessingCardActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");
            MercadoPago.startGuessingCardActivity(this.mActivity, this.mKey, this.mRequireSecurityCode,
                    this.mRequireIssuer, this.mShowBankDeals, this.mPaymentPreference, this.mDecorationPreference,
                    this.mToken, this.mPaymentMethodList);
        }

        public void startCardVaultActivity() {
            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mAmount == null) throw new IllegalStateException("amount is null");
            if (this.mSite == null) throw new IllegalStateException("site is null");
            MercadoPago.startCardVaultActivity(this.mActivity, this.mKey, this.mAmount, this.mSite,
                    this.mPaymentPreference, this.mDecorationPreference, this.mToken, this.mPaymentMethodList);
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
                        this.mAmount, this.mSite, this.mShowBankDeals,
                        this.mPaymentPreference, this.mDecorationPreference, this.mPaymentMethodSearch);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }
        }
    }
}