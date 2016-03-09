package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.Instruction;
import com.mercadopago.model.InstructionActionInfo;
import com.mercadopago.model.InstructionReference;
import com.mercadopago.model.Payment;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.LayoutUtil;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class InstructionsActivity extends AppCompatActivity {

    //Controls
    private CardView mBottomCardView;
    private LinearLayout mReferencesLayout;
    private Activity mActivity;
    private TextView mTitle;
    private TextView mPrimaryInfo;
    private TextView mSecondaryInfo;
    private TextView mTertiaryInfo;
    private TextView mAccreditationMessage;
    private TextView mActionButton;

    //Params
    private Payment mPayment;
    private String mMerchantPublicKey;

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

        LayoutUtil.showProgressLayout(this);
        //TODO cambiar por mPayment.getId() cuando estén andando los servicios
        mercadoPago.getInstructions((long) 1826446924, "oxxo", new Callback<Instruction>() {
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
        mTitle.setText(instruction.getTitle());
        setInformationMessages(instruction);
        setReferencesInformation(instruction);
        mAccreditationMessage.setText(instruction.getAcreditationMessage());
        setActions(instruction);
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
        LinearLayout.LayoutParams marginParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        marginParams.setMargins(0, 10, 0, 10);
        for(InstructionReference reference : instruction.getReferences()) {
            TextView currentTitleTextView = new TextView(this);
            TextView currentValueTextView = new TextView(this);

            if(reference.getLabel() != null && !reference.getLabel().isEmpty()) {
                currentTitleTextView.setText(reference.getLabel());
                currentTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.mpsdk_smaller_text));
                mReferencesLayout.addView(currentTitleTextView);
            }

            currentValueTextView.setText(Html.fromHtml("<b>" + reference.getFormattedReference() + "</b>"));
            currentValueTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.mpsdk_large_text));
            currentTitleTextView.setLayoutParams(marginParams);
            mReferencesLayout.addView(currentValueTextView);
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
                stringBuilder.append("<br/><br/>");
            }
        }
        return stringBuilder.toString();
    }

    private void getActivityParameters() {
        mPayment = (Payment) getIntent().getExtras().getSerializable("payment");
        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");
    }

    private void initializeControls() {
        mReferencesLayout = (LinearLayout) findViewById(R.id.referencesLayout);
        mBottomCardView = (CardView) findViewById(R.id.bottomCardView);
        mTitle = (TextView) findViewById(R.id.title);
        mPrimaryInfo = (TextView) findViewById(R.id.primaryInfo);
        mSecondaryInfo = (TextView) findViewById(R.id.secondaryInfo);
        mTertiaryInfo = (TextView) findViewById(R.id.tertiaryInfo);
        mAccreditationMessage = (TextView) findViewById(R.id.accreditationMessage);
        mActionButton = (TextView) findViewById(R.id.actionButton);
    }
}
