package com.mercadopago.presenters;

import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.Cause;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.SavedESCCardToken;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.mvp.MvpPresenter;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.preferences.PaymentPreference;
import com.mercadopago.providers.CardVaultProvider;
import com.mercadopago.tracking.utils.TrackingUtil;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.TextUtil;
import com.mercadopago.views.CardVaultView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by vaserber on 10/12/16.
 */

public class CardVaultPresenter extends MvpPresenter<CardVaultView, CardVaultProvider> {

    protected FailureRecovery mFailureRecovery;
    protected String mBin;

    //Activity parameters
    protected PaymentRecovery mPaymentRecovery;
    protected PaymentPreference mPaymentPreference;
    protected List<PaymentMethod> mPaymentMethodList;
    protected Site mSite;
    protected boolean mInstallmentsEnabled;
    protected boolean mInstallmentsReviewEnabled;
    protected boolean mAutomaticSelection;
    protected BigDecimal mAmount;
    protected String mMerchantBaseUrl;
    protected String mMerchantDiscountUrl;
    protected String mMerchantGetDiscountUri;
    protected Map<String, String> mDiscountAdditionalInfo;
    protected boolean mInstallmentsListShown;
    protected boolean mIssuersListShown;

    //Activity result
    protected PaymentMethod mPaymentMethod;
    protected PayerCost mPayerCost;
    protected Issuer mIssuer;

    //Card Info
    protected CardInfo mCardInfo;
    protected Token mToken;
    protected Card mCard;

    //Discount
    protected boolean mDiscountEnabled;
    protected boolean mDirectDiscountEnabled;
    protected Discount mDiscount;
    protected String mPayerEmail;
    protected List<PayerCost> mPayerCostsList;
    protected List<Issuer> mIssuersList;

    //Security Code
    protected String mESC;
    protected SavedESCCardToken mESCCardToken;

    public CardVaultPresenter() {
        super();
        this.mInstallmentsEnabled = true;
        this.mDiscountEnabled = true;
        this.mPaymentPreference = new PaymentPreference();
    }

    public void initialize() {
        try {
            validateParameters();
            onValidStart();
        } catch (final IllegalStateException exception) {
            getView().showError(new MercadoPagoError(exception.getMessage(), false), "");
        }
    }

    private boolean viewAttached() {
        return getView() != null;
    }

    public void setPaymentRecovery(final PaymentRecovery paymentRecovery) {
        this.mPaymentRecovery = paymentRecovery;
    }

    public void setPaymentPreference(final PaymentPreference paymentPreference) {
        this.mPaymentPreference = paymentPreference;
    }

    public void setPaymentMethodList(final List<PaymentMethod> paymentMethodList) {
        this.mPaymentMethodList = paymentMethodList;
    }

    public void setSite(final Site site) {
        this.mSite = site;
    }

    public void setInstallmentsEnabled(final boolean installmentsEnabled) {
        this.mInstallmentsEnabled = installmentsEnabled;
    }

    public void setCard(final Card card) {
        this.mCard = card;
    }

    public void setAmount(final BigDecimal amount) {
        this.mAmount = amount;
    }

    public void setFailureRecovery(final FailureRecovery failureRecovery) {
        this.mFailureRecovery = failureRecovery;
    }

    public Issuer getIssuer() {
        return mIssuer;
    }

    public void setIssuer(final Issuer mIssuer) {
        this.mIssuer = mIssuer;
    }

    public Token getToken() {
        return mToken;
    }

    public void setToken(final Token mToken) {
        this.mToken = mToken;
    }

    public PaymentMethod getPaymentMethod() {
        return mPaymentMethod;
    }

    public void setPaymentMethod(final PaymentMethod mPaymentMethod) {
        this.mPaymentMethod = mPaymentMethod;
    }

    public PayerCost getPayerCost() {
        return mPayerCost;
    }

    public void setPayerCost(final PayerCost mPayerCost) {
        this.mPayerCost = mPayerCost;
    }

    public BigDecimal getAmount() {
        return mAmount;
    }

    public PaymentRecovery getPaymentRecovery() {
        return mPaymentRecovery;
    }

    public PaymentPreference getPaymentPreference() {
        return mPaymentPreference;
    }

    public List<PaymentMethod> getPaymentMethodList() {
        return mPaymentMethodList;
    }

    public Site getSite() {
        return mSite;
    }

    public Card getCard() {
        return mCard;
    }

    public String getESC() {
        return mESC;
    }

