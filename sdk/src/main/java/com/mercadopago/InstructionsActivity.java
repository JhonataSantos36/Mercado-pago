package com.mercadopago;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.constants.PaymentMethods;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MercadoPagoContext;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Instruction;
import com.mercadopago.model.InstructionActionInfo;
import com.mercadopago.model.InstructionReference;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.util.ScaleUtil;

import java.util.ArrayList;
import java.util.List;

public class InstructionsActivity extends MercadoPagoActivity {

    //Const
    private static final String INSTRUCTIONS_NOT_FOUND_FOR_TYPE = "instruction not found for type";

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
    protected MPTextView mActionButton;
    protected MPTextView mExitTextView;
    protected MPTextView mReferencePrimaryInfo;
    protected MPTextView mPrimaryInfoInstructions;
    protected View mPrimaryInfoSeparator;

    //Params
    protected Payment mPayment;
    protected String mPaymentTypeId;
    protected String mMerchantPublicKey;

    @Override
    protected void getActivityParameters() {
        mMerchantPublicKey = MercadoPagoContext.getInstance().getPublicKey();
        mPaymentTypeId = getIntent().getStringExtra("paymentTypeId");
        mPayment = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("payment"), Payment.class);
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
        if (mMerchantPublicKey == null) {
            throw new IllegalStateException("merchant public key not set");
        }
        if (mPayment == null) {
            throw new IllegalStateException("payment not set");
        }
        if (mPaymentTypeId == null) {
            throw new IllegalStateException("payment type id not set");
        }
        if (MercadoPagoUtil.isCard(mPaymentTypeId)) {
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
        mActionButton = (MPTextView) findViewById(R.id.mpsdkActionButton);
        mReferencePrimaryInfo = (MPTextView) findViewById(R.id.mpsdkReferencePrimaryInfo);
        mPrimaryInfoInstructions = (MPTextView) findViewById(R.id.mpsdkPrimaryInfoInstructions);
        mPrimaryInfoSeparator = findViewById(R.id.mpsdkPrimaryInfoSeparator);
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

        showLoading();
        mMercadoPago.getPaymentResult(mPayment.getId(), mPaymentTypeId, new Callback<PaymentResult>() {
            @Override
            public void success(PaymentResult paymentResult) {
                List<Instruction> instructions
                        = paymentResult.getInstructions() == null ? new ArrayList<Instruction>() : paymentResult.getInstructions();
                if (instructions.isEmpty()) {
                    ErrorUtil.startErrorActivity(getActivity(), getActivity().getString(R.string.mpsdk_standard_error_message), INSTRUCTIONS_NOT_FOUND_FOR_TYPE + mPaymentTypeId, false);
                } else {
                    resolveInstructionsFound(instructions);
                }
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

    private void resolveInstructionsFound(List<Instruction> instructions) {
        Instruction instruction = getInstruction(instructions);
        if (instruction == null) {
            ErrorUtil.startErrorActivity(this, this.getString(R.string.mpsdk_standard_error_message), "instruction not found for type " + mPaymentTypeId, false);
        } else {
            showInstructions(instruction);
        }
        stopLoading();
    }

    private void showLoading() {
        LayoutUtil.showProgressLayout(this);
    }

    private void stopLoading() {
        LayoutUtil.showRegularLayout(this);
    }

    private Instruction getInstruction(List<Instruction> instructions) {
        Instruction instruction;
        if (instructions.size() == 1) {
            instruction = instructions.get(0);
        } else {
            instruction = getInstructionForType(instructions, mPaymentTypeId);
        }
        return instruction;
    }

    private Instruction getInstructionForType(List<Instruction> instructions, String paymentTypeId) {
        Instruction instructionForType = null;
        for (Instruction instruction : instructions) {
            if (instruction.getType().equals(paymentTypeId)) {
                instructionForType = instruction;
                break;
            }
        }
        return instructionForType;
    }

    protected void showInstructions(Instruction instruction) {
        MPTracker.getInstance().trackScreen("INSTRUCTIONS", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, this);

        setTitle(instruction.getTitle());
        setReferencesInformation(instruction);
        setInformationMessages(instruction);
        mAccreditationMessage.setText(instruction.getAcreditationMessage());
        setActions(instruction);
    }

    protected void setTitle(String title) {
        Spanned formattedTitle = CurrenciesUtil.formatCurrencyInText("<br>", mPayment.getTransactionAmount(), mPayment.getCurrencyId(), title, false, true);
        mTitle.setText(formattedTitle);
    }

    protected void setActions(Instruction instruction) {
        if (instruction.getActions() != null && !instruction.getActions().isEmpty()) {
            final InstructionActionInfo actionInfo = instruction.getActions().get(0);
            if (actionInfo.getTag().equals(InstructionActionInfo.Tags.LINK)
                    && actionInfo.getUrl() != null && !actionInfo.getUrl().isEmpty()) {
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
        int marginBottom = ScaleUtil.getPxFromDp(13, this);
        for (InstructionReference reference : instruction.getReferences()) {
            MPTextView currentTitleTextView = new MPTextView(this);
            MPTextView currentValueTextView = new MPTextView(this);

            if (reference.hasValue()) {
                if (reference.hasLabel()) {
                    currentTitleTextView.setText(reference.getLabel());
                    currentTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.mpsdk_smaller_text));
                    currentTitleTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                    mReferencesLayout.addView(currentTitleTextView);
                }

                String formattedReference = reference.getFormattedReference();
                int referenceSize = getTextSizeForReference();

                currentValueTextView.setText(formattedReference);
                currentValueTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, referenceSize);

                if (reference.isNumericReference()) {
                    marginParams.setMargins(150, 0, 150, marginBottom);
                } else {
                    marginParams.setMargins(0, 0, 0, marginBottom);
                }

                currentValueTextView.setLayoutParams(marginParams);

                currentValueTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                currentValueTextView.setTextColor(Color.BLACK);
                currentValueTextView.setTypeface(currentTitleTextView.getTypeface(), Typeface.NORMAL);

                mReferencesLayout.addView(currentValueTextView);
            }
        }
    }

    private int getTextSizeForReference() {
        return getResources().getDimensionPixelSize(R.dimen.mpsdk_title_text);
    }

    private void setInformationMessages(Instruction instruction) {
        if (instruction.getInfo() != null && !instruction.getInfo().isEmpty()) {
            if (isRedLinkAtm()) {
                mPrimaryInfoSeparator.setVisibility(View.VISIBLE);
                mReferencePrimaryInfo.setVisibility(View.VISIBLE);
                mPrimaryInfoInstructions.setVisibility(View.VISIBLE);

                mPrimaryInfo.setText(instruction.getInfo().get(0));
                mReferencePrimaryInfo.setText(instruction.getInfo().get(6));
                mPrimaryInfoInstructions.setText(getTitleReferences(instruction.getInfo()));
            } else {
                mPrimaryInfo.setText(Html.fromHtml(getInfoHtmlText(instruction.getInfo())));
            }
        } else {
            mPrimaryInfo.setVisibility(View.GONE);
        }

        if (instruction.getSecondaryInfo() != null && !instruction.getSecondaryInfo().isEmpty()) {
            mSecondaryInfo.setText(Html.fromHtml(getInfoHtmlText(instruction.getSecondaryInfo())));
        } else {
            mSecondaryInfo.setVisibility(View.GONE);
        }

        if (instruction.getTertiaryInfo() != null && !instruction.getTertiaryInfo().isEmpty()) {
            mTertiaryInfo.setText(Html.fromHtml(getInfoHtmlText(instruction.getTertiaryInfo())));
        } else {
            mTertiaryInfo.setVisibility(View.GONE);
        }
    }

    protected Boolean isRedLinkAtm() {
        return mPayment.getPaymentMethodId().equals(PaymentMethods.ARGENTINA.REDLINK) && mPaymentTypeId.equals(PaymentTypes.ATM);
    }

    protected String getTitleReferences(List<String> info) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(info.get(2) + "\n");
        stringBuilder.append(info.get(3) + "\n");
        stringBuilder.append(info.get(4) + "\n");

        return stringBuilder.toString();
    }

    protected String getInfoHtmlText(List<String> info) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : info) {
            stringBuilder.append(line);
            if (!line.equals(info.get(info.size() - 1))) {
                stringBuilder.append("<br/>");
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                recoverFromFailure();
            } else {
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
        MPTracker.getInstance().trackScreen("INSTRUCTIONS", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, this);

        if (mBackPressedOnce) {
            super.onBackPressed();
        } else {
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
