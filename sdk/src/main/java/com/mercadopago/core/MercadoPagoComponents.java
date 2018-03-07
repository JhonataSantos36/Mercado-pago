package com.mercadopago.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.mercadopago.BankDealsActivity;
import com.mercadopago.CardVaultActivity;
import com.mercadopago.DiscountsActivity;
import com.mercadopago.GuessingCardActivity;
import com.mercadopago.InstallmentsActivity;
import com.mercadopago.IssuersActivity;
import com.mercadopago.PayerInformationActivity;
import com.mercadopago.PaymentMethodsActivity;
import com.mercadopago.PaymentTypesActivity;
import com.mercadopago.PaymentVaultActivity;
import com.mercadopago.ReviewPaymentMethodsActivity;
import com.mercadopago.SecurityCodeActivity;
import com.mercadopago.callbacks.OnConfirmPaymentCallback;
import com.mercadopago.callbacks.OnReviewChange;
import com.mercadopago.model.BankDeal;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Item;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.model.PaymentType;
import com.mercadopago.model.Reviewable;
import com.mercadopago.model.Site;
import com.mercadopago.model.Summary;
import com.mercadopago.model.Token;
import com.mercadopago.paymentresult.PaymentResultActivity;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.PaymentPreference;
import com.mercadopago.preferences.PaymentResultScreenPreference;
import com.mercadopago.preferences.ReviewScreenPreference;
import com.mercadopago.preferences.ServicePreference;
import com.mercadopago.review_and_confirm.models.PaymentModel;
import com.mercadopago.review_and_confirm.models.TermsAndConditionsModel;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewItemsView;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewPaymentOffView;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewPaymentOnView;
import com.mercadopago.uicontrollers.reviewandconfirm.SummaryView;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.MercadoPagoUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mreverter on 1/17/17.
 */

public class MercadoPagoComponents {

    private MercadoPagoComponents() {
    }

    public static class Activities {

        public static final int CUSTOMER_CARDS_REQUEST_CODE = 0;
        public static final int PAYMENT_METHODS_REQUEST_CODE = 1;
        public static final int INSTALLMENTS_REQUEST_CODE = 2;
        public static final int ISSUERS_REQUEST_CODE = 3;
        public static final int PAYMENT_RESULT_REQUEST_CODE = 5;
        public static final int CALL_FOR_AUTHORIZE_REQUEST_CODE = 7;
        public static final int PENDING_REQUEST_CODE = 8;
        public static final int REJECTION_REQUEST_CODE = 9;
        public static final int PAYMENT_VAULT_REQUEST_CODE = 10;
        public static final int BANK_DEALS_REQUEST_CODE = 11;
        public static final int GUESSING_CARD_REQUEST_CODE = 13;
        public static final int INSTRUCTIONS_REQUEST_CODE = 14;
        public static final int CARD_VAULT_REQUEST_CODE = 15;
        public static final int CONGRATS_REQUEST_CODE = 16;
        public static final int PAYMENT_TYPES_REQUEST_CODE = 17;
        public static final int SECURITY_CODE_REQUEST_CODE = 18;
        public static final int DISCOUNTS_REQUEST_CODE = 19;
        public static final int REVIEW_AND_CONFIRM_REQUEST_CODE = 20;
        public static final int REVIEW_PAYMENT_METHODS_REQUEST_CODE = 21;
        public static final int PAYER_INFORMATION_REQUEST_CODE = 22;

        public static final int HOOK_1 = 50;
        public static final int HOOK_1_ACCOUNT_MONEY = 51;
        public static final int HOOK_1_PLUGIN = 52;

        public static final int HOOK_2 = 60;

        public static final int HOOK_3 = 70;

        public static final int PLUGIN_PAYMENT_METHOD_REQUEST_CODE = 100;

        public static final int PLUGIN_PAYMENT_REQUEST_CODE = 200;

        private Activities() {
        }

        public static class PaymentVaultActivityBuilder {

            private Activity activity;
            private List<Card> cards;
            private PaymentPreference paymentPreference;
            private BigDecimal amount;
            private Site site;
            private String merchantPublicKey;
            private String merchantBaseUrl;
            private String merchantGetCustomerUri;
            private String merchantAccessToken;
            private Boolean installmentsEnabled;
            private Boolean showBankDeals;
            private PaymentMethodSearch paymentMethodSearch;
            private String payerAccessToken;
            private Integer maxSavedCards;
            private String payerEmail;
            private Discount discount;
            private boolean discountEnabled;
            private boolean directDiscountEnabled;
            private boolean installmentsReviewEnabled;
            private boolean showAllSavedCardsEnabled;
            private boolean escEnabled;
            private String merchantDiscountBaseUrl;
            private String merchantGetDiscountUri;
            private Map<String, String> discountAdditionalInfo;
            private CheckoutPreference checkoutPreference;

            public PaymentVaultActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public PaymentVaultActivityBuilder setCards(List<Card> cards) {
                this.cards = cards;
                return this;
            }

            public PaymentVaultActivityBuilder setAmount(BigDecimal amount) {
                this.amount = amount;
                return this;
            }

            public PaymentVaultActivityBuilder setSite(Site site) {
                this.site = site;
                return this;
            }

            public PaymentVaultActivityBuilder setInstallmentsEnabled(Boolean installmentsEnabled) {
                this.installmentsEnabled = installmentsEnabled;
                return this;
            }

            public PaymentVaultActivityBuilder setShowBankDeals(Boolean showBankDeals) {
                this.showBankDeals = showBankDeals;
                return this;
            }

            public PaymentVaultActivityBuilder setPaymentMethodSearch(PaymentMethodSearch paymentMethodSearch) {
                this.paymentMethodSearch = paymentMethodSearch;
                return this;
            }

            public PaymentVaultActivityBuilder setPaymentPreference(PaymentPreference paymentPreference) {
                this.paymentPreference = paymentPreference;
                return this;
            }

            public PaymentVaultActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public PaymentVaultActivityBuilder setMerchantBaseUrl(String merchantBaseUrl) {
                this.merchantBaseUrl = merchantBaseUrl;
                return this;
            }

            public PaymentVaultActivityBuilder setMerchantGetCustomerUri(String merchantGetCustomerUri) {
                this.merchantGetCustomerUri = merchantGetCustomerUri;
                return this;
            }

            public PaymentVaultActivityBuilder setMerchantAccessToken(String merchantAccessToken) {
                this.merchantAccessToken = merchantAccessToken;
                return this;
            }

            public PaymentVaultActivityBuilder setPayerAccessToken(String payerAccessToken) {
                this.payerAccessToken = payerAccessToken;
                return this;
            }

