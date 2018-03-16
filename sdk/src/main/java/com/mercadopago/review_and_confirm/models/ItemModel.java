package com.mercadopago.review_and_confirm.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.mercadopago.paymentresult.formatter.BodyAmountFormatter;
import com.mercadopago.util.TextUtils;

import java.math.BigDecimal;

public class ItemModel implements Parcelable {

    public final String imageUrl;
    public final String title;
    public final String subtitle;
    public final Integer quantity;
    @VisibleForTesting
    public final String currencyId;
    @VisibleForTesting
    public final String unitPrice;


    public ItemModel(final String imageUrl,
                     final String title,
                     @Nullable final String subtitle,
                     final Integer quantity,
                     final String currencyId,
                     @Nullable final BigDecimal unitPrice) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.subtitle = subtitle;
        this.quantity = quantity;
        this.currencyId = currencyId;
        this.unitPrice = unitPrice == null ? "" : unitPrice.toString();
    }


    public boolean hasToShowQuantity() {
        return !Integer.valueOf(1).equals(quantity);
    }

    public boolean hasToShowPrice() {
        return TextUtils.isNotEmpty(unitPrice);
    }

    public String getPrice() {
        return new BodyAmountFormatter(currencyId, new BigDecimal(unitPrice)).formatNumber(new BigDecimal(unitPrice));
    }

    protected ItemModel(Parcel in) {
        imageUrl = in.readString();
        title = in.readString();
        subtitle = in.readString();
        if (in.readByte() == 0) {
            quantity = null;
        } else {
            quantity = in.readInt();
        }
        currencyId = in.readString();
        unitPrice = in.readString();
    }

    public static final Creator<ItemModel> CREATOR = new Creator<ItemModel>() {
        @Override
        public ItemModel createFromParcel(Parcel in) {
            return new ItemModel(in);
        }

        @Override
        public ItemModel[] newArray(int size) {
            return new ItemModel[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(imageUrl);
        dest.writeString(title);
        dest.writeString(subtitle);
        if (quantity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(quantity);
        }
        dest.writeString(currencyId);
        dest.writeString(unitPrice);
    }
}
