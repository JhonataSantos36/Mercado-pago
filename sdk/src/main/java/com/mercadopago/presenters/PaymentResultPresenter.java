package com.mercadopago.presenters;

import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.model.Site;
import com.mercadopago.mvp.MvpPresenter;
import com.mercadopago.providers.PaymentResultProvider;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.PaymentResultView;

import java.math.BigDecimal;

import static com.mercadopago.util.TextUtil.isEmpty;

public class PaymentResultPresenter extends MvpPresenter<PaymentResultView, PaymentResultProvider> {
    private Boolean discountEnabled;
    private PaymentResult paymentResult;
    private Site site;
    private BigDecimal amount;

    public void initialize() {
        try {
            validateParameters();
            onValidStart();
        } catch (IllegalStateException exception) {
            onInvalidStart(exception.getMessage());
        }
    }

    private void validateParameters() {
        if (paymentResult == null) {
            throw new IllegalStateException("payment result is null");
        } else if (paymentResult.getPaymentData() == null) {
            throw new IllegalStateException("payment data is null");
        }
        if (!isStatusValid()) {
            throw new IllegalStateException("payment not does not have status");
        }
    }

    private void onValidStart() {
        if (paymentResult.getPaymentStatusDetail() != null && paymentResult.getPaymentStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_PENDING_WAITING_PAYMENT)) {
            getView().showInstructions(site, amount, paymentResult);
        } else if (paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_IN_PROCESS) ||
                paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_PENDING)) {
            getView().showPending(paymentResult);
        } else if (isCardOrAccountMoney() || isPlugin()) {
            startPaymentsOnResult();
        } else if (paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_REJECTED)) {
            getView().showRejection(paymentResult);
        }
    }

    protected void onInvalidStart(String errorDetail) {
        getView().showError(getResourcesProvider().getStandardErrorMessage(), errorDetail);
    }

    private boolean isCardOrAccountMoney() {
        return MercadoPagoUtil.isCard(paymentResult.getPaymentData().getPaymentMethod().getPaymentTypeId()) ||
                paymentResult.getPaymentData().getPaymentMethod().getPaymentTypeId().equals(PaymentTypes.ACCOUNT_MONEY);
    }

    private boolean isPlugin() {
        return PaymentTypes.PLUGIN.equalsIgnoreCase(paymentResult.getPaymentData().getPaymentMethod().getPaymentTypeId());
    }

    private void startPaymentsOnResult() {
        if (paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_APPROVED)) {
            getView().showCongrats(site, amount, paymentResult, discountEnabled);
        } else if (paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_REJECTED)) {
            if (isStatusDetailValid() && paymentResult.getPaymentStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE)) {
                getView().showCallForAuthorize(site, paymentResult);
            } else {
                getView().showRejection(paymentResult);
            }
        } else {
            getView().showError(getResourcesProvider().getStandardErrorMessage());
        }
    }


    private Boolean isStatusValid() {
        return !isEmpty(paymentResult.getPaymentStatus());
    }

    private Boolean isStatusDetailValid() {
        return !isEmpty(paymentResult.getPaymentStatusDetail());
    }

    public void setDiscountEnabled(Boolean discountEnabled) {
        this.discountEnabled = discountEnabled;
    }

    public void setPaymentResult(PaymentResult paymentResult) {
        this.paymentResult = paymentResult;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
