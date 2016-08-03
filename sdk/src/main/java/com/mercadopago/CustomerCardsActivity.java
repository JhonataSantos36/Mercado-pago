package com.mercadopago;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;

import java.lang.reflect.Type;
import java.util.List;

public class CustomerCardsActivity extends MercadoPagoActivity {

    protected RecyclerView mRecyclerView;
    protected Toolbar mToolbar;
    protected TextView mTitle;

    private List<Card> mCards;

    @Override
    protected void getActivityParameters() {
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Card>>(){}.getType();
            mCards = gson.fromJson(this.getIntent().getStringExtra("cards"), listType);
        } catch (Exception ex) {
            mCards = null;
        }
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
        if(mCards == null) {
            throw new IllegalStateException("cards not set");
        }
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.mpsdk_activity_customer_cards);
    }

    @Override
    protected void initializeControls() {
        initializeToolbar();
        mRecyclerView = (RecyclerView) findViewById(R.id.mpsdkCustomerCardsList);
    }

    @Override
    protected void onValidStart() {
        fillData();
    }

    @Override
    protected void onInvalidStart(String message) {
        ErrorUtil.startErrorActivity(this, message, false);
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

        if(isCustomColorSet()) {
            mToolbar.setBackgroundColor(getCustomBaseColor());
        }
        if(isDarkFontEnabled()) {
            mTitle.setTextColor(getDarkFontColor());
            Drawable upArrow = mToolbar.getNavigationIcon();
            upArrow.setColorFilter(getDarkFontColor(), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
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
                if(card != null) {
                    returnIntent.putExtra("card", JsonUtil.getInstance().toJson(card));
                }
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        }));
    }

    public void onOtherPaymentMethodClicked(View view) {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

}
