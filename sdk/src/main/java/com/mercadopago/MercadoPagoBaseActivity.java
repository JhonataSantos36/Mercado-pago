package com.mercadopago;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.util.Locale;

/**
 * Created by mreverter on 1/25/17.
 */
public abstract class MercadoPagoBaseActivity extends AppCompatActivity {

    private String mLanguage;
    private String mCountry;

    @Override
    protected void onCreate(Bundle savedInstance) {
        if (savedInstance == null) {
            mLanguage = getResources().getConfiguration().locale.getLanguage();
            mCountry = getResources().getConfiguration().locale.getCountry();
        } else {
            mLanguage = savedInstance.getString("language");
            mCountry = savedInstance.getString("country");
        }
        updateLanguage(mLanguage, mCountry);
        super.onCreate(savedInstance);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("language", mLanguage);
        outState.putString("country", mCountry);
    }

    private void updateLanguage(@NonNull String language, @NonNull String country) {
        final Configuration cfg = new Configuration();
        final Locale current = getResources().getConfiguration().locale;
        cfg.locale = new Locale(language, country);
        if (cfg.locale.getLanguage() != null && !current.getLanguage().equals(cfg.locale.getLanguage())) {
            getResources().updateConfiguration(cfg, null);
        }
    }

    public void overrideTransitionIn() {
        overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
    }

    public void overrideTransitionOut() {
        overridePendingTransition(R.anim.mpsdk_slide_left_to_right_in, R.anim.mpsdk_slide_left_to_right_out);
    }

    public void overrideTransitionFadeInFadeOut() {
        overridePendingTransition(R.anim.mpsdk_fade_in_seamless, R.anim.mpsdk_fade_out_seamless);
    }

}
