package com.mercadopago.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.model.Issuer;
import com.mercadopago.views.MPTextView;

import java.util.ArrayList;
import java.util.List;

public class CardIssuersAdapter extends  RecyclerView.Adapter<CardIssuersAdapter.ViewHolder> {

    private Context mContext;
    private List<Issuer> mIssuers;

    public CardIssuersAdapter(Context context) {
        this.mContext = context;
        this.mIssuers = new ArrayList<>();
    }

    public void addResults(List<Issuer> list) {
        mIssuers.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        mIssuers.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View adapterView = inflater.inflate(R.layout.adapter_issuers, parent, false);
        ViewHolder viewHolder = new ViewHolder(adapterView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Issuer issuer = mIssuers.get(position);
        holder.mIssuersTextView.setText(issuer.getName());
    }


    public Issuer getItem(int position) {
        return mIssuers.get(position);
    }

    @Override
    public int getItemCount() {
        return mIssuers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public MPTextView mIssuersTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mIssuersTextView = (MPTextView) itemView.findViewById(R.id.adapter_issuers_text);
        }
    }

}
