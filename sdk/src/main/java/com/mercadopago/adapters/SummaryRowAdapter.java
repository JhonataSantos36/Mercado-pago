package com.mercadopago.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mercadopago.R;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.model.SummaryRow;
import com.mercadopago.uicontrollers.summaryrows.SummaryRowView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mromar on 9/11/17.
 */
public class SummaryRowAdapter extends RecyclerView.Adapter<SummaryRowAdapter.ViewHolder> {
    private final List<SummaryRow> mSummaryRows;
    private final OnSelectedCallback<Integer> mCallback;

    public SummaryRowAdapter(OnSelectedCallback<Integer> callback) {
        mSummaryRows = new ArrayList<>();
        mCallback = callback;
    }

    public void addResults(List<SummaryRow> list) {
        mSummaryRows.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View adapterView = inflater.inflate(R.layout.mpsdk_adapter_summary_row, parent, false);
        return new ViewHolder(adapterView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SummaryRow summaryRow = mSummaryRows.get(position);
        holder.mSummaryRowView.drawSummaryRow(summaryRow);
    }

    @Override
    public int getItemCount() {
        return mSummaryRows.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public FrameLayout mSummaryRowContainer;
        public SummaryRowView mSummaryRowView;

        public ViewHolder(View itemView) {
            super(itemView);
            mSummaryRowContainer = itemView.findViewById(R.id.mpsdkSummaryRowAdapterContainer);
            mSummaryRowView = new SummaryRowView(itemView.getContext());
            mSummaryRowView.inflateInParent(mSummaryRowContainer, true);
            mSummaryRowView.initializeControls();

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
