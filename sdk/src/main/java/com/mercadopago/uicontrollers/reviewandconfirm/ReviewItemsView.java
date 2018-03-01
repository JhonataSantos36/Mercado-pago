package com.mercadopago.uicontrollers.reviewandconfirm;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.adapters.ReviewProductAdapter;
import com.mercadopago.constants.ReviewKeys;
import com.mercadopago.model.Item;
import com.mercadopago.model.Reviewable;
import com.mercadopago.preferences.ReviewScreenPreference;

import java.util.List;

/**
 * Created by mreverter on 2/2/17.
 */
public class ReviewItemsView extends Reviewable {
    private final List<Item> items;
    private final String currency;
    private final ReviewScreenPreference reviewScreenPreference;
    private Context context;
    private View view;
    private RecyclerView reviewItemsRecyclerView;

    public ReviewItemsView(Context context, List<Item> items, String currencyId, ReviewScreenPreference reviewScreenPreference) {
        this.context = context;
        this.items = items;
        this.currency = currencyId;
        this.reviewScreenPreference = reviewScreenPreference;
    }

    @Override
    public void draw() {
        ReviewProductAdapter adapter = new ReviewProductAdapter(items, currency, reviewScreenPreference);
        reviewItemsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void initializeControls() {
        reviewItemsRecyclerView = (RecyclerView) getView().findViewById(R.id.mpsdkReviewItemsRecyclerView);
        reviewItemsRecyclerView.setNestedScrollingEnabled(false);
        reviewItemsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        this.view = LayoutInflater.from(context)
                .inflate(R.layout.mpsdk_adapter_review_items, parent, attachToRoot);
        return this.view;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public String getKey() {
        return ReviewKeys.ITEMS;
    }
}
