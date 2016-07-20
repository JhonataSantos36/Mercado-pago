package com.mercadopago;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;

import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.MPTextView;

import java.math.BigDecimal;

import static android.text.TextUtils.isEmpty;

public class CongratsActivity extends MercadoPagoActivity {

    // Controls
    protected MPTextView mPayerEmailDescription;
    protected MPTextView mLastFourDigitsDescription;
    protected MPTextView mInstallmentsDescription;
    protected MPTextView mInterestAmountDescription;
    protected MPTextView mPaymentIdDescription;
    protected MPTextView mCongratulationSubtitle;
    protected View mPaymentIdSeparator;
    protected ImageView mPaymentMethodImage;
    protected MPTextView mExit;

    // Activity parameters
    protected Payment mPayment;
    protected PaymentMethod mPaymentMethod;
    protected String mMerchantPublicKey;

    //Local values
    private boolean mBackPressedOnce;

    @Override
    protected void getActivityParameters() {
        mMerchantPublicKey = getIntent().getStringExtra("merchantPublicKey");
        mPayment = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("payment"), Payment.class);
        mPaymentMethod = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("paymentMethod"), PaymentMethod.class);
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
        if(mMerchantPublicKey == null) {
            throw new IllegalStateException("merchant public key not set");
        }
        if(mPayment == null) {
            throw new IllegalStateException("payment not set");
        }
        if(mPaymentMethod == null) {
            throw new IllegalStateException("payment method not set");
        }
    }

    @Override
    protected void setContentView() {
        MPTracker.getInstance().trackScreen("CONGRATS", "3", mMerchantPublicKey, "MLA", "1.0", getActivity());

        setContentView(R.layout.mpsdk_activity_congrats);
    }

    @Override
    protected void initializeControls(){
        mPayerEmailDescription = (MPTextView) findViewById(R.id.mpsdkPayerEmailDescription);
        mLastFourDigitsDescription = (MPTextView) findViewById(R.id.mpsdkLastFourDigitsDescription);
        mInstallmentsDescription = (MPTextView) findViewById(R.id.mpsdkInstallmentsDescription);
        mInterestAmountDescription = (MPTextView) findViewById(R.id.mpsdkInterestAmountDescription);
        mPaymentIdDescription = (MPTextView) findViewById(R.id.mpsdkPaymentIdDescription);
        mCongratulationSubtitle = (MPTextView) findViewById(R.id.mpsdkCongratulationSubtitle);
        mPaymentIdSeparator = findViewById(R.id.mpsdkPaymentIdSeparator);
        mPaymentMethodImage = (ImageView) findViewById(R.id.mpsdkPaymentMethodImage);
        mExit = (MPTextView) findViewById(R.id.mpsdkExitCongrats);
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithOkResult();
            }
        });
    }

    @Override
    protected void onValidStart(){
        setPaymentEmailDescription();
        setLastFourDigitsCard();
        setInstallmentsDescription();
        setPaymentIdDescription();
    }

    @Override
    protected void onInvalidStart(String errorMessage) {
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), errorMessage,false);
    }

    private void setPaymentIdDescription() {
        if(isPaymentIdValid()){
            String message = getString(R.string.mpsdk_payment_id_description) + " " + mPayment.getId();
            mPaymentIdDescription.setText(message);
        }
        else{
            mPaymentIdDescription.setVisibility(View.GONE);
            mPaymentIdSeparator.setVisibility(View.GONE);
        }
    }

    private void setInterestAmountDescription() {
        if (hasInterests()) {
            StringBuilder sb = new StringBuilder();

            sb.append("( ");
            sb.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getTotalPaidAmount(), mPayment.getCurrencyId()));
            sb.append(" )");
            mInterestAmountDescription.setText(CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getTotalPaidAmount(),
                    mPayment.getCurrencyId(), sb.toString(), true, true));
        }
        else {
            mInterestAmountDescription.setText(getString(R.string.mpsdk_zero_rate));
        }
    }

    private boolean hasInterests() {
        if (mPayment.getFeeDetails() != null && mPayment.getFeeDetails().size() > 0) {
            for (int i = 0; i < mPayment.getFeeDetails().size(); i++) {
                if (mPayment.getFeeDetails().get(i).isFinancialFree()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setInstallmentsDescription() {
        if (isInstallmentQuantityValid() && isInstallmentAmountValid() && isTotalPaidAmountValid() && CurrenciesUtil.isValidCurrency(mPayment.getCurrencyId())){
            if (mPayment.getInstallments()>1){
                mInstallmentsDescription.setText(getInstallmentsText());
                setInterestAmountDescription();
            }
            else {
                //Installments quantity 0 or 1
                StringBuilder sb = new StringBuilder();
                sb.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getTotalPaidAmount(), mPayment.getCurrencyId()));

                mInstallmentsDescription.setText(CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getTotalPaidAmount(),
                        mPayment.getCurrencyId(), sb.toString(), true, true));

                mInterestAmountDescription.setVisibility(View.GONE);
            }
        }
        else {
            mInstallmentsDescription.setVisibility(View.GONE);
            mInterestAmountDescription.setVisibility(View.GONE);
        }
    }

    private void setLastFourDigitsCard() {
        if (isLastFourDigitsValid() && isPaymentMethodValid()){
            setPaymentMethodImage();
            String message = getString(R.string.mpsdk_last_digits_label) + " " + mPayment.getCard().getLastFourDigits();
            mLastFourDigitsDescription.setText(message);
        }
        else{
            mLastFourDigitsDescription.setVisibility(View.GONE);
            mPaymentMethodImage.setVisibility(View.GONE);
        }
    }

    private void setPaymentMethodImage() {
        int resourceId = MercadoPagoUtil.getPaymentMethodIcon(this, mPaymentMethod.getId());
        if(resourceId != 0) {
            mPaymentMethodImage.setImageResource(resourceId);
        } else {
            mPaymentMethodImage.setVisibility(View.GONE);
        }
    }

    private void setPaymentEmailDescription() {
        if(isPayerEmailValid()) {
            mPayerEmailDescription.setText(mPayment.getPayer().getEmail());
        } else {
            mCongratulationSubtitle.setVisibility(View.GONE);
            mPayerEmailDescription.setVisibility(View.GONE);
        }
    }

    private Boolean isPaymentIdValid(){
        return mPayment.getId() != null && mPayment.getId() >= 0;
    }

    private Boolean isTotalPaidAmountValid(){
        return mPayment.getTransactionDetails() != null && mPayment.getTransactionDetails().getTotalPaidAmount() != null
                && (mPayment.getTransactionDetails().getTotalPaidAmount().compareTo(BigDecimal.ZERO))>0;
    }

    private Boolean isInstallmentAmountValid(){
        return mPayment.getTransactionDetails() != null && mPayment.getTransactionDetails().getInstallmentAmount() != null &&
                    mPayment.getTransactionDetails().getInstallmentAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    private Boolean isInstallmentQuantityValid(){
        return mPayment.getInstallments() != null && mPayment.getInstallments() >= 0;
    }

    private Boolean isPayerEmailValid(){
        return mPayment.getPayer() != null && !isEmpty(mPayment.getPayer().getEmail());
    }

    private Boolean isPaymentMethodValid(){
        return isPaymentMethodIdValid() && !isEmpty(mPaymentMethod.getName());
    }

    private Boolean isPaymentMethodIdValid(){
        return !isEmpty(mPaymentMethod.getId()) && mPaymentMethod.getId().equals(mPayment.getPaymentMethodId());
    }

    private Boolean isLastFourDigitsValid(){
        return mPayment.getCard() != null && !isEmpty(mPayment.getCard().getLastFourDigits());
    }

    private Spanned getInstallmentsText() {
        StringBuffer sb = new StringBuffer();
        sb.append(mPayment.getInstallments());
        sb.append(" ");
        sb.append(getString(R.string.mpsdk_installments_of));
        sb.append(" ");
        sb.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getInstallmentAmount(), mPayment.getCurrencyId()));
        return CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getInstallmentAmount(),
                mPayment.getCurrencyId(), sb.toString(), true, true);
    }

    private void finishWithOkResult() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(mBackPressedOnce) {
            finishWithOkResult();
        }
        else {
            Snackbar.make(mExit, getString(R.string.mpsdk_press_again_to_leave), Snackbar.LENGTH_LONG).show();
            mBackPressedOnce = true;
            resetBackPressedOnceIn(4000);
        }
    }

    private void resetBackPressedOnceIn(final int mills) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(mills);
                    mBackPressedOnce = false;
                } catch (InterruptedException e) {
                    //Do nothing
                }
            }
        }).start();
    }
}
