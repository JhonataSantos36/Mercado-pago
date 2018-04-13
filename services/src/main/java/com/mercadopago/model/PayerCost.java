package com.mercadopago.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.VisibleForTesting;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayerCost implements Parcelable {

    private static final String CFT = "CFT";
    private static final String TEA = "TEA";
    private Integer installments;
    private List<String> labels;
    private String recommendedMessage;
    private BigDecimal installmentRate;
    private BigDecimal totalAmount;
    private BigDecimal minAllowedAmount;
    private BigDecimal maxAllowedAmount;
    private BigDecimal installmentAmount;

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public BigDecimal getInstallmentRate() {
        return installmentRate;
    }

    public void setInstallmentRate(BigDecimal installmentRate) {
        this.installmentRate = installmentRate;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public BigDecimal getInstallmentAmount() {
        return installmentAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getTEAPercent() {
        return getRates().get(TEA);
    }

    public String getCFTPercent() {
        return getRates().get(CFT);
    }

    public Map<String, String> getRates() {
        Map<String, String> ratesMap = new HashMap<>();

        if (isValidLabels()) {
            for (String label : labels) {
                if (label.contains(CFT) || label.contains(TEA)) {
                    String[] ratesRaw = label.split("\\|");
                    for (String rate : ratesRaw) {
                        String[] rates = rate.split("_");
                        ratesMap.put(rates[0], rates[1]);
                    }
                }
            }
        }
        return ratesMap;
    }

    public Boolean hasRates() {
        return hasTEA() && hasCFT();
    }

    public Boolean hasCFT() {
        return getCFTPercent() != null;
    }

    public Boolean hasTEA() {
        return getTEAPercent() != null;
    }

    private Boolean isValidLabels() {
        return labels != null && labels.size() > 0;
    }

    public boolean hasMultipleInstallments() {
        return installments != null && installments > 1;
    }

    @Override
    public String toString() {
        return installments.toString();
    }

    public static final Creator<PayerCost> CREATOR = new Creator<PayerCost>() {
        @Override
        public PayerCost createFromParcel(Parcel in) {
            return new PayerCost(in);
        }

        @Override
        public PayerCost[] newArray(int size) {
            return new PayerCost[size];
        }
    };

    @VisibleForTesting
    public PayerCost() {
    }

    protected PayerCost(Parcel in) {
        if (in.readByte() == 0) {
            installments = null;
        } else {
            installments = in.readInt();
        }
        labels = in.createStringArrayList();
        recommendedMessage = in.readString();
        installmentRate = new BigDecimal(in.readString());
        totalAmount = new BigDecimal(in.readString());
        minAllowedAmount = new BigDecimal(in.readString());
        maxAllowedAmount = new BigDecimal(in.readString());
        installmentAmount = new BigDecimal(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        if (installments == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(installments);
        }
        dest.writeStringList(labels);
        dest.writeString(recommendedMessage);

        dest.writeString(installmentRate.toString());
        dest.writeString(totalAmount.toString());
        dest.writeString(minAllowedAmount.toString());
        dest.writeString(maxAllowedAmount.toString());
        dest.writeString(installmentAmount.toString());
    }
}
