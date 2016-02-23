package com.mercadopago.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mercadopago.R;
import com.mercadopago.model.Issuer;

import java.util.List;

/**
 * Created by mreverter on 28/12/15.
 */
public class IssuersSpinnerAdapter extends BaseAdapter {

    private List<Issuer> mData;
    private static LayoutInflater mInflater = null;
    private String mError;

    public IssuersSpinnerAdapter(Activity activity, List<Issuer> data) {
        mData = data;
        mError = "";
        Issuer selectionIssuer = new Issuer();
        selectionIssuer.setName(activity.getString(R.string.mpsdk_select_issuer_label));
        data.add(0, selectionIssuer);
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mData.size();
    }

    public Issuer getItem(int position) {
        try {
            Issuer item;
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

        Issuer issuer = mData.get(position);

        TextView label = (TextView) row.findViewById(R.id.label);
        label.setText(issuer.getName());

        return row;
    }

    public void setError(View v, String error) {
        TextView name = (TextView) v.findViewById(R.id.label);
        name.setError(error);
        mError = error;
    }

    public String getError(){
        return mError;
    }
}
