package com.mercadopago.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.mercadopago.CustomerCardsActivity;
import com.mercadopago.ReviewAndConfirmActivity;
import com.mercadopago.callbacks.OnReviewChange;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Item;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Reviewable;
import com.mercadopago.model.Site;
import com.mercadopago.preferences.PaymentPreference;
import com.mercadopago.preferences.ReviewScreenPreference;
import com.mercadopago.uicontrollers.discounts.DiscountRowView;
import com.mercadopago.uicontrollers.installments.InstallmentsReviewView;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewItemsView;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewPaymentOffView;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewPaymentOnView;
import com.mercadopago.uicontrollers.savedcards.SavedCardRowView;
import com.mercadopago.uicontrollers.savedcards.SavedCardView;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.MercadoPagoUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mreverter on 6/10/16.
 */
@Deprecated
public class MercadoPagoUI {

    public static class Activities {

        public static final int CUSTOMER_CARDS_REQUEST_CODE = 0;

        /**
         * Card selection, responds: card
         * Requires:
         * Activity {@link android.app.Activity}
         * Cards {@link List}&lt;{@link Customer}&gt;
         * <p>
         * Get results as Jsons, using {@link JsonUtil#fromJson(String, Class)}
         * in method {@link android.app.Activity#onActivityResult(int, int, Intent)}  of the caller activity
         * from the Intent with keys:
         * "card" {@link Card}
         * <p>
         * RESULT_CANCEL:
         * "mpException" {@link PaymentMethod}
         */
        public static class SavedCardsActivityBuilder {

            private Activity activity;
            private List<Card> cards;
            private String title;
            private String customActionMessage;
            private PaymentPreference paymentPreference;
            private Integer selectionImageResId;
            private String selectionConfirmPromptText;
            private String merchantBaseUrl;
            private String merchantGetCustomerUri;
            private String merchantAccessToken;

            public SavedCardsActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public SavedCardsActivityBuilder setSelectionImage(@DrawableRes Integer drawableResId) {
                this.selectionImageResId = drawableResId;
                return this;
            }

            public SavedCardsActivityBuilder setSelectionConfirmPromptText(String text) {
                this.selectionConfirmPromptText = text;
                return this;
            }

            public SavedCardsActivityBuilder setCards(List<Card> cards) {
                this.cards = cards;
                return this;
            }

            public SavedCardsActivityBuilder setPaymentPreference(PaymentPreference paymentPreference) {
                this.paymentPreference = paymentPreference;
                return this;
            }

            public SavedCardsActivityBuilder setTitle(String title) {
                this.title = title;
                return this;
            }

            public SavedCardsActivityBuilder setMerchantBaseUrl(String merchantBaseUrl) {
                this.merchantBaseUrl = merchantBaseUrl;
                return this;
            }

            public SavedCardsActivityBuilder setMerchantGetCustomerUri(String merchantGetCustomerUri) {
                this.merchantGetCustomerUri = merchantGetCustomerUri;
                return this;
            }

            public SavedCardsActivityBuilder setMerchantAccessToken(String merchantAccessToken) {
                this.merchantAccessToken = merchantAccessToken;
                return this;
            }

            public SavedCardsActivityBuilder setCustomActionMessage(String customActionMessage) {
                this.customActionMessage = customActionMessage;
                return this;
            }

            public void startActivity() {

                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.cards == null && (TextUtils.isEmpty(merchantBaseUrl)
                        || TextUtils.isEmpty(merchantGetCustomerUri)
                        || TextUtils.isEmpty(merchantAccessToken))) {
                    throw new IllegalStateException("cards or merchant server info required");
                }
                startCustomerCardsActivity();
            }

