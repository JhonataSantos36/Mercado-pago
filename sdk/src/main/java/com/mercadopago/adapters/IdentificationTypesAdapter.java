package com.mercadopago.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mercadopago.R;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.IdentificationType;

import java.util.List;

public class IdentificationTypesAdapter extends BaseAdapter {

    private List<IdentificationType> mData;

    public IdentificationTypesAdapter(List<IdentificationType> data) {
        mData = data;
    }

    public int getCount() {
        return mData.size();
    }

    public Object getItem(int position) {
        try {
            return mData.get(position);
        } catch (Exception ex) {
            return null;
        }
    }

    public List<IdentificationType> getIdentificationTypes() {
        return mData;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        if (convertView == null)
            row = LayoutInflater.from(parent.getContext()).inflate(R.layout.mpsdk_row_simple_spinner, parent, false);

        IdentificationType identificationType = mData.get(position);
        MPTextView label = row.findViewById(R.id.mpsdkItemTitle);
        label.setText(identificationType.getName());
        return row;
    }
}
