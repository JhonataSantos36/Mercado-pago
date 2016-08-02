package com.mercadopago;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Instruction;
import com.mercadopago.model.InstructionActionInfo;
import com.mercadopago.model.InstructionReference;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.util.ScaleUtil;
import com.mercadopago.views.MPButton;
import com.mercadopago.views.MPTextView;

import java.util.List;

public class InstructionsActivity extends MercadoPagoActivity {

    //Values
    protected MercadoPago mMercadoPago;
    protected Boolean mBackPressedOnce;

    //Controls
    protected LinearLayout mReferencesLayout;
    protected MPTextView mTitle;
    protected MPTextView mPrimaryInfo;
    protected MPTextView mSecondaryInfo;
    protected MPTextView mTertiaryInfo;
    protected MPTextView mAccreditationMessage;
    protected MPButton mActionButton;
    protected MPTextView mExitTextView;

    //Params
    protected Payment mPayment;
    protected PaymentMethod mPaymentMethod;
    protected String mMerchantPublicKey;

    @Override
    protected void getActivityParameters() {
        mMerchantPublicKey = getIntent().getStringExtra("merchantPublicKey");
        mPayment = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("payment"), Payment.class);
        mPaymentMethod = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);
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
        if(MercadoPagoUtil.isCard(mPaymentMethod.getPaymentTypeId())) {
            throw new IllegalStateException("payment method cannot be card");
        }
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.mpsdk_activity_instructions);
    }

    @Override
    protected void initializeControls() {
        mReferencesLayout = (LinearLayout) findViewById(R.id.mpsdkReferencesLayout);
        mTitle = (MPTextView) findViewById(R.id.mpsdkTitle);
        mPrimaryInfo = (MPTextView) findViewById(R.id.mpsdkPrimaryInfo);
        mSecondaryInfo = (MPTextView) findViewById(R.id.mpsdkSecondaryInfo);
        mTertiaryInfo = (MPTextView) findViewById(R.id.mpsdkTertiaryInfo);
        mAccreditationMessage = (MPTextView) findViewById(R.id.mpsdkAccreditationMessage);
        mActionButton = (MPButton) findViewById(R.id.mpsdkActionButton);
        mExitTextView = (MPTextView) findViewById(R.id.mpsdkExitInstructions);
        mExitTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                animateOut();
            }
        });
    }

    @Override
    protected void onInvalidStart(String message) {
        ErrorUtil.startErrorActivity(this, message, false);
    }

    @Override
    protected void onValidStart() {
        mBackPressedOnce = false;
        mMercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(mMerchantPublicKey)
                .build();
        getInstructionsAsync();
    }

    protected void getInstructionsAsync() {

        LayoutUtil.showProgressLayout(this);
        mMercadoPago.getInstructions(mPayment.getId(), mPaymentMethod.getPaymentTypeId(), new Callback<Instruction>() {
            @Override
            public void success(Instruction instruction) {
                showInstructions(instruction);
                LayoutUtil.showRegularLayout(getActivity());
            }

            @Override
            public void failure(ApiException apiException) {
                if (isActivityActive()) {
                    ApiUtil.showApiExceptionError(getActivity(), apiException);
                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            getInstructionsAsync();
                        }
                    });
                }
            }
        });
    }

    protected void showInstructions(Instruction instruction) {
        MPTracker.getInstance().trackScreen( "INSTRUCTIONS", 2, mMerchantPublicKey, BuildConfig.VERSION_NAME, this);

        setTitle(instruction.getTitle());
        setInformationMessages(instruction);
        setReferencesInformation(instruction);
        mAccreditationMessage.setText(instruction.getAcreditationMessage());
        setActions(instruction);
    }

    protected void setTitle(String title) {
        Spanned formattedTitle = CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionAmount(), mPayment.getCurrencyId(), title, true, true);
        mTitle.setText(formattedTitle);
    }

    protected void setActions(Instruction instruction) {
        if(instruction.getActions() != null && !instruction.getActions().isEmpty()) {
            final InstructionActionInfo actionInfo = instruction.getActions().get(0);
            if(actionInfo.getUrl() != null && !actionInfo.getUrl().isEmpty()) {
                mActionButton.setVisibility(View.VISIBLE);
                mActionButton.setText(actionInfo.getLabel());
                mActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(actionInfo.getUrl()));
                        startActivity(browserIntent);
                    }
                });
            }
        }
    }

    protected void setReferencesInformation(Instruction instruction) {
        LinearLayout.LayoutParams marginParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int marginTop = ScaleUtil.getPxFromDp(3, this);
        int marginBottom = ScaleUtil.getPxFromDp(15, this);
        for(InstructionReference reference : instruction.getReferences()) {
            MPTextView currentTitleTextView = new MPTextView(this);
            MPTextView currentValueTextView = new MPTextView(this);

            if(reference.hasValue()) {

                if (reference.hasLabel()) {
                    currentTitleTextView.setText(reference.getLabel().toUpperCase());
                    currentTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.mpsdk_smaller_text));
                    mReferencesLayout.addView(currentTitleTextView);
                }

                String formattedReference = reference.getFormattedReference();
                int referenceSize = getTextSizeForReference();

                currentValueTextView.setText(formattedReference);
                currentValueTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, referenceSize);

                marginParams.setMargins(0, marginTop, 0, marginBottom);
                currentValueTextView.setLayoutParams(marginParams);
                mReferencesLayout.addView(currentValueTextView);
            }
        }
    }

    private int getTextSizeForReference() {
        return getResources().getDimensionPixelSize(R.dimen.mpsdk_large_text);
    }

    private void setInformationMessages(Instruction instruction) {
        if(instruction.getInfo() != null && !instruction.getInfo().isEmpty()) {
            mPrimaryInfo.setText(Html.fromHtml(getInfoHtmlText(instruction.getInfo())));
        }
        else {
            mPrimaryInfo.setVisibility(View.GONE);
        }
        if(instruction.getSecondaryInfo() != null && !instruction.getSecondaryInfo().isEmpty()) {
            mSecondaryInfo.setText(Html.fromHtml(getInfoHtmlText(instruction.getSecondaryInfo())));
        }
        else {
            mSecondaryInfo.setVisibility(View.GONE);
        }
        if(instruction.getTertiaryInfo() != null && !instruction.getTertiaryInfo().isEmpty()) {
            mTertiaryInfo.setText(Html.fromHtml(getInfoHtmlText(instruction.getTertiaryInfo())));
        }
        else {
            mTertiaryInfo.setVisibility(View.GONE);
        }
    }

    protected String getInfoHtmlText(List<String> info) {
        StringBuilder stringBuilder = new StringBuilder();
        for(String line : info) {
            stringBuilder.append(line);
            if(!line.equals(info.get(info.size() - 1))) {
                stringBuilder.append("<br/>");
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                recoverFromFailure();
            }
            else {
                setResult(RESULT_CANCELED, data);
                finish();
            }
        }
    }

    private void animateOut() {
        overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
    }

    @Override
    public void onBackPressed() {
        MPTracker.getInstance().trackEvent("INSTRUCTION", "BACK_PRESSED", 2, mMerchantPublicKey, BuildConfig.VERSION_NAME, this);

        if(mBackPressedOnce) {
            super.onBackPressed();
        }
        else {
            Snackbar.make(mTertiaryInfo, getString(R.string.mpsdk_press_again_to_leave), Snackbar.LENGTH_LONG).show();
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