            public PaymentVaultActivityBuilder setMaxSavedCards(Integer maxSavedCards) {
                this.maxSavedCards = maxSavedCards;
                return this;
            }

            public PaymentVaultActivityBuilder setShowAllSavedCardsEnabled(boolean showAll) {
                this.showAllSavedCardsEnabled = showAll;
                return this;
            }

            public PaymentVaultActivityBuilder setESCEnabled(boolean escEnabled) {
                this.escEnabled = escEnabled;
                return this;
            }

            public PaymentVaultActivityBuilder setCheckoutPreference(final CheckoutPreference checkoutPreference) {
                this.checkoutPreference = checkoutPreference;
                return this;
            }

            public PaymentVaultActivityBuilder setPayerEmail(String payerEmail) {
                this.payerEmail = payerEmail;
                return this;
            }

            public PaymentVaultActivityBuilder setDiscountEnabled(boolean discountEnabled) {
                this.discountEnabled = discountEnabled;
                return this;
            }

            public PaymentVaultActivityBuilder setDiscount(Discount discount) {
                this.discount = discount;
                return this;
            }

            public PaymentVaultActivityBuilder setMerchantDiscountBaseUrl(String merchantDiscountBaseUrl) {
                this.merchantDiscountBaseUrl = merchantDiscountBaseUrl;
                return this;
            }

            public PaymentVaultActivityBuilder setMerchantGetDiscountUri(String merchantGetDiscountUri) {
                this.merchantGetDiscountUri = merchantGetDiscountUri;
                return this;
            }

            public PaymentVaultActivityBuilder setDirectDiscountEnabled(boolean directDiscountEnabled) {
                this.directDiscountEnabled = directDiscountEnabled;
                return this;
            }

            public PaymentVaultActivityBuilder setDiscountAdditionalInfo(Map<String, String> discountAdditionalInfo) {
                this.discountAdditionalInfo = discountAdditionalInfo;
                return this;
            }

            public PaymentVaultActivityBuilder setInstallmentsReviewEnabled(boolean installmentsReviewEnabled) {
                this.installmentsReviewEnabled = installmentsReviewEnabled;
                return this;
            }

            public void startActivity() {
                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.merchantPublicKey == null && this.payerAccessToken == null)
                    throw new IllegalStateException("key is null");

                startPaymentVaultActivity();
            }

            private void startPaymentVaultActivity() {
                Intent paymentVaultIntent = new Intent(activity, PaymentVaultActivity.class);
                paymentVaultIntent.putExtra("merchantPublicKey", merchantPublicKey);
                paymentVaultIntent.putExtra("merchantBaseUrl", merchantBaseUrl);
                paymentVaultIntent.putExtra("merchantGetCustomerUri", merchantGetCustomerUri);
                paymentVaultIntent.putExtra("merchantAccessToken", merchantAccessToken);
                paymentVaultIntent.putExtra("amount", amount.toString());
                paymentVaultIntent.putExtra("site", JsonUtil.getInstance().toJson(site));
                paymentVaultIntent.putExtra("installmentsEnabled", installmentsEnabled);
                paymentVaultIntent.putExtra("showBankDeals", showBankDeals);
                paymentVaultIntent.putExtra("merchantBaseUrl", merchantBaseUrl);
                paymentVaultIntent.putExtra("merchantGetCustomerUri", merchantGetCustomerUri);
                paymentVaultIntent.putExtra("merchantAccessToken", merchantAccessToken);
                paymentVaultIntent.putExtra("paymentMethodSearch", JsonUtil.getInstance().toJson(paymentMethodSearch));
                paymentVaultIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));
                paymentVaultIntent.putExtra("checkoutPreference", JsonUtil.getInstance().toJson(checkoutPreference));

                Gson gson = new Gson();
                paymentVaultIntent.putExtra("cards", gson.toJson(cards));
                paymentVaultIntent.putExtra("payerAccessToken", payerAccessToken);
                paymentVaultIntent.putExtra("maxSavedCards", maxSavedCards);
                paymentVaultIntent.putExtra("showAllSavedCardsEnabled", showAllSavedCardsEnabled);
                paymentVaultIntent.putExtra("escEnabled", escEnabled);

                //Discounts
                paymentVaultIntent.putExtra("payerEmail", payerEmail);
                paymentVaultIntent.putExtra("discount", JsonUtil.getInstance().toJson(discount));
                paymentVaultIntent.putExtra("discountEnabled", discountEnabled);

                paymentVaultIntent.putExtra("directDiscountEnabled", directDiscountEnabled);
                paymentVaultIntent.putExtra("installmentsReviewEnabled", installmentsReviewEnabled);
                paymentVaultIntent.putExtra("merchantDiscountBaseUrl", merchantDiscountBaseUrl);
                paymentVaultIntent.putExtra("merchantGetDiscountUri", merchantGetDiscountUri);
                paymentVaultIntent.putExtra("discountAdditionalInfo", JsonUtil.getInstance().toJson(discountAdditionalInfo));

