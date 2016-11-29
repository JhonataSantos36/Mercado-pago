package com.mercadopago.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mercadopago.R;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.model.Issuer;
import com.mercadopago.uicontrollers.issuers.IssuersView;

import java.util.ArrayList;
import java.util.List;

public class IssuersAdapter extends RecyclerView.Adapter<IssuersAdapter.ViewHolder> {

    private Context mContext;
    private List<Issuer> mIssuers;
    private OnSelectedCallback<Integer> mCallback;

    public IssuersAdapter(Context context, OnSelectedCallback<Integer> callback) {
        this.mContext = context;
        this.mIssuers = new ArrayList<>();
        this.mCallback = callback;
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
        View adapterView = inflater.inflate(R.layout.mpsdk_adapter_issuer, parent, false);
        ViewHolder viewHolder = new ViewHolder(adapterView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Issuer issuer = mIssuers.get(position);
        holder.mIssuersView.drawIssuer(issuer);
    }


    public Issuer getItem(int position) {
        return mIssuers.get(position);
    }

    @Override
    public int getItemCount() {
        return mIssuers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public FrameLayout mIssuerContainer;
        public IssuersView mIssuersView;

        public ViewHolder(View itemView) {
            super(itemView);
            mIssuerContainer = (FrameLayout) itemView.findViewById(R.id.mpsdkIssuerAdapterContainer);
            mIssuersView = new IssuersView(mContext);
            mIssuersView.inflateInParent(mIssuerContainer, true);
            mIssuersView.initializeControls();

            itemView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event != null && event.getAction() == KeyEvent.ACTION_DOWN
                            && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                        mCallback.onSelected(getLayoutPosition());
                        return true;
                    }
                    return false;
                }
            });
        }
    }

}
