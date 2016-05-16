package com.mercadopago;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spanned;
import android.view.View;

import com.mercadopago.model.Payment;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.MPTextView;

import java.math.BigDecimal;

import static android.text.TextUtils.isEmpty;

public class CongratsActivity extends AppCompatActivity {

    // Controls
    protected MPTextView mPayerEmailDescription;
    protected MPTextView mLastFourDigitsTextView;
    protected MPTextView mInstallmentsDescription;
    protected MPTextView mTotalAmountDescription;
    protected MPTextView mPaymentIdDescription;
    protected MPTextView mCongratulationSubtitle;
    protected MPTextView mExitOfCongrat;

    protected MPTextView mAuthorizeDescription;
    protected MPTextView mPaymentAmountDescription;
    protected MPTextView mAuthorized;
    protected MPTextView mSelectPaymentMethod;
    protected MPTextView mExitOfCallForAuthorize;

    protected MPTextView mRejectionTitle;
    protected MPTextView mRejectionSubtitle;

    protected MPTextView mPendingSubtitle;
    protected MPTextView mExitOfPending;

    // Activity parameters
    protected Payment mPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityParameters();

        if (mPayment.getStatus() != null && !isEmpty(mPayment.getStatus())){
            if (mPayment.getStatus().equals("approved")){
                showCongrats();
            }
            if (mPayment.getStatus().equals("in_process")){
                showPending();
            }
            else if (mPayment.getStatus().equals("rejected")){
                if(mPayment.getStatusDetail() != null && !isEmpty(mPayment.getStatusDetail())) {
                    if (mPayment.getStatusDetail().equals("cc_rejected_call_for_authorize")) {
                        showCallForAuthorize();
                    }
                    else {
                        showRejection();
                    }
                }
                else {
                    cancelAndFinishActivity();
                }
            }
        }
        else {
            cancelAndFinishActivity();
        }
    }

    private void showPending(){
        setContentView(R.layout.activity_pending);
        initializePendingControls();
        fillPendingData();
    }

    private void showRejection() {
        setContentView(R.layout.activity_rejection);
        //initializeRejectionControls();
        //fillRejectedData();
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
        mExitOfPending = (MPTextView) findViewById(R.id.exitOfPending);
        mExitOfPending.setOnClickListener(new View.OnClickListener() {
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
    }

    private void initializeCallForAuthControls() {
        mAuthorizeDescription = (MPTextView) findViewById(R.id.authorizeDescription);
        mPaymentAmountDescription = (MPTextView) findViewById(R.id.paymentAmountDescription);
        mAuthorized = (MPTextView) findViewById(R.id.authorized);
        mSelectPaymentMethod = (MPTextView) findViewById(R.id.selectPaymentMethod);
        mExitOfCallForAuthorize = (MPTextView) findViewById(R.id.exitOfCallForAuthorize);
    }

    private void initializeCongratsControls() {
        mPayerEmailDescription = (MPTextView) findViewById(R.id.payerEmailDescription);
        mLastFourDigitsTextView = (MPTextView) findViewById(R.id.lastFourDigitsDescription);
        mInstallmentsDescription = (MPTextView) findViewById(R.id.installmentsDescription);
        mTotalAmountDescription = (MPTextView) findViewById(R.id.totalAmountDescription);
        mPaymentIdDescription = (MPTextView) findViewById(R.id.paymentIdDescription);
        mCongratulationSubtitle = (MPTextView) findViewById(R.id.congratulationsSubtitle);
        mExitOfCongrat = (MPTextView) findViewById(R.id.exitOfCongrat);
        mExitOfCongrat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    private void fillPendingData(){
        if (mPayment.getStatusDetail() != null && !isEmpty(mPayment.getStatusDetail())){
            if(mPayment.getStatusDetail().equals("pending_contingency")){
                mPendingSubtitle.setText(getString(R.string.mpsdk_subtitle_pending_contingency));
            }
            if(mPayment.getStatusDetail().equals("pending_review_manual")){
                //TODO ver que subtitulos van
                mPendingSubtitle.setText("Que ponemos acá?");
            }
        }
        else {
            mPendingSubtitle.setVisibility(View.GONE);
        }
    }

    private void fillRejectedData(){
        if (mPayment.getCard() != null && !isEmpty(mPayment.getCard().getPaymentMethod().getId()) && mPayment.getStatusDetail() != null && !isEmpty(mPayment.getStatusDetail())){
            //TODO agregar validaciones
            if (mPayment.getStatusDetail().equals("cc_rejected_other_reason")) {
                String titleMessage = mPayment.getCard().getPaymentMethod().getName() + " " + getString(R.string.mpsdk_title_other_reason_rejection);
                mRejectionTitle.setText(titleMessage);
                mRejectionSubtitle.setText(getString(R.string.mpsdk_text_select_other_rejection));
            }
            if (mPayment.getStatusDetail().equals("cc_rejected_bad_filled_other")){
                mRejectionTitle.setText(getString(R.string.mpsdk_title_bad_filled_other_rejection));
                String subtitleMessage = "Algún dato de tu " + mPayment.getCard().getPaymentMethod().getId() + " es incorrecto.";
                mRejectionSubtitle.setText(subtitleMessage);
            }
            if (mPayment.getStatusDetail().equals("cc_rejected_bad_filled_card_number")){
                mRejectionTitle.setText(getString(R.string.mpsdk_title_bad_filled_other_rejection));
                String subtitleMessage = "Algún número de tu " + mPayment.getCard().getPaymentMethod().getId() + " es incorrecto.";
                mRejectionSubtitle.setText(subtitleMessage);
            }
            if (mPayment.getStatusDetail().equals("cc_rejected_bad_filled_security_code")){
                mRejectionTitle.setText(getString(R.string.mpsdk_title_bad_filled_other_rejection));
                mRejectionSubtitle.setText(getString(R.string.mpsdk_title_bad_filled_security_code_rejection));
            }
            if (mPayment.getStatusDetail().equals("cc_rejected_bad_filled_date")){
                mRejectionTitle.setText(getString(R.string.mpsdk_title_bad_filled_other_rejection));
                mRejectionSubtitle.setText(getString(R.string.mpsdk_title_bad_filled_date_rejection));
            }
            if (mPayment.getStatusDetail().equals("rejected_high_risk")){
                mRejectionTitle.setText(getString(R.string.mpsdk_title_rejection_high_risk));
                mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_high_risk));
            }
            if (mPayment.getStatusDetail().equals("cc_rejected_insufficient_amount")){
                String titleMenssage = "Tu " + mPayment.getCard().getPaymentMethod().getName() + " no tiene fondos suficientes";
                mRejectionTitle.setText(titleMenssage);
                mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_insufficient_amount));
            }
            if (mPayment.getStatusDetail().equals("cc_rejected_max_attempts")){
                mRejectionTitle.setText(getString(R.string.mpsdk_title_rejection_max_attempts));
                mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_max_attempts));
            }
            if (mPayment.getStatusDetail().equals("cc_rejected_duplicated_payment")){
                String titleMessage = mPayment.getCard().getPaymentMethod().getName() + " no procesó el pago";
                mRejectionTitle.setText(titleMessage);
                mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_duplicated_payment));
            }
            if (mPayment.getStatusDetail().equals("cc_rejected_card_disabled")){
                String titleMessage = "Llama a " + mPayment.getCard().getPaymentMethod().getName() + " para que active tu tarjeta";
                mRejectionTitle.setText(titleMessage);
                mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_card_disabled));
            }
            else{
                cancelAndFinishActivity();
            }
        }
        else{
            mRejectionTitle.setVisibility(View.GONE);
            mRejectionSubtitle.setVisibility(View.GONE);
        }

    }

    private void fillCallForAuthData(){
        setDescription();
        setAuthorized();
    }

    private void setAuthorized(){
        if (mPayment.getCard() != null && !isEmpty(mPayment.getCard().getPaymentMethod().getId())){
            String message = getString(R.string.mpsdk_text_authorized_call_for_authorize) + " " + mPayment.getCard().getPaymentMethod().getName() + " " + "y me autorizó";
            mAuthorized.setText(message);
        }
        else{
            mAuthorized.setVisibility(View.GONE);
        }
    }

    private void setDescription() {
        //TODO revisar las validacionees
        if (mPayment.getCard() != null && !isEmpty(mPayment.getCard().getPaymentMethod().getName()) &&
                mPayment.getTransactionDetails().getTotalPaidAmount() != null && mPayment.getTransactionDetails().getTotalPaidAmount().compareTo(BigDecimal.ZERO) >= 0){
            String authorizeDescription = getString(R.string.mpsdk_title_activity_call_for_authorize) + " " + mPayment.getCard().getPaymentMethod().getName() + " el";

            mAuthorizeDescription.setText(authorizeDescription);
            mPaymentAmountDescription.setText(getTotalAmountText());
        }
        else {
            mAuthorizeDescription.setVisibility(View.GONE);
            mPaymentAmountDescription.setVisibility(View.GONE);
        }
    }

    private void fillCongratsData() {
        if (mPayment != null) {
            setPaymentEmailDescription();
            setLastFourDigitsCard();
            setInstallmentsDescription();
            setTotalAmountDescription();
            setPaymentDescription();
        }
        else {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    private void setPaymentDescription() {
        if(mPayment.getId() != null && mPayment.getId() >= 0){
            String message = getString(R.string.mpsdk_payment_id_description) + mPayment.getId();
            mPaymentIdDescription.setText(message);
        }
        else{
            mPaymentIdDescription.setVisibility(View.GONE);
        }
    }

    private void setTotalAmountDescription() {
        if(mPayment.getTransactionDetails() != null && mPayment.getTransactionDetails().getTotalPaidAmount() != null
                && (mPayment.getTransactionDetails().getTotalPaidAmount().compareTo(BigDecimal.ZERO))>=0){

            if (hasInterests()){
                String message = "(" + (mPayment.getTransactionDetails().getTotalPaidAmount()).toString() + ")";
                mTotalAmountDescription.setText(message);
            }
            else{
                mTotalAmountDescription.setText(getString(R.string.mpsdk_text_without_interest));
            }
        }
        else{
            mTotalAmountDescription.setVisibility(View.GONE);
        }
    }

    private boolean hasInterests() {
        if (mPayment.getFeeDetails() != null && mPayment.getFeeDetails().size() > 0) {
            for (int i = 0; i < mPayment.getFeeDetails().size(); i++) {
                if (mPayment.getFeeDetails().get(i).getType().equals("financing_fee")) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setInstallmentsDescription() {
        if (mPayment.getInstallments() != null && mPayment.getInstallments() >= 0
                && mPayment.getTransactionDetails().getInstallmentAmount() != null){
            mInstallmentsDescription.setText(getInstallmentsText());
        }
        else {
            mInstallmentsDescription.setVisibility(View.GONE);
        }
    }

    private void setLastFourDigitsCard() {
        if (mPayment.getCard() != null && !isEmpty(mPayment.getCard().getLastFourDigits()) && !isEmpty(mPayment.getCard().getPaymentMethod().getId())){
            String message = getString(R.string.mpsdk_last_digits_label) + " " + mPayment.getCard().getLastFourDigits();

            mLastFourDigitsTextView.setText(message);
            mLastFourDigitsTextView.setCompoundDrawablesWithIntrinsicBounds(MercadoPagoUtil.getPaymentMethodIcon(this, mPayment.getCard().getPaymentMethod().getId()), 0, 0, 0);
        }
        else{
            mLastFourDigitsTextView.setVisibility(View.GONE);
        }
    }

    private void setPaymentEmailDescription() {
        if(mPayment.getPayer() != null && !isEmpty(mPayment.getPayer().getEmail())) {
            mPayerEmailDescription.setText(mPayment.getPayer().getEmail());
        } else {
            mCongratulationSubtitle.setVisibility(View.GONE);
            mPayerEmailDescription.setVisibility(View.GONE);
        }
    }

    private void getActivityParameters(){
        mPayment = (Payment) getIntent().getExtras().getSerializable("payment");
    }

    private void cancelAndFinishActivity(){
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    private Spanned getInstallmentsText() {
        StringBuffer sb = new StringBuffer();
        sb.append(mPayment.getInstallments());
        sb.append(" ");
        sb.append("de");
        sb.append(" ");
        sb.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getInstallmentAmount(), mPayment.getCurrencyId()));
        return CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getInstallmentAmount(),
                mPayment.getCurrencyId(), sb.toString(), true, true);
    }

    private Spanned getTotalAmountText() {
        StringBuffer sb = new StringBuffer();
        sb.append("pago");
        sb.append(" ");
        sb.append("de");
        sb.append(" ");
        sb.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getTotalPaidAmount(), mPayment.getCurrencyId()));
        sb.append(" ");
        sb.append("a");
        return CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getTotalPaidAmount(),
                mPayment.getCurrencyId(), sb.toString(), true, true);
    }
}
