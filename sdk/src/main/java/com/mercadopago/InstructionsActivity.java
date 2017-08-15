package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
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
import com.mercadopago.core.MercadoPagoServices;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Instruction;
import com.mercadopago.model.InstructionActionInfo;
import com.mercadopago.model.InstructionReference;
import com.mercadopago.model.Instructions;
import com.mercadopago.model.PaymentData;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.model.Site;
import com.mercadopago.preferences.PaymentResultScreenPreference;
import com.mercadopago.providers.MPTrackingProvider;
import com.mercadopago.px_tracking.model.ScreenViewEvent;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.util.ScaleUtil;
import com.mercadopago.util.TextUtil;
import com.mercadopago.util.TrackingUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InstructionsActivity extends MercadoPagoBaseActivity {

    //Const
    private static final String INSTRUCTIONS_NOT_FOUND_FOR_TYPE = "instruction not found for type";

    protected MercadoPagoServices mMercadoPagoServices;
    protected Boolean mBackPressedOnce;
    protected Activity mActivity;

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
    protected String mMerchantPublicKey;
    protected PaymentResult mPaymentResult;
    protected Long mPaymentId;
    protected String mPaymentTypeId;
    protected String mPaymentMethodId;
    protected Site mSite;
    protected String mCurrencyId;
    protected BigDecimal mTotalAmount;
    protected PaymentResultScreenPreference mPaymentResultScreenPreference;
    private FailureRecovery failureRecovery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityParameters();
        setContentView();
        initializeControls();
        customizeScreen();
        mActivity = this;
        try {
            validateActivityParameters();
            onValidStart();
        } catch (IllegalStateException exception) {
            onInvalidStart(exception.getMessage());
        }
    }

    protected void getActivityParameters() {
        mMerchantPublicKey = getIntent().getStringExtra("merchantPublicKey");
        mPaymentResult = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("paymentResult"), PaymentResult.class);
        mPaymentResultScreenPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("paymentResultScreenPreference"), PaymentResultScreenPreference.class);
        if (getIntent().getStringExtra("amount") != null) {
            mTotalAmount = new BigDecimal(getIntent().getStringExtra("amount"));
        }
        mSite = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("site"), Site.class);
    }

    protected void validateActivityParameters() throws IllegalStateException {
        if (mMerchantPublicKey == null) {
            throw new IllegalStateException("merchant public key not set");
        }
        if (mPaymentResult == null) {
            throw new IllegalStateException("payment result not set");
        }
        if (mPaymentResult.getPaymentData() != null && mPaymentResult.getPaymentData().getPaymentMethod() != null &&
                mPaymentResult.getPaymentData().getPaymentMethod().getPaymentTypeId() != null &&
                MercadoPagoUtil.isCard(mPaymentResult.getPaymentData().getPaymentMethod().getPaymentTypeId())) {
            throw new IllegalStateException("payment method cannot be card");
        }
    }

    protected void setContentView() {
        setContentView(R.layout.mpsdk_activity_instructions);
    }

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
            }
        });
    }

    private void customizeScreen() {
        if (mPaymentResultScreenPreference != null && !TextUtil.isEmpty(mPaymentResultScreenPreference.getExitButtonTitle())) {
            mExitTextView.setText(mPaymentResultScreenPreference.getExitButtonTitle());
        }
    }

    protected void onInvalidStart(String message) {
        ErrorUtil.startErrorActivity(this, message, false, mMerchantPublicKey);
    }

    protected void onValidStart() {
        initializePaymentData();
        trackScreen();
        mBackPressedOnce = false;
        mMercadoPagoServices = new MercadoPagoServices.Builder()
                .setContext(this)
                .setPublicKey(mMerchantPublicKey)
                .build();
        getInstructionsAsync();
    }

    private void initializePaymentData() {
        mPaymentId = mPaymentResult.getPaymentId();
        PaymentData paymentData = mPaymentResult.getPaymentData();
        if (paymentData != null) {
            if (paymentData.getPaymentMethod() != null) {
                mPaymentTypeId = paymentData.getPaymentMethod().getPaymentTypeId();
                mPaymentMethodId = paymentData.getPaymentMethod().getId();
            }
        }
        if (mSite != null) {
            mCurrencyId = mSite.getCurrencyId();
        }
    }

    protected void trackScreen() {
        MPTrackingProvider mpTrackingProvider = new MPTrackingProvider.Builder()
                .setContext(this)
                .setCheckoutVersion(BuildConfig.VERSION_NAME)
                .setPublicKey(mMerchantPublicKey)
                .build();


        ScreenViewEvent.Builder builder = new ScreenViewEvent.Builder()
                .setScreenId(TrackingUtil.SCREEN_ID_PAYMENT_RESULT_INSTRUCTIONS)
                .setScreenName(TrackingUtil.SCREEN_NAME_PAYMENT_RESULT_INSTRUCTIONS)
                .addMetaData(TrackingUtil.METADATA_PAYMENT_IS_EXPRESS, TrackingUtil.IS_EXPRESS_DEFAULT_VALUE)
                .addMetaData(TrackingUtil.METADATA_PAYMENT_STATUS, mPaymentResult.getPaymentStatus())
                .addMetaData(TrackingUtil.METADATA_PAYMENT_STATUS_DETAIL, mPaymentResult.getPaymentStatusDetail())
                .addMetaData(TrackingUtil.METADATA_PAYMENT_ID, String.valueOf(mPaymentResult.getPaymentId()));

        if (mPaymentMethodId != null) {
            builder.addMetaData(TrackingUtil.METADATA_PAYMENT_METHOD_ID, mPaymentMethodId);
        }
        if (mPaymentTypeId != null) {
            builder.addMetaData(TrackingUtil.METADATA_PAYMENT_TYPE_ID, mPaymentTypeId);
        }

        ScreenViewEvent event = builder.build();
        mpTrackingProvider.addTrackEvent(event);
    }

    protected void getInstructionsAsync() {

        showLoading();
        mMercadoPagoServices.getInstructions(mPaymentId, mPaymentTypeId, new Callback<Instructions>() {
            @Override
            public void success(Instructions instructions) {
                List<Instruction> instructionsList
                        = instructions.getInstructions() == null ? new ArrayList<Instruction>() : instructions.getInstructions();
                if (instructionsList.isEmpty()) {
                    ErrorUtil.startErrorActivity(mActivity, mActivity.getString(R.string.mpsdk_standard_error_message), INSTRUCTIONS_NOT_FOUND_FOR_TYPE + mPaymentTypeId, false, mMerchantPublicKey);
                } else {
                    resolveInstructionsFound(instructionsList);
                }
            }

            @Override
            public void failure(ApiException apiException) {
                ApiUtil.showApiExceptionError(mActivity, apiException, mMerchantPublicKey, ApiUtil.RequestOrigin.GET_INSTRUCTIONS);
                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        getInstructionsAsync();
                    }
                });
            }
        });
    }

    private void resolveInstructionsFound(List<Instruction> instructionsList) {
        Instruction instruction = getInstruction(instructionsList);
        if (instruction == null) {
            ErrorUtil.startErrorActivity(this, this.getString(R.string.mpsdk_standard_error_message), "instruction not found for type " + mPaymentTypeId, false, mMerchantPublicKey);
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

        setTitle(instruction.getTitle());
        setReferencesInformation(instruction);
        setInformationMessages(instruction);
        mAccreditationMessage.setText(instruction.getAcreditationMessage());
        setActions(instruction);
    }

    protected void setTitle(String title) {
        Spanned formattedTitle = CurrenciesUtil.formatCurrencyInText("<br>", mTotalAmount, mCurrencyId, title, false, true);
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
        return mPaymentMethodId.equals(PaymentMethods.ARGENTINA.REDLINK) && mPaymentTypeId.equals(PaymentTypes.ATM);
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

    @Override
    public void onBackPressed() {

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

    public void setFailureRecovery(FailureRecovery failureRecovery) {
        this.failureRecovery = failureRecovery;
    }

    private void recoverFromFailure() {
        if(failureRecovery != null) {
            failureRecovery.recover();
        }
    }
}
