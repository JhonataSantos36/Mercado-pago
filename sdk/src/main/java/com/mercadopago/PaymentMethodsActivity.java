package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.mercadopago.model.PaymentMethodPreference;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.LayoutUtil;

import java.lang.reflect.Type;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PaymentMethodsActivity extends AppCompatActivity {

    private Activity mActivity;
    private String mMerchantPublicKey;
    private RecyclerView mRecyclerView;
    private boolean mShowBankDeals;
    private boolean mSupportMPApp;
    private List<String> mExcludedPaymentMethodIds;
    private List<String> mSupportedPaymentTypes;
    private List<String> mExcludedPaymentTypes;
    private String mDefaultPaymentMethodId;

    private PaymentMethodPreference mPaymentMethodPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView();

        mActivity = this;

        getActivityParameters();

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

    private void getActivityParameters() {

        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");
        if (mMerchantPublicKey == null) {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
            return;
        }

        mShowBankDeals = this.getIntent().getBooleanExtra("showBankDeals", true);
        mSupportMPApp = this.getIntent().getBooleanExtra("supportMPApp", false);

        if (this.getIntent().getStringExtra("supportedPaymentTypes") != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>(){}.getType();
            mSupportedPaymentTypes = gson.fromJson(this.getIntent().getStringExtra("supportedPaymentTypes"), listType);
        }

        if (this.getIntent().getStringExtra("excludedPaymentMethodIds") != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>(){}.getType();
            mExcludedPaymentMethodIds = gson.fromJson(this.getIntent().getStringExtra("excludedPaymentMethodIds"), listType);
        }
        if (this.getIntent().getStringExtra("excludedPaymentTypes") != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>(){}.getType();
            mExcludedPaymentTypes = gson.fromJson(this.getIntent().getStringExtra("excludedPaymentTypes"), listType);
        }
        mDefaultPaymentMethodId = this.getIntent().getStringExtra("defaultPaymentMethodId");
    }

    private void createPaymentMethodPreference() {
        mPaymentMethodPreference = new PaymentMethodPreference();
        mPaymentMethodPreference.setExcludedPaymentMethodIds(this.mExcludedPaymentMethodIds);
        mPaymentMethodPreference.setSupportedPaymentTypes(this.mSupportedPaymentTypes);
        mPaymentMethodPreference.setExcludedPaymentTypes(this.mExcludedPaymentTypes);
        mPaymentMethodPreference.setDefaultPaymentMethodId(this.mDefaultPaymentMethodId);
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

        MercadoPago mercadoPago = new MercadoPago.Builder()
                .setContext(mActivity)
                .setPublicKey(merchantPublicKey)
                .build();

        mercadoPago.getPaymentMethods(new Callback<List<PaymentMethod>>() {
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

        List<PaymentMethod> paymentMethodList = mPaymentMethodPreference.getSupportedPaymentMethods(paymentMethods);
        if(mSupportMPApp)
        {
            addMPAppMethod(paymentMethodList);
        }
        return paymentMethodList;
    }

    private void addMPAppMethod(List<PaymentMethod> list) {

        PaymentMethod mpAppMethod = new PaymentMethod();
        mpAppMethod.setId(getResources().getString(R.string.mpsdk_mp_app_id));
        mpAppMethod.setName(getResources().getString(R.string.mpsdk_mp_app_name));
        list.add(0, mpAppMethod);
    }
}

