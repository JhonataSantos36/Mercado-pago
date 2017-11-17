package com.mercadopago.presenters;

import android.text.TextUtils;

import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.controllers.CheckoutTimer;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.exceptions.CardTokenException;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.BankDeal;
import com.mercadopago.model.CardInformation;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Cardholder;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Identification;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.PaymentType;
import com.mercadopago.model.SecurityCode;
import com.mercadopago.model.Setting;
import com.mercadopago.model.Token;
import com.mercadopago.mvp.MvpPresenter;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.preferences.PaymentPreference;
import com.mercadopago.providers.GuessingCardProvider;
import com.mercadopago.tracker.MPTrackingContext;
import com.mercadopago.uicontrollers.card.CardView;
import com.mercadopago.uicontrollers.card.FrontCardView;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.TextUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.util.MPCardMaskUtil;
import com.mercadopago.views.GuessingCardActivityView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by vaserber on 10/13/16.
 */

public class GuessingCardPresenter extends MvpPresenter<GuessingCardActivityView, GuessingCardProvider> {

    public static final int CARD_DEFAULT_SECURITY_CODE_LENGTH = 4;
    public static final int CARD_DEFAULT_IDENTIFICATION_NUMBER_LENGTH = 12;

    //Card controller
    private PaymentMethodGuessingController mPaymentMethodGuessingController;
    private List<IdentificationType> mIdentificationTypes;

    private FailureRecovery mFailureRecovery;

    //Activity parameters
    private String mPublicKey;
    private String mSiteId;
    private PaymentRecovery mPaymentRecovery;
    private PaymentMethod mPaymentMethod;
    private List<PaymentMethod> mPaymentMethodList;
    private Identification mIdentification;
    private boolean mIdentificationNumberRequired;
    private PaymentPreference mPaymentPreference;
    private String mMerchantBaseUrl;
    private String mMerchantDiscountUrl;
    private String mMerchantGetDiscountUri;
    private Map<String, String> mDiscountAdditionalInfo;
    private Boolean mShowDiscount;

    //Card Settings
    private CardInformation mCardInfo;
    private int mSecurityCodeLength;
    private String mSecurityCodeLocation;
    private boolean mIsSecurityCodeRequired;
    private boolean mEraseSpace;

    //Card Info
    private String mBin;
    private String mCardNumber;
    private String mCardholderName;
    private String mExpiryMonth;
    private String mExpiryYear;
    private String mSecurityCode;
    private IdentificationType mIdentificationType;
    private String mIdentificationNumber;
    private CardToken mCardToken;
    private Token mToken;
    private PaymentType mPaymentType;

    //Extra info
    private List<BankDeal> mBankDealsList;
    private boolean showPaymentTypes;
    private List<PaymentType> mPaymentTypesList;
    private Boolean mShowBankDeals;

    //Discount
    private Boolean mDiscountEnabled;
    private Boolean mDirectDiscountEnabled;
    private String mPayerEmail;
    private BigDecimal mTransactionAmount;
    private Discount mDiscount;
    private String mPrivateKey;
    private int mCurrentNumberLength;
    private Issuer mIssuer;

    public GuessingCardPresenter() {
        super();
        this.mShowBankDeals = true;
        this.mDiscountEnabled = false;
        this.mShowDiscount = false;
        this.mEraseSpace = true;
    }

    public void initialize() {
        try {
            validateParameters();
            onValidStart();
        } catch (IllegalStateException exception) {
            getView().showError(new MercadoPagoError(exception.getMessage(), false), "");
        }
    }

    private boolean isTimerEnabled() {
        return CheckoutTimer.getInstance().isTimerEnabled();
    }

    private void validateParameters() throws IllegalStateException {
        if (mPublicKey == null) {
            throw new IllegalStateException(getResourcesProvider().getMissingPublicKeyErrorMessage());
        }
    }

    private void onValidStart() {
        initializeCardToken();
        getView().onValidStart();
        if (isTimerEnabled()) {
            getView().initializeTimer();
        } else {
            resolveBankDeals();
        }
        checkToLoadDiscountOrPaymentMethods();

        fillRecoveryFields();
    }

    public void setCurrentNumberLength(int currentNumberLength) {
        this.mCurrentNumberLength = currentNumberLength;
    }

    public FailureRecovery getFailureRecovery() {
        return mFailureRecovery;
    }

    public void setFailureRecovery(FailureRecovery failureRecovery) {
        this.mFailureRecovery = failureRecovery;
    }

    public String getPublicKey() {
        return mPublicKey;
    }

    public void setPublicKey(String publicKey) {
        this.mPublicKey = publicKey;
    }

    public void setSiteId(String siteId) {
        this.mSiteId = siteId;
    }

    public PaymentRecovery getPaymentRecovery() {
        return mPaymentRecovery;
    }

