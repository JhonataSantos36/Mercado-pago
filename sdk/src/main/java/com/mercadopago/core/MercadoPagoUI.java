package com.mercadopago.core;

import com.google.gson.Gson;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;

import com.mercadopago.CustomerCardsActivity;
import com.mercadopago.ReviewAndConfirmActivity;
import com.mercadopago.callbacks.OnConfirmPaymentCallback;
import com.mercadopago.callbacks.OnReviewChange;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.model.Card;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.preferences.PaymentPreference;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Item;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Reviewable;
import com.mercadopago.model.Site;
import com.mercadopago.uicontrollers.discounts.DiscountRowView;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewItemsView;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewPaymentOffView;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewPaymentOnView;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewSummaryView;
import com.mercadopago.uicontrollers.savedcards.SavedCardRowView;
import com.mercadopago.uicontrollers.savedcards.SavedCardView;
import com.mercadopago.uicontrollers.savedcards.SavedCardsListView;
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
         * Cards {@link List}&lt;{@link com.mercadopago.model.Customer}&gt;
         * <p>
         * Get results as Jsons, using {@link com.mercadopago.util.JsonUtil#fromJson(String, Class)}
         * in method {@link android.app.Activity#onActivityResult(int, int, Intent)}  of the caller activity
         * from the Intent with keys:
         * "card" {@link com.mercadopago.model.Card}
         * <p>
         * RESULT_CANCEL:
         * "mpException" {@link com.mercadopago.model.PaymentMethod}
         */
        public static class SavedCardsActivityBuilder {

            private Activity activity;
            private List<Card> cards;
            private String title;
            private String footerText;
            private DecorationPreference decorationPreference;
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

            public SavedCardsActivityBuilder setDecorationPreference(DecorationPreference decorationPreference) {
                this.decorationPreference = decorationPreference;
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

            public SavedCardsActivityBuilder setFooter(String footerText) {
                this.footerText = footerText;
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
                customerCardsIntent.putExtra("footerText", footerText);
                customerCardsIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));
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
            private String extraPaymentMethodInfo;
            private List<Item> items;
            private DecorationPreference decorationPreference;
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

            public ReviewAndConfirmBuilder setExtraPaymentMethodInfo(String extraPaymentMethodInfo) {
                this.extraPaymentMethodInfo = extraPaymentMethodInfo;
                return this;
            }

            public ReviewAndConfirmBuilder setItems(List<Item> items) {
                this.items = items;
                return this;
            }

            public ReviewAndConfirmBuilder setDecorationPreference(DecorationPreference decorationPreference) {
                this.decorationPreference = decorationPreference;
                return this;
            }

            public ReviewAndConfirmBuilder setDiscount(Discount discount) {
                this.discount = discount;
                return this;
            }

            public void startActivity() {

                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.paymentMethod == null) throw new IllegalStateException("payment method is null");
                if (this.items == null) throw new IllegalStateException("items not set");
                if (MercadoPagoUtil.isCard(paymentMethod.getPaymentTypeId())) {
                    if (this.payerCost == null) throw new IllegalStateException("payer cost is null");
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
                intent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));
                intent.putExtra("cardInfo", JsonUtil.getInstance().toJson(cardInfo));
                intent.putExtra("extraPaymentMethodInfo", extraPaymentMethodInfo);
                intent.putExtra("discount", JsonUtil.getInstance().toJson(discount));
                intent.putExtra("items", new Gson().toJson(items));

                activity.startActivityForResult(intent, MercadoPago.REVIEW_AND_CONFIRM_REQUEST_CODE);
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
            private DecorationPreference decorationPreference;
            private OnReviewChange reviewChangeCallback;
            private Boolean editionEnabled;

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

            public ReviewPaymentMethodOnBuilder setDecorationPreference(DecorationPreference decorationPreference) {
                this.decorationPreference = decorationPreference;
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
                return new ReviewPaymentOnView(context, paymentMethod, cardInfo, payerCost, currencyId, reviewChangeCallback, editionEnabled, decorationPreference);
            }
        }

        public static class SavedCardsListViewBuilder {

            private Context context;
            private List<Card> cards;
            private String footerText;
            private OnSelectedCallback<Card> onSelectedCallback;
            private int selectionImageResId;

            public SavedCardsListViewBuilder setContext(Context context) {
                this.context = context;
                return this;
            }

            public SavedCardsListViewBuilder setSelectionImage(@DrawableRes int drawableResId) {
                this.selectionImageResId = drawableResId;
                return this;
            }

            public SavedCardsListViewBuilder setCards(List<Card> cards) {
                this.cards = cards;
                return this;
            }

            public SavedCardsListViewBuilder setFooter(String footerText) {
                this.footerText = footerText;
                return this;
            }

            public SavedCardsListViewBuilder setOnSelectedCallback(OnSelectedCallback<Card> onSelectedCallback) {
                this.onSelectedCallback = onSelectedCallback;
                return this;
            }

            public SavedCardsListView build() {
                return new SavedCardsListView(context, cards, footerText, selectionImageResId, onSelectedCallback);
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

            public ReviewItemsViewBuilder addItem(Item item) {
                this.items.add(item);
                return this;
            }

            public ReviewItemsViewBuilder addItems(List<Item> items) {
                this.items.addAll(items);
                return this;
            }

            public ReviewItemsView build() {
                return new ReviewItemsView(context, items, currencyId);
            }
        }

        public static class ReviewPaymentMethodOffBuilder {

            private Context context;
            private PaymentMethod paymentMethod;
            private String paymentMethodInfo;
            private BigDecimal amount;
            private Site site;
            private DecorationPreference decorationPreference;
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

            public ReviewPaymentMethodOffBuilder setExtraPaymentMethodInfo(String paymentMethodInfo) {
                this.paymentMethodInfo = paymentMethodInfo;
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

            public ReviewPaymentMethodOffBuilder setDecorationPreference(DecorationPreference decorationPreference) {
                this.decorationPreference = decorationPreference;
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
                return new ReviewPaymentOffView(context, paymentMethod, paymentMethodInfo, amount, site, reviewChangeCallback, editionEnabled, decorationPreference);
            }

        }
    }
}
