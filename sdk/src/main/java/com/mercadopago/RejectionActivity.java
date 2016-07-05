package com.mercadopago;

import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;

import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.MPTextView;

public class RejectionActivity extends MercadoPagoActivity {

    //Controls
    protected MPTextView mRejectionTitle;
    protected MPTextView mRejectionSubtitle;
    protected FrameLayout mSelectOtherPaymentMethodByRejection;
    protected MPTextView mExit;

    //Params
    protected Payment mPayment;
    protected PaymentMethod mPaymentMethod;

    @Override
    protected void getActivityParameters() {
        super.getActivityParameters();
        mPayment = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("payment"), Payment.class);
        mPaymentMethod = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("paymentMethod"), PaymentMethod.class);
    }

    @Override
    protected void setContentView() {
        MPTracker.getInstance().trackScreen("REJECTED", "3", getMerchantPublicKey(), "MLA", "1.0", getActivity());

        setTheme(R.style.Theme_RejectionMercadoPagoTheme_NoActionbar);
        setContentView(R.layout.mpsdk_activity_rejection);
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
        if(getMerchantPublicKey() == null) {
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
    protected void initializeControls(){
        mRejectionTitle = (MPTextView) findViewById(R.id.mpsdkRejectionTitle);
        mRejectionSubtitle = (MPTextView) findViewById(R.id.mpsdkRejectionSubtitle);
        mSelectOtherPaymentMethodByRejection = (FrameLayout) findViewById(R.id.mpsdkSelectOtherPaymentMethodByRejection);

        mSelectOtherPaymentMethodByRejection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("selectOther", true);
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });
        mExit = (MPTextView) findViewById(R.id.mpsdkExitRejection);
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithOkResult();
            }
        });
    }

    private void finishWithOkResult() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void onValidStart(){
        if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_OTHER_REASON)) {
            String titleMessage = mPaymentMethod.getName() + " " + getString(R.string.mpsdk_title_other_reason_rejection);
            mRejectionTitle.setText(titleMessage);
            mRejectionSubtitle.setText(getString(R.string.mpsdk_text_select_other_rejection));
        }
        else if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_OTHER)){
            mRejectionTitle.setText(getString(R.string.mpsdk_title_bad_filled_other_rejection));
            String subtitleMessage = getString(R.string.mpsdk_text_some_number) + " " + mPaymentMethod.getName() + " " + getString(R.string.mpsdk_text_is_incorrect);
            mRejectionSubtitle.setText(subtitleMessage);
        }
        else if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_CARD_NUMBER)){
            mRejectionTitle.setText(getString(R.string.mpsdk_title_bad_filled_other_rejection));
            String subtitleMessage = getString(R.string.mpsdk_text_some_number) + " " + mPaymentMethod.getName() + " " + getString(R.string.mpsdk_text_is_incorrect);
            mRejectionSubtitle.setText(subtitleMessage);
        }
        else if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT)){
            String titleMessage = getString(R.string.mpsdk_text_you) + " " + mPaymentMethod.getName() + " " + getString(R.string.mpsdk_text_insufficient_amount);
            mRejectionTitle.setText(titleMessage);

            if (isCardPaymentTypeCreditCard()){
                mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_insufficient_amount_credit_card));
            }
            else {
                mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_insufficient_amount));
            }
        }
        else if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_DUPLICATED_PAYMENT)){
            String titleMessage = mPaymentMethod.getName() + " " + getString(R.string.mpsdk_title_other_reason_rejection);
            mRejectionTitle.setText(titleMessage);
            mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_duplicated_payment));
        }
        else if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED)){
            String titleMessage = getString(R.string.mpsdk_text_call_to) + " " + mPaymentMethod.getName() + " " + getString(R.string.mpsdk_text_active_card);
            mRejectionTitle.setText(titleMessage);
            mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_card_disabled));
        }
        else if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_SECURITY_CODE)){
            mRejectionTitle.setText(getString(R.string.mpsdk_title_bad_filled_other_rejection));
            mRejectionSubtitle.setText(getString(R.string.mpsdk_title_bad_filled_security_code_rejection));
        }
        else if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE)){
            mRejectionTitle.setText(getString(R.string.mpsdk_title_bad_filled_other_rejection));
            mRejectionSubtitle.setText(getString(R.string.mpsdk_title_bad_filled_date_rejection));
        }
        else if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_REJECTED_HIGH_RISK)){
            mRejectionTitle.setText(getString(R.string.mpsdk_title_rejection_high_risk));
            mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_high_risk));
        }
        else if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_MAX_ATTEMPTS)){
            mRejectionTitle.setText(getString(R.string.mpsdk_title_rejection_max_attempts));
            mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_max_attempts));
        }
        else {
            mRejectionTitle.setText(R.string.mpsdk_title_bad_filled_other_rejection);
            mRejectionSubtitle.setVisibility(View.GONE);
        }
    }

    private boolean isCardPaymentTypeCreditCard(){
        return MercadoPagoUtil.isCardPaymentType(mPaymentMethod.getPaymentTypeId()) && mPaymentMethod.getPaymentTypeId().equals("credit_card");
    }

    @Override
    protected void onInvalidStart(String message){
        ErrorUtil.startErrorActivity(this, message, false);
    }
}
