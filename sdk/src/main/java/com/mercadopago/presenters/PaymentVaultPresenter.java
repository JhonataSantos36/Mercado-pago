package com.mercadopago.presenters;

import com.mercadopago.R;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CustomSearchItem;
import com.mercadopago.model.Customer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.Site;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.PaymentVaultView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static android.text.TextUtils.isEmpty;

/**
 * Created by mreverter on 6/9/16.
 */
public class PaymentVaultPresenter {

    private static final int MAX_SAVED_CARDS = 3;

    private PaymentVaultView mPaymentVaultView;

    private String mMerchantPublicKey;
    private Site mSite;
    private MercadoPago mMercadoPago;
    private PaymentMethodSearchItem mSelectedSearchItem;
    private PaymentMethodSearch mPaymentMethodSearch;
    private List<CustomSearchItem> mCustomSearchItems;
    private List<Card> mSavedCards;
    private String mMerchantBaseUrl;
    private String mMerchantGetCustomerUri;
    private String mMerchantAccessToken;
    private PaymentPreference mPaymentPreference;
    private BigDecimal mAmount;

    public void attachView(PaymentVaultView paymentVaultView) {
        this.mPaymentVaultView = paymentVaultView;
    }

    public void initialize(String key) {
        mCustomSearchItems = new ArrayList<>();
        mMercadoPago = new MercadoPago.Builder()
                .setPublicKey(key)
                .setContext(mPaymentVaultView.getContext())
                .build();

        if (isItemSelected()) {
            showSelectedItemChildren();
        } else {
            initPaymentMethodSearch();
        }
    }

