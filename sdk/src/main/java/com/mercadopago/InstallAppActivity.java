package com.mercadopago;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.net.URLEncoder;

public class InstallAppActivity extends AppCompatActivity {

    private String mDeepLink;
    private String mPackageName;
    private String mPreferenceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDeepLink = this.getIntent().getStringExtra("deepLink");
        mPackageName = this.getIntent().getStringExtra("packageName");
        mPreferenceId = this.getIntent().getStringExtra("preferenceId");

        try {

            callMPApp();

        } catch (ActivityNotFoundException ex) {

            setContentView(R.layout.activity_install_app);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    public void submit(View view) {

        goToPlayStore();
    }

    private void goToPlayStore() {

        String campaign = "";
        try {
            campaign = "&referrer=" + URLEncoder.encode("utm_source=admob&deep_link=" + URLEncoder.encode(getMPDeepLink(), "utf-8"), "utf-8");
        } catch (Exception ignore) {
        }

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.mercadopago.wallet" + campaign)));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.mercadopago.wallet" + campaign)));
        }
    }

    private void callMPApp() {

        Intent intent = new Intent();
        intent.setData(Uri.parse(getMPDeepLink()));
        startActivity(intent);
    }

    private String getMPDeepLink() {

        return getString(R.string.mpsdk_mp_app_deep_link_prefix) + mPreferenceId +
                "&response_package_name=" + mPackageName +
                "&response_deep_link=" + mDeepLink;
    }
}