    public void setESC(final String esc) {
        this.mESC = esc;
    }

    public void setCardInfo(final CardInfo cardInfo) {
        this.mCardInfo = cardInfo;
        if (mCardInfo == null) {
            mBin = "";
        } else {
            mBin = mCardInfo.getFirstSixDigits();
        }
    }

    public void setPayerEmail(final String payerEmail) {
        this.mPayerEmail = payerEmail;
    }

    public String getPayerEmail() {
        return this.mPayerEmail;
    }

    public void setDiscount(final Discount discount) {
        this.mDiscount = discount;
    }

    public Discount getDiscount() {
        return mDiscount;
    }

    public void setDiscountEnabled(final boolean discountEnabled) {
        this.mDiscountEnabled = discountEnabled;
    }

    public boolean getDiscountEnabled() {
        return this.mDiscountEnabled;
    }

    public void setDiscountAdditionalInfo(final Map<String, String> discountAdditionalInfo) {
        this.mDiscountAdditionalInfo = discountAdditionalInfo;
    }

    public Map<String, String> getDiscountAdditionalInfo() {
        return this.mDiscountAdditionalInfo;
    }

    public void setInstallmentsReviewEnabled(final boolean installmentReviewEnabled) {
        this.mInstallmentsReviewEnabled = installmentReviewEnabled;
    }

    public Boolean getInstallmentsReviewEnabled() {
        return this.mInstallmentsReviewEnabled;
    }

    public CardInfo getCardInfo() {
        return mCardInfo;
    }

    public Integer getCardNumberLength() {
        return PaymentMethodGuessingController.getCardNumberLength(mPaymentMethod, mBin);
    }

    public void setMerchantBaseUrl(final String merchantBaseUrl) {
        this.mMerchantBaseUrl = merchantBaseUrl;
    }

    public String getMerchantBaseUrl() {
        return this.mMerchantBaseUrl;
    }

    public void setMerchantDiscountBaseUrl(final String merchantDiscountUrl) {
        this.mMerchantDiscountUrl = merchantDiscountUrl;
    }

    public String getMerchantDiscountBaseUrl() {
        return this.mMerchantDiscountUrl;
    }

    public void setMerchantGetDiscountUri(final String merchantGetDiscountUri) {
        this.mMerchantGetDiscountUri = merchantGetDiscountUri;
    }

    public String getMerchantGetDiscountUri() {
        return mMerchantGetDiscountUri;
    }

    public void setDirectDiscountEnabled(final boolean directDiscountEnabled) {
        this.mDirectDiscountEnabled = directDiscountEnabled;
    }

    public boolean getDirectDiscountEnabled() {
        return this.mDirectDiscountEnabled;
    }

    public void setAutomaticSelection(final boolean automaticSelection) {
        this.mAutomaticSelection = automaticSelection;
    }

    public boolean getAutomaticSelection() {
        return mAutomaticSelection;
    }

    public boolean isInstallmentsListShown() {
        return mInstallmentsListShown;
    }

    public boolean isIssuersListShown() {
        return mIssuersListShown;
    }

    public void setInstallmentsListShown(final boolean installmentsListShown) {
        mInstallmentsListShown = installmentsListShown;
    }

    public void setIssuersListShown(final boolean issuersListShown) {
        mIssuersListShown = issuersListShown;
    }

    private void checkStartInstallmentsActivity() {
        if (isInstallmentsEnabled() && mPayerCost == null) {
            mInstallmentsListShown = true;
            askForInstallments();
        } else {
            getView().finishWithResult();
        }
    }

    private void askForInstallments() {
        if (mIssuersListShown) {
            getView().askForInstallmentsFromIssuers();
        } else if (!savedCardAvailable()) {
            getView().askForInstallmentsFromNewCard();
        } else {
            getView().askForInstallments();
        }
    }

    private void checkStartIssuersActivity() {
        if (mIssuer == null) {
            mIssuersListShown = true;
            getView().startIssuersActivity();
        } else {
            checkStartInstallmentsActivity();
        }
    }

    public boolean isInstallmentsEnabled() {
        return mInstallmentsEnabled;
    }

    private void validateParameters() throws IllegalStateException {
        if (mInstallmentsEnabled) {
            if (mSite == null) {
                throw new IllegalStateException(getResourcesProvider().getMissingSiteErrorMessage());
            } else if (mAmount == null) {
                throw new IllegalStateException(getResourcesProvider().getMissingAmountErrorMessage());
            }
        }
    }

