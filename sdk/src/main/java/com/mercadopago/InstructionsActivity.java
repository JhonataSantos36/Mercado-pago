package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.Instruction;
import com.mercadopago.model.InstructionActionInfo;
import com.mercadopago.model.InstructionReference;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.views.MPButton;
import com.mercadopago.views.MPTextView;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class InstructionsActivity extends AppCompatActivity {

    //Controls
    private LinearLayout mReferencesLayout;
    private Activity mActivity;
    private MPTextView mTitle;
    private MPTextView mPrimaryInfo;
    private MPTextView mSecondaryInfo;
    private MPTextView mTertiaryInfo;
    private MPTextView mAccreditationMessage;
    private MPButton mActionButton;

    //Params
    private Payment mPayment;
    private String mMerchantPublicKey;
    private PaymentMethod mPaymentMethod;
    private String mCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        getActivityParameters();
        initializeControls();
        mActivity = this;
        MercadoPago mercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(mMerchantPublicKey)
                .build();
        getInstructionsAsync(mercadoPago);
    }

    private void getInstructionsAsync(MercadoPago mercadoPago) {

        final View progress = this.findViewById(R.id.progressLayout);
        progress.setVisibility(View.VISIBLE);

        //TODO cambiar por mPayment.getId() cuando estén andando los servicios
        mercadoPago.getInstructions((long) 1826446924, mPaymentMethod.getId(), new Callback<Instruction>() {
            @Override
            public void success(Instruction instruction, Response response) {
                showInstructions(instruction);
                LayoutUtil.showRegularLayout(mActivity);
            }

            @Override
            public void failure(RetrofitError error) {
                ApiUtil.finishWithApiException(mActivity, error);
            }
        });
    }

    private void showInstructions(Instruction instruction) {
        setTitle(instruction.getTitle());
        setInformationMessages(instruction);
        setReferencesInformation(instruction);
        mAccreditationMessage.setText(instruction.getAcreditationMessage());
        setActions(instruction);
    }

    private void setTitle(String title) {
        Spanned formattedTitle = CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionAmount(), mPayment.getCurrencyId(), title, true, true);
        mTitle.setText(formattedTitle);
    }

    private void setActions(Instruction instruction) {
        if(instruction.getActions() != null && !instruction.getActions().isEmpty()) {
            final InstructionActionInfo actionInfo = instruction.getActions().get(0);
            if(actionInfo.getUrl() != null) {
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
        else
        {
            mActionButton.setVisibility(View.GONE);
        }
    }

    private void setReferencesInformation(Instruction instruction) {
        LinearLayout.LayoutParams marginParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        marginParams.setMargins(0, 10, 0, 10);
        for(InstructionReference reference : instruction.getReferences()) {
            MPTextView currentTitleTextView = new MPTextView(this);
            MPTextView currentValueTextView = new MPTextView(this);

            if(reference.hasValue()) {

                if (reference.hasLabel()) {
                    currentTitleTextView.setText(reference.getLabel().toUpperCase());
                    currentTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.mpsdk_smaller_text));
                    mReferencesLayout.addView(currentTitleTextView);
                }

                currentValueTextView.setText(Html.fromHtml("<b>" + reference.getFormattedReference() + "</b>"));
                currentValueTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.mpsdk_large_text));
                currentTitleTextView.setLayoutParams(marginParams);
                mReferencesLayout.addView(currentValueTextView);
            }
        }
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

    private String getInfoHtmlText(List<String> info) {
        StringBuilder stringBuilder = new StringBuilder();
        for(String line : info) {
            stringBuilder.append(line);
            if(!line.equals(info.get(info.size() - 1))) {
                stringBuilder.append("<br/>");
            }
        }
        return stringBuilder.toString();
    }

    private void getActivityParameters() {
        mPayment = (Payment) getIntent().getExtras().getSerializable("payment");
        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");
        mPaymentMethod = (PaymentMethod) getIntent().getSerializableExtra("paymentMethod");
        mCurrency = getIntent().getStringExtra("currencyId");
    }

    private void initializeControls() {
        mReferencesLayout = (LinearLayout) findViewById(R.id.referencesLayout);
        mTitle = (MPTextView) findViewById(R.id.title);
        mPrimaryInfo = (MPTextView) findViewById(R.id.primaryInfo);
        mSecondaryInfo = (MPTextView) findViewById(R.id.secondaryInfo);
        mTertiaryInfo = (MPTextView) findViewById(R.id.tertiaryInfo);
        mAccreditationMessage = (MPTextView) findViewById(R.id.accreditationMessage);
        mActionButton = (MPButton) findViewById(R.id.actionButton);
    }
}
