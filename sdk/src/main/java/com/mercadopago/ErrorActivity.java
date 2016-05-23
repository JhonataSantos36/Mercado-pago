package com.mercadopago;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercadopago.exceptions.MPException;

public class ErrorActivity extends AppCompatActivity {

    private MPException mMPException;

    private TextView mErrorMessageTextView;
    private TextView mRetryTextView;
    private ImageView mErrorImageView;
    private ViewGroup mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animateErrorScreenLaunch();
        setContentView(R.layout.activity_error);
        getActivityParameters();
        if(validParameters()) {
            initializeControls();
            fillData();
        }
        else {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }

    private void animateErrorScreenLaunch() {
        overridePendingTransition(R.anim.fade_in_seamless, R.anim.fade_out_seamless);
    }

    private boolean validParameters() {
        return mMPException != null;
    }

    private void getActivityParameters() {
        this.mMPException = (MPException) getIntent().getSerializableExtra("mpException");
    }

    private void initializeControls() {
        this.mRefreshLayout = (ViewGroup) findViewById(R.id.refreshLayout);
        this.mErrorMessageTextView = (TextView) findViewById(R.id.errorMessage);
        this.mRetryTextView = (TextView) findViewById(R.id.errorRetry);
        this.mRetryTextView = (TextView) findViewById(R.id.errorRetry);
        this.mErrorImageView = (ImageView) findViewById(R.id.errorImage);
    }

    private void fillData() {
        String message;
        if(mMPException.getApiException() != null) {
            message = this.getString(R.string.mpsdk_standard_error_message);
        }
        else {
            message = mMPException.getMessage();
        }

        this.mErrorMessageTextView.setText(message);

        if (mMPException.isRecoverable())
        {
            mErrorImageView.setImageResource(R.drawable.ic_refresh);
            mRefreshLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
        else
        {
            mErrorImageView.setImageResource(R.drawable.ic_rejected);
            mRetryTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("mpException", mMPException);
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