            private void startCustomerCardsActivity() {
                Intent customerCardsIntent = new Intent(activity, CustomerCardsActivity.class);
                Gson gson = new Gson();
                customerCardsIntent.putExtra("merchantBaseUrl", merchantBaseUrl);
                customerCardsIntent.putExtra("merchantGetCustomerUri", merchantGetCustomerUri);
                customerCardsIntent.putExtra("merchantAccessToken", merchantAccessToken);
                customerCardsIntent.putExtra("cards", gson.toJson(cards));
                customerCardsIntent.putExtra("title", title);
                customerCardsIntent.putExtra("selectionConfirmPromptText", selectionConfirmPromptText);
                customerCardsIntent.putExtra("selectionImageResId", selectionImageResId);
                customerCardsIntent.putExtra("customActionMessage", customActionMessage);
                customerCardsIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));
                activity.startActivityForResult(customerCardsIntent, CUSTOMER_CARDS_REQUEST_CODE);
            }
        }

        public static class ReviewAndConfirmBuilder {
            private Activity activity;
            private PaymentMethod paymentMethod;
            private PayerCost payerCost;
            private BigDecimal amount;
            private Site site;
            private CardInfo cardInfo;
            private Boolean editionEnabled;
            private String paymentMethodCommentInfo;
            private String paymentMethodDescriptionInfo;
            private List<Item> items;
            private Discount discount;

            public ReviewAndConfirmBuilder setActivity(Activity activity) {
                this.activity = activity;
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

            public ReviewAndConfirmBuilder setCardInfo(CardInfo cardInfo) {
                this.cardInfo = cardInfo;
                return this;
            }

            public ReviewAndConfirmBuilder setEditionEnabled(Boolean editionEnabled) {
                this.editionEnabled = editionEnabled;
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

            public void startActivity() {

                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.paymentMethod == null)
                    throw new IllegalStateException("payment method is null");
                if (this.items == null) throw new IllegalStateException("items not set");
                if (MercadoPagoUtil.isCard(paymentMethod.getPaymentTypeId())) {
                    if (this.payerCost == null)
                        throw new IllegalStateException("payer cost is null");
                    if (this.cardInfo == null) throw new IllegalStateException("card info is null");
                }
                startReviewAndConfirmActivity();
            }

            private void startReviewAndConfirmActivity() {
                Intent intent = new Intent(activity, ReviewAndConfirmActivity.class);
                intent.putExtra("editionEnabled", editionEnabled);
                intent.putExtra("amount", amount.toString());
                intent.putExtra("site", JsonUtil.getInstance().toJson(site));
                intent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
                intent.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
                intent.putExtra("cardInfo", JsonUtil.getInstance().toJson(cardInfo));
                intent.putExtra("paymentMethodCommentInfo", paymentMethodCommentInfo);
                intent.putExtra("paymentMethodDescriptionInfo", paymentMethodDescriptionInfo);
                intent.putExtra("discount", JsonUtil.getInstance().toJson(discount));
                intent.putExtra("items", new Gson().toJson(items));

                activity.startActivityForResult(intent, MercadoPagoComponents.Activities.REVIEW_AND_CONFIRM_REQUEST_CODE);
            }
        }
    }

    public static class Views {

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

            public ReviewPaymentMethodOnBuilder setSite(Site site) {
                this.site = site;
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

            public Reviewable build() {
                return new ReviewPaymentOnView(context, paymentMethod, cardInfo, payerCost, site, reviewChangeCallback, editionEnabled);
            }
        }

        public static class SavedCardViewBuilder {
            private Context context;
            private Card card;
            private Integer selectionImageResId;

            public SavedCardViewBuilder setContext(Context context) {
                this.context = context;
                return this;
            }

            public SavedCardViewBuilder setCard(Card card) {
                this.card = card;
                return this;
            }

            public SavedCardViewBuilder setSelectionImage(@DrawableRes Integer drawableResId) {
                this.selectionImageResId = drawableResId;
                return this;
            }

            public SavedCardView build() {
                return new SavedCardRowView(context, card, selectionImageResId);
            }
        }

        public static class DiscountRowViewBuilder {
            private Context context;
            private Discount discount;
            private BigDecimal transactionAmount;
            private String currencyId;
            private Boolean shortRowEnabled;
            private Boolean discountEnabled;
            private Boolean showArrow;
            private Boolean showSeparator;

            public DiscountRowViewBuilder setContext(Context context) {
                this.context = context;
                return this;
            }

            public DiscountRowViewBuilder setDiscount(Discount discount) {
                this.discount = discount;
                return this;
            }

            public DiscountRowViewBuilder setTransactionAmount(BigDecimal transactionAmount) {
                this.transactionAmount = transactionAmount;
                return this;
            }

            public DiscountRowViewBuilder setCurrencyId(String currencyId) {
                this.currencyId = currencyId;
                return this;
            }

            public DiscountRowViewBuilder setShortRowEnabled(Boolean shortRowEnabled) {
                this.shortRowEnabled = shortRowEnabled;
                return this;
            }

            public DiscountRowViewBuilder setDiscountEnabled(Boolean discountEnabled) {
                this.discountEnabled = discountEnabled;
                return this;
            }

            public DiscountRowViewBuilder setShowArrow(Boolean showArrow) {
                this.showArrow = showArrow;
                return this;
            }

            public DiscountRowViewBuilder setShowSeparator(Boolean showSeparator) {
                this.showSeparator = showSeparator;
                return this;
            }

            public DiscountRowView build() {
                return new DiscountRowView(context, discount, transactionAmount, currencyId, shortRowEnabled,
                        discountEnabled, showArrow, showSeparator);
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
            private String paymentMethodComment;
            private String paymentMethodDescription;
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

            public ReviewPaymentMethodOffBuilder setPaymentMethodCommentInfo(String paymentMethodComment) {
                this.paymentMethodComment = paymentMethodComment;
                return this;
            }

            public ReviewPaymentMethodOffBuilder setPaymentMethodDescriptionInfo(String paymentMethodDescription) {
                this.paymentMethodDescription = paymentMethodDescription;
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
                return new ReviewPaymentOffView(context, paymentMethod, paymentMethodComment, paymentMethodDescription, amount, site, reviewChangeCallback, editionEnabled);
            }
        }

        public static class InstallmentsReviewViewBuilder {
            private Context context;
            private PayerCost payerCost;
            private String currencyId;

            public InstallmentsReviewViewBuilder setContext(Context context) {
                this.context = context;
                return this;
            }

            public InstallmentsReviewViewBuilder setPayerCost(PayerCost payerCost) {
                this.payerCost = payerCost;
                return this;
            }

            public InstallmentsReviewViewBuilder setCurrencyId(String currencyId) {
                this.currencyId = currencyId;
                return this;
            }

            public InstallmentsReviewView build() {
                return new InstallmentsReviewView(context, payerCost, currencyId);
            }
        }
    }
}
