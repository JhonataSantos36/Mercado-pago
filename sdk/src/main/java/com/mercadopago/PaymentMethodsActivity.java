package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.PaymentMethodsAdapter;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.decorations.DividerItemDecoration;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.LayoutUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PaymentMethodsActivity extends Activity {

    protected MercadoPago mMercadoPago;
    private Activity mActivity;
    private String mMerchantPublicKey;
    private RecyclerView mRecyclerView;
    private boolean mShowBankDeals;
    private boolean mSupportMPApp;
    private String mPaymentTypeSupported;
    private List<String> mExcludedPaymentMethodIds;
    private List<String> mExcludedPaymentTypesIds;
    private String mDefaultPaymentMethodId;

    private PaymentPreference mPaymentPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView();
        mActivity = this;
        getActivityParameters();

        mMercadoPago = createMercadoPago(mMerchantPublicKey);

        createPaymentMethodPreference();

        // Set recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.payment_methods_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        // Set a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load payment methods
        getPaymentMethodsAsync(mMerchantPublicKey);

    }

    protected MercadoPago createMercadoPago(String publicKey) {
        return new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(publicKey)
                .build();
    }

    private void getActivityParameters() {

        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");
        if (mMerchantPublicKey == null) {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
            return;
        }

        mPaymentTypeSupported = this.getIntent().getStringExtra("paymentTypeId");
        mShowBankDeals = this.getIntent().getBooleanExtra("showBankDeals", true);
        mSupportMPApp = this.getIntent().getBooleanExtra("supportMPApp", false);


        if (this.getIntent().getStringExtra("excludedPaymentMethodIds") != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>(){}.getType();
            mExcludedPaymentMethodIds = gson.fromJson(this.getIntent().getStringExtra("excludedPaymentMethodIds"), listType);
        }
        if (this.getIntent().getStringExtra("excludedPaymentTypes") != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>(){}.getType();
            mExcludedPaymentTypesIds = gson.fromJson(this.getIntent().getStringExtra("excludedPaymentTypes"), listType);
        }
        mDefaultPaymentMethodId = this.getIntent().getStringExtra("defaultPaymentMethodId");
    }

    private void createPaymentMethodPreference() {
        mPaymentPreference = new PaymentPreference();
        mPaymentPreference.setExcludedPaymentMethodIds(this.mExcludedPaymentMethodIds);
        mPaymentPreference.setExcludedPaymentTypeIds(this.mExcludedPaymentTypesIds);
        mPaymentPreference.setDefaultPaymentMethodId(this.mDefaultPaymentMethodId);
    }

    protected void setContentView() {
        setContentView(R.layout.activity_payment_methods);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mShowBankDeals) {
            getMenuInflater().inflate(R.menu.payment_methods, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_bank_deals) {
            new MercadoPago.StartActivityBuilder()
                    .setActivity(this)
                    .setPublicKey(mMerchantPublicKey)
                    .startBankDealsActivity();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    public void refreshLayout(View view) {
        getPaymentMethodsAsync(mMerchantPublicKey);
    }

    private void getPaymentMethodsAsync(String merchantPublicKey) {

        LayoutUtil.showProgressLayout(mActivity);

        mMercadoPago.getPaymentMethods(new Callback<List<PaymentMethod>>() {
            @Override
            public void success(List<PaymentMethod> paymentMethods, Response response) {
                mRecyclerView.setAdapter(new PaymentMethodsAdapter(mActivity, getSupportedPaymentMethods(paymentMethods), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Return to parent
                        Intent returnIntent = new Intent();
                        PaymentMethod selectedPaymentMethod = (PaymentMethod) view.getTag();
                        returnIntent.putExtra("paymentMethod", selectedPaymentMethod);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                }));
                LayoutUtil.showRegularLayout(mActivity);
            }

            @Override
            public void failure(RetrofitError error) {

                ApiUtil.finishWithApiException(mActivity, error);
            }
        });
    }

    private List<PaymentMethod> getSupportedPaymentMethods(List<PaymentMethod> paymentMethods) {

        List<PaymentMethod> paymentMethodList = mPaymentPreference.getSupportedPaymentMethods(paymentMethods);

        paymentMethodList = getPaymentMethodsOfType(mPaymentTypeSupported, paymentMethodList);

        if(mSupportMPApp)
        {
            addMPAppMethod(paymentMethodList);
        }
        return paymentMethodList;
    }

    private List<PaymentMethod> getPaymentMethodsOfType(String paymentTypeId, List<PaymentMethod> paymentMethodList) {

        if(paymentMethodList != null && paymentTypeId != null && !paymentTypeId.isEmpty()) {

            List<PaymentMethod> validPaymentMethods = new ArrayList<>();

            for (PaymentMethod currentPaymentMethod : paymentMethodList) {
                if(currentPaymentMethod.getPaymentTypeId().equals(paymentTypeId)) {
                    validPaymentMethods.add(currentPaymentMethod);
                }
            }
            return validPaymentMethods;
        } else {
            return paymentMethodList;
        }
    }

    private void addMPAppMethod(List<PaymentMethod> list) {

        PaymentMethod mpAppMethod = new PaymentMethod();
        mpAppMethod.setId(getResources().getString(R.string.mpsdk_mp_app_id));
        mpAppMethod.setName(getResources().getString(R.string.mpsdk_mp_app_name));
        list.add(0, mpAppMethod);
    }
}