    public void setPaymentRecovery(PaymentRecovery paymentRecovery) {
        this.mPaymentRecovery = paymentRecovery;
        if (recoverWithCardholder()) {
            saveCardholderName(paymentRecovery.getToken().getCardHolder().getName());
            saveIdentificationNumber(paymentRecovery.getToken().getCardHolder().getIdentification().getNumber());
        }
    }

    private void fillRecoveryFields() {
        if (recoverWithCardholder()) {
            getView().setCardholderName(mPaymentRecovery.getToken().getCardHolder().getName());
            getView().setIdentificationNumber(mPaymentRecovery.getToken().getCardHolder().getIdentification().getNumber());
        }
    }

    private boolean recoverWithCardholder() {
        return mPaymentRecovery != null && mPaymentRecovery.getToken() != null &&
                mPaymentRecovery.getToken().getCardHolder() != null;
    }

    public PaymentMethod getPaymentMethod() {
        return mPaymentMethod;
    }

    public List<IdentificationType> getIdentificationTypes() {
        return this.mIdentificationTypes;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.mPaymentMethod = paymentMethod;
        if (paymentMethod == null) {
            clearCardSettings();
        }
    }

    public boolean hasToShowPaymentTypes() {
        return showPaymentTypes;
    }

    public boolean isSecurityCodeRequired() {
        return mIsSecurityCodeRequired;
    }

    public void setSecurityCodeRequired(boolean required) {
        this.mIsSecurityCodeRequired = required;
        if (required) {
        }
    }

    public void setSecurityCodeLength(int securityCodeLength) {
        this.mSecurityCodeLength = securityCodeLength;
    }

    private void clearCardSettings() {
        mSecurityCodeLength = CARD_DEFAULT_SECURITY_CODE_LENGTH;
        mSecurityCodeLocation = CardView.CARD_SIDE_BACK;
        mIsSecurityCodeRequired = true;
        mBin = "";
    }

    public String getSecurityCodeLocation() {
        return mSecurityCodeLocation;
    }

    public int getSecurityCodeLength() {
        return mSecurityCodeLength;
    }

    public void setToken(Token token) {
        this.mToken = token;
    }

    public Token getToken() {
        return mToken;
    }

    public CardToken getCardToken() {
        return mCardToken;
    }

    public void setCardToken(CardToken cardToken) {
        this.mCardToken = cardToken;
    }

    public List<PaymentMethod> getPaymentMethodList() {
        return mPaymentMethodList;
    }

    public void setPaymentMethodList(List<PaymentMethod> paymentMethodList) {
        this.mPaymentMethodList = paymentMethodList;
    }

    public void setPaymentTypesList(List<PaymentType> paymentTypesList) {
        this.mPaymentTypesList = paymentTypesList;
    }

    public void setIdentificationTypesList(List<IdentificationType> identificationTypesList) {
        this.mIdentificationTypes = identificationTypesList;
    }

    public void setBankDealsList(List<BankDeal> bankDealsList) {
        this.mBankDealsList = bankDealsList;
    }

    public Identification getIdentification() {
        return mIdentification;
    }

    public void setIdentification(Identification identification) {
        this.mIdentification = identification;
    }

    public boolean isIdentificationNumberRequired() {
        return mIdentificationNumberRequired;
    }

    public void setIdentificationNumberRequired(boolean identificationNumberRequired) {
        this.mIdentificationNumberRequired = identificationNumberRequired;
        if (identificationNumberRequired) {
            getView().showIdentificationInput();
        }
    }

    public PaymentPreference getPaymentPreference() {
        return mPaymentPreference;
    }

    public void setPaymentPreference(PaymentPreference paymentPreference) {
        this.mPaymentPreference = paymentPreference;
    }

    private void initializeCardToken() {
        mCardToken = new CardToken("", null, null, "", "", "", "");
    }

    public String getSecurityCodeFront() {
        String securityCode = null;
        if (mSecurityCodeLocation.equals(CardView.CARD_SIDE_FRONT)) {
            securityCode = getSecurityCode();
        }
        return securityCode;
    }

    public CardInformation getCardInformation() {
        return mCardInfo;
    }

    private boolean isCardLengthResolved() {
        return mPaymentMethod != null && mBin != null;
    }

    public Integer getCardNumberLength() {
        return PaymentMethodGuessingController.getCardNumberLength(mPaymentMethod, mBin);
    }

    public void initializeGuessingCardNumberController() {
        List<PaymentMethod> supportedPaymentMethods = mPaymentPreference
                .getSupportedPaymentMethods(mPaymentMethodList);
        mPaymentMethodGuessingController = new PaymentMethodGuessingController(
                supportedPaymentMethods, mPaymentPreference.getDefaultPaymentTypeId(),
                mPaymentPreference.getExcludedPaymentTypes());
    }