                activity.startActivityForResult(paymentVaultIntent, PAYMENT_VAULT_REQUEST_CODE);
            }
        }

        public static class ReviewAndConfirmBuilder {
            private Activity activity;
            private PaymentMethod paymentMethod;
            private PayerCost payerCost;
            private BigDecimal amount;
            private Site site;
            private Issuer issuer;
            private Boolean hasExtraPaymentMethods;
            private String paymentMethodCommentInfo;
            private String paymentMethodDescriptionInfo;
            private List<Item> items;
            private Discount discount;
            private Token token;
            private ReviewScreenPreference reviewScreenPreference;
            private Boolean termsAndConditionsEnabled;
            private Boolean discountEnabled;
            private String merchantPublicKey;

            public ReviewAndConfirmBuilder setIssuer(Issuer issuer) {
                this.issuer = issuer;
                return this;
            }

            public ReviewAndConfirmBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public ReviewAndConfirmBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public ReviewAndConfirmBuilder setPaymentMethod(PaymentMethod paymentMehtod) {
                this.paymentMethod = paymentMehtod;
                return this;
            }

            public ReviewAndConfirmBuilder setPayerCost(PayerCost payerCost) {
                this.payerCost = payerCost;
                return this;
            }

            public ReviewAndConfirmBuilder setAmount(BigDecimal amount) {
                this.amount = amount;
                return this;
            }

            public ReviewAndConfirmBuilder setSite(Site site) {
                this.site = site;
                return this;
            }

            public ReviewAndConfirmBuilder setToken(Token token) {
                this.token = token;
                return this;
            }

            public ReviewAndConfirmBuilder setHasExtraPaymentMethods(Boolean hasExtraPaymentMethods) {
                this.hasExtraPaymentMethods = hasExtraPaymentMethods;
                return this;
            }

            public ReviewAndConfirmBuilder setPaymentMethodCommentInfo(String paymentMethodCommentInfo) {
                this.paymentMethodCommentInfo = paymentMethodCommentInfo;
                return this;
            }

            public ReviewAndConfirmBuilder setPaymentMethodDescriptionInfo(String paymentMethodDescriptionInfo) {
                this.paymentMethodDescriptionInfo = paymentMethodDescriptionInfo;
                return this;
            }

            public ReviewAndConfirmBuilder setItems(List<Item> items) {
                this.items = items;
                return this;
            }

            public ReviewAndConfirmBuilder setDiscount(Discount discount) {
                this.discount = discount;
                return this;
            }

            public ReviewAndConfirmBuilder setReviewScreenPreference(ReviewScreenPreference reviewScreenPreference) {
                this.reviewScreenPreference = reviewScreenPreference;
                return this;
            }

            public ReviewAndConfirmBuilder setTermsAndConditionsEnabled(Boolean termsAndConditionsEnabled) {
                this.termsAndConditionsEnabled = termsAndConditionsEnabled;
                return this;
            }

            public ReviewAndConfirmBuilder setDiscountEnabled(Boolean discountEnabled) {
                this.discountEnabled = discountEnabled;
                return this;
            }

            public void startActivity() {

                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.paymentMethod == null)
                    throw new IllegalStateException("payment method is null");
                if (this.items == null) throw new IllegalStateException("items not set");
                if (MercadoPagoUtil.isCard(paymentMethod.getPaymentTypeId())) {
                    if (this.payerCost == null)
                        throw new IllegalStateException("payer cost is null");
                    if (this.token == null) throw new IllegalStateException("token is null");
                }

                startReviewAndConfirmActivity();
            }

            private void startReviewAndConfirmActivity() {
                // TODO remove
//                Intent intent = new Intent(activity, ReviewAndConfirmActivity.class);
//                intent.putExtra("merchantPublicKey", merchantPublicKey);
//                intent.putExtra("hasExtraPaymentMethods", hasExtraPaymentMethods);
//                intent.putExtra("amount", amount.toString());
//                intent.putExtra("site", JsonUtil.getInstance().toJson(site));
//                intent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
//                intent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
//                intent.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
//                intent.putExtra("token", JsonUtil.getInstance().toJson(token));
//                intent.putExtra("paymentMethodCommentInfo", paymentMethodCommentInfo);
//                intent.putExtra("paymentMethodDescriptionInfo", paymentMethodDescriptionInfo);
//                intent.putExtra("discount", JsonUtil.getInstance().toJson(discount));
//                intent.putExtra("items", new Gson().toJson(items));
//                intent.putExtra("termsAndConditionsEnabled", termsAndConditionsEnabled);
//                intent.putExtra("discountEnabled", discountEnabled);
//                intent.putExtra("reviewScreenPreference", JsonUtil.getInstance().toJson(reviewScreenPreference));
//                activity.startActivityForResult(intent, MercadoPagoComponents.Activities.REVIEW_AND_CONFIRM_REQUEST_CODE);

                //TODO remove the two trues and validate data.
                TermsAndConditionsModel termsAndConditionsModel = new TermsAndConditionsModel(site.getId(), termsAndConditionsEnabled);
                PaymentModel paymentModel = new PaymentModel(paymentMethod, token, issuer, hasExtraPaymentMethods);
                com.mercadopago.review_and_confirm.ReviewAndConfirmActivity.start(activity, merchantPublicKey, termsAndConditionsModel, paymentModel);
            }
        }

        public static class CardVaultActivityBuilder {
            private Activity activity;
            private String merchantPublicKey;
            private BigDecimal amount;
            private Site site;
            private Boolean installmentsEnabled;
            private Boolean showBankDeals;
            private Boolean escEnabled;
            private PaymentPreference paymentPreference;
            private List<PaymentMethod> paymentMethodList;
            private Card card;
            private PaymentRecovery paymentRecovery;
            private Discount discount;
            private boolean discountEnabled;
            private boolean directDiscountEnabled;
            private boolean installmentsReviewEnabled;
            private boolean automaticSelection;
            private String payerEmail;
            private String payerAccessToken;

            public CardVaultActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public CardVaultActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public CardVaultActivityBuilder setAmount(BigDecimal amount) {
                this.amount = amount;
                return this;
            }

            public CardVaultActivityBuilder setSite(Site site) {
                this.site = site;
                return this;
            }

            public CardVaultActivityBuilder setCard(Card card) {
                this.card = card;
                return this;
            }

            public CardVaultActivityBuilder setAcceptedPaymentMethods(List<PaymentMethod> paymentMethods) {
                this.paymentMethodList = paymentMethods;
                return this;
            }


            public CardVaultActivityBuilder setInstallmentsEnabled(Boolean installmentsEnabled) {
                this.installmentsEnabled = installmentsEnabled;
                return this;
            }

            public CardVaultActivityBuilder setInstallmentsReviewEnabled(boolean installmentsReviewEnabled) {
                this.installmentsReviewEnabled = installmentsReviewEnabled;
                return this;
            }

            public CardVaultActivityBuilder setDiscountEnabled(boolean discountEnabled) {
                this.discountEnabled = discountEnabled;
                return this;
            }

            public CardVaultActivityBuilder setDiscount(Discount discount) {
                this.discount = discount;
                return this;
            }

            public CardVaultActivityBuilder setShowBankDeals(Boolean showBankDeals) {
                this.showBankDeals = showBankDeals;
                return this;
            }

            public CardVaultActivityBuilder setESCEnabled(Boolean escEnabled) {
                this.escEnabled = escEnabled;
                return this;
            }

            public CardVaultActivityBuilder setPaymentPreference(PaymentPreference paymentPreference) {
                this.paymentPreference = paymentPreference;
                return this;
            }

            public CardVaultActivityBuilder setPaymentRecovery(PaymentRecovery paymentRecovery) {
                this.paymentRecovery = paymentRecovery;
                return this;
            }

            public CardVaultActivityBuilder setDirectDiscountEnabled(boolean directDiscountEnabled) {
                this.directDiscountEnabled = directDiscountEnabled;
                return this;
            }

            public CardVaultActivityBuilder setPayerEmail(String payerEmail) {
                this.payerEmail = payerEmail;
                return this;
            }

            public CardVaultActivityBuilder setPayerAccessToken(String accessToken) {
                this.payerAccessToken = accessToken;
                return this;
            }

            public CardVaultActivityBuilder setAutomaticSelection(Boolean automaticSelection) {
                this.automaticSelection = automaticSelection;
                return this;
            }

            public void startActivity() {

                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.merchantPublicKey == null && this.payerAccessToken == null)
                    throw new IllegalStateException("key is null");
                if (this.installmentsEnabled != null && this.installmentsEnabled) {
                    if (this.amount == null) throw new IllegalStateException("amount is null");
                    if (this.site == null) throw new IllegalStateException("site is null");
                }
                startCardVaultActivity();
            }

            private void startCardVaultActivity() {
                Intent cardVaultIntent = new Intent(activity, CardVaultActivity.class);
                cardVaultIntent.putExtra("merchantPublicKey", merchantPublicKey);

                if (amount != null) {
                    cardVaultIntent.putExtra("amount", amount.toString());
                }

                cardVaultIntent.putExtra("site", JsonUtil.getInstance().toJson(site));

                cardVaultIntent.putExtra("installmentsEnabled", installmentsEnabled);

                cardVaultIntent.putExtra("showBankDeals", showBankDeals);

                cardVaultIntent.putExtra("payerEmail", payerEmail);

                cardVaultIntent.putExtra("payerAccessToken", payerAccessToken);

                cardVaultIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));

                cardVaultIntent.putExtra("paymentMethodList", JsonUtil.getInstance().toJson(paymentMethodList));

                cardVaultIntent.putExtra("paymentRecovery", JsonUtil.getInstance().toJson(paymentRecovery));

                cardVaultIntent.putExtra("card", JsonUtil.getInstance().toJson(card));

                cardVaultIntent.putExtra("installmentsReviewEnabled", installmentsReviewEnabled);

                cardVaultIntent.putExtra("discount", JsonUtil.getInstance().toJson(discount));

                cardVaultIntent.putExtra("discountEnabled", discountEnabled);

                cardVaultIntent.putExtra("directDiscountEnabled", directDiscountEnabled);

                cardVaultIntent.putExtra("automaticSelection", automaticSelection);

                cardVaultIntent.putExtra("escEnabled", escEnabled);

                activity.startActivityForResult(cardVaultIntent, CARD_VAULT_REQUEST_CODE);
            }
        }

        public static class GuessingCardActivityBuilder {
            private Activity activity;
            private String merchantPublicKey;
            private String siteId;
            private Boolean showBankDeals;
            private PaymentPreference paymentPreference;
            private List<PaymentMethod> paymentMethodList;
            private Card card;
            private PaymentRecovery paymentRecovery;
            private Boolean requireSecurityCode;
            private Boolean requireIssuer;
            private BigDecimal amount;
            private String payerEmail;
            private Discount discount;
            private Boolean discountEnabled;
            private Boolean directDiscountEnabled;
            private Boolean showDiscount;
            private String payerAccessToken;

            public GuessingCardActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public GuessingCardActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public GuessingCardActivityBuilder setSiteId(String siteId) {
                this.siteId = siteId;
                return this;
            }

            public GuessingCardActivityBuilder setPayerAccessToken(String payerAccessToken) {
                this.payerAccessToken = payerAccessToken;
                return this;
            }

            public GuessingCardActivityBuilder setCard(Card card) {
                this.card = card;
                return this;
            }

            public GuessingCardActivityBuilder setAcceptedPaymentMethods(List<PaymentMethod> paymentMethods) {
                this.paymentMethodList = paymentMethods;
                return this;
            }

            public GuessingCardActivityBuilder setShowBankDeals(Boolean showBankDeals) {
                this.showBankDeals = showBankDeals;
                return this;
            }

            public GuessingCardActivityBuilder setPaymentRecovery(PaymentRecovery paymentRecovery) {
                this.paymentRecovery = paymentRecovery;
                return this;
            }

            public GuessingCardActivityBuilder setPaymentPreference(PaymentPreference paymentPreference) {
                this.paymentPreference = paymentPreference;
                return this;
            }

            public GuessingCardActivityBuilder setRequireIssuer(Boolean requireIssuer) {
                this.requireIssuer = requireIssuer;
                return this;
            }

            public GuessingCardActivityBuilder setRequireSecurityCode(Boolean requireSecurityCode) {
                this.requireSecurityCode = requireSecurityCode;
                return this;
            }

            public GuessingCardActivityBuilder setAmount(BigDecimal amount) {
                this.amount = amount;
                return this;
            }

            public GuessingCardActivityBuilder setPayerEmail(String payerEmail) {
                this.payerEmail = payerEmail;
                return this;
            }

            public GuessingCardActivityBuilder setDiscount(Discount discount) {
                this.discount = discount;
                return this;
            }

            public GuessingCardActivityBuilder setDiscountEnabled(Boolean discountEnabled) {
                this.discountEnabled = discountEnabled;
                return this;
            }

            public GuessingCardActivityBuilder setDirectDiscountEnabled(Boolean directDiscountEnabled) {
                this.directDiscountEnabled = directDiscountEnabled;
                return this;
            }

            public GuessingCardActivityBuilder setShowDiscount(Boolean showDiscount) {
                this.showDiscount = showDiscount;
                return this;
            }

            public void startActivity() {

                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.merchantPublicKey == null && this.payerAccessToken == null)
                    throw new IllegalStateException("key is null");


                startGuessingCardActivity();
            }

            private void startGuessingCardActivity() {
                Intent guessingCardIntent = new Intent(activity, GuessingCardActivity.class);
                guessingCardIntent.putExtra("merchantPublicKey", merchantPublicKey);

                guessingCardIntent.putExtra("siteId", siteId);

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

                guessingCardIntent.putExtra("paymentRecovery", JsonUtil.getInstance().toJson(paymentRecovery));

                guessingCardIntent.putExtra("card", JsonUtil.getInstance().toJson(card));

                if (amount != null) {
                    guessingCardIntent.putExtra("amount", amount.toString());
                }

                guessingCardIntent.putExtra("payerEmail", payerEmail);

                guessingCardIntent.putExtra("payerAccessToken", payerAccessToken);

                guessingCardIntent.putExtra("discount", JsonUtil.getInstance().toJson(discount));

                guessingCardIntent.putExtra("discountEnabled", discountEnabled);

                guessingCardIntent.putExtra("directDiscountEnabled", directDiscountEnabled);

                guessingCardIntent.putExtra("showDiscount", showDiscount);

                activity.startActivityForResult(guessingCardIntent, GUESSING_CARD_REQUEST_CODE);
            }
        }

        public static class PaymentMethodsActivityBuilder {

            private Activity activity;
            private String merchantPublicKey;
            private PaymentPreference paymentPreference;
            private Boolean showBankDeals;

            public PaymentMethodsActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public PaymentMethodsActivityBuilder setShowBankDeals(Boolean showBankDeals) {
                this.showBankDeals = showBankDeals;
                return this;
            }

            public PaymentMethodsActivityBuilder setPaymentPreference(PaymentPreference paymentPreference) {
                this.paymentPreference = paymentPreference;
                return this;
            }

            public PaymentMethodsActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public void startActivity() {
                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.merchantPublicKey == null)
                    throw new IllegalStateException("public key is null");
                startPaymentMethodsActivity();
            }

            private void startPaymentMethodsActivity() {
                Intent paymentMethodsIntent = new Intent(activity, PaymentMethodsActivity.class);
                paymentMethodsIntent.putExtra("merchantPublicKey", merchantPublicKey);
                paymentMethodsIntent.putExtra("showBankDeals", showBankDeals);
                paymentMethodsIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));

                activity.startActivityForResult(paymentMethodsIntent, PAYMENT_METHODS_REQUEST_CODE);
            }
        }

        public static class IssuersActivityBuilder {
            private Activity activity;
            private CardInfo cardInformation;
            private String merchantPublicKey;
            private PaymentMethod paymentMethod;
            private List<Issuer> issuers;
            private String payerAccessToken;

            public IssuersActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public IssuersActivityBuilder setCardInfo(CardInfo cardInformation) {
                this.cardInformation = cardInformation;
                return this;
            }

            public IssuersActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public IssuersActivityBuilder setPaymentMethod(PaymentMethod paymentMethod) {
                this.paymentMethod = paymentMethod;
                return this;
            }

            public IssuersActivityBuilder setIssuers(List<Issuer> issuers) {
                this.issuers = issuers;
                return this;
            }

            public IssuersActivityBuilder setPayerAccessToken(String payerAccessToken) {
                this.payerAccessToken = payerAccessToken;
                return this;
            }

            public void startActivity() {
                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.merchantPublicKey == null && this.payerAccessToken == null)
                    throw new IllegalStateException("key is null");
                if (this.paymentMethod == null)
                    throw new IllegalStateException("payment method is null");
                startIssuersActivity();
            }

            private void startIssuersActivity() {
                Intent intent = new Intent(activity, IssuersActivity.class);
                intent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
                intent.putExtra("merchantPublicKey", merchantPublicKey);
                intent.putExtra("payerAccessToken", payerAccessToken);
                intent.putExtra("issuers", JsonUtil.getInstance().toJson(issuers));
                intent.putExtra("cardInfo", JsonUtil.getInstance().toJson(cardInformation));
                activity.startActivityForResult(intent, ISSUERS_REQUEST_CODE);
            }
        }

        public static class InstallmentsActivityBuilder {

            private Activity activity;
            private BigDecimal amount;
            private Site site;
            private CardInfo cardInfo;
            private String merchantPublicKey;
            private List<PayerCost> payerCosts;
            private Issuer issuer;
            private PaymentMethod paymentMethod;
            private PaymentPreference paymentPreference;
            private String payerEmail;
            private Discount discount;
            private Boolean discountEnabled;
            private Boolean installmentsEnabled;
            private Boolean installmentsReviewEnabled;
            private String payerAccessToken;

            public InstallmentsActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public InstallmentsActivityBuilder setAmount(BigDecimal amount) {
                this.amount = amount;
                return this;
            }

            public InstallmentsActivityBuilder setSite(Site site) {
                this.site = site;
                return this;
            }

            public InstallmentsActivityBuilder setCardInfo(CardInfo cardInformation) {
                this.cardInfo = cardInformation;
                return this;
            }

            public InstallmentsActivityBuilder setPaymentPreference(PaymentPreference paymentPreference) {
                this.paymentPreference = paymentPreference;
                return this;
            }

            public InstallmentsActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public InstallmentsActivityBuilder setPayerAccessToken(String payerAccessToken) {
                this.payerAccessToken = payerAccessToken;
                return this;
            }

            public InstallmentsActivityBuilder setPaymentMethod(PaymentMethod paymentMethod) {
                this.paymentMethod = paymentMethod;
                return this;
            }

            public InstallmentsActivityBuilder setIssuer(Issuer issuer) {
                this.issuer = issuer;
                return this;
            }

            public InstallmentsActivityBuilder setPayerCosts(List<PayerCost> payerCosts) {
                this.payerCosts = payerCosts;
                return this;
            }

            public InstallmentsActivityBuilder setPayerEmail(String payerEmail) {
                this.payerEmail = payerEmail;
                return this;
            }

            public InstallmentsActivityBuilder setDiscount(Discount discount) {
                this.discount = discount;
                return this;
            }

            public InstallmentsActivityBuilder setDiscountEnabled(Boolean discountEnabled) {
                this.discountEnabled = discountEnabled;
                return this;
            }

            public InstallmentsActivityBuilder setInstallmentsReviewEnabled(Boolean installmentsReviewEnabled) {
                this.installmentsReviewEnabled = installmentsReviewEnabled;
                return this;
            }

            public InstallmentsActivityBuilder setInstallmentsEnabled(Boolean installmentsEnabled) {
                this.installmentsEnabled = installmentsEnabled;
                return this;
            }

            public void startActivity() {
                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.site == null) throw new IllegalStateException("site is null");
                if (this.amount == null) throw new IllegalStateException("amount is null");
                if (payerCosts == null) {
                    if (this.merchantPublicKey == null && this.payerAccessToken == null)
                        throw new IllegalStateException("key is null");
                    if (this.issuer == null) throw new IllegalStateException("issuer is null");
                    if (this.paymentMethod == null)
                        throw new IllegalStateException("payment method is null");
                }
                startInstallmentsActivity();
            }

            private void startInstallmentsActivity() {
                Intent intent = new Intent(activity, InstallmentsActivity.class);

                if (amount != null) {
                    intent.putExtra("amount", amount.toString());
                }
                intent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
                intent.putExtra("merchantPublicKey", merchantPublicKey);
                intent.putExtra("payerAccessToken", payerAccessToken);
                intent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
                intent.putExtra("site", JsonUtil.getInstance().toJson(site));
                intent.putExtra("payerCosts", JsonUtil.getInstance().toJson(payerCosts));
                intent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));
                intent.putExtra("cardInfo", JsonUtil.getInstance().toJson(cardInfo));
                intent.putExtra("payerEmail", payerEmail);
                intent.putExtra("discount", JsonUtil.getInstance().toJson(discount));
                intent.putExtra("discountEnabled", discountEnabled);
                intent.putExtra("installmentsEnabled", installmentsEnabled);
                intent.putExtra("installmentsReviewEnabled", installmentsReviewEnabled);

                activity.startActivityForResult(intent, INSTALLMENTS_REQUEST_CODE);
            }
        }

        public static class SecurityCodeActivityBuilder {
            private Activity activity;
            private CardInfo cardInformation;
            private String merchantPublicKey;
            private String siteId;
            private String payerAccessToken;
            private PaymentMethod paymentMethod;
            private Integer congratsDisplay;
            private Discount discount;
            private boolean discountEnabled;
            private List<Issuer> issuers;
            private Card card;
            private Token token;
            private boolean escEnabled;
            private PaymentRecovery paymentRecovery;
            private String reason;

            public SecurityCodeActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public SecurityCodeActivityBuilder setCardInfo(CardInfo cardInformation) {
                this.cardInformation = cardInformation;
                return this;
            }

            public SecurityCodeActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public SecurityCodeActivityBuilder setSiteId(String siteId) {
                this.siteId = siteId;
                return this;
            }

            public SecurityCodeActivityBuilder setPayerAccessToken(String payerAccessToken) {
                this.payerAccessToken = payerAccessToken;
                return this;
            }

            public SecurityCodeActivityBuilder setESCEnabled(boolean escEnabled) {
                this.escEnabled = escEnabled;
                return this;
            }

            public SecurityCodeActivityBuilder setTrackingReason(String reason) {
                this.reason = reason;
                return this;
            }

            public SecurityCodeActivityBuilder setPaymentRecovery(PaymentRecovery paymentRecovery) {
                this.paymentRecovery = paymentRecovery;
                return this;
            }

            public SecurityCodeActivityBuilder setPaymentMethod(PaymentMethod paymentMethod) {
                this.paymentMethod = paymentMethod;
                return this;
            }

            public SecurityCodeActivityBuilder setCard(Card card) {
                this.card = card;
                return this;
            }

            public SecurityCodeActivityBuilder setToken(Token token) {
                this.token = token;
                return this;
            }

            public SecurityCodeActivityBuilder setIssuers(List<Issuer> issuers) {
                this.issuers = issuers;
                return this;
            }

            public void startActivity() {
                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.merchantPublicKey == null) throw new IllegalStateException("key is null");
                if (this.cardInformation == null)
                    throw new IllegalStateException("card info is null");
                if (this.paymentMethod == null)
                    throw new IllegalStateException("payment method is null");
                if (this.card != null && this.token != null && this.paymentRecovery == null)
                    throw new IllegalStateException("can't start with card and token at the same time if it's not recoverable");
                if (this.card == null && this.token == null)
                    throw new IllegalStateException("card and token can't both be null");

                startSecurityCodeActivity();
            }

            private void startSecurityCodeActivity() {
                Intent intent = new Intent(activity, SecurityCodeActivity.class);
                intent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
                intent.putExtra("token", JsonUtil.getInstance().toJson(token));
                intent.putExtra("card", JsonUtil.getInstance().toJson(card));
                intent.putExtra("merchantPublicKey", merchantPublicKey);
                intent.putExtra("payerAccessToken", payerAccessToken);
                intent.putExtra("siteId", siteId);
                intent.putExtra("cardInfo", JsonUtil.getInstance().toJson(cardInformation));
                intent.putExtra("paymentRecovery", JsonUtil.getInstance().toJson(paymentRecovery));
                intent.putExtra("escEnabled", escEnabled);
                intent.putExtra("reason", reason);
                activity.startActivityForResult(intent, SECURITY_CODE_REQUEST_CODE);
            }
        }

        public static class PaymentTypesActivityBuilder {
            private Activity activity;
            private CardInfo cardInformation;
            private String merchantPublicKey;
            private List<PaymentMethod> paymentMethods;
            private List<PaymentType> paymentTypes;

            public PaymentTypesActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public PaymentTypesActivityBuilder setCardInfo(CardInfo cardInformation) {
                this.cardInformation = cardInformation;
                return this;
            }

            public PaymentTypesActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public PaymentTypesActivityBuilder setPaymentMethods(List<PaymentMethod> paymentMethods) {
                this.paymentMethods = paymentMethods;
                return this;
            }

            public PaymentTypesActivityBuilder setPaymentTypes(List<PaymentType> paymentTypes) {
                this.paymentTypes = paymentTypes;
                return this;
            }

            public void startActivity() {
                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.merchantPublicKey == null) throw new IllegalStateException("key is null");
                if (this.paymentMethods == null)
                    throw new IllegalStateException("payment method list is null");
                if (this.paymentTypes == null)
                    throw new IllegalStateException("payment types list is null");

                startSecurityCodeActivity();
            }

            private void startSecurityCodeActivity() {
                Intent intent = new Intent(activity, PaymentTypesActivity.class);
                intent.putExtra("paymentMethods", JsonUtil.getInstance().toJson(paymentMethods));
                intent.putExtra("paymentTypes", JsonUtil.getInstance().toJson(paymentTypes));
                intent.putExtra("merchantPublicKey", merchantPublicKey);
                intent.putExtra("cardInfo", JsonUtil.getInstance().toJson(cardInformation));
                activity.startActivityForResult(intent, PAYMENT_TYPES_REQUEST_CODE);
            }
        }

        public static class PayerInformationActivityBuilder {
            private Activity activity;
            private String merchantPublicKey;
            private String payerAccessToken;

            public PayerInformationActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public PayerInformationActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public PayerInformationActivityBuilder setPayerAccessToken(String payerAccessToken) {
                this.payerAccessToken = payerAccessToken;
                return this;
            }

            public void startActivity() {

                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.merchantPublicKey == null)
                    throw new IllegalStateException("key is null");
                startPayerInformationActivity();
            }

            private void startPayerInformationActivity() {
                Intent payerInformationIntent = new Intent(activity, PayerInformationActivity.class);

                payerInformationIntent.putExtra("merchantPublicKey", merchantPublicKey);
                payerInformationIntent.putExtra("payerAccessToken", payerAccessToken);

                activity.startActivityForResult(payerInformationIntent, PAYER_INFORMATION_REQUEST_CODE);
            }
        }

        public static class DiscountsActivityBuilder {
            private Activity activity;
            private String merchantPublicKey;

            private BigDecimal amount;
            private Boolean directDiscountEnabled;
            private Discount discount;
            private String payerEmail;

            public DiscountsActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public DiscountsActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public DiscountsActivityBuilder setDirectDiscountEnabled(Boolean directDiscountEnabled) {
                this.directDiscountEnabled = directDiscountEnabled;
                return this;
            }

            public DiscountsActivityBuilder setAmount(BigDecimal amount) {
                this.amount = amount;
                return this;
            }

            public DiscountsActivityBuilder setDiscount(Discount discount) {
                this.discount = discount;
                return this;
            }

            public DiscountsActivityBuilder setPayerEmail(String payerEmail) {
                this.payerEmail = payerEmail;
                return this;
            }

            public void startActivity() {
                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.merchantPublicKey == null) throw new IllegalStateException("key is null");
                if (this.amount == null) throw new IllegalStateException("amount is null");

                startDiscountsActivity();
            }

            private void startDiscountsActivity() {
                Intent discountsIntent = new Intent(activity, DiscountsActivity.class);
                discountsIntent.putExtra("merchantPublicKey", merchantPublicKey);
                discountsIntent.putExtra("amount", amount.toString());
                discountsIntent.putExtra("directDiscountEnabled", directDiscountEnabled);
                discountsIntent.putExtra("discount", JsonUtil.getInstance().toJson(discount));
                discountsIntent.putExtra("payerEmail", payerEmail);

                activity.startActivityForResult(discountsIntent, DISCOUNTS_REQUEST_CODE);
            }
        }

        public static class PaymentResultActivityBuilder {
            private Activity activity;
            private String merchantPublicKey;
            private String payerAccessToken;
            private Integer congratsDisplay;
            private Discount discount;
            private boolean discountEnabled;
            private PaymentResult paymentResult;
            private Site site;
            private BigDecimal amount;
            private PaymentResultScreenPreference paymentResultScreenPreference;
            private ServicePreference servicePreference;

            public PaymentResultActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public PaymentResultActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public PaymentResultActivityBuilder setPayerAccessToken(String accessToken) {
                this.payerAccessToken = accessToken;
                return this;
            }

            public PaymentResultActivityBuilder setDiscount(Discount discount) {
                this.discount = discount;
                return this;
            }

            public PaymentResultActivityBuilder setCongratsDisplay(Integer congratsDisplay) {
                this.congratsDisplay = congratsDisplay;
                return this;
            }

            public PaymentResultActivityBuilder setSite(Site site) {
                this.site = site;
                return this;
            }

            public PaymentResultActivityBuilder setAmount(BigDecimal amount) {
                this.amount = amount;
                return this;
            }

            public PaymentResultActivityBuilder setPaymentResultScreenPreference(PaymentResultScreenPreference preference) {
                this.paymentResultScreenPreference = preference;
                return this;
            }

            public PaymentResultActivityBuilder setPaymentResult(PaymentResult paymentResult) {
                this.paymentResult = paymentResult;
                return this;
            }

            public PaymentResultActivityBuilder setDiscountEnabled(boolean discountEnabled) {
                this.discountEnabled = discountEnabled;
                return this;
            }

            public PaymentResultActivityBuilder setServicePreference(ServicePreference servicePreference) {
                this.servicePreference = servicePreference;
                return this;
            }

            public void startActivity() {
                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.paymentResult == null)
                    throw new IllegalStateException("payment result is null");
                if (this.merchantPublicKey == null)
                    throw new IllegalStateException("public key is null");

                startPaymentResultActivity();
            }

            private void startPaymentResultActivity() {
                Intent resultIntent = new Intent(activity, PaymentResultActivity.class);
                resultIntent.putExtra("merchantPublicKey", merchantPublicKey);
                resultIntent.putExtra("payerAccessToken", payerAccessToken);
                resultIntent.putExtra("discount", JsonUtil.getInstance().toJson(discount));
                resultIntent.putExtra("discountEnabled", discountEnabled);
                resultIntent.putExtra("congratsDisplay", congratsDisplay);
                resultIntent.putExtra("paymentResult", JsonUtil.getInstance().toJson(paymentResult));
                resultIntent.putExtra("site", JsonUtil.getInstance().toJson(site));
                resultIntent.putExtra("paymentResultScreenPreference", JsonUtil.getInstance().toJson(paymentResultScreenPreference));
                resultIntent.putExtra("servicePreference", JsonUtil.getInstance().toJson(servicePreference));
                if (amount != null) {
                    resultIntent.putExtra("amount", amount.toString());
                }

                activity.startActivityForResult(resultIntent, PAYMENT_RESULT_REQUEST_CODE);
            }
        }

        public static class BankDealsActivityBuilder {

            private Activity activity;
            private String merchantPublicKey;
            private List<BankDeal> bankDeals;
            private String payerAccessToken;

            public BankDealsActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public BankDealsActivityBuilder setBankDeals(List<BankDeal> bankDeals) {
                this.bankDeals = bankDeals;
                return this;
            }

            public BankDealsActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public BankDealsActivityBuilder setPayerAccessToken(String payerAccessToken) {
                this.payerAccessToken = payerAccessToken;
                return this;
            }

            public void startActivity() {
                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.merchantPublicKey == null)
                    throw new IllegalStateException("public key is null");
                startBankDealsActivity();
            }

            private void startBankDealsActivity() {
                Intent bankDealsIntent = new Intent(activity, BankDealsActivity.class);
                bankDealsIntent.putExtra("merchantPublicKey", merchantPublicKey);
                bankDealsIntent.putExtra("payerAccessToken", payerAccessToken);
                if (bankDeals != null) {
                    bankDealsIntent.putExtra("bankDeals", JsonUtil.getInstance().toJson(bankDeals));
                }
                activity.startActivityForResult(bankDealsIntent, BANK_DEALS_REQUEST_CODE);
            }
        }

        public static class ReviewPaymentMethodsActivityBuilder {

            private Activity activity;
            private List<PaymentMethod> paymentMethods;
            private String publicKey;

            public ReviewPaymentMethodsActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public ReviewPaymentMethodsActivityBuilder setPaymentMethods(List<PaymentMethod> paymentMethods) {
                this.paymentMethods = paymentMethods;
                return this;
            }

            public ReviewPaymentMethodsActivityBuilder setPublicKey(String publicKey) {
                this.publicKey = publicKey;
                return this;
            }

            public void startActivity() {
                if (this.activity == null)
                    throw new IllegalStateException("activity is null");
                if (this.publicKey == null)
                    throw new IllegalStateException("public key is null");
                if (this.paymentMethods == null)
                    throw new IllegalStateException("payment methods is null");
                if (this.paymentMethods.isEmpty())
                    throw new IllegalStateException("payment methods is empty");
                startReviewPaymentMethodsActivity();
            }

            private void startReviewPaymentMethodsActivity() {
                Intent intent = new Intent(activity, ReviewPaymentMethodsActivity.class);
                intent.putExtra("paymentMethods", JsonUtil.getInstance().toJson(paymentMethods));
                intent.putExtra("publicKey", publicKey);
                activity.startActivityForResult(intent, REVIEW_PAYMENT_METHODS_REQUEST_CODE);
            }
        }
    }

    public static class Views {

        private Views() {
        }

        public static class ReviewPaymentMethodOnBuilder {

            private Context context;
            private String currencyId;
            private PaymentMethod paymentMethod;
            private PayerCost payerCost;
            private CardInfo cardInfo;
            private OnReviewChange reviewChangeCallback;
            private Boolean editionEnabled;
            private Site site;

            public ReviewPaymentMethodOnBuilder setContext(Context context) {
                this.context = context;
                return this;
            }

            public ReviewPaymentMethodOnBuilder setCurrencyId(String currencyId) {
                this.currencyId = currencyId;
                return this;
            }

            public ReviewPaymentMethodOnBuilder setPaymentMethod(PaymentMethod paymentMethod) {
                this.paymentMethod = paymentMethod;
                return this;
            }

            public ReviewPaymentMethodOnBuilder setPayerCost(PayerCost payerCost) {
                this.payerCost = payerCost;
                return this;
            }

            public ReviewPaymentMethodOnBuilder setCardInfo(CardInfo cardInfo) {
                this.cardInfo = cardInfo;
                return this;
            }

            public ReviewPaymentMethodOnBuilder setReviewChangeCallback(OnReviewChange reviewChangeCallback) {
                this.reviewChangeCallback = reviewChangeCallback;
                return this;
            }

            public ReviewPaymentMethodOnBuilder setEditionEnabled(Boolean editionEnabled) {
                this.editionEnabled = editionEnabled;
                return this;
            }

            public ReviewPaymentMethodOnBuilder setSite(Site site) {
                this.site = site;
                return this;
            }

            public Reviewable build() {
                return new ReviewPaymentOnView(context, paymentMethod, cardInfo, payerCost, site, reviewChangeCallback, editionEnabled);
            }

        }

        public static class SummaryViewBuilder {
            private Context context;
            private String currencyId;
            private BigDecimal amount;
            private PayerCost payerCost;
            private PaymentMethod paymentMethod;
            private OnConfirmPaymentCallback callback;
            private Discount discount;
            private Summary summary;
            private Issuer issuer;
            private Site site;

            public SummaryViewBuilder setSite(Site site) {
                this.site = site;
                return this;
            }

            public SummaryViewBuilder setIssuer(Issuer issuer) {
                this.issuer = issuer;
                return this;
            }

            public SummaryViewBuilder setContext(Context context) {
                this.context = context;
                return this;
            }

            public SummaryViewBuilder setCurrencyId(String currencyId) {
                this.currencyId = currencyId;
                return this;
            }

            public SummaryViewBuilder setAmount(BigDecimal amount) {
                this.amount = amount;
                return this;
            }

            public SummaryViewBuilder setPayerCost(PayerCost payerCost) {
                this.payerCost = payerCost;
                return this;
            }

            public SummaryViewBuilder setPaymentMethod(PaymentMethod paymentMethod) {
                this.paymentMethod = paymentMethod;
                return this;
            }

            public SummaryViewBuilder setDiscount(Discount discount) {
                this.discount = discount;
                return this;
            }

            public SummaryViewBuilder setConfirmPaymentCallback(OnConfirmPaymentCallback callback) {
                this.callback = callback;
                return this;
            }

            public SummaryViewBuilder setSummary(Summary summary) {
                this.summary = summary;
                return this;
            }

            public SummaryView build() {
                return new SummaryView(context, paymentMethod, payerCost, amount, discount, currencyId, site, issuer, summary, callback);
            }
        }

        public static class ReviewItemsViewBuilder {
            private Context context;
            private String currencyId;
            private List<Item> items;
            private ReviewScreenPreference reviewScreenPreference;

            public ReviewItemsViewBuilder() {
                items = new ArrayList<>();
            }

            public ReviewItemsViewBuilder setContext(Context context) {
                this.context = context;
                return this;
            }

            public ReviewItemsViewBuilder setCurrencyId(String currencyId) {
                this.currencyId = currencyId;
                return this;
            }

            public ReviewItemsViewBuilder setReviewScreenPreference(ReviewScreenPreference reviewScreenPreference) {
                this.reviewScreenPreference = reviewScreenPreference;
                return this;
            }

            public ReviewItemsViewBuilder addItem(Item item) {
                this.items.add(item);
                return this;
            }

            public ReviewItemsViewBuilder addItems(List<Item> items) {
                this.items.addAll(items);
                return this;
            }

            public ReviewItemsView build() {
                return new ReviewItemsView(context, items, currencyId, reviewScreenPreference);
            }
        }

        public static class ReviewPaymentMethodOffBuilder {

            private Context context;
            private PaymentMethod paymentMethod;
            private String paymentMethodCommentInfo;
            private String paymentMethodDescriptionInfo;
            private BigDecimal amount;
            private Site site;
            private OnReviewChange reviewChangeCallback;
            private Boolean editionEnabled;

            public ReviewPaymentMethodOffBuilder setContext(Context context) {
                this.context = context;
                return this;
            }

            public ReviewPaymentMethodOffBuilder setPaymentMethod(PaymentMethod paymentMethod) {
                this.paymentMethod = paymentMethod;
                return this;
            }

            public ReviewPaymentMethodOffBuilder setPaymentMethodCommentInfo(String paymentMethodCommentInfo) {
                this.paymentMethodCommentInfo = paymentMethodCommentInfo;
                return this;
            }

            public ReviewPaymentMethodOffBuilder setPaymentMethodDescriptionInfo(String paymentMethodDescriptionInfo) {
                this.paymentMethodDescriptionInfo = paymentMethodDescriptionInfo;
                return this;
            }

            public ReviewPaymentMethodOffBuilder setAmount(BigDecimal amount) {
                this.amount = amount;
                return this;
            }

            public ReviewPaymentMethodOffBuilder setSite(Site site) {
                this.site = site;
                return this;
            }

            public ReviewPaymentMethodOffBuilder setReviewChangeCallback(OnReviewChange reviewChangeCallback) {
                this.reviewChangeCallback = reviewChangeCallback;
                return this;
            }

            public ReviewPaymentMethodOffBuilder setEditionEnabled(Boolean editionEnabled) {
                this.editionEnabled = editionEnabled;
                return this;
            }

            public ReviewPaymentOffView build() {
                return new ReviewPaymentOffView(context, paymentMethod, paymentMethodCommentInfo, paymentMethodDescriptionInfo, amount, site, reviewChangeCallback, editionEnabled);
            }

        }
    }
}
