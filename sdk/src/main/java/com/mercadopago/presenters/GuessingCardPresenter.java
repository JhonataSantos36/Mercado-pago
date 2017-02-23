package com.mercadopago.presenters;

import android.content.Context;
import android.text.TextUtils;

import com.mercadopago.R;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.core.MercadoPagoServices;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.BankDeal;
import com.mercadopago.model.CardInformation;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Cardholder;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Identification;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.PaymentType;
import com.mercadopago.model.SecurityCode;
import com.mercadopago.model.Setting;
import com.mercadopago.model.Token;
import com.mercadopago.preferences.PaymentPreference;
import com.mercadopago.uicontrollers.card.CardView;
import com.mercadopago.uicontrollers.card.FrontCardView;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.GuessingCardActivityView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaserber on 10/13/16.
 */

public class GuessingCardPresenter {

    public static final int CARD_DEFAULT_SECURITY_CODE_LENGTH = 4;
    public static final int CARD_DEFAULT_IDENTIFICATION_NUMBER_LENGTH = 12;

    //Card controller
    protected PaymentMethodGuessingController mPaymentMethodGuessingController;
    protected List<IdentificationType> mIdentificationTypes;

    private GuessingCardActivityView mView;
    private Context mContext;
    private FailureRecovery mFailureRecovery;

    //Mercado Pago instance
    private MercadoPagoServices mMercadoPago;

    //Activity parameters
    private String mPublicKey;
    private PaymentRecovery mPaymentRecovery;
    private PaymentMethod mPaymentMethod;
    private List<PaymentMethod> mPaymentMethodList;
    private Identification mIdentification;
    private boolean mIdentificationNumberRequired;
    private PaymentPreference mPaymentPreference;

    //Card Settings
    private CardInformation mCardInfo;
    private int mSecurityCodeLength;
    private String mSecurityCodeLocation;
    private boolean mIsSecurityCodeRequired;

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
    private String mPayerEmail;
    private BigDecimal mTransactionAmount;
    private Discount mDiscount;
    private String mPrivateKey;

    public GuessingCardPresenter(Context context) {
        this.mContext = context;
    }

