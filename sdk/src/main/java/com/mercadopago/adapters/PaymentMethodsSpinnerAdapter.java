package com.mercadopago.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mercadopago.R;
import com.mercadopago.model.PaymentMethod;

import java.util.List;

/**
 * Created by mreverter on 28/12/15.
 */
public class PaymentMethodsSpinnerAdapter extends BaseAdapter {
    private List<PaymentMethod> mData;
    private static LayoutInflater mInflater = null;
    private String mError;

    public PaymentMethodsSpinnerAdapter(Activity activity, List<PaymentMethod> data) {
        mError = "";
        mData = data;
        PaymentMethod selectionIssuer = new PaymentMethod();
        selectionIssuer.setName(activity.getString(R.string.mpsdk_select_pm_label));
        data.add(0, selectionIssuer);
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mData.size();
    }

    public Object getItem(int position) {
        try {
            PaymentMethod item;
            if(position == 0)
                item = null;
            else
                item = mData.get(position);

            return item;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public long getItemId(int position) {
        mError = "";
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        if(convertView == null)
            row = mInflater.inflate(R.layout.row_simple_spinner, parent, false);

        PaymentMethod pm = mData.get(position);

        TextView label = (TextView) row.findViewById(R.id.label);
        label.setText(pm.getName());

        return row;
    }

    public void setError(View v, String error) {
        TextView label = (TextView) v.findViewById(R.id.label);
        label.setError(error);
        mError = error;
    }

    public String getError(){
        return mError;
    }
}
