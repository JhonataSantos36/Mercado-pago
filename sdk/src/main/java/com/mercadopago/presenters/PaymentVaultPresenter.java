package com.mercadopago.presenters;

import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.Card;
import com.mercadopago.model.CustomSearchItem;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Payer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.Site;
import com.mercadopago.mvp.MvpPresenter;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.preferences.PaymentPreference;
import com.mercadopago.providers.PaymentVaultProvider;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.PaymentVaultView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PaymentVaultPresenter extends MvpPresenter<PaymentVaultView, PaymentVaultProvider> {

    private static final String ACCOUNT_MONEY_ID = "account_money";
    private static final String MISMATCHING_PAYMENT_METHOD_ERROR = "Payment method in search not found";

    private Site mSite;
    private Discount mDiscount;
    private PaymentMethodSearchItem mSelectedSearchItem;
    private PaymentMethodSearch mPaymentMethodSearch;
    private String mPayerAccessToken;
    private String mPayerEmail;
    private PaymentPreference mPaymentPreference;
    private BigDecimal mAmount;
    private Boolean mInstallmentsReviewEnabled;
    private Boolean mDiscountEnabled = true;
    private Boolean mDirectDiscountEnabled = true;
    private Integer mMaxSavedCards;

    private boolean mSelectAutomatically;
    private FailureRecovery failureRecovery;

    public void initialize(boolean selectAutomatically) {
        try {
            mSelectAutomatically = selectAutomatically;
            validateParameters();
            onValidStart();
        } catch (IllegalStateException exception) {
            getView().showError(new MercadoPagoError(exception.getMessage(), false));
        }
    }


    private void onValidStart() {
        if (mDiscountEnabled) {
            initPaymentVaultDiscountFlow();
        } else {
            initPaymentVaultFlow();
        }
    }

    private void initPaymentVaultDiscountFlow() {

        if (isItemSelected()) {
            initializeDiscountRow();
            showSelectedItemChildren();
        } else {
            loadDiscount();
        }
    }

    private void initPaymentVaultFlow() {
        initializeDiscountRow();

        if (isItemSelected()) {
            showSelectedItemChildren();

        } else {
            initPaymentMethodSearch();
        }
    }

    private void loadDiscount() {
        if (mDirectDiscountEnabled && mDiscount == null) {
            getView().showProgress();
            getDirectDiscount();
        } else {
            initializeDiscountRow();
            initPaymentVaultFlow();
        }
    }

    public void onDiscountOptionSelected() {
        getView().startDiscountFlow(mAmount);
    }

    public void initializeDiscountRow() {
        if (isViewAttached()) {
            getView().showDiscount(mAmount);
        }
    }

    private void getDirectDiscount() {
        getView().showProgress();
        getResourcesProvider().getDirectDiscount(mAmount.toString(), mPayerEmail, new OnResourcesRetrievedCallback<Discount>() {
            @Override
            public void onSuccess(Discount discount) {
                mDiscount = discount;
                initializeDiscountRow();
                initPaymentVaultFlow();
            }

            @Override
            public void onFailure(MercadoPagoError error) {
                mDirectDiscountEnabled = false;
                initializeDiscountRow();
                initPaymentVaultFlow();
                mDiscountEnabled = false;
            }
        });
    }

    public void onDiscountReceived(Discount discount) {
        setDiscount(discount);
        clearPaymentMethodOptions();
        initializeDiscountRow();
        initPaymentVaultFlow();
    }

    private void clearPaymentMethodOptions() {
        getView().cleanPaymentMethodOptions();
        mPaymentMethodSearch = null;
    }

    private void validateParameters() throws IllegalStateException {
        if (mPaymentPreference != null) {
            if (!mPaymentPreference.validMaxInstallments()) {
                throw new IllegalStateException(getResourcesProvider().getInvalidMaxInstallmentsErrorMessage());
            }
            if (!mPaymentPreference.validDefaultInstallments()) {
                throw new IllegalStateException(getResourcesProvider().getInvalidDefaultInstallmentsErrorMessage());
            }
            if (!mPaymentPreference.excludedPaymentTypesValid()) {
                throw new IllegalStateException(getResourcesProvider().getAllPaymentTypesExcludedErrorMessage());
            }
        }
        if (!isAmountValid()) {
            throw new IllegalStateException(getResourcesProvider().getInvalidAmountErrorMessage());
        }
        if (!isSiteConfigurationValid()) {
            throw new IllegalStateException(getResourcesProvider().getInvalidSiteConfigurationErrorMessage());
        }
    }

    private boolean isAmountValid() {
        return mAmount != null && mAmount.compareTo(BigDecimal.ZERO) >= 0;
    }

    private boolean isSiteConfigurationValid() {
        boolean isValid = true;
        if (mSite == null) {
            isValid = false;
        } else if (mSite.getCurrencyId() == null) {
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
        getView().setTitle(getResourcesProvider().getTitle());

        if (mPaymentMethodSearch == null) {
            getPaymentMethodSearchAsync();
        } else {
            resolveAvailablePaymentMethods();
        }
    }

    private void getPaymentMethodSearchAsync() {

        if (isViewAttached()) {
            getView().showProgress();
            Payer payer = new Payer();
            payer.setAccessToken(mPayerAccessToken);

            getResourcesProvider().getPaymentMethodSearch(mAmount, mPaymentPreference, payer, mSite, new OnResourcesRetrievedCallback<PaymentMethodSearch>() {

                @Override
                public void onSuccess(PaymentMethodSearch paymentMethodSearch) {
                    mPaymentMethodSearch = paymentMethodSearch;
                    resolveAvailablePaymentMethods();
                }

                @Override
                public void onFailure(MercadoPagoError error) {
                    if (isViewAttached()) {
                        getView().showError(error);

                        setFailureRecovery(new FailureRecovery() {
                            @Override
                            public void recover() {
                                getPaymentMethodSearchAsync();
                            }
                        });
                    }
                }
            });
        }
    }

    private void setFailureRecovery(FailureRecovery failureRecovery) {
        this.failureRecovery = failureRecovery;
    }

    private void showSelectedItemChildren() {
        getView().setTitle(mSelectedSearchItem.getChildrenHeader());
        getView().showSearchItems(mSelectedSearchItem.getChildren(), getPaymentMethodSearchItemSelectionCallback());
    }

    private void resolveAvailablePaymentMethods() {

        if (isViewAttached()) {

            if (noPaymentMethodsAvailable()) {
                showEmptyPaymentMethodsError();
            } else if (isOnlyUniqueSearchSelectionAvailable() && mSelectAutomatically) {
                selectItem(mPaymentMethodSearch.getGroups().get(0), true);
            } else if (isOnlyAccountMoneyEnabled() && mSelectAutomatically) {
                selectAccountMoney(mPaymentMethodSearch.getCustomSearchItems().get(0));
            } else {
                showAvailableOptions();
                getView().hideProgress();
            }
        }
    }

    public boolean isOnlyAccountMoneyEnabled() {
        return mPaymentMethodSearch.hasCustomSearchItems()
                && mPaymentMethodSearch.getCustomSearchItems().size() == 1
                && mPaymentMethodSearch.getCustomSearchItems().get(0).getId().equals(ACCOUNT_MONEY_ID)
                && (mPaymentMethodSearch.getGroups() == null || mPaymentMethodSearch.getGroups().isEmpty());
    }

    private void selectItem(PaymentMethodSearchItem item) {
        selectItem(item, false);
    }

    private void selectItem(PaymentMethodSearchItem item, Boolean automaticSelection) {

        if (item.hasChildren()) {
            getView().showSelectedItem(item);
        } else if (item.isPaymentType()) {
            startNextStepForPaymentType(item, automaticSelection);
        } else if (item.isPaymentMethod()) {
            resolvePaymentMethodSelection(item);
        }
    }

    private void selectCard(Card card) {
        getView().startSavedCardFlow(card, mAmount);
    }

    private void showAvailableOptions() {

        if (mPaymentMethodSearch.hasCustomSearchItems()) {
            List<CustomSearchItem> shownCustomItems;
            if (mMaxSavedCards != null && mMaxSavedCards > 0) {
                shownCustomItems = getLimitedCustomOptions(mPaymentMethodSearch.getCustomSearchItems(), mMaxSavedCards);
            } else {
                shownCustomItems = mPaymentMethodSearch.getCustomSearchItems();
            }
            getView().showCustomOptions(shownCustomItems, getCustomOptionCallback());
        }

        if (searchItemsAvailable()) {
            getView().showSearchItems(mPaymentMethodSearch.getGroups(), getPaymentMethodSearchItemSelectionCallback());
        }
    }

    private OnSelectedCallback<PaymentMethodSearchItem> getPaymentMethodSearchItemSelectionCallback() {
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
                } else if (ACCOUNT_MONEY_ID.equals(searchItem.getPaymentMethodId())) {
                    selectAccountMoney(searchItem);
                }
            }
        };
    }

    private void selectAccountMoney(CustomSearchItem searchItem) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(ACCOUNT_MONEY_ID);
        paymentMethod.setName(searchItem.getDescription());
        paymentMethod.setPaymentTypeId(searchItem.getType());
        getView().selectPaymentMethod(paymentMethod);
    }


    private Card getCardWithPaymentMethod(CustomSearchItem searchItem) {
        PaymentMethod paymentMethod = mPaymentMethodSearch.getPaymentMethodById(searchItem.getPaymentMethodId());
        Card selectedCard = getCardById(mPaymentMethodSearch.getCards(), searchItem.getId());
        if (paymentMethod != null) {
            selectedCard.setPaymentMethod(paymentMethod);
            if (selectedCard.getSecurityCode() == null && paymentMethod.getSettings() != null && paymentMethod.getSettings().get(0) != null) {
                selectedCard.setSecurityCode(paymentMethod.getSettings().get(0).getSecurityCode());
            }
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
        startNextStepForPaymentType(item, false);
    }

    private void startNextStepForPaymentType(PaymentMethodSearchItem item, Boolean automaticSelection) {

        if (mPaymentPreference == null) {
            mPaymentPreference = new PaymentPreference();
        }

        if (MercadoPagoUtil.isCard(item.getId())) {
            getView().startCardFlow(item.getId(), mAmount, automaticSelection);
        } else {
            getView().startPaymentMethodsSelection();
        }
    }

    private void resolvePaymentMethodSelection(PaymentMethodSearchItem item) {
        PaymentMethod selectedPaymentMethod = mPaymentMethodSearch.getPaymentMethodBySearchItem(item);
        if (selectedPaymentMethod == null) {
            showMismatchingPaymentMethodError();
        } else {
            getView().selectPaymentMethod(selectedPaymentMethod);
        }
    }

    public boolean isOnlyUniqueSearchSelectionAvailable() {
        return searchItemsAvailable() && mPaymentMethodSearch.getGroups().size() == 1 && !mPaymentMethodSearch.hasCustomSearchItems();
    }

    private boolean searchItemsAvailable() {
        return mPaymentMethodSearch != null && mPaymentMethodSearch.getGroups() != null && !mPaymentMethodSearch.getGroups().isEmpty();
    }

    private boolean noPaymentMethodsAvailable() {
        return (mPaymentMethodSearch.getGroups() == null || mPaymentMethodSearch.getGroups().isEmpty())
                && (mPaymentMethodSearch.getCustomSearchItems() == null || mPaymentMethodSearch.getCustomSearchItems().isEmpty());
    }

    private void showEmptyPaymentMethodsError() {
        String errorMessage = getResourcesProvider().getEmptyPaymentMethodsErrorMessage();
        getView().showError(new MercadoPagoError(errorMessage, false));
    }

    private void showMismatchingPaymentMethodError() {
        String errorMessage = getResourcesProvider().getStandardErrorMessage();
        getView().showError(new MercadoPagoError(errorMessage, MISMATCHING_PAYMENT_METHOD_ERROR, false));
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

    public PaymentPreference getPaymentPreference() {
        return mPaymentPreference;
    }

    public void setPaymentPreference(PaymentPreference mPaymentPreference) {
        this.mPaymentPreference = mPaymentPreference;
    }

    public void setAmount(BigDecimal mAmount) {
        this.mAmount = mAmount;
    }

    public void setPayerAccessToken(String payerAccessToken) {
        this.mPayerAccessToken = payerAccessToken;
    }

    public void setDiscount(Discount discount) {
        this.mDiscount = discount;
    }

    public Discount getDiscount() {
        return mDiscount;
    }

    public void setPayerEmail(String payerEmail) {
        this.mPayerEmail = payerEmail;
    }

    public String getPayerEmail() {
        return mPayerEmail;
    }

    public void setInstallmentsReviewEnabled(Boolean installmentReviewEnabled) {
        this.mInstallmentsReviewEnabled = installmentReviewEnabled;
    }

    public Boolean getInstallmentsReviewEnabled() {
        return this.mInstallmentsReviewEnabled;
    }

    public void setDiscountEnabled(Boolean discountEnabled) {
        this.mDiscountEnabled = discountEnabled;
    }

    public void setDirectDiscountEnabled(Boolean directDiscountEnabled) {
        this.mDirectDiscountEnabled = directDiscountEnabled;
    }

    public Boolean getDirectDiscountEnabled() {
        return this.mDirectDiscountEnabled;
    }

    public Boolean getDiscountEnabled() {
        return this.mDiscountEnabled;
    }

    public void setMaxSavedCards(int maxSavedCards) {
        this.mMaxSavedCards = maxSavedCards;
    }

    public void recoverFromFailure() {
        if (failureRecovery != null) {
            failureRecovery.recover();
        }
    }

    private List<CustomSearchItem> getLimitedCustomOptions(List<CustomSearchItem> customSearchItems, Integer maxSavedCards) {
        List<CustomSearchItem> limitedItems = new ArrayList<>();
        if (maxSavedCards != null && maxSavedCards > 0) {
            int cardsAdded = 0;
            for (CustomSearchItem customSearchItem : customSearchItems) {
                if (MercadoPagoUtil.isCard(customSearchItem.getType()) && cardsAdded < maxSavedCards) {
                    limitedItems.add(customSearchItem);
                    cardsAdded++;
                } else if (!MercadoPagoUtil.isCard(customSearchItem.getType())) {
                    limitedItems.add(customSearchItem);
                }
            }
        } else {
            limitedItems = customSearchItems;
        }
        return limitedItems;
    }
}
