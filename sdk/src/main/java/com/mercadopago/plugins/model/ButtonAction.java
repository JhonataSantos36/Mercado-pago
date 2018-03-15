package com.mercadopago.plugins.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.mercadopago.components.Action;

public class ButtonAction extends Action implements Parcelable {

    private final String name;
    private final int resCode;

    public ButtonAction(@NonNull final String name, final int resCode) {
        this.name = name;
        this.resCode = resCode;
    }

    public String getName() {
        return name;
    }

    public int getResCode() {
        return resCode;
    }

    protected ButtonAction(Parcel in) {
        name = in.readString();
        resCode = in.readInt();
    }

    public static final Creator<ButtonAction> CREATOR = new Creator<ButtonAction>() {
        @Override
        public ButtonAction createFromParcel(Parcel in) {
            return new ButtonAction(in);
        }

        @Override
        public ButtonAction[] newArray(int size) {
            return new ButtonAction[size];
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
}
