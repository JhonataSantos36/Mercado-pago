package com.mercadopago;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mercadopago.controllers.CheckoutErrorHandler;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.ApiException;
import com.mercadopago.tracker.FlowHandler;
import com.mercadopago.tracker.MPTrackingContext;
import com.mercadopago.tracking.model.ScreenViewEvent;
import com.mercadopago.tracking.utils.TrackingUtil;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;

public class ErrorActivity extends MercadoPagoBaseActivity {

    private MercadoPagoError mMercadoPagoError;
    private String mPublicKey;
    private TextView mErrorMessageTextView;
    private View mRetryView;
    private View mExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animateErrorScreenLaunch();

        if (CheckoutErrorHandler.getInstance().hasCustomErrorLayout()) {
            setContentView(CheckoutErrorHandler.getInstance().getCustomErrorLayout());
        } else {
            setContentView(R.layout.mpsdk_activity_error);
        }

        getActivityParameters();
        if (validParameters()) {
            initializeControls();
            trackScreen();
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
        this.mMercadoPagoError = JsonUtil.getInstance().fromJson(getIntent().getStringExtra(ErrorUtil.ERROR_EXTRA_KEY), MercadoPagoError.class);
        this.mPublicKey = getIntent().getStringExtra(ErrorUtil.PUBLIC_KEY_EXTRA);
    }

    private void trackScreen() {
        MPTrackingContext mpTrackingContext = new MPTrackingContext.Builder(this, mPublicKey)
                .setVersion(BuildConfig.VERSION_NAME)
                .build();

        ScreenViewEvent.Builder builder = new ScreenViewEvent.Builder()
                .setFlowId(FlowHandler.getInstance().getFlowId())
                .setScreenId(TrackingUtil.SCREEN_ID_ERROR)
                .setScreenName(TrackingUtil.SCREEN_NAME_ERROR);

        if (mMercadoPagoError != null) {

            if (mMercadoPagoError.getApiException() != null) {
                ApiException apiException = mMercadoPagoError.getApiException();

                if (apiException.getStatus() != null) {
                    builder.addProperty(TrackingUtil.PROPERTY_ERROR_STATUS, String.valueOf(apiException.getStatus()));
                }
                if (apiException.getCause() != null && !apiException.getCause().isEmpty() && apiException.getCause().get(0).getCode() != null) {
                    builder.addProperty(TrackingUtil.PROPERTY_ERROR_CODE, String.valueOf(apiException.getCause().get(0).getCode()));
                }
            }

            if (mMercadoPagoError.getRequestOrigin() != null && !mMercadoPagoError.getRequestOrigin().isEmpty()) {
                builder.addProperty(TrackingUtil.PROPERTY_ERROR_REQUEST, mMercadoPagoError.getRequestOrigin());
            }
        }

        ScreenViewEvent event = builder.build();

        mpTrackingContext.trackEvent(event);
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
        intent.putExtra(ErrorUtil.ERROR_EXTRA_KEY, JsonUtil.getInstance().toJson(mMercadoPagoError));
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