    public List<PaymentMethod> getAllSupportedPaymentMethods() {
        List<PaymentMethod> list = null;
        if (mPaymentMethodGuessingController != null) {
            list = mPaymentMethodGuessingController.getAllSupportedPaymentMethods();
        }
        return list;
    }

    private void startGuessingForm() {
        initializeGuessingCardNumberController();
        getView().initializeTitle();
        getView().setCardNumberListeners(mPaymentMethodGuessingController);
        getView().setCardholderNameListeners();
        getView().setExpiryDateListeners();
        getView().setSecurityCodeListeners();
        getView().setIdentificationTypeListeners();
        getView().setIdentificationNumberListeners();
        getView().setNextButtonListeners();
        getView().setBackButtonListeners();
        getView().setErrorContainerListener();
        getView().setContainerAnimationListeners();
        checkPaymentMethodsSupported(false);
    }

    private void checkPaymentMethodsSupported(boolean withAnimation) {
        if (onlyOnePaymentMethodSupported()) {
            getView().setExclusionWithOneElementInfoView(getAllSupportedPaymentMethods().get(0), withAnimation);
        }
    }

    private boolean onlyOnePaymentMethodSupported() {
        List<PaymentMethod> supportedPaymentMethods = getAllSupportedPaymentMethods();
        return supportedPaymentMethods != null && supportedPaymentMethods.size() == 1;
    }

    private void setInvalidCardMessage() {
        if (onlyOnePaymentMethodSupported()) {
            getView().setInvalidCardOnePaymentMethodErrorView();
        } else {
            getView().setInvalidCardMultipleErrorView();
        }
    }

    public String getPaymentTypeId() {
        if (mPaymentMethodGuessingController == null) {
            if (mPaymentPreference == null) {
                return null;
            } else {
                return mPaymentPreference.getDefaultPaymentTypeId();
            }
        } else {
            return mPaymentMethodGuessingController.getPaymentTypeId();
        }
    }

    private void checkToLoadDiscountOrPaymentMethods() {
        if (showDiscount()) {
            loadDiscount();
        } else {
            loadPaymentMethods();
        }
    }

    private void loadDiscount() {
        initializeDiscountRow();
        loadPaymentMethods();

    }

    private void getDirectDiscount() {
        if (isMerchantServerDiscountsAvailable()) {
            getMerchantDirectDiscount();
        } else {
            getMPDirectDiscount();
        }
    }