    public void setView(GuessingCardActivityView view) {
        this.mView = view;
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
            mView.setCardholderName(mPaymentRecovery.getToken().getCardHolder().getName());
            mView.setIdentificationNumber(mPaymentRecovery.getToken().getCardHolder().getIdentification().getNumber());
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
            mView.showSecurityCodeInput();
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
            mView.showIdentificationInput();
        }
    }

    public PaymentPreference getPaymentPreference() {
        return mPaymentPreference;
    }

    public void setPaymentPreference(PaymentPreference paymentPreference) {
        this.mPaymentPreference = paymentPreference;
    }

    public void initializeCardToken() {
        mCardToken = new CardToken("", null, null, "", "", "", "");
    }

    public void validateActivityParameters() {
        if (mPublicKey == null) {
            mView.onInvalidStart("public key not set");
        } else {
            mView.onValidStart();
            fillRecoveryFields();
        }
    }

    public String getSecurityCodeFront() {
        String securityCode = null;
        if (mSecurityCodeLocation.equals(CardView.CARD_SIDE_FRONT)) {
            securityCode = getSecurityCode();
        }
        return securityCode;
    }

    public void initializeMercadoPago() {
        if (mPublicKey == null) {
            return;
        }
        mMercadoPago = new MercadoPagoServices.Builder()
                .setContext(mContext)
                .setPublicKey(mPublicKey)
                .setPrivateKey(mPrivateKey)
                .build();
    }

    public CardInformation getCardInformation() {
        return mCardInfo;
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

    protected void startGuessingForm() {
        initializeGuessingCardNumberController();
        mView.initializeTitle();
        mView.setCardNumberListeners(mPaymentMethodGuessingController);
        mView.setCardholderNameListeners();
        mView.setExpiryDateListeners();
        mView.setSecurityCodeListeners();
        mView.setIdentificationTypeListeners();
        mView.setIdentificationNumberListeners();
        mView.setNextButtonListeners();
        mView.setBackButtonListeners();
    }

    public String getPaymentTypeId() {
        if (mPaymentMethodGuessingController == null) {
            return null;
        } else {
            return mPaymentMethodGuessingController.getPaymentTypeId();
        }
    }

    public void initialize() {
        if (showDiscount()) {
            getDirectDiscount();
        } else {
            loadPaymentMethods();
        }
    }

    private Boolean isAmountValid() {
        return mTransactionAmount != null && mTransactionAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public void initializeDiscountActivity() {
        mView.startDiscountActivity(mTransactionAmount);
    }

    public void initializeDiscountRow() {
        mView.showDiscountRow(mTransactionAmount);
    }

    private void getDirectDiscount() {
        mMercadoPago.getDirectDiscount(mTransactionAmount.toString(), mPayerEmail, new Callback<Discount>() {
            @Override
            public void success(Discount discount) {
                mDiscount = discount;
                initializeDiscountRow();
                loadPaymentMethods();
            }

            @Override
            public void failure(ApiException apiException) {
                initializeDiscountRow();
                loadPaymentMethods();
            }
        });
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

    public Boolean getDiscountEnabled() {
        return this.mDiscountEnabled;
    }

    public BigDecimal getTransactionAmount() {
        BigDecimal amount;

        if (mDiscount == null) {
            amount = mTransactionAmount;
        } else {
            amount = mDiscount.getAmountWithDiscount(mTransactionAmount);
        }

        return amount;
    }

    public void loadPaymentMethods() {
        if (mPaymentMethodList == null || mPaymentMethodList.isEmpty()) {
            getPaymentMethodsAsync();
        } else {
            mView.showInputContainer();
            startGuessingForm();
        }
    }

    public void resolveBankDeals() {
        if (mShowBankDeals) {
            getBankDealsAsync();
        } else {
            mView.hideBankDeals();
        }
    }

    protected void getPaymentMethodsAsync() {
        mMercadoPago.getPaymentMethods(new Callback<List<PaymentMethod>>() {
            @Override
            public void success(List<PaymentMethod> paymentMethods) {
                mView.showInputContainer();
                mPaymentMethodList = paymentMethods;
                startGuessingForm();
            }

            @Override
            public void failure(ApiException apiException) {
                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        getPaymentMethodsAsync();
                    }
                });
                mView.showApiExceptionError(apiException);
            }
        });
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
            mView.hideSecurityCodeInput();
        }
        Setting setting = PaymentMethodGuessingController.getSettingByPaymentMethodAndBin(mPaymentMethod, mBin);
        if (setting == null) {
            mView.startErrorView("", "Setting not found for BIN");
        } else {
            int cardNumberLength = getCardNumberLength();
            int spaces = FrontCardView.CARD_DEFAULT_AMOUNT_SPACES;
            if (cardNumberLength == FrontCardView.CARD_NUMBER_DINERS_LENGTH || cardNumberLength == FrontCardView.CARD_NUMBER_AMEX_LENGTH) {
                spaces = FrontCardView.CARD_AMEX_DINERS_AMOUNT_SPACES;
            }
            mView.setCardNumberInputMaxLength(cardNumberLength + spaces);
            SecurityCode securityCode = setting.getSecurityCode();
            if (securityCode == null) {
                mSecurityCodeLength = CARD_DEFAULT_SECURITY_CODE_LENGTH;
                mSecurityCodeLocation = CardView.CARD_SIDE_BACK;
            } else {
                mSecurityCodeLength = securityCode.getLength();
                mSecurityCodeLocation = securityCode.getCardLocation();
            }
            mView.setSecurityCodeInputMaxLength(mSecurityCodeLength);
            mView.setSecurityCodeViewLocation(mSecurityCodeLocation);
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
            mView.hideIdentificationInput();
        }
    }

    private void getIdentificationTypesAsync() {
        mMercadoPago.getIdentificationTypes(new Callback<List<IdentificationType>>() {
            @Override
            public void success(List<IdentificationType> identificationTypes) {
                if (identificationTypes.isEmpty()) {
                    mView.startErrorView(mContext.getString(R.string.mpsdk_standard_error_message),
                            "identification types call is empty at GuessingCardActivity");
                } else {
                    mIdentificationType = identificationTypes.get(0);
                    mView.initializeIdentificationTypes(identificationTypes);
                    mIdentificationTypes = identificationTypes;
                }
            }

            @Override
            public void failure(ApiException apiException) {
                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        getIdentificationTypesAsync();
                    }
                });
                mView.showApiExceptionError(apiException);
            }
        });
    }

    public List<BankDeal> getBankDealsList() {
        return mBankDealsList;
    }

    private void getBankDealsAsync() {
        mMercadoPago.getBankDeals(new Callback<List<BankDeal>>() {
            @Override
            public void success(final List<BankDeal> bankDeals) {
                if (bankDeals != null) {
                    if (bankDeals.isEmpty()) {
                        mView.hideBankDeals();
                    } else if (bankDeals.size() >= 1) {
                        mBankDealsList = bankDeals;
                        mView.showBankDeals();
                    }
                }
            }

            @Override
            public void failure(ApiException apiException) {
                //do nothing
            }
        });
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
            mView.setIdentificationNumberRestrictions(identificationType.getType());
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
                    throw new RuntimeException(mContext.getString(R.string.mpsdk_invalid_card_number_incomplete));
                } else if (getCardNumber().length() == MercadoPagoUtil.BIN_LENGTH) {
                    throw new RuntimeException(mContext.getString(R.string.mpsdk_invalid_payment_method));
                } else {
                    throw new RuntimeException(mContext.getString(R.string.mpsdk_invalid_payment_method));
                }
            }
            mCardToken.validateCardNumber(mContext, mPaymentMethod);
            mView.clearErrorView();
            return true;
        } catch (Exception e) {
            mView.setErrorView(e.getMessage());
            mView.setErrorCardNumber();
            return false;
        }
    }

    public boolean validateCardName() {
        Cardholder cardHolder = new Cardholder();
        cardHolder.setName(getCardholderName());
        cardHolder.setIdentification(mIdentification);
        mCardToken.setCardholder(cardHolder);
        if (mCardToken.validateCardholderName()) {
            mView.clearErrorView();
            return true;
        } else {
            mView.setErrorView(mContext.getString(R.string.mpsdk_invalid_empty_name));
            mView.setErrorCardholderName();
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
            mView.clearErrorView();
            return true;
        } else {
            mView.setErrorView(mContext.getString(R.string.mpsdk_invalid_expiry_date));
            mView.setErrorExpiryDate();
            return false;
        }
    }

    public boolean validateSecurityCode() {
        mCardToken.setSecurityCode(getSecurityCode());
        try {
            mCardToken.validateSecurityCode(mContext, mPaymentMethod);
            mView.clearErrorView();
            return true;
        } catch (Exception e) {
            setCardSecurityCodeErrorView(e.getMessage());
            return false;
        }
    }

    private void setCardSecurityCodeErrorView(String message) {
        if (!isSecurityCodeRequired()) {
            return;
        }
        mView.setErrorView(message);
        mView.setErrorSecurityCode();
    }

    public boolean validateIdentificationNumber() {
        mIdentification.setNumber(getIdentificationNumber());
        mCardToken.getCardholder().setIdentification(mIdentification);
        boolean ans = mCardToken.validateIdentificationNumber(mIdentificationType);
        if (ans) {
            mView.clearErrorView();
            mView.clearErrorIdentificationNumber();
        } else {
            setCardIdentificationErrorView(mContext.getString(R.string.mpsdk_invalid_identification_number));
        }
        return ans;
    }

    private void setCardIdentificationErrorView(String message) {
        mView.setErrorView(message);
        mView.setErrorIdentificationNumber();
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

    public Boolean showDiscount() {
        return mDiscountEnabled && mDiscount == null && isAmountValid();
    }

    public void setShowBankDeals(Boolean showBankDeals) {
        this.mShowBankDeals = showBankDeals;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.mTransactionAmount = transactionAmount;
    }

    public void setPrivateKey(String privateKey) {
        this.mPrivateKey = privateKey;
    }

    public String getPrivateKey() {
        return mPrivateKey;
    }
}
