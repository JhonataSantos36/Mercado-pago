package com.mercadopago.core;

import android.content.Context;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Item;
import com.mercadopago.model.PayerCost;
import com.mercadopago.preferences.ReviewScreenPreference;
import com.mercadopago.uicontrollers.discounts.DiscountRowView;
import com.mercadopago.uicontrollers.installments.InstallmentsReviewView;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewItemsView;
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
    }

    public static class Views {

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
