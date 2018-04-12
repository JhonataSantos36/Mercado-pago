package com.mercadopago.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FinancialInstitution implements Parcelable {

    private String id;
    private String description;

    protected FinancialInstitution(Parcel in) {
        id = in.readString();
        description = in.readString();
    }

    public static final Creator<FinancialInstitution> CREATOR = new Creator<FinancialInstitution>() {
        @Override
        public FinancialInstitution createFromParcel(Parcel in) {
            return new FinancialInstitution(in);
        }

        @Override
        public FinancialInstitution[] newArray(int size) {
            return new FinancialInstitution[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(id);
        dest.writeString(description);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
