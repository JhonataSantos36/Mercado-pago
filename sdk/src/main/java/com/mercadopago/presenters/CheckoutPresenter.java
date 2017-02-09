package com.mercadopago.presenters;

import com.mercadopago.exceptions.CheckoutPreferenceException;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.mvp.MvpPresenter;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.providers.CheckoutProvider;
import com.mercadopago.views.CheckoutActivityView;

/**
 * Created by vaserber on 2/1/17.
 */

public class CheckoutPresenter extends MvpPresenter<CheckoutActivityView, CheckoutProvider> {

    private CheckoutPreference mCheckoutPreference;
    private String mMerchantPublicKey;


    protected Long mTransactionId;
    protected PaymentMethod mSelectedPaymentMethod;
    protected Issuer mSelectedIssuer;
    protected PayerCost mSelectedPayerCost;
    protected Token mCreatedToken;
    protected Payment mCreatedPayment;
    protected Site mSite;

    protected boolean mPaymentMethodEditionRequested;

    protected PaymentRecovery mPaymentRecovery;
    protected String mCustomerId;
    private Integer mCongratsDisplay;
    private Boolean mBinaryModeEnabled;
    private Discount mDiscount;
    private Boolean mDiscountEnabled;
    private Discount discount;

    public void start() {

        try {
            validateInputs();
            handleCheckoutPreference();
        } catch (IllegalStateException exception) {
            getView().showError(exception.getMessage());
        }
    }

    private void handleCheckoutPreference() {
        if(mCheckoutPreference.hasId()) {
            getResourcesProvider().getCheckoutPreference(mCheckoutPreference.getId(), new OnResourcesRetrievedCallback<CheckoutPreference>() {

                @Override
                public void onSuccess(CheckoutPreference checkoutPreference) {
                    startCheckout(checkoutPreference);
                }

                @Override
                public void onFailure(MercadoPagoError error) {
                    getView().showError(error);
                }
            });
        } else {
            startCheckout(mCheckoutPreference);
        }
    }

    private void startCheckout(CheckoutPreference checkoutPreference) {

        try {
            checkoutPreference.validate();
            getView().initializeCheckout(checkoutPreference);
            getView().toPaymentMethodsSelection(mDiscountEnabled, mDiscount);
        } catch (CheckoutPreferenceException e) {
            //TODO Handle
        }
    }

    private void validateInputs() throws IllegalStateException {

    }

    public void onBackFromReviewAndConfirm() {
        getView().backToPaymentMethodsSelection(mDiscountEnabled, mDiscount);
    }

    public void setCheckoutPreference(CheckoutPreference checkoutPreference) {
        this.mCheckoutPreference = checkoutPreference;
    }

    public void setCongratsDisplay(Integer congratsDisplay) {
        this.mCongratsDisplay = congratsDisplay;
    }

    public void setBinaryModeEnabled(Boolean binaryModeEnabled) {
        this.mBinaryModeEnabled = binaryModeEnabled;
    }

    public void setDiscount(Discount discount) {
        this.mDiscount = discount;
    }

    public void setDiscountEnabled(Boolean discountEnabled) {
        this.mDiscountEnabled = discountEnabled;
    }

    public Boolean areDiscountsEnabled() {
        return mDiscountEnabled;
    }

    public Discount getDiscount() {
        return discount;
    }
}
