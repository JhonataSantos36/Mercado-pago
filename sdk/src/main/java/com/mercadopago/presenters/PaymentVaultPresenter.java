package com.mercadopago.presenters;

import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.constants.PaymentMethods;
import com.mercadopago.core.CheckoutStore;
import com.mercadopago.core.MercadoPagoComponents;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.hooks.Hook;
import com.mercadopago.hooks.HookHelper;
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
import com.mercadopago.plugins.PaymentMethodPlugin;
import com.mercadopago.plugins.model.PaymentMethodInfo;
import com.mercadopago.preferences.PaymentPreference;
import com.mercadopago.providers.PaymentVaultProvider;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.PaymentVaultView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaymentVaultPresenter extends MvpPresenter<PaymentVaultView, PaymentVaultProvider> {

    private static final String ACCOUNT_MONEY_ID = "account_money";
    private static final String MISMATCHING_PAYMENT_METHOD_ERROR = "Payment method in search not found";

    private Site mSite;
    private Discount mDiscount;
    private PaymentMethod mSelectedPaymentMethod;
    private PaymentMethodSearchItem mSelectedSearchItem;
    private PaymentMethodSearch mPaymentMethodSearch;
    private String mPayerAccessToken;
    private String mPayerEmail;
    private PaymentPreference mPaymentPreference;
    private BigDecimal mAmount;
    private Boolean mInstallmentsReviewEnabled;
    private Boolean mDiscountEnabled = true;
    private Boolean mDirectDiscountEnabled = true;
    private Boolean mShowAllSavedCardsEnabled = false;
    private Integer mMaxSavedCards;

    private boolean mSelectAutomatically;
    private FailureRecovery failureRecovery;

    private PaymentMethodSearchItem resumeItem;
    private boolean skipHook = false;
    private boolean hook1Displayed = false;
    private PaymentMethod accountMoneyPaymentMethod;

    public void initialize(boolean selectAutomatically) {
        try {
            mSelectAutomatically = selectAutomatically;
            validateParameters();
            onValidStart();
        } catch (IllegalStateException exception) {
            getView().showError(new MercadoPagoError(exception.getMessage(), false), "");
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
            checkAvailablePaymentMethods();
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
            }
        });
    }

    public void onDiscountReceived(Discount discount) {
        setDiscount(discount);
        clearPaymentMethodOptions();
        initializeDiscountRow();
        initPaymentVaultFlow();
    }

    public void onPayerInformationReceived(Payer payer) {
        getView().finishPaymentMethodSelection(mSelectedPaymentMethod, payer);
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
        if (!isAmountValid(mAmount)) {
            throw new IllegalStateException(getResourcesProvider().getInvalidAmountErrorMessage());
        }
        if (!isSiteConfigurationValid()) {
            throw new IllegalStateException(getResourcesProvider().getInvalidSiteConfigurationErrorMessage());
        }
    }

    private Boolean isAmountValid(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) >= 0;
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

        getView().initializeMPTracker();

        getView().trackInitialScreen();

        getView().setTitle(getResourcesProvider().getTitle());

        checkAvailablePaymentMethods();
    }

    private void checkAvailablePaymentMethods() {
        if (mPaymentMethodSearch == null) {
            getPaymentMethodSearchAsync();
        } else {
            showPaymentMethodGroup();
        }
    }

    private void showPaymentMethodGroup() {
        if (isItemSelected()) {
            showSelectedItemChildren();
        } else {
            resolveAvailablePaymentMethods();
        }
    }

    private void reselectSearchItem() {
        if (mSelectedSearchItem != null) {
            if (mPaymentMethodSearch == null || noPaymentMethodsAvailable()) {
                showEmptyPaymentMethodsError();
            } else {
                //Reemplazo el selected item por el nuevo que vino de la api
                List<PaymentMethodSearchItem> groups = mPaymentMethodSearch.getGroups();
                for (PaymentMethodSearchItem item : groups) {
                    if (item.getId().equals(mSelectedSearchItem.getId())) {
                        mSelectedSearchItem = item;
                        break;
                    }
                }
            }
        }
    }

    public BigDecimal getTransactionAmount() {
        BigDecimal amount;

        if (mDiscount != null && mDiscountEnabled && mDiscount.isValid()) {
            amount = mDiscount.getAmountWithDiscount(mAmount);
        } else {
            amount = mAmount;
        }

        return amount;
    }

    private void getPaymentMethodSearchAsync() {
        if (isViewAttached()) {
            getView().showProgress();
            Payer payer = new Payer();
            payer.setAccessToken(mPayerAccessToken);


            getResourcesProvider().getPaymentMethodSearch(getTransactionAmount(), mPaymentPreference, payer, mSite, new OnResourcesRetrievedCallback<PaymentMethodSearch>() {

                @Override
                public void onSuccess(PaymentMethodSearch paymentMethodSearch) {
                    mPaymentMethodSearch = paymentMethodSearch;
                    reselectSearchItem();
                    showPaymentMethodGroup();
                }

                @Override
                public void onFailure(MercadoPagoError error) {
                    if (isViewAttached()) {
                        getView().showError(error, ApiUtil.RequestOrigin.PAYMENT_METHOD_SEARCH);

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
        getView().initializeMPTracker();
        getView().trackChildrenScreen();

        getView().setTitle(mSelectedSearchItem.getChildrenHeader());
        getView().showSearchItems(mSelectedSearchItem.getChildren(), getPaymentMethodSearchItemSelectionCallback());
        getView().hideProgress();
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

    private void selectCard(final Card card) {
        getView().startSavedCardFlow(card, mAmount);
    }

    private void showAvailableOptions() {

        final List<PaymentMethodInfo> pluginUpItems = new ArrayList<>();
        final List<PaymentMethodInfo> pluginDownItems = new ArrayList<>();
        final List<PaymentMethodPlugin> paymentMethodPlugins = CheckoutStore.getInstance().getPaymentMethodPluginList();

        if (paymentMethodPlugins != null && !paymentMethodPlugins.isEmpty()) {
            for (PaymentMethodPlugin plugin : paymentMethodPlugins) {
                final PaymentMethodInfo info = plugin.getPaymentMethodInfo();
                if (info != null) {
                    if (PaymentMethodPlugin.POSIION_TOP.equalsIgnoreCase(plugin.displayOrder())) {
                        pluginUpItems.add(info);
                    } else if (PaymentMethodPlugin.POSIION_BOTTOM.equalsIgnoreCase(plugin.displayOrder())) {
                        pluginDownItems.add(info);
                    }
                }
            }
        }

        getView().showPluginOptions(pluginUpItems);

        if (mPaymentMethodSearch.hasCustomSearchItems()) {
            List<CustomSearchItem> shownCustomItems;

            if (mShowAllSavedCardsEnabled) {
                shownCustomItems = mPaymentMethodSearch.getCustomSearchItems();
            } else {
                shownCustomItems = getLimitedCustomOptions(mPaymentMethodSearch.getCustomSearchItems(), mMaxSavedCards);
            }


            getView().showCustomOptions(shownCustomItems, getCustomOptionCallback());
        }

        if (searchItemsAvailable()) {
            getView().showSearchItems(mPaymentMethodSearch.getGroups(), getPaymentMethodSearchItemSelectionCallback());
        }

        getView().showPluginOptions(pluginDownItems);
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
        accountMoneyPaymentMethod = new PaymentMethod();
        accountMoneyPaymentMethod.setId(ACCOUNT_MONEY_ID);
        accountMoneyPaymentMethod.setName(searchItem.getDescription());
        accountMoneyPaymentMethod.setPaymentTypeId(searchItem.getType());
        if (!showHook1(ACCOUNT_MONEY_ID, MercadoPagoComponents.Activities.HOOK_1_ACCOUNT_MONEY)) {
            getView().finishPaymentMethodSelection(accountMoneyPaymentMethod);
        }
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

    private void startNextStepForPaymentType(final PaymentMethodSearchItem item, final boolean automaticSelection) {

        if (skipHook || (!hook1Displayed && !showHook1(item.getId()))) {
            skipHook = false;

            if (mPaymentPreference == null) {
                mPaymentPreference = new PaymentPreference();
            }

            if (MercadoPagoUtil.isCard(item.getId())) {
                getView().startCardFlow(item.getId(), mAmount, automaticSelection);
            } else {
                getView().startPaymentMethodsSelection();
            }

        } else {

            resumeItem = item;
        }
    }

    private void resolvePaymentMethodSelection(PaymentMethodSearchItem item) {

        final PaymentMethod selectedPaymentMethod = mPaymentMethodSearch.getPaymentMethodBySearchItem(item);

        if (skipHook || (!hook1Displayed && !showHook1(selectedPaymentMethod.getPaymentTypeId()))) {
            skipHook = false;

            if (selectedPaymentMethod == null) {
                showMismatchingPaymentMethodError();
            } else if (selectedPaymentMethod.getId().equals(PaymentMethods.BRASIL.BOLBRADESCO)) {
                mSelectedPaymentMethod = selectedPaymentMethod;
                getView().collectPayerInformation();
            } else {
                getView().finishPaymentMethodSelection(selectedPaymentMethod);
            }

        } else {
            resumeItem = item;
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
        getView().showError(new MercadoPagoError(errorMessage, false), "");
    }

    private void showMismatchingPaymentMethodError() {
        String errorMessage = getResourcesProvider().getStandardErrorMessage();
        getView().showError(new MercadoPagoError(errorMessage, MISMATCHING_PAYMENT_METHOD_ERROR, false), "");
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

    public void setShowAllSavedCardsEnabled(boolean showAll) {
        this.mShowAllSavedCardsEnabled = showAll;
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

    //###Hooks HACKS #######################################################

    public void onHookContinue() {
        if (resumeItem != null) {
            skipHook = true;
            selectItem(resumeItem, true);
        }
    }

    public void onHookReset() {
        hook1Displayed = false;
        resumeItem = null;
    }

    public boolean showHook1(final String typeId) {
        return showHook1(typeId, MercadoPagoComponents.Activities.HOOK_1);
    }

    public boolean showHook1(final String typeId, final int requestCode) {

        final Map<String, Object> data = CheckoutStore.getInstance().getData();
        final Hook hook = HookHelper.activateBeforePaymentMethodConfig(
                CheckoutStore.getInstance().getCheckoutHooks(), typeId, data);

        if (resumeItem == null && hook != null && getView() != null) {
            hook1Displayed = true;
            getView().showHook(hook, requestCode);
            return true;
        }
        return false;
    }

    public PaymentMethod getAccountMoneyPaymentMethod() {
        return accountMoneyPaymentMethod;
    }
}