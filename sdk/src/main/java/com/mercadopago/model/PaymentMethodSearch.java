package com.mercadopago.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mreverter on 15/1/16.
 */
public class PaymentMethodSearch {

    private List<PaymentMethodSearchItem> groups;

    @SerializedName("custom_options")
    private List<CustomSearchItem> customSearchItems;

    private List<PaymentMethod> paymentMethods;

    private List<Card> cards;

    private AccountMoney accountMoney;

    public List<PaymentMethodSearchItem> getGroups() {
        return groups;
    }

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public boolean hasSearchItems() {
        return this.groups != null && !this.groups.isEmpty();
    }

    public PaymentMethod getPaymentMethodBySearchItem(PaymentMethodSearchItem item) {
        PaymentMethod requiredPaymentMethod = null;
        if (paymentMethods != null && item != null && item.getId() != null) {
            for (PaymentMethod currentPaymentMethod : paymentMethods) {
                if (itemMatchesPaymentMethod(item, currentPaymentMethod)) {
                    requiredPaymentMethod = currentPaymentMethod;
                    requiredPaymentMethod.setPaymentTypeId(getPaymentTypeIdFromItem(item, currentPaymentMethod));
                }
            }
        }
        return requiredPaymentMethod;
    }

    private String getPaymentTypeIdFromItem(PaymentMethodSearchItem item, PaymentMethod paymentMethod) {
        //Remove payment method id from item id and the splitter
        String paymentType;
        String itemIdWithoutPaymentMethod = item.getId().replaceFirst(paymentMethod.getId(), "");
        if (itemIdWithoutPaymentMethod.isEmpty()) {
            paymentType = paymentMethod.getPaymentTypeId();
        } else {
            paymentType = itemIdWithoutPaymentMethod.substring(1);
        }
        return paymentType;
    }

    private boolean itemMatchesPaymentMethod(PaymentMethodSearchItem item, PaymentMethod paymentMethod) {
        return item.getId().startsWith(paymentMethod.getId());
    }

    public PaymentMethodSearchItem getSearchItemByPaymentMethod(PaymentMethod selectedPaymentMethod) {
        PaymentMethodSearchItem requiredItem = null;
        if (selectedPaymentMethod != null) {

            requiredItem = searchItemMatchingPaymentMethod(selectedPaymentMethod);

        }
        return requiredItem;
    }

    private PaymentMethodSearchItem searchItemMatchingPaymentMethod(PaymentMethod paymentMethod) {
        return searchItemInList(groups, paymentMethod);
    }

    private PaymentMethodSearchItem searchItemInList(List<PaymentMethodSearchItem> list, PaymentMethod paymentMethod) {
        PaymentMethodSearchItem requiredItem = null;
        for (PaymentMethodSearchItem currentItem : list) {

            //Case like "pagofacil", without the payment type in the item id.
            if (itemMatchesPaymentMethod(currentItem, paymentMethod) && currentItem.getId().equals(paymentMethod.getId())) {
                requiredItem = currentItem;
                break;
            }
            //Case like "bancomer_ticket", with the payment type in the item id
            else if (itemMatchesPaymentMethod(currentItem, paymentMethod)) {
                //Remove payment method id from item id
                String potentialPaymentType = currentItem.getId().replaceFirst(paymentMethod.getId(), "");
                if (potentialPaymentType.endsWith(paymentMethod.getPaymentTypeId())) {
                    requiredItem = currentItem;
                    break;
                }
            } else if (currentItem.hasChildren()) {
                requiredItem = searchItemInList(currentItem.getChildren(), paymentMethod);
                if (requiredItem != null) {
                    break;
                }
            }
        }
        return requiredItem;
    }

    public PaymentMethod getPaymentMethodById(String paymentMethodId) {
        PaymentMethod foundPaymentMethod = null;
        if (paymentMethods != null) {
            for (PaymentMethod paymentMethod : paymentMethods) {
                if (paymentMethod.getId().equals(paymentMethodId)) {
                    foundPaymentMethod = paymentMethod;
                    break;
                }
            }
        }
        return foundPaymentMethod;
    }

    public Card getCardById(String cardId) {
        Card foundCard = null;
        if (cards != null) {
            for (Card card : cards) {
                if (card.getId().equals(cardId)) {
                    foundCard = card;
                    break;
                }
            }
        }
        return foundCard;
    }

    public List<CustomSearchItem> getCustomSearchItems() {
        return customSearchItems;
    }

    public boolean hasCustomSearchItems() {
        return customSearchItems != null && !customSearchItems.isEmpty();
    }

    public List<Card> getCards() {
        return cards;
    }

    public AccountMoney getAccountMoney() {
        return accountMoney;
    }

    public boolean hasSavedCards() {
        return cards != null && !cards.isEmpty();
    }

    public void setCards(List<Card> cards, String lastFourDigitsText) {
        if (cards != null) {
            this.customSearchItems = new ArrayList<>();
            this.cards = new ArrayList<>();

            for (Card card : cards) {
                CustomSearchItem searchItem = new CustomSearchItem();
                searchItem.setDescription(lastFourDigitsText + " " + card.getLastFourDigits());
                searchItem.setType(card.getPaymentMethod().getPaymentTypeId());
                searchItem.setId(card.getId());
                searchItem.setPaymentMethodId(card.getPaymentMethod().getId());
                this.customSearchItems.add(searchItem);
                this.cards.add(card);
            }
        }
    }

    public void setGroups(List<PaymentMethodSearchItem> groups) {
        this.groups = groups;
    }

    public void setCustomSearchItems(List<CustomSearchItem> customSearchItems) {
        this.customSearchItems = customSearchItems;
    }

    public void setPaymentMethods(List<PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public void setAccountMoney(AccountMoney accountMoney) {
        this.accountMoney = accountMoney;
    }
}
