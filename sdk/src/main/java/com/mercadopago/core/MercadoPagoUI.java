package com.mercadopago.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.mercadopago.CustomerCardsActivity;
import com.mercadopago.callbacks.OnConfirmPaymentCallback;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.model.Card;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Discount;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.uicontrollers.discounts.DiscountRowView;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewSummaryView;
import com.mercadopago.uicontrollers.savedcards.SavedCardRowView;
import com.mercadopago.uicontrollers.savedcards.SavedCardView;
import com.mercadopago.uicontrollers.savedcards.SavedCardsListView;
import com.mercadopago.util.JsonUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mreverter on 6/10/16.
 */
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
    }

    public static class Views {

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

            public DiscountRowViewBuilder setShowArrow(Boolean showArrow) {
                this.showArrow = showArrow;
                return this;
            }

            public DiscountRowViewBuilder setShowSeparator(Boolean showSeparator) {
                this.showSeparator = showSeparator;
                return this;
            }

            public DiscountRowView build() {
                return new DiscountRowView(context, discount, transactionAmount, currencyId, shortRowEnabled, showArrow, showSeparator);
            }
        }

        public static class SummaryViewBuilder {
            private Context context;
            private String currencyId;
            private BigDecimal amount;
            private BigDecimal discountPercentage;
            private BigDecimal discountAmount;
            private PayerCost payerCost;
            private PaymentMethod paymentMethod;
            private DecorationPreference decorationPreference;
            private OnConfirmPaymentCallback callback;


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

            public SummaryViewBuilder setDiscountPercentage(BigDecimal discountPercentage) {
                this.discountPercentage = discountPercentage;
                return this;
            }

            public SummaryViewBuilder setDiscountAmount(BigDecimal discountAmount) {
                this.discountAmount = discountAmount;
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

            public SummaryViewBuilder setDecorationPreference(DecorationPreference decorationPreference) {
                this.decorationPreference = decorationPreference;
                return this;
            }

            public SummaryViewBuilder setCallback(OnConfirmPaymentCallback callback) {
                this.callback = callback;
                return this;
            }

            public ReviewSummaryView build() {
                return new ReviewSummaryView(context, currencyId, amount, payerCost, paymentMethod,
                        discountPercentage, discountAmount, callback, decorationPreference);
            }
        }
    }
}
