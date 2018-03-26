package com.mercadopago.plugins.model;


import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.mercadopago.components.Action;

public class ExitAction extends Action implements Parcelable {

    private final String name;
    private final int resCode;

    public static final String EXTRA_CLIENT_RES_CODE = "extra_res_code";

    public ExitAction(@NonNull final String name, final int resCode) {
        this.name = name;
        this.resCode = resCode;
    }

    public String getName() {
        return name;
    }

    protected ExitAction(Parcel in) {
        name = in.readString();
        resCode = in.readInt();
    }

    public static final Creator<ExitAction> CREATOR = new Creator<ExitAction>() {
        @Override
        public ExitAction createFromParcel(Parcel in) {
            return new ExitAction(in);
        }

        @Override
        public ExitAction[] newArray(int size) {
            return new ExitAction[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(name);
        dest.writeInt(resCode);
    }

    public Intent toIntent() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CLIENT_RES_CODE, resCode);
        return intent;
    }
}
