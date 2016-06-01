package com.mercadopago;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spanned;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.MPTextView;

import java.math.BigDecimal;

import static android.text.TextUtils.isEmpty;

public class CongratsActivity extends AppCompatActivity {

    // Controls
    protected MPTextView mPayerEmailDescription;
    protected MPTextView mLastFourDigitsDescription;
    protected MPTextView mInstallmentsDescription;
    protected MPTextView mInterestAmountDescription;
    protected MPTextView mPaymentIdDescription;
    protected MPTextView mCongratulationSubtitle;
    protected View mPaymentIdSeparator;
    protected ImageView mPaymentMethodImage;

    protected MPTextView mCallForAuthTitle;
    protected MPTextView mAuthorizedPaymentMethod;
    protected MPTextView mSelectOtherPaymentMethod;

    protected MPTextView mRejectionTitle;
    protected MPTextView mRejectionSubtitle;
    protected FrameLayout mSelectOtherPaymentMethodByRejection;

    protected MPTextView mPendingSubtitle;

    protected MPTextView mExit;

    // Activity parameters
    protected Payment mPayment;
    protected PaymentMethod mPaymentMethod;

    //Local values
    private boolean mBackPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityParameters();
        this.mBackPressedOnce = false;

        if (mPayment != null && isStatusValid()){
            if (mPayment.getStatus().equals(Payment.StatusCodes.STATUS_APPROVED)){
                showCongrats();
            }
            else if (mPayment.getStatus().equals(Payment.StatusCodes.STATUS_IN_PROCESS)){
                showPending();
            }
            else if (mPayment.getStatus().equals(Payment.StatusCodes.STATUS_REJECTED)){
                if(isStatusDetailValid()) {
                    if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE)) {
                        showCallForAuthorize();
                    }
                    else {
                        showRejection();
                    }
                }
                else {
                    showRejection();
                }
            }
            else{
                showRejection();
            }
        }
        else {
            showRejection();
        }
    }

    private void showPending(){
        setTheme(R.style.Theme_InfoMercadoPagoTheme_NoActionbar);
        setContentView(R.layout.activity_pending);
        initializePendingControls();
        fillPendingData();
    }

    private void showRejection() {
        setTheme(R.style.Theme_RejectionMercadoPagoTheme_NoActionbar);
        setContentView(R.layout.activity_rejection);
        initializeRejectionControls();
        fillRejectionData();
    }

    private void showCallForAuthorize() {
        setTheme(R.style.Theme_CallForAuthorizeMercadoPagoTheme_NoActionbar);
        setContentView(R.layout.activity_call_for_authorize);
        initializeCallForAuthControls();
        fillCallForAuthData();
    }

    private void showCongrats() {
        setTheme(R.style.Theme_CongratsMercadoPagoTheme_NoActionbar);
        setContentView(R.layout.activity_congrats);
        initializeCongratsControls();
        fillCongratsData();
    }

    private void initializePendingControls(){
        mPendingSubtitle = (MPTextView) findViewById(R.id.pendingSubtitle);
        mExit = (MPTextView) findViewById(R.id.exitPending);
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithOkResult();
            }
        });
    }

    private void initializeRejectionControls() {
        mRejectionTitle = (MPTextView) findViewById(R.id.rejectionTitle);
        mRejectionSubtitle = (MPTextView) findViewById(R.id.rejectionSubtitle);
        mSelectOtherPaymentMethodByRejection = (FrameLayout) findViewById(R.id.selectOtherPaymentMethodByRejection);

        mSelectOtherPaymentMethodByRejection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("selectOther", true);
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });
        mExit = (MPTextView) findViewById(R.id.exitRejection);
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithOkResult();
            }
        });

    }

    private void initializeCallForAuthControls() {
        mCallForAuthTitle = (MPTextView) findViewById(R.id.callForAuthorizeTitle);
        mAuthorizedPaymentMethod = (MPTextView) findViewById(R.id.authorizedPaymentMethod);
        mAuthorizedPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("retry", true);
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });
        mSelectOtherPaymentMethod = (MPTextView) findViewById(R.id.selectOtherPaymentMethod);
        mSelectOtherPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("selectOther", true);
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });
        mExit = (MPTextView) findViewById(R.id.exitCallForAuthorize);
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithOkResult();
            }
        });
    }

    private void initializeCongratsControls() {
        mPayerEmailDescription = (MPTextView) findViewById(R.id.payerEmailDescription);
        mLastFourDigitsDescription = (MPTextView) findViewById(R.id.lastFourDigitsDescription);
        mInstallmentsDescription = (MPTextView) findViewById(R.id.installmentsDescription);
        mInterestAmountDescription = (MPTextView) findViewById(R.id.interestAmountDescription);
        mPaymentIdDescription = (MPTextView) findViewById(R.id.paymentIdDescription);
        mCongratulationSubtitle = (MPTextView) findViewById(R.id.congratulationSubtitle);
        mPaymentIdSeparator = findViewById(R.id.paymentIdSeparator);
        mPaymentMethodImage = (ImageView) findViewById(R.id.paymentMethodImage);
        mExit = (MPTextView) findViewById(R.id.exitCongrat);
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithOkResult();
            }
        });
    }

    private void fillPendingData() {
        if (isStatusDetailValid()) {
            if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_PENDING_CONTINGENCY)) {
                mPendingSubtitle.setText(getString(R.string.mpsdk_subtitle_pending_contingency));
            }
            if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_PENDING_REVIEW_MANUAL)) {
                //TODO ver que subtitulo va
                mPendingSubtitle.setText(getString(R.string.mpsdk_subtitle_pending_contingency));
            }
        } else {
            mPendingSubtitle.setVisibility(View.GONE);
        }
    }

    private void fillRejectionData() {
        if (mPayment != null && isStatusDetailValid()){

            if (isPaymentMethodNameValid() && mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_OTHER_REASON)) {
                String titleMessage = mPaymentMethod.getName() + " " + getString(R.string.mpsdk_title_other_reason_rejection);
                mRejectionTitle.setText(titleMessage);
                mRejectionSubtitle.setText(getString(R.string.mpsdk_text_select_other_rejection));
            }
            else if (isPaymentMethodNameValid() && mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_OTHER)){
                mRejectionTitle.setText(getString(R.string.mpsdk_title_bad_filled_other_rejection));
                String subtitleMessage = getString(R.string.mpsdk_text_some_number) + " " + mPaymentMethod.getName() + " " + getString(R.string.mpsdk_text_is_incorrect);
                mRejectionSubtitle.setText(subtitleMessage);
            }
            else if (isPaymentMethodNameValid() && mPayment.getStatusDetail().equals(Payment.StatusCodes.CC_REJECTED_BAD_FILLED_CARD_NUMBER)){
                mRejectionTitle.setText(getString(R.string.mpsdk_title_bad_filled_other_rejection));
                String subtitleMessage = getString(R.string.mpsdk_text_some_number) + " " + mPaymentMethod.getName() + " " + getString(R.string.mpsdk_text_is_incorrect);
                mRejectionSubtitle.setText(subtitleMessage);
            }
            else if (mPayment.getStatusDetail().equals(Payment.StatusCodes.CC_REJECTED_BAD_FILLED_SECURITY_CODE)){
                mRejectionTitle.setText(getString(R.string.mpsdk_title_bad_filled_other_rejection));
                mRejectionSubtitle.setText(getString(R.string.mpsdk_title_bad_filled_security_code_rejection));
            }
            else if (mPayment.getStatusDetail().equals(Payment.StatusCodes.CC_REJECTED_BAD_FILLED_DATE)){
                mRejectionTitle.setText(getString(R.string.mpsdk_title_bad_filled_other_rejection));
                mRejectionSubtitle.setText(getString(R.string.mpsdk_title_bad_filled_date_rejection));
            }
            else if (mPayment.getStatusDetail().equals(Payment.StatusCodes.REJECTED_HIGH_RISK)){
                mRejectionTitle.setText(getString(R.string.mpsdk_title_rejection_high_risk));
                mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_high_risk));
            }
            else if (isPaymentMethodNameValid() && mPayment.getStatusDetail().equals(Payment.StatusCodes.CC_REJECTED_INSUFFICIENT_AMOUNT)){
                String titleMessage = getString(R.string.mpsdk_text_you) + " " + mPaymentMethod.getName() + " " + getString(R.string.mpsdk_text_insufficient_amount);
                mRejectionTitle.setText(titleMessage);

                if (isCardPaymentTypeCreditCard()){
                    mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_insufficient_amount_credit_card));
                }
                else {
                    mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_insufficient_amount));
                }
            }
            else if (mPayment.getStatusDetail().equals(Payment.StatusCodes.CC_REJECTED_MAX_ATTEMPTS)){
                mRejectionTitle.setText(getString(R.string.mpsdk_title_rejection_max_attempts));
                mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_max_attempts));
            }
            else if (isPaymentMethodNameValid() && mPayment.getStatusDetail().equals(Payment.StatusCodes.CC_REJECTED_DUPLICATED_PAYMENT)){
                String titleMessage = mPaymentMethod.getName() + " " + getString(R.string.mpsdk_title_other_reason_rejection);
                mRejectionTitle.setText(titleMessage);
                mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_duplicated_payment));
            }
            else if (isPaymentMethodNameValid() && mPayment.getStatusDetail().equals(Payment.StatusCodes.CC_REJECTED_CARD_DISABLED)){
                String titleMessage = getString(R.string.mpsdk_text_call_to) + " " + mPaymentMethod.getName() + " " + getString(R.string.mpsdk_text_active_card);
                mRejectionTitle.setText(titleMessage);
                mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_card_disabled));
            }
            else{
                mRejectionTitle.setText(R.string.mpsdk_title_bad_filled_other_rejection);
            }
        }
        else{
            mRejectionTitle.setText(R.string.mpsdk_title_bad_filled_other_rejection);
            mRejectionSubtitle.setVisibility(View.GONE);
        }
    }

    private boolean isCardPaymentTypeCreditCard(){
        return MercadoPagoUtil.isCardPaymentType(mPaymentMethod.getPaymentTypeId()) && mPaymentMethod.getPaymentTypeId().equals("credit_card");
    }

    private void fillCallForAuthData(){
            setDescription();
            setAuthorized();
    }

    private void setAuthorized(){
        if (isPaymentMethodNameValid()){
            String message = getString(R.string.mpsdk_text_authorized_call_for_authorize) + " " + mPaymentMethod.getName() + " " + getString(R.string.mpsdk_text_and_he_authorized);
            mAuthorizedPaymentMethod.setText(message);
        }
        else{
            mAuthorizedPaymentMethod.setVisibility(View.GONE);
        }
    }

    private void setDescription() {
        if (isPaymentMethodNameValid() && isCurrencyIdValid() && isTotalPaidAmountValid()){
            StringBuilder sb = new StringBuilder();
            sb.append(getString(R.string.mpsdk_title_activity_call_for_authorize));
            sb.append(" " + mPaymentMethod.getName() + " ");
            sb.append(getString(R.string.mpsdk_text_the_payment) + " ");
            sb.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getTotalPaidAmount(), mPayment.getCurrencyId()));
            sb.append(" " + getString(R.string.mpsdk_text_to_mercado_pago));

            mCallForAuthTitle.setText(CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getTotalPaidAmount(),
                    mPayment.getCurrencyId(), sb.toString(), true, true));
        }
        else {
            mCallForAuthTitle.setText(getString(R.string.mpsdk_error_title_activity_call_for_authorize));
        }
    }

    private void fillCongratsData() {
        setPaymentEmailDescription();
        setLastFourDigitsCard();
        setInstallmentsDescription();
        setInterestAmountDescription();
        setPaymentIdDescription();
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
        if(isTotalPaidAmountValid()){
            if (mPayment.getInstallments()>1) {
                if (hasInterests()) {
                    StringBuilder sb = new StringBuilder();

                    sb.append(" (");
                    sb.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getTotalPaidAmount(), mPayment.getCurrencyId()));
                    sb.append(")");
                    mInterestAmountDescription.setText(CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getTotalPaidAmount(),
                            mPayment.getCurrencyId(), sb.toString(), true, true));
                }
                else {
                    mInterestAmountDescription.setText(getString(R.string.mpsdk_zero_rate));
                }
            }
            else{
                mInterestAmountDescription.setVisibility(View.GONE);
            }
        }
        else{
            mInterestAmountDescription.setVisibility(View.GONE);
            mInstallmentsDescription.setVisibility(View.GONE);
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
        if (isInstallmentQuantityValid() && isInstallmentAmountValid()){
            mInstallmentsDescription.setText(getInstallmentsText());
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

    private Boolean isStatusValid(){
        return !isEmpty(mPayment.getStatus());
    }

    private Boolean isStatusDetailValid(){
        return !isEmpty(mPayment.getStatusDetail());
    }

    private Boolean isCurrencyIdValid(){
        return !isEmpty(mPayment.getCurrencyId());
    }

    private Boolean isPaymentIdValid(){
        return mPayment.getId() != null && mPayment.getId() >= 0;
    }

    private Boolean isTotalPaidAmountValid(){
        return mPayment.getTransactionDetails() != null && mPayment.getTransactionDetails().getTotalPaidAmount() != null
                && (mPayment.getTransactionDetails().getTotalPaidAmount().compareTo(BigDecimal.ZERO))>=0;
    }

    private Boolean isInstallmentAmountValid(){
        return mPayment.getTransactionDetails() != null && mPayment.getTransactionDetails().getInstallmentAmount() != null &&
                    mPayment.getTransactionDetails().getInstallmentAmount().compareTo(BigDecimal.ZERO) >= 0;
    }

    private Boolean isInstallmentQuantityValid(){
        return mPayment.getInstallments() != null && mPayment.getInstallments() >= 0;
    }

    private Boolean isPayerEmailValid(){
        return mPayment.getPayer() != null && !isEmpty(mPayment.getPayer().getEmail());
    }

    private Boolean isPaymentMethodNameValid(){
        return isPaymentMethodValid() && !isEmpty(mPaymentMethod.getName());
    }

    private Boolean isPaymentMethodValid(){
        return mPayment.getPaymentMethodId().equals(mPaymentMethod.getId());
    }

    private Boolean isLastFourDigitsValid(){
        return mPayment.getCard() != null && !isEmpty(mPayment.getCard().getLastFourDigits());
    }

    private void getActivityParameters(){
        mPayment = (Payment) getIntent().getExtras().getSerializable("payment");
        mPaymentMethod = (PaymentMethod) getIntent().getExtras().getSerializable("paymentMethod");
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

    private void finishWithOkResult() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
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
