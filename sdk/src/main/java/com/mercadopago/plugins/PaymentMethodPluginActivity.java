package com.mercadopago.plugins;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mercadopago.BuildConfig;
import com.mercadopago.components.Action;
import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.BackAction;
import com.mercadopago.components.Component;
import com.mercadopago.components.ComponentManager;
import com.mercadopago.components.NextAction;
import com.mercadopago.core.CheckoutStore;
import com.mercadopago.plugins.model.PaymentMethodInfo;
import com.mercadopago.tracker.FlowHandler;
import com.mercadopago.tracker.MPTrackingContext;
import com.mercadopago.tracking.model.ScreenViewEvent;
import com.mercadopago.tracking.utils.TrackingUtil;

/**
 * Created by nfortuna on 12/13/17.
 */

public class PaymentMethodPluginActivity extends AppCompatActivity implements ActionDispatcher {

    private static final String SCREEN_NAME_CONFIG_PAYMENT_METHOD_PLUGIN = "CONFIG_PAYMENT_METHOD";
    private static final String PUBLIC_KEY = "public_key";

    private String mPublicKey;

    public static Intent getIntent(@NonNull final Context context, @NonNull final String publicKey) {
        Intent intent = new Intent(context, PaymentMethodPluginActivity.class);
        intent.putExtra(PUBLIC_KEY, publicKey);
        return intent;
    }

    private ComponentManager componentManager;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final PaymentMethodInfo paymentMethodInfo =
                CheckoutStore.getInstance().getSelectedPaymentMethodInfo(this);
        final PaymentMethodPlugin plugin = CheckoutStore
                .getInstance().getPaymentMethodPluginById(paymentMethodInfo.id);

        Intent intent = getIntent();
        mPublicKey = intent.getStringExtra(PUBLIC_KEY);

        trackScreen(plugin.getId());

        if (plugin == null) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        final PluginComponent.Props props = new PluginComponent.Props.Builder()
                .setData(CheckoutStore.getInstance().getData())
                .setCheckoutPreference(CheckoutStore.getInstance().getCheckoutPreference())
                .build();

        final Component component = plugin.createConfigurationComponent(props, this);
        componentManager = new ComponentManager(this);

        if (component == null) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        component.setDispatcher(this);
        componentManager.render(component);
    }

    private void trackScreen(String id) {

        String screenName = SCREEN_NAME_CONFIG_PAYMENT_METHOD_PLUGIN + "_" + id;

        MPTrackingContext mTrackingContext = new MPTrackingContext.Builder(this, mPublicKey)
                .setCheckoutVersion(BuildConfig.VERSION_NAME)
                .build();

        ScreenViewEvent event = new ScreenViewEvent.Builder()
                .setFlowId(FlowHandler.getInstance().getFlowId())
                .setScreenId(screenName)
                .setScreenName(screenName)
                .build();

        mTrackingContext.trackEvent(event);
    }

    @Override
    public void dispatch(final Action action) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (action instanceof NextAction) {
                    setResult(RESULT_OK);
                    finish();
                } else if (action instanceof BackAction) {
                    onBackPressed();
                } else {
                    componentManager.dispatch(action);
                }
            }
        });
    }
}