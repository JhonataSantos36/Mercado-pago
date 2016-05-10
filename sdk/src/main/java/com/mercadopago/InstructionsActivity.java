package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.Instruction;
import com.mercadopago.model.InstructionActionInfo;
import com.mercadopago.model.InstructionReference;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.ScaleUtil;
import com.mercadopago.views.MPButton;
import com.mercadopago.views.MPTextView;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class InstructionsActivity extends AppCompatActivity {

    //Values
    protected MercadoPago mMercadoPago;
    protected FailureRecovery failureRecovery;

    //Controls
    protected LinearLayout mReferencesLayout;
    protected Activity mActivity;
    protected MPTextView mTitle;
    protected MPTextView mPrimaryInfo;
    protected MPTextView mSecondaryInfo;
    protected MPTextView mTertiaryInfo;
    protected MPTextView mAccreditationMessage;
    protected MPButton mActionButton;
    protected MPTextView mExitTextView;

    //Params
    protected Payment mPayment;
    protected String mMerchantPublicKey;
    protected PaymentMethod mPaymentMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        getActivityParameters();
        initializeControls();
        mActivity = this;
        mMercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(mMerchantPublicKey)
                .build();
        getInstructionsAsync();
    }

    protected void getInstructionsAsync() {


        LayoutUtil.showProgressLayout(this);
        //TODO cambiar por mPayment.getId() cuando estén andando los servicios
        mMercadoPago.getInstructions(mPayment.getId(), mPaymentMethod.getId(), mPaymentMethod.getPaymentTypeId(), new Callback<Instruction>() {
            @Override
            public void success(Instruction instruction, Response response) {
                showInstructions(instruction);
                LayoutUtil.showRegularLayout(mActivity);
            }

            @Override
            public void failure(RetrofitError error) {
                ApiUtil.showApiExceptionError(mActivity, error);
                failureRecovery = new FailureRecovery() {
                    @Override
                    public void recover() {
                        getInstructionsAsync();
                    }
                };
            }
        });
    }

    protected void showInstructions(Instruction instruction) {
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
        int marginBottom = ScaleUtil.getPxFromDp(7, this);
        marginParams.setMargins(0, marginTop, 0, marginBottom);
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
                int referenceSize = getTextSizeForReference(formattedReference, reference.getSeparator());

                currentValueTextView.setText(formattedReference);
                currentValueTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, referenceSize);
                currentValueTextView.setLayoutParams(marginParams);
                mReferencesLayout.addView(currentValueTextView);
            }
        }
    }

    private int getTextSizeForReference(String formattedReference, String separator) {

        int textSize;
        String referenceWithoutSeparator = separator == null ? formattedReference :formattedReference.replace(separator, "");
        if(android.text.TextUtils.isDigitsOnly(referenceWithoutSeparator)) {
            textSize = getResources().getDimensionPixelSize(R.dimen.mpsdk_large_text);
        }
        else {
            textSize = getResources().getDimensionPixelSize(R.dimen.mpsdk_regular_text);
        }
        return textSize;
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

    protected void getActivityParameters() {
        mPayment = (Payment) getIntent().getExtras().getSerializable("payment");
        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");
        mPaymentMethod = (PaymentMethod) getIntent().getSerializableExtra("paymentMethod");
    }

    protected void initializeControls() {
        mReferencesLayout = (LinearLayout) findViewById(R.id.referencesLayout);
        mTitle = (MPTextView) findViewById(R.id.title);
        mPrimaryInfo = (MPTextView) findViewById(R.id.primaryInfo);
        mSecondaryInfo = (MPTextView) findViewById(R.id.secondaryInfo);
        mTertiaryInfo = (MPTextView) findViewById(R.id.tertiaryInfo);
        mAccreditationMessage = (MPTextView) findViewById(R.id.accreditationMessage);
        mActionButton = (MPButton) findViewById(R.id.actionButton);
        mExitTextView = (MPTextView) findViewById(R.id.exitTextView);
        mExitTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                animateOut();
            }
        });
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
        overridePendingTransition(R.anim.slide_right_to_left_in, R.anim.slide_right_to_left_out);
    }

    private void recoverFromFailure() {
        if(failureRecovery != null) {
            failureRecovery.recover();
        }
    }
}