    public void validateParameters() throws IllegalStateException {
        if (mPaymentPreference != null) {
            if (!mPaymentPreference.validMaxInstallments()) {
                throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_max_installments));
            }
            if (!mPaymentPreference.validDefaultInstallments()) {
                throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_default_installments));
            }
            if (!mPaymentPreference.excludedPaymentTypesValid()) {
                throw new IllegalStateException(getString(R.string.mpsdk_error_message_excluded_all_payment_type));
            }
        }
        if (!isAmountValid()) {
            throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_amount));
        }
        if (!isCurrencyIdValid()) {
            throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_currency));
        }
        if (!isMerchantPublicKeyValid()) {
            throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_merchant));
        }
    }

    private boolean isAmountValid() {
        return mAmount != null && mAmount.compareTo(BigDecimal.ZERO) >= 0;
    }

    private boolean isMerchantPublicKeyValid() {
        return mMerchantPublicKey != null;
    }

    private boolean isCurrencyIdValid() {
        boolean isValid = true;

        if (mSite.getCurrencyId() == null) {
            isValid = false;
        } else if (!CurrenciesUtil.isValidCurrency(mSite.getCurrencyId())) {
            isValid = false;
        }
        return isValid;
    }

    public boolean isItemSelected() {
        return mSelectedSearchItem != null;
    }

    private void initPaymentMethodSearch() {
        mPaymentVaultView.setTitle(getString(R.string.mpsdk_title_activity_payment_vault));

        if (mPaymentMethodSearch == null) {
            getPaymentMethodSearchAsync();
        } else {
            onPaymentMethodSearchSet();
        }
    }

    private void getPaymentMethodSearchAsync() {

        List<String> excludedPaymentTypes = mPaymentPreference == null ? null : mPaymentPreference.getExcludedPaymentTypes();
        List<String> excludedPaymentMethodIds = mPaymentPreference == null ? null : mPaymentPreference.getExcludedPaymentMethodIds();

        mPaymentVaultView.showProgress();
        mMercadoPago.getPaymentMethodSearch(mAmount, excludedPaymentTypes, excludedPaymentMethodIds, new Callback<PaymentMethodSearch>() {

            @Override
            public void success(PaymentMethodSearch paymentMethodSearch) {
                mPaymentMethodSearch = paymentMethodSearch;
                onPaymentMethodSearchSet();
            }

            @Override
            public void failure(ApiException apiException) {
                if (viewAttached()) {
                    mPaymentVaultView.showApiException(apiException);
                    mPaymentVaultView.setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            getPaymentMethodSearchAsync();
                        }
                    });
                }
            }
        });
    }

    private boolean viewAttached() {
        return mPaymentVaultView != null;
    }

    private void showSelectedItemChildren() {
        mPaymentVaultView.setTitle(mSelectedSearchItem.getChildrenHeader());
        mPaymentVaultView.showSearchItems(mSelectedSearchItem.getChildren(), getPaymentMethodSearchItemSelectionCallback());
    }

    private void resolveAvailablePaymentMethods() {

        if (viewAttached()) {

            if (noPaymentMethodsAvailable()) {
                showEmptyPaymentMethodsError();
            } else if (isOnlyUniqueSearchSelectionAvailable()) {
                selectItem(mPaymentMethodSearch.getGroups().get(0));
            } else {
                showAvailableOptions();
                mPaymentVaultView.hideProgress();
            }
        }
    }

    private void selectItem(PaymentMethodSearchItem item) {
        if (item.hasChildren()) {
            mPaymentVaultView.restartWithSelectedItem(item);
        } else if (item.isPaymentType()) {
            startNextStepForPaymentType(item);
        } else if (item.isPaymentMethod()) {
            resolvePaymentMethodSelection(item);
        }
    }

    private void selectCard(Card card) {
        mPaymentVaultView.startSavedCardFlow(card);
    }

    private void showAvailableOptions() {

        if (!mPaymentMethodSearch.hasSavedCards() && savedCardsAvailable()) {
            mCustomSearchItems.addAll(createCustomSearchItemsFromCards(mSavedCards));
        }

        if (mSavedCards != null && mSavedCards.size() > MAX_SAVED_CARDS) {
            mCustomSearchItems = getLimitedCustomOptions(mCustomSearchItems, MAX_SAVED_CARDS);
        }

        if (customSearchItemsAvailable()) {
            mPaymentVaultView.showCustomOptions(mCustomSearchItems, getCustomOptionCallback());
        }

        if (searchItemsAvailable()) {
            mPaymentVaultView.showSearchItems(mPaymentMethodSearch.getGroups(), getPaymentMethodSearchItemSelectionCallback());
        }
    }

    private List<CustomSearchItem> getLimitedCustomOptions(List<CustomSearchItem> customSearchItems, int maxSavedCards) {
        List<CustomSearchItem> limitedItems = new ArrayList<>();
        int cardsAdded = 0;
        for (CustomSearchItem customSearchItem : customSearchItems) {
            if (MercadoPagoUtil.isCard(customSearchItem.getType()) && cardsAdded < maxSavedCards) {
                limitedItems.add(customSearchItem);
                cardsAdded++;
            } else if (!MercadoPagoUtil.isCard(customSearchItem.getType())) {
                limitedItems.add(customSearchItem);
            }
        }
        return limitedItems;
    }

    private List<CustomSearchItem> createCustomSearchItemsFromCards(List<Card> cards) {
        List<CustomSearchItem> customSearchItems = new ArrayList<>();
        if (cards != null) {
            for (Card card : cards) {
                CustomSearchItem searchItem = new CustomSearchItem();
                searchItem.setDescription(mPaymentVaultView.getContext().getString(R.string.mpsdk_last_digits_label) + " " + card.getLastFourDigits());
                searchItem.setType(card.getPaymentMethod().getPaymentTypeId());
                searchItem.setId(card.getId());
                searchItem.setPaymentMethodId(card.getPaymentMethod().getId());
                customSearchItems.add(searchItem);
            }
        }
        return customSearchItems;
    }

    private boolean customSearchItemsAvailable() {
        return mCustomSearchItems != null && !mCustomSearchItems.isEmpty();
    }

    protected OnSelectedCallback<PaymentMethodSearchItem> getPaymentMethodSearchItemSelectionCallback() {
        return new OnSelectedCallback<PaymentMethodSearchItem>() {
            @Override
            public void onSelected(PaymentMethodSearchItem item) {
                selectItem(item);
            }
        };
    }

    private OnSelectedCallback<CustomSearchItem> getCustomOptionCallback() {
        return new OnSelectedCallback<CustomSearchItem>() {
            @Override
            public void onSelected(CustomSearchItem searchItem) {
                if (MercadoPagoUtil.isCard(searchItem.getType())) {
                    Card card = getCardWithPaymentMethod(searchItem);
                    selectCard(card);
                }
            }
        };
    }

    private Card getCardWithPaymentMethod(CustomSearchItem searchItem) {
        PaymentMethod paymentMethod = mPaymentMethodSearch.getPaymentMethodById(searchItem.getPaymentMethodId());
        Card selectedCard = getCardById(mSavedCards, searchItem.getId());
        if (paymentMethod != null) {
            selectedCard.setPaymentMethod(paymentMethod);
        }
        return selectedCard;
    }

    private Card getCardById(List<Card> savedCards, String cardId) {
        Card foundCard = null;
        for (Card card : savedCards) {
            if (card.getId().equals(cardId)) {
                foundCard = card;
                break;
            }
        }
        return foundCard;
    }

    private void startNextStepForPaymentType(PaymentMethodSearchItem item) {

        if (mPaymentPreference == null) {
            mPaymentPreference = new PaymentPreference();
        }
        mPaymentPreference.setDefaultPaymentTypeId(item.getId());

        if (MercadoPagoUtil.isCard(item.getId())) {
            mPaymentVaultView.startCardFlow();
        } else {
            mPaymentVaultView.startPaymentMethodsActivity();
        }
    }

    private void resolvePaymentMethodSelection(PaymentMethodSearchItem item) {
        PaymentMethod selectedPaymentMethod = mPaymentMethodSearch.getPaymentMethodBySearchItem(item);
        if (selectedPaymentMethod == null) {
            showMismatchingPaymentMethodError();
        } else {
            mPaymentVaultView.selectPaymentMethod(selectedPaymentMethod);
        }
    }

    private void onPaymentMethodSearchSet() {

        if (mPaymentMethodSearch.hasSavedCards()) {
            mSavedCards = mPaymentMethodSearch.getCards();
        }
        if (mPaymentMethodSearch.hasCustomSearchItems()) {
            mCustomSearchItems.addAll(mPaymentMethodSearch.getCustomSearchItems());
        }

        if (!savedCardsAvailable() && isMerchantServerInfoAvailable()) {
            getCustomerAsync();
        } else {
            resolveAvailablePaymentMethods();
        }
    }

    private void getCustomerAsync() {
        mPaymentVaultView.showProgress();
        MerchantServer.getCustomer(mPaymentVaultView.getContext(), mMerchantBaseUrl, mMerchantGetCustomerUri, mMerchantAccessToken, new Callback<Customer>() {
            @Override
            public void success(Customer customer) {
                mSavedCards = mPaymentPreference == null ? customer.getCards() : mPaymentPreference.getValidCards(customer.getCards());
                resolveAvailablePaymentMethods();
            }

            @Override
            public void failure(ApiException apiException) {
                resolveAvailablePaymentMethods();
            }
        });
    }

    public boolean isOnlyUniqueSearchSelectionAvailable() {
        return searchItemsAvailable() && mPaymentMethodSearch.getGroups().size() == 1 && !savedCardsAvailable();
    }

    private boolean savedCardsAvailable() {
        return mSavedCards != null && !mSavedCards.isEmpty();
    }

    private boolean searchItemsAvailable() {
        return mPaymentMethodSearch != null && mPaymentMethodSearch.getGroups() != null && !mPaymentMethodSearch.getGroups().isEmpty();
    }

    private boolean isMerchantServerInfoAvailable() {
        return !isEmpty(mMerchantBaseUrl) && !isEmpty(mMerchantGetCustomerUri) && !isEmpty(mMerchantAccessToken);
    }

    private boolean noPaymentMethodsAvailable() {
        return (mSavedCards == null || mSavedCards.isEmpty())
                && (mPaymentMethodSearch.getGroups() == null || mPaymentMethodSearch.getGroups().isEmpty());
    }

    private void showEmptyPaymentMethodsError() {
        mPaymentVaultView.showError(new MPException(getString(R.string.mpsdk_no_payment_methods_found), false));
    }

    private void showMismatchingPaymentMethodError() {
        mPaymentVaultView.showError(new MPException(getString(R.string.mpsdk_standard_error_message), "Payment method in search not found", false));
    }

    private String getString(int resId) {
        return mPaymentVaultView.getContext().getString(resId);
    }

    public String getMerchantPublicKey() {
        return mMerchantPublicKey;
    }

    public void setMerchantPublicKey(String mMerchantPublicKey) {
        this.mMerchantPublicKey = mMerchantPublicKey;
    }

    public Site getSite() {
        return mSite;
    }

    public void setSite(Site mSite) {
        this.mSite = mSite;
    }

    public PaymentMethodSearchItem getSelectedSearchItem() {
        return mSelectedSearchItem;
    }

    public void setSelectedSearchItem(PaymentMethodSearchItem mSelectedSearchItem) {
        this.mSelectedSearchItem = mSelectedSearchItem;
    }

    public PaymentMethodSearch getPaymentMethodSearch() {
        return mPaymentMethodSearch;
    }

    public void setPaymentMethodSearch(PaymentMethodSearch mPaymentMethodSearch) {
        this.mPaymentMethodSearch = mPaymentMethodSearch;
    }

    public void setSavedCards(List<Card> mSavedCards) {
        this.mSavedCards = mSavedCards;
    }

    public void setMerchantBaseUrl(String mMerchantBaseUrl) {
        this.mMerchantBaseUrl = mMerchantBaseUrl;
    }

    public void setMerchantGetCustomerUri(String mMerchantGetCustomerUri) {
        this.mMerchantGetCustomerUri = mMerchantGetCustomerUri;
    }

    public void setMerchantAccessToken(String mMerchantAccessToken) {
        this.mMerchantAccessToken = mMerchantAccessToken;
    }

    public PaymentPreference getPaymentPreference() {
        return mPaymentPreference;
    }

    public void setPaymentPreference(PaymentPreference mPaymentPreference) {
        this.mPaymentPreference = mPaymentPreference;
    }

    public BigDecimal getAmount() {
        return mAmount;
    }

    public void setAmount(BigDecimal mAmount) {
        this.mAmount = mAmount;
    }

    public void detach() {
        mPaymentVaultView = null;
    }
}
