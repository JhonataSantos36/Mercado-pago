package com.mercadopago;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mercadopago.controllers.CheckoutErrorHandler;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.JsonUtil;

public class ErrorActivity extends MercadoPagoBaseActivity {

    private MercadoPagoError mMercadoPagoError;
    private TextView mErrorMessageTextView;
    private View mRetryView;
    private View mExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animateErrorScreenLaunch();

        if(CheckoutErrorHandler.getInstance().hasCustomErrorLayout()) {
            setContentView(CheckoutErrorHandler.getInstance().getCustomErrorLayout());
        } else {
            setContentView(R.layout.mpsdk_activity_error);
        }

        getActivityParameters();
        if (validParameters()) {
            initializeControls();
            fillData();
        } else {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }

    private void animateErrorScreenLaunch() {
        overridePendingTransition(R.anim.mpsdk_fade_in_seamless, R.anim.mpsdk_fade_out_seamless);
    }

    private boolean validParameters() {
        return mMercadoPagoError != null;
    }

    private void getActivityParameters() {
        this.mMercadoPagoError = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("mercadoPagoError"), MercadoPagoError.class);
    }

    private void initializeControls() {
        this.mErrorMessageTextView = (TextView) findViewById(R.id.mpsdkErrorMessage);
        this.mRetryView = findViewById(R.id.mpsdkErrorRetry);
        this.mExit = findViewById(R.id.mpsdkExit);
        this.mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void fillData() {
        String message;
        if (mMercadoPagoError.getApiException() != null) {
            message = ApiUtil.getApiExceptionMessage(this, mMercadoPagoError.getApiException());
        } else {
            message = mMercadoPagoError.getMessage();
        }

        this.mErrorMessageTextView.setText(message);

        if (mMercadoPagoError.isRecoverable()) {
            mRetryView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        } else {
            mRetryView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra("mercadoPagoError", JsonUtil.getInstance().toJson(mMercadoPagoError));
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
