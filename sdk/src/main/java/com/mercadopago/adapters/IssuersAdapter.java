package com.mercadopago.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.model.Issuer;
import com.mercadopago.views.MPTextView;

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
        View adapterView = inflater.inflate(R.layout.mpsdk_row_issuers, parent, false);
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        public MPTextView mIssuersTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mIssuersTextView = (MPTextView) itemView.findViewById(R.id.mpsdkAdapterIssuersText);

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