    private Boolean isAmountValid() {
        return mTransactionAmount != null && mTransactionAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public void initializeDiscountActivity() {
        getView().startDiscountActivity(mTransactionAmount);
    }

    private void initializeDiscountRow() {
        getView().showDiscountRow(mTransactionAmount);
    }

    private void getMPDirectDiscount() {
        getResourcesProvider().getMPDirectDiscount(mTransactionAmount.toString(), mPayerEmail, new OnResourcesRetrievedCallback<Discount>() {
            @Override
            public void onSuccess(Discount discount) {
                onDiscountSuccess(discount);
            }

            @Override
            public void onFailure(MercadoPagoError error) {
                onDiscountFailure();
            }
        });
    }

    private void getMerchantDirectDiscount() {
        String merchantDiscountUrl = getMerchantServerDiscountUrl();

        getResourcesProvider().getDirectDiscountAsync(mTransactionAmount.toString(), mPayerEmail, merchantDiscountUrl, mMerchantGetDiscountUri, mDiscountAdditionalInfo, new OnResourcesRetrievedCallback<Discount>() {
            @Override
            public void onSuccess(Discount discount) {
                onDiscountSuccess(discount);
            }

            @Override
            public void onFailure(MercadoPagoError error) {
                onDiscountFailure();
            }
        });
    }

    private void onDiscountSuccess(Discount discount) {
        onDiscountReceived(discount);
        loadPaymentMethods();
    }

    private void onDiscountFailure() {
        mDirectDiscountEnabled = false;
        initializeDiscountRow();
        loadPaymentMethods();
    }

    public void onDiscountReceived(Discount discount) {
        setDiscount(discount);
        initializeDiscountRow();
    }

    public Discount getDiscount() {
        return mDiscount;
    }

    public void setDiscount(Discount discount) {
        this.mDiscount = discount;
    }

    public void setPayerEmail(String payerEmail) {
        this.mPayerEmail = payerEmail;
    }

    public String getPayerEmail() {
        return mPayerEmail;
    }

    public void setDiscountEnabled(Boolean discountEnabled) {
        this.mDiscountEnabled = discountEnabled;
    }

    public void setDiscountAdditionalInfo(Map<String, String> discountAdditionalInfo) {
        this.mDiscountAdditionalInfo = discountAdditionalInfo;
    }

    public Map<String, String> getDiscountAdditionalInfo() {
        return this.mDiscountAdditionalInfo;
    }

    public void setMerchantDiscountBaseUrl(String merchantDiscountUrl) {
        this.mMerchantDiscountUrl = merchantDiscountUrl;
    }

    public String getMerchantDiscountBaseUrl() {
        return this.mMerchantDiscountUrl;
    }

    public void setMerchantBaseUrl(String merchantBaseUrl) {
        this.mMerchantBaseUrl = merchantBaseUrl;
    }

    public String getMerchantBaseUrl() {
        return this.mMerchantBaseUrl;
    }

    public void setMerchantGetDiscountUri(String merchantGetDiscountUri) {
        this.mMerchantGetDiscountUri = merchantGetDiscountUri;
    }

    public String getMerchantGetDiscountUri() {
        return mMerchantGetDiscountUri;
    }

    public Boolean getDiscountEnabled() {
        return this.mDiscountEnabled;
    }

    public void setDirectDiscountEnabled(Boolean directDiscountEnabled) {
        this.mDirectDiscountEnabled = directDiscountEnabled;
    }

    public void setShowDiscount(Boolean showDiscount) {
        this.mShowDiscount = showDiscount;
    }

    public Boolean getDirectDiscountEnabled() {
        return this.mDirectDiscountEnabled;
    }

    public BigDecimal getTransactionAmount() {
        BigDecimal amount;

        if (mDiscount != null && mDiscountEnabled && mDiscount.isValid()) {
            amount = mDiscount.getAmountWithDiscount(mTransactionAmount);
        } else {
            amount = mTransactionAmount;
        }

        return amount;
    }

    private void loadPaymentMethods() {
        if (mPaymentMethodList == null || mPaymentMethodList.isEmpty()) {
            getPaymentMethodsAsync();
        } else {
            getView().showInputContainer();
            startGuessingForm();
        }
    }

    public void resolveBankDeals() {
        if (mShowBankDeals) {
            getBankDealsAsync();
        } else {
            getView().hideBankDeals();
        }
    }

    private void getPaymentMethodsAsync() {
        getResourcesProvider().getPaymentMethodsAsync(new OnResourcesRetrievedCallback<List<PaymentMethod>>() {
            @Override
            public void onSuccess(List<PaymentMethod> paymentMethods) {
                resolvePaymentMethodsAsync(paymentMethods);
            }

            @Override
            public void onFailure(MercadoPagoError error) {
                if (isViewAttached()) {
                    getView().showError(error, ApiUtil.RequestOrigin.GET_PAYMENT_METHODS);
                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            getPaymentMethodsAsync();
                        }
                    });
                }
            }
        });
    }

    private void resolvePaymentMethodsAsync(List<PaymentMethod> paymentMethods) {
        getView().showInputContainer();
        mPaymentMethodList = paymentMethods;
        startGuessingForm();
    }

    public void resolvePaymentMethodListSet(List<PaymentMethod> paymentMethodList, String bin) {
        saveBin(bin);
        if (paymentMethodList.isEmpty()) {
            getView().setCardNumberInputMaxLength(MercadoPagoUtil.BIN_LENGTH);
            setInvalidCardMessage();
        } else if (paymentMethodList.size() == 1) {
            onPaymentMethodSet(paymentMethodList.get(0));
        } else if (shouldAskPaymentType(paymentMethodList)) {
            enablePaymentTypeSelection(paymentMethodList);
            onPaymentMethodSet(paymentMethodList.get(0));

        } else {
            onPaymentMethodSet(paymentMethodList.get(0));
        }
    }

    public void onPaymentMethodSet(PaymentMethod paymentMethod) {
        if (mPaymentMethod == null) {
            setPaymentMethod(paymentMethod);
            configureWithSettings();
            loadIdentificationTypes();
            getView().setPaymentMethod(paymentMethod);
        }
        getView().resolvePaymentMethodSet(paymentMethod);
    }

    public void resolvePaymentMethodCleared() {
        getView().clearErrorView();
        getView().hideRedErrorContainerView(true);
        getView().restoreBlackInfoContainerView();
        getView().clearCardNumberInputLength();

        if (!isPaymentMethodResolved()) {
            return;
        }
        clearSpaceErasableSettings();
        getView().clearCardNumberEditTextMask();
        setPaymentMethod(null);
        getView().clearSecurityCodeEditText();
        initializeCardToken();
        setIdentificationNumberRequired(true);
        setSecurityCodeRequired(true);
        disablePaymentTypeSelection();
        getView().checkClearCardView();
        checkPaymentMethodsSupported(true);
    }

    public void setSelectedPaymentType(PaymentType paymentType) {
        if (mPaymentMethodGuessingController == null) {
            return;
        }
        for (PaymentMethod paymentMethod : mPaymentMethodGuessingController.getGuessedPaymentMethods()) {
            if (paymentMethod.getPaymentTypeId().equals(paymentType.getId())) {
                setPaymentMethod(paymentMethod);
            }
        }
    }

    public String getSavedBin() {
        return mBin;
    }

    public void saveBin(String bin) {
        mBin = bin;
        mPaymentMethodGuessingController.saveBin(bin);
    }

    public void configureWithSettings() {
        if (mPaymentMethod == null) return;

        mIsSecurityCodeRequired = mPaymentMethod.isSecurityCodeRequired(mBin);
        if (!mIsSecurityCodeRequired) {
            getView().hideSecurityCodeInput();
        }
        Setting setting = PaymentMethodGuessingController.getSettingByPaymentMethodAndBin(mPaymentMethod, mBin);
        if (setting == null) {
            getView().showError(new MercadoPagoError(getResourcesProvider().getSettingNotFoundForBinErrorMessage(), false), "");
        } else {
            int cardNumberLength = getCardNumberLength();
            int spaces = FrontCardView.CARD_DEFAULT_AMOUNT_SPACES;

            if (cardNumberLength == FrontCardView.CARD_NUMBER_DINERS_LENGTH || cardNumberLength == FrontCardView.CARD_NUMBER_AMEX_LENGTH || cardNumberLength == FrontCardView.CARD_NUMBER_MAESTRO_SETTING_1_LENGTH) {
                spaces = FrontCardView.CARD_AMEX_DINERS_AMOUNT_SPACES;
            } else if (cardNumberLength == FrontCardView.CARD_NUMBER_MAESTRO_SETTING_2_LENGTH) {
                spaces = FrontCardView.CARD_NUMBER_MAESTRO_SETTING_2_AMOUNT_SPACES;
            }
            getView().setCardNumberInputMaxLength(cardNumberLength + spaces);
            SecurityCode securityCode = setting.getSecurityCode();
            if (securityCode == null) {
                mSecurityCodeLength = CARD_DEFAULT_SECURITY_CODE_LENGTH;
                mSecurityCodeLocation = CardView.CARD_SIDE_BACK;
            } else {
                mSecurityCodeLength = securityCode.getLength();
                mSecurityCodeLocation = securityCode.getCardLocation();
            }
            getView().setSecurityCodeInputMaxLength(mSecurityCodeLength);
            getView().setSecurityCodeViewLocation(mSecurityCodeLocation);
        }
    }

    public void loadIdentificationTypes() {
        if (mPaymentMethod == null) {
            return;
        }
        mIdentificationNumberRequired = getPaymentMethod().isIdentificationNumberRequired();
        if (mIdentificationNumberRequired) {
            getIdentificationTypesAsync();
        } else {
            getView().hideIdentificationInput();
        }
    }

    private void getIdentificationTypesAsync() {
        getResourcesProvider().getIdentificationTypesAsync(new OnResourcesRetrievedCallback<List<IdentificationType>>() {
            @Override
            public void onSuccess(List<IdentificationType> identificationTypes) {
                resolveIdentificationTypes(identificationTypes);
            }

            @Override
            public void onFailure(MercadoPagoError error) {
                if (isViewAttached()) {
                    getView().showError(error, ApiUtil.RequestOrigin.GET_IDENTIFICATION_TYPES);
                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            getIdentificationTypesAsync();
                        }
                    });
                }
            }
        });
    }

    private void resolveIdentificationTypes(List<IdentificationType> identificationTypes) {
        if (identificationTypes.isEmpty()) {
            getView().showError(new MercadoPagoError(getResourcesProvider().getMissingIdentificationTypesErrorMessage(), false), ApiUtil.RequestOrigin.GET_IDENTIFICATION_TYPES);
        } else {
            mIdentificationType = identificationTypes.get(0);
            getView().initializeIdentificationTypes(identificationTypes);
            mIdentificationTypes = identificationTypes;
        }
    }

    public List<BankDeal> getBankDealsList() {
        return mBankDealsList;
    }

    private void getBankDealsAsync() {
        getResourcesProvider().getBankDealsAsync(new OnResourcesRetrievedCallback<List<BankDeal>>() {
            @Override
            public void onSuccess(List<BankDeal> bankDeals) {
                resolveBankDeals(bankDeals);
            }

            @Override
            public void onFailure(MercadoPagoError error) {
                if (isViewAttached()) {
                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            getBankDealsAsync();
                        }
                    });
                }
            }
        });

    }

    private void resolveBankDeals(List<BankDeal> bankDeals) {
        if (isViewAttached()) {
            if (bankDeals == null || bankDeals.isEmpty()) {
                getView().hideBankDeals();
            } else {
                mBankDealsList = bankDeals;
                getView().showBankDeals();
            }
        }
    }

    public void enablePaymentTypeSelection(List<PaymentMethod> paymentMethodList) {
        List<PaymentType> paymentTypesList = new ArrayList<>();
        for (PaymentMethod pm : paymentMethodList) {
            PaymentType type = new PaymentType(pm.getPaymentTypeId());
            paymentTypesList.add(type);
        }
        mPaymentTypesList = paymentTypesList;
        mPaymentType = paymentTypesList.get(0);
        showPaymentTypes = true;
    }

    public void disablePaymentTypeSelection() {
        mPaymentType = null;
        showPaymentTypes = false;
        mPaymentTypesList = null;
    }

    public PaymentMethodGuessingController getGuessingController() {
        return mPaymentMethodGuessingController;
    }

    public List<PaymentMethod> getGuessedPaymentMethods() {
        if (mPaymentMethodGuessingController == null) {
            return null;
        }
        return mPaymentMethodGuessingController.getGuessedPaymentMethods();
    }

    public List<PaymentType> getPaymentTypes() {
        return mPaymentTypesList;
    }

    public void saveCardNumber(String cardNumber) {
        this.mCardNumber = cardNumber;
    }

    public void saveCardholderName(String cardholderName) {
        this.mCardholderName = cardholderName;
    }

    public void saveExpiryMonth(String expiryMonth) {
        this.mExpiryMonth = expiryMonth;
    }

    public void saveExpiryYear(String expiryYear) {
        this.mExpiryYear = expiryYear;
    }

    public void saveSecurityCode(String securityCode) {
        this.mSecurityCode = securityCode;
    }

    public void saveIdentificationNumber(String identificationNumber) {
        this.mIdentificationNumber = identificationNumber;
    }

    public void saveIdentificationType(IdentificationType identificationType) {
        this.mIdentificationType = identificationType;
        if (identificationType != null) {
            mIdentification.setType(identificationType.getId());
            getView().setIdentificationNumberRestrictions(identificationType.getType());
        }
    }

    public IdentificationType getIdentificationType() {
        return this.mIdentificationType;
    }

    public void setIdentificationNumber(String number) {
        mIdentificationNumber = number;
        mIdentification.setNumber(number);
    }

    public String getCardNumber() {
        return mCardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.mCardNumber = cardNumber;
    }

    public String getCardholderName() {
        return mCardholderName;
    }

    public void setCardholderName(String name) {
        this.mCardholderName = name;
    }

    public String getExpiryMonth() {
        return mExpiryMonth;
    }

    public String getExpiryYear() {
        return mExpiryYear;
    }

    public void setExpiryMonth(String expiryMonth) {
        this.mExpiryMonth = expiryMonth;
    }

    public void setExpiryYear(String expiryYear) {
        this.mExpiryYear = expiryYear;
    }

    public String getSecurityCode() {
        return mSecurityCode;
    }

    public String getIdentificationNumber() {
        return mIdentificationNumber;
    }

    public int getIdentificationNumberMaxLength() {
        int maxLength = CARD_DEFAULT_IDENTIFICATION_NUMBER_LENGTH;
        if (mIdentificationType != null) {
            maxLength = mIdentificationType.getMaxLength();
        }
        return maxLength;
    }

    public boolean validateCardNumber() {
        mCardToken.setCardNumber(getCardNumber());
        try {
            if (mPaymentMethod == null) {
                if (getCardNumber() == null || getCardNumber().length() < MercadoPagoUtil.BIN_LENGTH) {
                    throw new CardTokenException(CardTokenException.INVALID_CARD_NUMBER_INCOMPLETE);
                } else if (getCardNumber().length() == MercadoPagoUtil.BIN_LENGTH) {
                    throw new CardTokenException(CardTokenException.INVALID_PAYMENT_METHOD);
                } else {
                    throw new CardTokenException(CardTokenException.INVALID_PAYMENT_METHOD);
                }
            }
            mCardToken.validateCardNumber(mPaymentMethod);
            getView().clearErrorView();
            return true;
        } catch (CardTokenException e) {
            getView().setErrorView(e);
            getView().setErrorCardNumber();
            return false;
        }
    }

    public boolean validateCardName() {
        Cardholder cardHolder = new Cardholder();
        cardHolder.setName(getCardholderName());
        cardHolder.setIdentification(mIdentification);
        mCardToken.setCardholder(cardHolder);
        if (mCardToken.validateCardholderName()) {
            getView().clearErrorView();
            return true;
        } else {
            getView().setErrorView(getResourcesProvider().getInvalidEmptyNameErrorMessage());
            getView().setErrorCardholderName();
            return false;
        }
    }

    public boolean validateExpiryDate() {
        String monthString = getExpiryMonth();
        String yearString = getExpiryYear();
        Integer month = (monthString == null || monthString.isEmpty()) ? null : Integer.valueOf(monthString);
        Integer year = (yearString == null || yearString.isEmpty()) ? null : Integer.valueOf(yearString);
        mCardToken.setExpirationMonth(month);
        mCardToken.setExpirationYear(year);
        if (mCardToken.validateExpiryDate()) {
            getView().clearErrorView();
            return true;
        } else {
            getView().setErrorView(getResourcesProvider().getInvalidExpiryDateErrorMessage());
            getView().setErrorExpiryDate();
            return false;
        }
    }

    public boolean validateSecurityCode() {
        mCardToken.setSecurityCode(getSecurityCode());
        try {
            mCardToken.validateSecurityCode(mPaymentMethod);
            getView().clearErrorView();
            return true;
        } catch (CardTokenException e) {
            setCardSecurityCodeErrorView(e);
            return false;
        }
    }

    private void setCardSecurityCodeErrorView(CardTokenException exception) {
        if (!isSecurityCodeRequired()) {
            return;
        }
        getView().setErrorView(exception);
        getView().setErrorSecurityCode();
    }

    public boolean validateIdentificationNumber() {
        mIdentification.setNumber(getIdentificationNumber());
        mCardToken.getCardholder().setIdentification(mIdentification);
        boolean ans = mCardToken.validateIdentificationNumber(mIdentificationType);
        if (ans) {
            getView().clearErrorView();
            getView().clearErrorIdentificationNumber();
        } else {
            setCardIdentificationErrorView(getResourcesProvider().getInvalidIdentificationNumberErrorMessage());
        }
        return ans;
    }

    private void setCardIdentificationErrorView(String message) {
        getView().setErrorView(message);
        getView().setErrorIdentificationNumber();
    }

    public boolean checkIsEmptyOrValidCardholderName() {
        return TextUtils.isEmpty(mCardholderName) || validateCardName();
    }

    public boolean checkIsEmptyOrValidExpiryDate() {
        return TextUtils.isEmpty(mExpiryMonth) || validateExpiryDate();
    }

    public boolean checkIsEmptyOrValidSecurityCode() {
        return TextUtils.isEmpty(mSecurityCode) || validateSecurityCode();
    }

    public boolean checkIsEmptyOrValidIdentificationNumber() {
        return TextUtils.isEmpty(mIdentificationNumber) || validateIdentificationNumber();
    }

    public void recoverFromFailure() {
        if (mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }

    private Boolean showDiscount() {
        return mDiscountEnabled && mShowDiscount;
    }

    public void setShowBankDeals(Boolean showBankDeals) {
        this.mShowBankDeals = showBankDeals;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.mTransactionAmount = transactionAmount;
    }

    public boolean isDefaultSpaceErasable() {

        if (MPCardMaskUtil.isDefaultSpaceErasable(mCurrentNumberLength)) {
            mEraseSpace = true;
        }

        if (isCardLengthResolved() && mEraseSpace && (getCardNumberLength() == FrontCardView.CARD_NUMBER_MAESTRO_SETTING_1_LENGTH || getCardNumberLength() == FrontCardView.CARD_NUMBER_MAESTRO_SETTING_2_LENGTH)) {
            mEraseSpace = false;
            return true;
        }
        return false;
    }

    private boolean isMerchantServerDiscountsAvailable() {
        return !TextUtil.isEmpty(getMerchantServerDiscountUrl()) && !TextUtil.isEmpty(mMerchantGetDiscountUri);
    }

    private String getMerchantServerDiscountUrl() {
        String merchantBaseUrl;

        if (TextUtil.isEmpty(mMerchantDiscountUrl)) {
            merchantBaseUrl = this.mMerchantBaseUrl;
        } else {
            merchantBaseUrl = this.mMerchantDiscountUrl;
        }

        return merchantBaseUrl;
    }

    public void setPrivateKey(String privateKey) {
        this.mPrivateKey = privateKey;
    }

    public String getPrivateKey() {
        return mPrivateKey;
    }

    public void clearSpaceErasableSettings() {
        this.mEraseSpace = true;
    }

    public boolean isPaymentMethodResolved() {
        return mPaymentMethod != null;
    }

    public void finishCardFlow() {
        createToken();
    }

    private void createToken() {
        getResourcesProvider().createTokenAsync(mCardToken, new OnResourcesRetrievedCallback<Token>() {
            @Override
            public void onSuccess(Token token) {
                resolveTokenRequest(token);
            }

            @Override
            public void onFailure(MercadoPagoError error) {
                resolveTokenCreationError(error, ApiUtil.RequestOrigin.CREATE_TOKEN);
            }
        });
    }

    public void resolveTokenRequest(Token token) {
        mToken = token;
        getIssuers();
    }

    private void resolveTokenCreationError(MercadoPagoError error, String requestOrigin) {
        if (wrongIdentificationNumber(error)) {
            showIdentificationNumberError();
        } else {
            setFailureRecovery(new FailureRecovery() {
                @Override
                public void recover() {
                    createToken();
                }
            });
            getView().showError(error, requestOrigin);
        }
    }

    private boolean wrongIdentificationNumber(MercadoPagoError error) {
        boolean answer = false;
        if (error.isApiException()) {
            ApiException apiException = error.getApiException();
            answer = apiException.containsCause(ApiException.ErrorCodes.INVALID_CARD_HOLDER_IDENTIFICATION_NUMBER);
        }
        return answer;
    }

    private void showIdentificationNumberError() {
        getView().hideProgress();
        getView().setErrorView(getResourcesProvider().getInvalidFieldErrorMessage());
        getView().setErrorIdentificationNumber();
    }

    private void getIssuers() {
        getResourcesProvider().getIssuersAsync(mPaymentMethod.getId(), mBin, new OnResourcesRetrievedCallback<List<Issuer>>() {
            @Override
            public void onSuccess(List<Issuer> issuers) {
                resolveIssuersList(issuers);
            }

            @Override
            public void onFailure(MercadoPagoError error) {
                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        getIssuers();
                    }
                });
                getView().showError(error, ApiUtil.RequestOrigin.GET_ISSUERS);
            }
        });
    }

    private void resolveIssuersList(List<Issuer> issuers) {
        if (issuers.size() == 1) {
            mIssuer = issuers.get(0);
            getInstallments();
        } else {
            getView().finishCardFlow(mPaymentMethod, mToken, mDiscount, mDirectDiscountEnabled, issuers);
        }
    }

    private void getInstallments() {
        getResourcesProvider().getInstallmentsAsync(mBin, getTransactionAmount(), mIssuer.getId(), mPaymentMethod.getId(), new OnResourcesRetrievedCallback<List<Installment>>() {
            @Override
            public void onSuccess(List<Installment> installments) {
                resolveInstallments(installments);
            }

            @Override
            public void onFailure(MercadoPagoError error) {
                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        getInstallments();
                    }
                });
                getView().showError(error, ApiUtil.RequestOrigin.GET_INSTALLMENTS);
            }
        });
    }

    private void resolveInstallments(List<Installment> installments) {
        String errorMessage = null;
        if (installments == null || installments.size() == 0) {
            errorMessage = getResourcesProvider().getMissingInstallmentsForIssuerErrorMessage();
        } else if (installments.size() == 1) {
            resolvePayerCosts(installments.get(0).getPayerCosts());
        } else {
            errorMessage = getResourcesProvider().getMultipleInstallmentsForIssuerErrorMessage();
        }
        if (errorMessage != null && isViewAttached()) {
            getView().showError(new MercadoPagoError(errorMessage, false), ApiUtil.RequestOrigin.GET_INSTALLMENTS);
        }
    }

    private void resolvePayerCosts(List<PayerCost> payerCosts) {
        PayerCost defaultPayerCost = mPaymentPreference.getDefaultInstallments(payerCosts);
        if (defaultPayerCost != null) {
            getView().finishCardFlow(mPaymentMethod, mToken, mDiscount, mDirectDiscountEnabled, mIssuer, defaultPayerCost);
        } else if (payerCosts.isEmpty()) {
            getView().showError(new MercadoPagoError(getResourcesProvider().getMissingPayerCostsErrorMessage(), false), ApiUtil.RequestOrigin.GET_INSTALLMENTS);
        } else if (payerCosts.size() == 1) {
            getView().finishCardFlow(mPaymentMethod, mToken, mDiscount, mDirectDiscountEnabled, mIssuer, payerCosts.get(0));
        } else {
            getView().finishCardFlow(mPaymentMethod, mToken, mDiscount, mDirectDiscountEnabled, mIssuer, payerCosts);
        }
    }

    public MPTrackingContext getTrackingContext() {
        return getResourcesProvider().getTrackingContext();
    }

    public void checkFinishWithCardToken() {
        if (hasToShowPaymentTypes() && getGuessedPaymentMethods() != null) {
            getView().askForPaymentType();
        } else {
            getView().showFinishCardFlow();
        }
    }

    public boolean shouldAskPaymentType(List<PaymentMethod> paymentMethodList) {

        boolean paymentTypeUndefined = false;
        String paymentType;


        if (paymentMethodList == null || paymentMethodList.isEmpty()) {
            paymentTypeUndefined = true;
        } else {
            paymentType = paymentMethodList.get(0).getPaymentTypeId();
            for (PaymentMethod currentPaymentMethod : paymentMethodList) {
                if (!paymentType.equals(currentPaymentMethod.getPaymentTypeId())) {
                    paymentTypeUndefined = true;
                    break;
                }
            }
        }
        return paymentTypeUndefined;
    }
}
