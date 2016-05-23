package com.mercadopago;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spanned;
import android.view.View;

import com.mercadopago.model.Payment;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.MPButton;
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
    protected MPTextView mExitCongrat;

    protected MPTextView mPaymentMethodAuthorizeDescription;
    protected MPTextView mPaymentAmountDescription;
    protected MPTextView mMercadoPagoDescription;
    protected MPTextView mAuthorizedPaymentMethod;
    protected MPTextView mSelectOtherPaymentMethod;
    protected MPTextView mExitCallForAuthorize;

    protected MPTextView mRejectionTitle;
    protected MPTextView mRejectionSubtitle;
    protected MPTextView mExitRejection;
    protected MPButton mSelectOtherPaymentMethodByRejection;

    protected MPTextView mPendingSubtitle;
    protected MPTextView mExitPending;

    // Activity parameters
    protected Payment mPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityParameters();

        if (mPayment != null && mPayment.getStatus() != null && !isEmpty(mPayment.getStatus())){
            if (mPayment.getStatus().equals(mPayment.STATUS_APPROVED)){
                showCongrats();
            }
            else if (mPayment.getStatus().equals(mPayment.STATUS_IN_PROCESS)){
                showPending();
            }
            else if (mPayment.getStatus().equals(mPayment.STATUS_REJECTED)){
                if(isStatusDetailValid()) {
                    if (mPayment.getStatusDetail().equals(mPayment.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE)) {
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
        setContentView(R.layout.activity_pending);
        initializePendingControls();
        fillPendingData();
    }

    private void showRejection() {
        setContentView(R.layout.activity_rejection);
        initializeRejectionControls();
        fillRejectionData();
    }

    private void showCallForAuthorize() {
        setContentView(R.layout.activity_call_for_authorize);
        initializeCallForAuthControls();
        fillCallForAuthData();
    }

    private void showCongrats() {
        setContentView(R.layout.activity_congrats);
        initializeCongratsControls();
        fillCongratsData();
    }

    private void initializePendingControls(){
        mPendingSubtitle = (MPTextView) findViewById(R.id.pendingSubtitle);
        mExitPending = (MPTextView) findViewById(R.id.exitPending);
        mExitPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    private void initializeRejectionControls() {
        mRejectionTitle = (MPTextView) findViewById(R.id.rejectionTitle);
        mRejectionSubtitle = (MPTextView) findViewById(R.id.rejectionSubtitle);
        mSelectOtherPaymentMethodByRejection = (MPButton) findViewById(R.id.selectOtherPaymentMethodByRejection);
        mSelectOtherPaymentMethodByRejection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("selectOther", true);
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });
        mExitRejection = (MPTextView) findViewById(R.id.exitRejection);
        mExitRejection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

    }

    private void initializeCallForAuthControls() {
        mPaymentMethodAuthorizeDescription = (MPTextView) findViewById(R.id.callForAuthorizeTitleFirstRow);
        mPaymentAmountDescription = (MPTextView) findViewById(R.id.callForAuthorizeTitleSecondRow);
        mMercadoPagoDescription = (MPTextView) findViewById(R.id.callForAuthorizeTitleThirdRow);
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
        mExitCallForAuthorize = (MPTextView) findViewById(R.id.exitCallForAuthorize);
        mExitCallForAuthorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                finish();
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
        mExitCongrat = (MPTextView) findViewById(R.id.exitCongrat);
        mExitCongrat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    private void fillPendingData() {
        if (isStatusDetailValid()) {
            if (mPayment.getStatusDetail().equals(mPayment.STATUS_DETAIL_PENDING_CONTINGENCY)) {
                mPendingSubtitle.setText(getString(R.string.mpsdk_subtitle_pending_contingency));
            }
            if (mPayment.getStatusDetail().equals(mPayment.STATUS_DETAIL_PENDING_REVIEW_MANUAL)) {
                //TODO ver que subtitulo va
                mPendingSubtitle.setText(getString(R.string.mpsdk_subtitle_pending_contingency));
            }
        } else {
            mPendingSubtitle.setVisibility(View.GONE);
        }
    }

    private void fillRejectionData() {
        if (mPayment != null && isPaymentMethodNameValid()){
            if (mPayment.getStatusDetail().equals(mPayment.STATUS_DETAIL_CC_REJECTED_OTHER_REASON)) {
                String titleMessage = mPayment.getCard().getPaymentMethod().getName() + " " + getString(R.string.mpsdk_title_other_reason_rejection);
                mRejectionTitle.setText(titleMessage);
                mRejectionSubtitle.setText(getString(R.string.mpsdk_text_select_other_rejection));
            }
            else if (mPayment.getStatusDetail().equals(mPayment.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_OTHER)){
                mRejectionTitle.setText(getString(R.string.mpsdk_title_bad_filled_other_rejection));
                String subtitleMessage = getString(R.string.mpsdk_text_some_number) + " " + mPayment.getCard().getPaymentMethod().getName() + " " + getString(R.string.mpsdk_text_is_incorrect);
                mRejectionSubtitle.setText(subtitleMessage);
            }
            else if (mPayment.getStatusDetail().equals(mPayment.CC_REJECTED_BAD_FILLED_CARD_NUMBER)){
                mRejectionTitle.setText(getString(R.string.mpsdk_title_bad_filled_other_rejection));
                String subtitleMessage = getString(R.string.mpsdk_text_some_number) + " " + mPayment.getCard().getPaymentMethod().getName() + " " + getString(R.string.mpsdk_text_is_incorrect);
                mRejectionSubtitle.setText(subtitleMessage);
            }
            else if (mPayment.getStatusDetail().equals(mPayment.CC_REJECTED_BAD_FILLED_SECURITY_CODE)){
                mRejectionTitle.setText(getString(R.string.mpsdk_title_bad_filled_other_rejection));
                mRejectionSubtitle.setText(getString(R.string.mpsdk_title_bad_filled_security_code_rejection));
            }
            else if (mPayment.getStatusDetail().equals(mPayment.CC_REJECTED_BAD_FILLED_DATE)){
                mRejectionTitle.setText(getString(R.string.mpsdk_title_bad_filled_other_rejection));
                mRejectionSubtitle.setText(getString(R.string.mpsdk_title_bad_filled_date_rejection));
            }
            else if (mPayment.getStatusDetail().equals(mPayment.REJECTED_HIGH_RISK)){
                mRejectionTitle.setText(getString(R.string.mpsdk_title_rejection_high_risk));
                mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_high_risk));
            }
            else if (mPayment.getStatusDetail().equals(mPayment.CC_REJECTED_INSUFFICIENT_AMOUNT)){
                String titleMessage = getString(R.string.mpsdk_text_you) + " " + mPayment.getCard().getPaymentMethod().getName() + " " + getString(R.string.mpsdk_text_insufficient_amount);
                mRejectionTitle.setText(titleMessage);
                mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_insufficient_amount));
            }
            else if (mPayment.getStatusDetail().equals(mPayment.CC_REJECTED_MAX_ATTEMPTS)){
                mRejectionTitle.setText(getString(R.string.mpsdk_title_rejection_max_attempts));
                mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_max_attempts));
            }
            else if (mPayment.getStatusDetail().equals(mPayment.CC_REJECTED_DUPLICATED_PAYMENT)){
                String titleMessage = mPayment.getCard().getPaymentMethod().getName() + " " + getString(R.string.mpsdk_title_other_reason_rejection);
                mRejectionTitle.setText(titleMessage);
                mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_duplicated_payment));
            }
            else if (mPayment.getStatusDetail().equals(mPayment.CC_REJECTED_CARD_DISABLED)){
                String titleMessage = getString(R.string.mpsdk_text_call_to) + " " + mPayment.getCard().getPaymentMethod().getName() + " " + getString(R.string.mpsdk_text_active_card);
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

    private void fillCallForAuthData(){
            setDescription();
            setAuthorized();
    }

    private void setAuthorized(){
        if (isPaymentMethodNameValid()){
            String message = getString(R.string.mpsdk_text_authorized_call_for_authorize) + " " + mPayment.getCard().getPaymentMethod().getName() + " " + getString(R.string.mpsdk_text_and_he_authorized);
            mAuthorizedPaymentMethod.setText(message);
        }
        else{
            mAuthorizedPaymentMethod.setVisibility(View.GONE);
        }
    }

    private void setDescription() {
        if (isPaymentMethodNameValid() && isCurrencyIdValid() && isTotalPaidAmountValid()){
            String paymentMethodAuthorizeDescription = getString(R.string.mpsdk_title_activity_call_for_authorize) + " " + mPayment.getCard().getPaymentMethod().getName() + " " + getString(R.string.mpsdk_text_the);
            mPaymentMethodAuthorizeDescription.setText(paymentMethodAuthorizeDescription);
            mPaymentAmountDescription.setText(getTotalAmountText());
        }
        else {
            mPaymentMethodAuthorizeDescription.setText(getString(R.string.mpsdk_error_title_activity_call_for_authorize));
            mPaymentAmountDescription.setVisibility(View.GONE);
            mMercadoPagoDescription.setVisibility(View.GONE);
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
        }
    }

    private void setInterestAmountDescription() {
        if(isTotalPaidAmountValid()){
            if (hasInterests()){
                String message = "(" + (mPayment.getTransactionDetails().getTotalPaidAmount()).toString() + ")";
                mInterestAmountDescription.setText(message);
            }
            else{
                mInterestAmountDescription.setText(getString(R.string.mpsdk_text_without_interest));
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
        if (isLastFourDigitsValid() && isPaymentMethodIdValid()){
            String message = getString(R.string.mpsdk_last_digits_label) + " " + mPayment.getCard().getLastFourDigits();
            mLastFourDigitsDescription.setText(message);
            mLastFourDigitsDescription.setCompoundDrawablesWithIntrinsicBounds(MercadoPagoUtil.getPaymentMethodIcon(this, mPayment.getCard().getPaymentMethod().getId()), 0, 0, 0);
        }
        else{
            mLastFourDigitsDescription.setVisibility(View.GONE);
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

    private Boolean isStatusDetailValid(){
        return mPayment.getStatusDetail() != null && !isEmpty(mPayment.getStatusDetail());
    }

    private Boolean isCurrencyIdValid(){
        return mPayment.getCurrencyId() != null && !isEmpty(mPayment.getCurrencyId());
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
        return mPayment.getPayer() != null && mPayment.getPayer().getEmail() != null &&
                !isEmpty(mPayment.getPayer().getEmail());
    }

    private Boolean isPaymentMethodNameValid(){
        return mPayment.getCard() != null && mPayment.getCard().getPaymentMethod() != null &&
                mPayment.getCard().getPaymentMethod().getName() != null && !isEmpty(mPayment.getCard().getPaymentMethod().getName());
    }

    private Boolean isPaymentMethodIdValid(){
        return mPayment.getCard() != null && mPayment.getCard().getPaymentMethod() != null &&
                mPayment.getCard().getPaymentMethod().getId() != null && !isEmpty(mPayment.getCard().getPaymentMethod().getId());
    }

    private Boolean isLastFourDigitsValid(){
        return mPayment.getCard() != null && mPayment.getCard().getLastFourDigits() != null &&
                !isEmpty(mPayment.getCard().getLastFourDigits());
    }

    private void getActivityParameters(){
        mPayment = (Payment) getIntent().getExtras().getSerializable("payment");
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

    private Spanned getTotalAmountText() {
        StringBuffer sb = new StringBuffer();
        sb.append(getString(R.string.mpsdk_installments_of));
        sb.append(" ");
        sb.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getTotalPaidAmount(), mPayment.getCurrencyId()));
        sb.append(" ");
        sb.append(getString(R.string.mpsdk_text_a));
        return CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getTotalPaidAmount(),
                mPayment.getCurrencyId(), sb.toString(), true, true);
    }
}