    public void recoverFromFailure() {
        if (mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }

    public List<PayerCost> getPayerCostList() {
        return mPayerCostsList;
    }

    private void getInstallmentsForCardAsync(final Card card) {
        String bin = TextUtil.isEmpty(mCardInfo.getFirstSixDigits()) ? "" : mCardInfo.getFirstSixDigits();
        Long issuerId = mCard.getIssuer() == null ? null : mCard.getIssuer().getId();
        String paymentMethodId = card.getPaymentMethod() == null ? "" : card.getPaymentMethod().getId();

        getResourcesProvider().getInstallmentsAsync(bin, issuerId, paymentMethodId, getTotalAmount(), new OnResourcesRetrievedCallback<List<Installment>>() {
            @Override
            public void onSuccess(final List<Installment> installments) {
                resolveInstallmentsList(installments);
            }

            @Override
            public void onFailure(final MercadoPagoError error) {
                if (viewAttached()) {
                    getView().showError(error, ApiUtil.RequestOrigin.GET_INSTALLMENTS);

                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            getInstallmentsForCardAsync(card);
                        }
                    });
                }
            }
        });
    }

    private void resolveInstallmentsList(final List<Installment> installments) {
        String errorMessage = null;
        if (installments.size() == 0) {
            errorMessage = getResourcesProvider().getMissingInstallmentsForIssuerErrorMessage();
        } else if (installments.size() == 1) {
            resolvePayerCosts(installments.get(0).getPayerCosts());
        } else {
            errorMessage = getResourcesProvider().getMultipleInstallmentsForIssuerErrorMessage();
        }
        if (errorMessage != null && isViewAttached()) {
            getView().showError(new MercadoPagoError(errorMessage, false), "");
        }
    }

    private BigDecimal getTotalAmount() {
        BigDecimal amount;

        if (!mDiscountEnabled || mDiscount == null) {
            amount = mAmount;
        } else {
            amount = mDiscount.getAmountWithDiscount(mAmount);
        }
        return amount;
    }

    private void resolvePayerCosts(final List<PayerCost> payerCosts) {
        PayerCost defaultPayerCost = mPaymentPreference.getDefaultInstallments(payerCosts);
        mPayerCostsList = payerCosts;

        if (defaultPayerCost != null) {
            mPayerCost = defaultPayerCost;
            getView().askForSecurityCodeWithoutInstallments();
        } else if (mPayerCostsList.isEmpty()) {
            getView().showError(new MercadoPagoError(getResourcesProvider().getMissingPayerCostsErrorMessage(), false), "");
        } else if (mPayerCostsList.size() == 1) {
            mPayerCost = payerCosts.get(0);
            getView().askForSecurityCodeWithoutInstallments();
        } else {
            mInstallmentsListShown = true;
            getView().askForInstallments();
        }
    }

    public void resolveIssuersRequest(final Issuer issuer) {
        mIssuersListShown = true;
        setIssuer(issuer);
        checkStartInstallmentsActivity();
    }

    public void resolveInstallmentsRequest(final PayerCost payerCost, final Discount discount) {
        setSelectedInstallments(payerCost, discount);

        if (savedCardAvailable()) {
            if (mInstallmentsListShown) {
                getView().askForSecurityCodeFromInstallments();
            } else {
                getView().askForSecurityCodeWithoutInstallments();
            }
        } else {
            getView().finishWithResult();
        }
    }

    private void setSelectedInstallments(final PayerCost payerCost, final Discount discount) {
        mInstallmentsListShown = true;
        setPayerCost(payerCost);
        setDiscount(discount);
    }

    public void resolveSecurityCodeRequest(final Token token) {
        setToken(token);
        if (tokenRecoveryAvailable()) {
            setPayerCost(getPaymentRecovery().getPayerCost());
            setIssuer(getPaymentRecovery().getIssuer());
        }
        getView().finishWithResult();
    }

    public void resolveNewCardRequest(final PaymentMethod paymentMethod, final Token token,
                                      final boolean directDiscountEnabled,
                                      final boolean discountEnabled,
                                      final PayerCost payerCost, final Issuer issuer,
                                      final List<PayerCost> payerCosts, final List<Issuer> issuers,
                                      final Discount discount) {

        setPaymentMethod(paymentMethod);
        setToken(token);
        setCardInfo(new CardInfo(token));
        setDirectDiscountEnabled(directDiscountEnabled);
        setDiscountEnabled(discountEnabled);
        setPayerCost(payerCost);
        setIssuer(issuer);
        setPayerCostsList(payerCosts);
        setIssuersList(issuers);

        if (discount != null) {
            setDiscount(discount);
        }

        checkStartIssuersActivity();
    }

    public void onResultCancel() {
        getView().cancelCardVault();
    }

    private void onValidStart() {
        mInstallmentsListShown = false;
        mIssuersListShown = false;
        if (viewAttached()) {
            getView().showProgressLayout();
        }
        if (tokenRecoveryAvailable()) {
            startTokenRecoveryFlow();
        } else if (savedCardAvailable()) {
            startSavedCardFlow();
        } else {
            startNewCardFlow();
        }
    }

    private void startTokenRecoveryFlow() {
        setCardInfo(new CardInfo(getPaymentRecovery().getToken()));
        setPaymentMethod(getPaymentRecovery().getPaymentMethod());
        setToken(getPaymentRecovery().getToken());
        getView().askForSecurityCodeFromTokenRecovery();
    }

    private void startSavedCardFlow() {
        setCardInfo(new CardInfo(getCard()));
        setPaymentMethod(getCard().getPaymentMethod());
        setIssuer(getCard().getIssuer());
        if (isInstallmentsEnabled()) {
            getInstallmentsForCardAsync(getCard());
        } else {
            getView().askForSecurityCodeWithoutInstallments();
        }
    }

    private void startNewCardFlow() {
        getView().askForCardInformation();
    }

    private boolean tokenRecoveryAvailable() {
        return getPaymentRecovery() != null && getPaymentRecovery().isTokenRecoverable();
    }

    private boolean savedCardAvailable() {
        return getCard() != null;
    }

    public void setPayerCostsList(final List<PayerCost> payerCostsList) {
        this.mPayerCostsList = payerCostsList;
    }

    private void setIssuersList(final List<Issuer> issuers) {
        mIssuersList = issuers;
    }

    public List<Issuer> getIssuersList() {
        return mIssuersList;
    }

    public void checkSecurityCodeFlow() {
        if (savedCardAvailable() && isESCSaved()) {
            createESCToken();
        } else {
            getView().startSecurityCodeActivity(TrackingUtil.SECURITY_CODE_REASON_SAVED_CARD);
        }
    }

    private boolean isESCSaved() {
        if (!isESCEmpty()) {
            return true;
        } else {
            setESC(getResourcesProvider().findESCSaved(mCard.getId()));
            return !isESCEmpty();
        }
    }

    private boolean isESCEmpty() {
        return mESC == null || mESC.isEmpty();
    }

    private void createESCToken() {
        if (savedCardAvailable() && !isESCEmpty()) {

            mESCCardToken = new SavedESCCardToken(mCard.getId(), "", true, mESC);

            getResourcesProvider().createESCTokenAsync(mESCCardToken, new OnResourcesRetrievedCallback<Token>() {
                @Override
                public void onSuccess(final Token token) {
                    mToken = token;
                    mToken.setLastFourDigits(mCard.getLastFourDigits());
                    getView().finishWithResult();
                }

                @Override
                public void onFailure(final MercadoPagoError error) {

                    if (error.isApiException() && error.getApiException().getStatus().equals(ApiUtil.StatusCodes.BAD_REQUEST)) {
                        List<Cause> causes = error.getApiException().getCause();
                        if (causes != null && !causes.isEmpty()) {
                            Cause cause = causes.get(0);
                            if (ApiException.ErrorCodes.INVALID_ESC.equals(cause.getCode()) ||
                                    ApiException.ErrorCodes.INVALID_FINGERPRINT.equals(cause.getCode())) {

                                getResourcesProvider().deleteESC(mESCCardToken.getCardId());

                                mESC = null;
                                if (viewAttached()) {
                                    getView().startSecurityCodeActivity(TrackingUtil.SECURITY_CODE_REASON_ESC);
                                }
                            } else {
                                recoverCreateESCToken(error);
                            }
                        }
                    } else {
                        recoverCreateESCToken(error);
                    }
                }
            });
        }
    }

    private void recoverCreateESCToken(final MercadoPagoError error) {
        if (viewAttached()) {
            getView().showError(error, ApiUtil.RequestOrigin.CREATE_TOKEN);

            setFailureRecovery(new FailureRecovery() {
                @Override
                public void recover() {
                    createESCToken();
                }
            });
        }
    }
}
