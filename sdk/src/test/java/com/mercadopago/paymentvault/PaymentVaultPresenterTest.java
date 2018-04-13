package com.mercadopago.paymentvault;

import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.constants.PaymentMethods;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.hooks.Hook;
import com.mercadopago.lite.exceptions.ApiException;
import com.mercadopago.mocks.PaymentMethodSearchs;
import com.mercadopago.model.Card;
import com.mercadopago.model.CustomSearchItem;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Payer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.PaymentTypes;
import com.mercadopago.model.Site;
import com.mercadopago.model.Sites;
import com.mercadopago.mvp.TaggedCallback;
import com.mercadopago.plugins.PaymentMethodPlugin;
import com.mercadopago.preferences.FlowPreference;
import com.mercadopago.preferences.PaymentPreference;
import com.mercadopago.presenters.PaymentVaultPresenter;
import com.mercadopago.providers.PaymentVaultProvider;
import com.mercadopago.utils.Discounts;
import com.mercadopago.views.PaymentVaultView;

import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class PaymentVaultPresenterTest {

    @Test
    public void ifSiteNotSetShowInvalidSiteError() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodWithoutCustomOptionsMLA();

        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);

        presenter.initialize(true);

        assertEquals(MockedProvider.INVALID_SITE, mockedView.errorShown.getMessage());
    }

    @Test
    public void ifInvalidCurrencySetShowInvalidSiteError() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodWithoutCustomOptionsMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(new Site("invalid_id", "invalid_currency"));
        presenter.setAmount(BigDecimal.TEN);

        presenter.initialize(true);

        assertEquals(MockedProvider.INVALID_SITE, mockedView.errorShown.getMessage());
    }

    @Test
    public void whenItemSelectedAvailableTrackIt() {
        PaymentVaultView mockView = mock(PaymentVaultView.class);
        PaymentVaultProvider mockProvider = mock(PaymentVaultProvider.class);
        Site mockSite = mock(Site.class);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockView);
        presenter.attachResourcesProvider(mockProvider);
        presenter.setSite(mockSite);

        PaymentMethodSearchItem mockPaymentOptions = mock(PaymentMethodSearchItem.class);

        presenter.setSelectedSearchItem(mockPaymentOptions);

        presenter.trackChildrenScreen();
        verify(mockProvider).trackChildrenScreen(mockPaymentOptions, mockSite.getId());
        verifyNoMoreInteractions(mockProvider);
        verifyNoMoreInteractions(mockView);
    }

    @Test
    public void whenItemSelectedNotAvailableTrackFirstOfGroup() {
        PaymentVaultView mockView = mock(PaymentVaultView.class);
        PaymentVaultProvider mockProvider = mock(PaymentVaultProvider.class);
        Site mockSite = mock(Site.class);

        PaymentMethodSearch mockPaymentOptions = mock(PaymentMethodSearch.class);
        PaymentMethodSearchItem mockPaymentOptionsItem = mock(PaymentMethodSearchItem.class);

        List<PaymentMethodSearchItem> paymentMethodSearchItems = Arrays.asList(mockPaymentOptionsItem);
        when(mockPaymentOptions.getGroups()).thenReturn(paymentMethodSearchItems);
        when(mockPaymentOptions.hasSearchItems()).thenReturn(true);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockView);
        presenter.attachResourcesProvider(mockProvider);

        presenter.setSite(mockSite);
        presenter.setPaymentMethodSearch(mockPaymentOptions);

        presenter.trackChildrenScreen();
        verify(mockProvider).trackChildrenScreen(paymentMethodSearchItems.get(0), mockSite.getId());
        verifyNoMoreInteractions(mockProvider);
        verifyNoMoreInteractions(mockView);
    }

    @Test
    public void ifAmountNotSetShowInvalidAmountError() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodWithoutCustomOptionsMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);

        presenter.initialize(true);

        assertEquals(MockedProvider.INVALID_AMOUNT, mockedView.errorShown.getMessage());
    }

    @Test
    public void ifInvalidAmountSetShowInvalidAmountError() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodWithoutCustomOptionsMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(BigDecimal.TEN.negate());

        presenter.initialize(true);

        assertEquals(MockedProvider.INVALID_AMOUNT, mockedView.errorShown.getMessage());
    }

    @Ignore
    @Test
    public void ifNoPaymentMethodsAvailableThenShowError() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = new PaymentMethodSearch();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(BigDecimal.TEN);

        presenter.initialize(true);

        assertTrue(mockedView.errorShown.getMessage().equals(MockedProvider.EMPTY_PAYMENT_METHODS));
    }

    @Test
    public void ifPaymentMethodSearchHasItemsShowThem() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodWithoutCustomOptionsMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);

        presenter.initialize(true);

        assertEquals(paymentMethodSearch.getGroups(), mockedView.searchItemsShown);
    }

    @Test
    public void ifPaymentMethodSearchHasPayerCustomOptionsShowThem() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);

        presenter.initialize(true);

        assertEquals(paymentMethodSearch.getCustomSearchItems(), mockedView.customOptionsShown);
    }

    @Test
    public void whenItemWithChildrenSelectedThenShowChildren() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);

        presenter.initialize(true);
        mockedView.simulateItemSelection(1);

        assertEquals(paymentMethodSearch.getGroups().get(1).getChildren(), mockedView.searchItemsShown);
    }

    @Test
    public void whenDiscountsItemSelectedThenStartDiscountFlow() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);

        presenter.initialize(true);
        presenter.onDiscountOptionSelected();

        assertTrue(mockedView.discountsFlowStarted);
    }

    //Automatic selections

    @Ignore
    @Test
    public void ifOnlyUniqueSearchItemAvailableRestartWithItSelected() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodSearchWithOnlyTicketMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);

        presenter.initialize(true);

        assertEquals(paymentMethodSearch.getGroups().get(0), mockedView.itemShown);
    }

    @Ignore
    @Test
    public void ifOnlyCardPaymentTypeAvailableStartCardFlow() {

        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodSearchWithOnlyCreditCardMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        BigDecimal amount = BigDecimal.TEN;
        presenter.setAmount(amount);
        presenter.setSite(Sites.ARGENTINA);

        presenter.initialize(true);

        assertTrue(mockedView.cardFlowStarted);
        assertEquals(paymentMethodSearch.getGroups().get(0).getId(), mockedView.selectedPaymentType);
        assertEquals(amount, mockedView.amountSentToCardFlow);
    }

    @Test
    public void ifOnlyCardPaymentTypeAvailableAndCardAvailableDoNotSelectAutomatically() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodSearchWithOnlyCreditCardAndOneCardMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        BigDecimal amount = BigDecimal.TEN;
        presenter.setAmount(amount);
        presenter.setSite(Sites.ARGENTINA);

        presenter.initialize(true);

        assertTrue(mockedView.customOptionsShown != null);
        assertFalse(mockedView.cardFlowStarted);
        assertFalse(mockedView.isItemShown);
    }

    @Test
    public void ifOnlyCardPaymentTypeAvailableButAutomaticSelectionDisabledThenDoNotSelectAutomatically() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodSearchWithOnlyCreditCardMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        BigDecimal amount = BigDecimal.TEN;
        presenter.setAmount(amount);
        presenter.setSite(Sites.ARGENTINA);

        presenter.initialize(false);

        assertFalse(mockedView.cardFlowStarted);
    }

    @Test
    public void ifOnlyCardPaymentTypeAvailableAndAccountMoneyAvailableDoNotSelectAutomatically() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodSearchWithOnlyCreditCardAndAccountMoneyMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        BigDecimal amount = BigDecimal.TEN;
        presenter.setAmount(amount);
        presenter.setSite(Sites.ARGENTINA);

        presenter.initialize(true);

        assertTrue(mockedView.customOptionsShown != null);
        assertFalse(mockedView.cardFlowStarted);
        assertFalse(mockedView.isItemShown);
    }

    @Test
    public void ifOnlyOffPaymentTypeAvailableAndAccountMoneyAvailableDoNotSelectAutomatically() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodSearchWithOnlyOneOffTypeAndAccountMoneyMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        BigDecimal amount = BigDecimal.TEN;
        presenter.setAmount(amount);
        presenter.setSite(Sites.ARGENTINA);

        presenter.initialize(true);

        assertTrue(mockedView.customOptionsShown != null);
        assertFalse(mockedView.cardFlowStarted);
        assertFalse(mockedView.isItemShown);
    }

    //User selections

    @Test
    public void ifItemSelectedShowItsChildren() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);

        presenter.initialize(true);

        mockedView.simulateItemSelection(1);

        assertEquals(paymentMethodSearch.getGroups().get(1).getChildren(), mockedView.searchItemsShown);
        assertEquals(paymentMethodSearch.getGroups().get(1), mockedView.itemShown);
    }

    @Test
    public void ifCardPaymentTypeSelectedStartCardFlow() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);

        presenter.initialize(true);

        mockedView.simulateItemSelection(0);

        assertEquals(paymentMethodSearch.getGroups().get(0).getId(), mockedView.selectedPaymentType);
        assertTrue(mockedView.cardFlowStarted);
    }

    @Test
    public void ifSavedCardSelectedStartSavedCardFlow() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);

        presenter.initialize(true);

        mockedView.simulateCustomItemSelection(1);

        assertTrue(mockedView.savedCardFlowStarted);
        assertTrue(mockedView.savedCardSelected.equals(paymentMethodSearch.getCards().get(0)));
    }

    //Payment Preference tests
    @Test
    public void ifAllPaymentTypesExcludedShowError() {
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentTypeIds(PaymentTypes.getAllPaymentTypes());

        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodWithoutCustomOptionsMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(BigDecimal.TEN);
        presenter.setPaymentPreference(paymentPreference);

        presenter.initialize(true);

        assertEquals(MockedProvider.ALL_TYPES_EXCLUDED, mockedView.errorShown.getMessage());
    }

    @Test
    public void ifInvalidDefaultInstallmentsShowError() {
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultInstallments(BigDecimal.ONE.negate().intValue());

        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodWithoutCustomOptionsMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(BigDecimal.TEN);
        presenter.setPaymentPreference(paymentPreference);

        presenter.initialize(true);

        assertEquals(MockedProvider.INVALID_DEFAULT_INSTALLMENTS, mockedView.errorShown.getMessage());
    }

    @Test
    public void ifInvalidMaxInstallmentsShowError() {
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setMaxAcceptedInstallments(BigDecimal.ONE.negate().intValue());

        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodWithoutCustomOptionsMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(BigDecimal.TEN);
        presenter.setPaymentPreference(paymentPreference);

        presenter.initialize(true);

        assertEquals(MockedProvider.INVALID_MAX_INSTALLMENTS, mockedView.errorShown.getMessage());
    }

    @Test
    public void ifMaxSavedCardNotSetDoNotLimitCardsShown() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);

        presenter.initialize(true);

        assertEquals(paymentMethodSearch.getCustomSearchItems().size(), mockedView.customOptionsShown.size());
    }

    @Test
    public void ifMaxSavedCardLimitCardsShown() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);
        presenter.setMaxSavedCards(1);

        presenter.initialize(true);

        //Account money + 1 card
        assertEquals(2, mockedView.customOptionsShown.size());
    }

    //Discounts
    @Test
    public void ifDiscountsAreNotEnabledNotShowDiscountRow() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);
        presenter.setDiscountEnabled(false);

        presenter.initialize(true);

        mockedView.simulateItemSelection(0);

        assertTrue(mockedView.showedDiscountRow);
        assertTrue(presenter.getDiscount() == null);
    }

    @Test
    public void ifDiscountsAreEnabledGetDirectDiscount() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        provider.setResponse(paymentMethodSearch);

        Discount discount = Discounts.getDiscountWithAmountOffMLA();
        provider.setDiscountResponse(discount);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);

        presenter.initialize(true);
    }

    @Test
    public void ifHasNotDirectDiscountsShowDiscountRow() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        provider.setResponse(paymentMethodSearch);

        ApiException apiException = Discounts.getDoNotFindCampaignApiException();
        MercadoPagoError mpException = new MercadoPagoError(apiException, "");
        provider.setDiscountResponse(mpException);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);

        presenter.initialize(true);

        mockedView.simulateItemSelection(0);

        assertTrue(provider.failedResponse.getApiException().getError().equals(provider.CAMPAIGN_DOES_NOT_MATCH_ERROR));
        assertTrue(mockedView.showedDiscountRow);
    }

    @Test
    public void ifIsDirectDiscountNotEnabledNotGetDirectDiscount() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        provider.setResponse(paymentMethodSearch);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);
        presenter.setDirectDiscountEnabled(false);

        presenter.initialize(true);

        mockedView.simulateItemSelection(0);

        assertTrue(mockedView.showedDiscountRow);
    }

    @Test
    public void ifResourcesRetrievalFailThenShowError() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        ApiException apiException = new ApiException();
        apiException.setMessage("Mocked failure");
        MercadoPagoError mercadoPagoError = new MercadoPagoError(apiException, "");
        provider.setResponse(mercadoPagoError);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);
        presenter.setMaxSavedCards(1);

        presenter.initialize(true);

        assertTrue(mockedView.errorShown.getApiException().equals(mercadoPagoError.getApiException()));
    }

    @Test
    public void whenResourcesRetrievalFailedAndRecoverRequestedThenRepeatRetrieval() {
        //Set Up
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        ApiException apiException = new ApiException();
        apiException.setMessage("Mocked failure");
        MercadoPagoError mercadoPagoError = new MercadoPagoError(apiException, "");
        provider.setResponse(mercadoPagoError);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);
        presenter.setMaxSavedCards(1);

        presenter.initialize(true);
        //Presenter gets resources, fails

        provider.setResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());
        presenter.recoverFromFailure();

        assertFalse(mockedView.searchItemsShown.isEmpty());
    }

    @Test
    public void whenResourcesRetrievalFailedButNoViewAttachedThenDoNotRepeatRetrieval() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        ApiException apiException = new ApiException();
        apiException.setMessage("Mocked failure");
        MercadoPagoError mercadoPagoError = new MercadoPagoError(apiException, "");
        provider.setResponse(mercadoPagoError);

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);
        presenter.setMaxSavedCards(1);

        presenter.initialize(true);

        presenter.detachView();

        provider.setResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());
        presenter.recoverFromFailure();

        assertTrue(mockedView.searchItemsShown == null);
    }

    @Test
    public void onDiscountReceivedThenShowIt() {
        PaymentVaultPresenter presenter = new PaymentVaultPresenter();

        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        provider.setResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());
        presenter.recoverFromFailure();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);
        presenter.setMaxSavedCards(1);

        presenter.initialize(true);

        Discount discount = new Discount();
        discount.setCurrencyId("ARS");
        discount.setId("123");
        discount.setAmountOff(new BigDecimal("10"));
        discount.setCouponAmount(new BigDecimal("10"));
        presenter.onDiscountReceived(discount);

        assertTrue(mockedView.showedDiscountRow);
    }

    @Test
    public void onDiscountReceivedThenRetrievePaymentMethodsAgain() {
        PaymentVaultPresenter presenter = new PaymentVaultPresenter();

        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch originalPaymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        originalPaymentMethodSearch.getGroups().remove(0);

        provider.setResponse(originalPaymentMethodSearch);

        presenter.recoverFromFailure();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);
        presenter.setMaxSavedCards(1);

        presenter.initialize(true);

        provider.setResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());

        Discount discount = new Discount();
        discount.setCurrencyId("ARS");
        discount.setId("123");
        discount.setAmountOff(new BigDecimal("10"));
        discount.setCouponAmount(new BigDecimal("10"));
        presenter.onDiscountReceived(discount);

        assertTrue(mockedView.searchItemsShown.size() != originalPaymentMethodSearch.getGroups().size());
    }

    @Test
    public void ifPaymentMethodSearchSetAndHasItemsThenShowThem() {
        PaymentVaultPresenter presenter = new PaymentVaultPresenter();

        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodWithoutCustomOptionsMLA();

        presenter.setPaymentMethodSearch(paymentMethodSearch);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);

        presenter.initialize(true);

        assertEquals(paymentMethodSearch.getGroups(), mockedView.searchItemsShown);
    }

    @Test
    public void ifPaymentMethodSearchItemIsNotCardAndDoesNotHaveChildrenThenStartPaymentMethodsSelection() {
        PaymentVaultPresenter presenter = new PaymentVaultPresenter();

        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getCompletePaymentMethodSearchMLA();
        paymentMethodSearch.getGroups().get(1).getChildren().removeAll(paymentMethodSearch.getGroups().get(1).getChildren());

        provider.setResponse(paymentMethodSearch);

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);

        presenter.initialize(true);

        mockedView.simulateItemSelection(1);
        assertTrue(mockedView.paymentMethodSelectionStarted);
    }

    @Test
    public void ifPaymentMethodTypeSelectedThenSelectPaymentMethod() {
        PaymentVaultPresenter presenter = new PaymentVaultPresenter();

        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodSearchWithPaymentMethodOnTop();

        provider.setResponse(paymentMethodSearch);

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);

        presenter.initialize(true);

        mockedView.simulateItemSelection(1);
        assertTrue(paymentMethodSearch.getGroups().get(1).getId().equals(mockedView.selectedPaymentMethod.getId()));
    }

    @Test
    public void ifShowAllSavedCardsTestThenShowThem() {

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();

        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        // 6 Saved Cards + Account Money
        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodSearchWithSavedCardsMLA();

        provider.setResponse(paymentMethodSearch);

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);
        // Set show all saved cards
        presenter.setShowAllSavedCardsEnabled(true);
        presenter.setMaxSavedCards(FlowPreference.DEFAULT_MAX_SAVED_CARDS_TO_SHOW);

        presenter.initialize(true);

        assertEquals(mockedView.customOptionsShown.size(), paymentMethodSearch.getCustomSearchItems().size());
    }

    @Test
    public void ifMaxSavedCardsSetThenShowWithLimit() {

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();

        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        // 6 Saved Cards + Account Money
        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodSearchWithSavedCardsMLA();

        provider.setResponse(paymentMethodSearch);

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);
        presenter.setMaxSavedCards(4);

        presenter.initialize(true);

        // 4 Cards + Account Money
        assertEquals(mockedView.customOptionsShown.size(), 5);
    }

    @Test
    public void ifMaxSavedCardsSetThenShowWithLimitAgain() {

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();

        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        // 6 Saved Cards + Account Money
        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodSearchWithSavedCardsMLA();

        provider.setResponse(paymentMethodSearch);

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);
        presenter.setMaxSavedCards(1);

        presenter.initialize(true);

        // 1 Card + Account Money
        assertEquals(mockedView.customOptionsShown.size(), 2);
    }

    @Test
    public void ifMaxSavedCardsSetAndShowAllSetThenShowAllSavedCards() {

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();

        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        // 6 Saved Cards + Account Money
        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodSearchWithSavedCardsMLA();

        provider.setResponse(paymentMethodSearch);

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);
        presenter.setShowAllSavedCardsEnabled(true);
        presenter.setMaxSavedCards(4);

        presenter.initialize(true);

        assertEquals(mockedView.customOptionsShown.size(), paymentMethodSearch.getCustomSearchItems().size());
    }

    @Test
    public void ifMaxSavedCardsSetMoreThanActualAmountOfCardsThenShowAll() {

        PaymentVaultPresenter presenter = new PaymentVaultPresenter();

        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        // 6 Saved Cards + Account Money
        PaymentMethodSearch paymentMethodSearch = PaymentMethodSearchs.getPaymentMethodSearchWithSavedCardsMLA();

        provider.setResponse(paymentMethodSearch);

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);
        // More cards than we have
        presenter.setMaxSavedCards(8);

        presenter.initialize(true);

        // Show every card we have
        assertEquals(mockedView.customOptionsShown.size(), paymentMethodSearch.getCustomSearchItems().size());
    }

    @Ignore
    @Test
    public void ifBoletoSelectedThenCollectPayerInformation() {

        // Setup mocks
        PaymentMethodSearchItem boletoItem = new PaymentMethodSearchItem();
        boletoItem.setId(PaymentMethods.BRASIL.BOLBRADESCO);
        boletoItem.setType("payment_method");

        PaymentMethodSearchItem anotherItem = new PaymentMethodSearchItem();
        anotherItem.setId(PaymentMethods.BRASIL.HIPERCARD);
        anotherItem.setType("payment_method");

        PaymentMethod boleto = new PaymentMethod();
        boleto.setId(PaymentMethods.BRASIL.BOLBRADESCO);
        boleto.setPaymentTypeId(PaymentTypes.TICKET);

        List<PaymentMethodSearchItem> items = new ArrayList<>();
        items.add(boletoItem);
        items.add(anotherItem);

        PaymentMethodSearch paymentMethodSearch = mock(PaymentMethodSearch.class);
        when(paymentMethodSearch.getGroups()).thenReturn(items);
        when(paymentMethodSearch.getPaymentMethodBySearchItem(boletoItem)).thenReturn(boleto);

        // Setup presenter
        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);
        presenter.setPaymentMethodSearch(paymentMethodSearch);

        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        // Simulate selection
        presenter.initialize(true);
        mockedView.simulateItemSelection(0);

        assertTrue(mockedView.payerInformationStarted);
    }

    @Ignore
    @Test
    public void ifPayerInformationCollectedThenFinishWithPaymentMethodAndPayer() {

        // Setup mocks
        PaymentMethodSearchItem boletoItem = new PaymentMethodSearchItem();
        boletoItem.setId(PaymentMethods.BRASIL.BOLBRADESCO);
        boletoItem.setType("payment_method");

        PaymentMethodSearchItem anotherItem = new PaymentMethodSearchItem();
        anotherItem.setId(PaymentMethods.BRASIL.HIPERCARD);
        anotherItem.setType("payment_method");

        PaymentMethod boleto = new PaymentMethod();
        boleto.setId(PaymentMethods.BRASIL.BOLBRADESCO);
        boleto.setPaymentTypeId(PaymentTypes.TICKET);

        List<PaymentMethodSearchItem> items = new ArrayList<>();
        items.add(boletoItem);
        items.add(anotherItem);

        PaymentMethodSearch paymentMethodSearch = mock(PaymentMethodSearch.class);
        when(paymentMethodSearch.getGroups()).thenReturn(items);
        when(paymentMethodSearch.getPaymentMethodBySearchItem(boletoItem)).thenReturn(boleto);

        Payer payer = new Payer();

        // Setup presenter
        PaymentVaultPresenter presenter = new PaymentVaultPresenter();
        presenter.setAmount(BigDecimal.TEN);
        presenter.setSite(Sites.ARGENTINA);
        presenter.setPaymentMethodSearch(paymentMethodSearch);

        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        // Simulate selection
        presenter.initialize(true);
        mockedView.simulateItemSelection(0);

        presenter.onPayerInformationReceived(payer);

        assertEquals(boleto, mockedView.selectedPaymentMethod);
        assertEquals(payer, mockedView.selectedPayer);
    }

    private class MockedProvider implements PaymentVaultProvider {

        private static final String INVALID_SITE = "invalid site";
        private static final String INVALID_AMOUNT = "invalid amount";
        private static final String ALL_TYPES_EXCLUDED = "all types excluded";
        private static final String INVALID_DEFAULT_INSTALLMENTS = "invalid default installments";
        private static final String INVALID_MAX_INSTALLMENTS = "invalid max installments";
        private static final String STANDARD_ERROR_MESSAGE = "standard error";
        private static final String EMPTY_PAYMENT_METHODS = "empty payment methods";
        private static final String CAMPAIGN_DOES_NOT_MATCH_ERROR = "campaign-doesnt-match";

        private boolean shouldFail;
        private boolean shouldDiscountFail;
        private PaymentMethodSearch successfulResponse;
        private Discount successfulDiscountResponse;
        private MercadoPagoError failedResponse;

        public void setResponse(PaymentMethodSearch paymentMethodSearch) {
            shouldFail = false;
            successfulResponse = paymentMethodSearch;
        }

        public void setResponse(MercadoPagoError exception) {
            shouldFail = true;
            failedResponse = exception;
        }

        public void setDiscountResponse(Discount discount) {
            shouldDiscountFail = false;
            successfulDiscountResponse = discount;
        }

        public void setDiscountResponse(MercadoPagoError exception) {
            shouldDiscountFail = true;
            failedResponse = exception;
        }

        @Override
        public String getTitle() {
            return "¿Cómo quieres pagar?";
        }

        @Override
        public void getPaymentMethodSearch(BigDecimal amount, PaymentPreference paymentPreference, Payer payer, Site site, TaggedCallback<PaymentMethodSearch> taggedCallback) {
            if (shouldFail) {
                taggedCallback.onFailure(failedResponse);
            } else {
                taggedCallback.onSuccess(successfulResponse);
            }
        }

        @Override
        public void getDirectDiscount(String amount, String payerEmail, TaggedCallback<Discount> taggedCallback) {
            if (shouldDiscountFail) {
                taggedCallback.onFailure(failedResponse);
            } else {
                taggedCallback.onSuccess(successfulDiscountResponse);
            }
        }

        @Override
        public String getInvalidSiteConfigurationErrorMessage() {
            return INVALID_SITE;
        }

        @Override
        public String getInvalidAmountErrorMessage() {
            return INVALID_AMOUNT;
        }

        @Override
        public String getAllPaymentTypesExcludedErrorMessage() {
            return ALL_TYPES_EXCLUDED;
        }

        @Override
        public String getInvalidDefaultInstallmentsErrorMessage() {
            return INVALID_DEFAULT_INSTALLMENTS;
        }

        @Override
        public String getInvalidMaxInstallmentsErrorMessage() {
            return INVALID_MAX_INSTALLMENTS;
        }

        @Override
        public String getStandardErrorMessage() {
            return STANDARD_ERROR_MESSAGE;
        }

        @Override
        public String getEmptyPaymentMethodsErrorMessage() {
            return EMPTY_PAYMENT_METHODS;
        }

        @Override
        public void trackInitialScreen(PaymentMethodSearch paymentMethodSearch, String siteId) {

        }

        @Override
        public void trackChildrenScreen(PaymentMethodSearchItem paymentMethodSearchItem, String siteId) {

        }
    }

    private class MockedView implements PaymentVaultView {

        private List<PaymentMethodSearchItem> searchItemsShown;
        private MercadoPagoError errorShown;
        private List<CustomSearchItem> customOptionsShown;
        private PaymentMethodSearchItem itemShown;
        private boolean cardFlowStarted = false;
        private BigDecimal amountSentToCardFlow;
        private boolean isItemShown;
        private PaymentMethod selectedPaymentMethod;
        private OnSelectedCallback<PaymentMethodSearchItem> itemSelectionCallback;
        private OnSelectedCallback<CustomSearchItem> customItemSelectionCallback;
        private String title;
        private boolean savedCardFlowStarted;
        private boolean payerInformationStarted;
        private Card savedCardSelected;
        private String selectedPaymentType;
        private Boolean showedDiscountRow;
        private boolean discountsFlowStarted = false;
        private boolean paymentMethodSelectionStarted = false;
        private Payer selectedPayer;

        @Override
        public void startSavedCardFlow(Card card, BigDecimal transactionAmount) {
            this.savedCardFlowStarted = true;
            this.savedCardSelected = card;
        }

        @Override
        public void showSelectedItem(PaymentMethodSearchItem item) {
            this.itemShown = item;
            this.isItemShown = true;
            this.searchItemsShown = item.getChildren();
        }

        @Override
        public void showProgress() {
            //Not yet tested
        }

        @Override
        public void hideProgress() {
            //Not yet tested
        }

        @Override
        public void showCustomOptions(List<CustomSearchItem> customSearchItems, OnSelectedCallback<CustomSearchItem> customSearchItemOnSelectedCallback) {
            this.customOptionsShown = customSearchItems;
            this.customItemSelectionCallback = customSearchItemOnSelectedCallback;
        }

        @Override
        public void showPluginOptions(List<PaymentMethodPlugin> items, String position) {

        }

        @Override
        public void showSearchItems(List<PaymentMethodSearchItem> searchItems, OnSelectedCallback<PaymentMethodSearchItem> paymentMethodSearchItemSelectionCallback) {
            this.searchItemsShown = searchItems;
            this.itemSelectionCallback = paymentMethodSearchItemSelectionCallback;
        }

        @Override
        public void showError(MercadoPagoError mpException, String requestOrigin) {
            errorShown = mpException;
        }

        @Override
        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public void startCardFlow(String paymentType, BigDecimal transactionAmount, Boolean automaticallySelection) {
            cardFlowStarted = true;
            amountSentToCardFlow = transactionAmount;
            selectedPaymentType = paymentType;
        }

        @Override
        public void startPaymentMethodsSelection() {
            paymentMethodSelectionStarted = true;
        }

        @Override
        public void finishPaymentMethodSelection(PaymentMethod selectedPaymentMethod) {
            this.selectedPaymentMethod = selectedPaymentMethod;
        }

        @Override
        public void finishPaymentMethodSelection(PaymentMethod paymentMethod, Payer payer) {
            this.selectedPaymentMethod = paymentMethod;
            this.selectedPayer = payer;
        }

        @Override
        public void showDiscount(BigDecimal transactionAmount) {
            this.showedDiscountRow = true;
        }

        @Override
        public void startDiscountFlow(BigDecimal transactionAmount) {
            discountsFlowStarted = true;
        }

        @Override
        public void collectPayerInformation() {
            this.payerInformationStarted = true;
        }

        @Override
        public void cleanPaymentMethodOptions() {
            //Not yet tested
        }

        @Override
        public void showHook(Hook hook, int code) {
            //Not yet tested
        }

        @Override
        public void showPaymentMethodPluginConfiguration() {

        }

        private void simulateItemSelection(int index) {
            itemSelectionCallback.onSelected(searchItemsShown.get(index));
        }

        private void simulateCustomItemSelection(int index) {
            customItemSelectionCallback.onSelected(customOptionsShown.get(index));
        }
    }

}
