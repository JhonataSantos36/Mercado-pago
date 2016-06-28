package com.mercadopago;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.CustomerCardsAdapter;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.decorations.DividerItemDecoration;
import com.mercadopago.model.Card;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.JsonUtil;

import java.lang.reflect.Type;
import java.util.List;

public class CustomerCardsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;
    private TextView mTitle;

    private List<Card> mCards;

    private DecorationPreference mDecorationPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityParameters();

        if(mDecorationPreference != null && mDecorationPreference.hasColors()) {
            setTheme(R.style.Theme_MercadoPagoTheme_NoActionBar);
        }

        setContentView();

        initializeToolbar();

        if (mCards == null) {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
            return;
        }

        initializeControls();
        fillData();
    }

    protected void setContentView() {
        //TODO validate AGREGAR PUBLIC KEY
        MPTracker.getInstance().trackScreen("CUSTOMER_CARDS", "2", "publicKey", "MLA", "1.0", this);
        setContentView(R.layout.mpsdk_activity_customer_cards);
    }

    private void initializeToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.mpsdkToolbar);
        mTitle = (TextView) findViewById(R.id.mpsdkToolbarTitle);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(mDecorationPreference != null) {
            if(mDecorationPreference.hasColors()) {
                mToolbar.setBackgroundColor(mDecorationPreference.getBaseColor());
            }
            if(mDecorationPreference.isDarkFontEnabled()) {
                mTitle.setTextColor(mDecorationPreference.getDarkFontColor(this));
                Drawable upArrow = mToolbar.getNavigationIcon();
                upArrow.setColorFilter(mDecorationPreference.getDarkFontColor(this), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
        }
    }

    private void getActivityParameters() {

        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Card>>(){}.getType();
            mCards = gson.fromJson(this.getIntent().getStringExtra("cards"), listType);
        } catch (Exception ex) {
            mCards = null;
        }

        if(getIntent().getStringExtra("decorationPreference") != null) {
            mDecorationPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("decorationPreference"), DecorationPreference.class);
        }
    }

    private void initializeControls() {
        mRecyclerView = (RecyclerView) findViewById(R.id.mpsdkCustomerCardsList);
    }

    private void fillData() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        // Set a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load cards
        mRecyclerView.setAdapter(new CustomerCardsAdapter(this, mCards, new OnSelectedCallback<Card>() {
            @Override
            public void onSelected(Card card) {
                // Return to parent
                Intent returnIntent = new Intent();
                returnIntent.putExtra("card", JsonUtil.getInstance().toJson(card));
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        }));
    }

    @Override
    public void onBackPressed() {
        MPTracker.getInstance().trackEvent("CUSTOMER_CARDS", "BACK_PRESSED", "2", "publicKey", "MLA", "1.0", this);
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }
}
