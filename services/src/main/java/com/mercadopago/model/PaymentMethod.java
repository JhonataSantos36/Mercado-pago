package com.mercadopago.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.VisibleForTesting;

import java.math.BigDecimal;
import java.util.List;

public class PaymentMethod implements Parcelable {

    private String id;
    private String name;
    private String paymentTypeId;
    private String status;
    private String secureThumbnail;
    private String deferredCapture;
    private Integer accreditationTime;
    private String merchantAccountId;
    private List<Setting> settings;
    private List<String> additionalInfoNeeded;
    private List<FinancialInstitution> financialInstitutions;
    private BigDecimal minAllowedAmount;
    private BigDecimal maxAllowedAmount;

    /**
     * Constructor for custom payment methods like plugin implementation
     *
     * @param id            paymentId
     * @param name          paymentName
     * @param paymentTypeId paymentTypeId
     */
    public PaymentMethod(final String id, final String name, final String paymentTypeId) {
        this.id = id;
        this.name = name;
        this.paymentTypeId = paymentTypeId;
    }

    /**
     * Constructor to make exclusions
     *
     * @param id paymentId
     */
    public PaymentMethod(final String id) {
        this.id = id;
    }

    @VisibleForTesting
    public PaymentMethod() {
    }

    public boolean isIssuerRequired() {
        return isAdditionalInfoNeeded("issuer_id");
    }

    public boolean isSecurityCodeRequired(String bin) {
        Setting setting = Setting.getSettingByBin(settings, bin);
        return (setting != null) && (setting.getSecurityCode() != null) &&
                (setting.getSecurityCode().getLength() != 0);
    }

    public boolean isIdentificationTypeRequired() {
        return isAdditionalInfoNeeded("cardholder_identification_type");
    }

    public boolean isIdentificationNumberRequired() {
        return isAdditionalInfoNeeded("cardholder_identification_number");
    }

    public List<String> getAdditionalInfoNeeded() {
        return additionalInfoNeeded;
    }

    public void setAdditionalInfoNeeded(List<String> additionalInfoNeeded) {
        this.additionalInfoNeeded = additionalInfoNeeded;
    }

    public List<FinancialInstitution> getFinancialInstitutions() {
        return financialInstitutions;
    }

    public void setFinancialInstitutions(final List<FinancialInstitution> financialInstitutions) {
        this.financialInstitutions = financialInstitutions;
    }

    private boolean isAdditionalInfoNeeded(String param) {

        if ((additionalInfoNeeded != null) && (additionalInfoNeeded.size() > 0)) {
            for (int i = 0; i < additionalInfoNeeded.size(); i++) {
                if (additionalInfoNeeded.get(i).equals(param)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isValidForBin(String bin) {
        return (Setting.getSettingByBin(getSettings(), bin) != null);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSecureThumbnail() {
        return secureThumbnail;
    }

    public void setSecureThumbnail(String secureThumbnail) {
        this.secureThumbnail = secureThumbnail;
    }


    public String getDeferredCapture() {
        return deferredCapture;
    }

    public void setDeferredCapture(String deferredCapture) {
        this.deferredCapture = deferredCapture;
    }

    public BigDecimal getMinAllowedAmount() {
        return minAllowedAmount;
    }

    public void setMinAllowedAmount(BigDecimal minAllowedAmount) {
        this.minAllowedAmount = minAllowedAmount;
    }

    public BigDecimal getMaxAllowedAmount() {
        return maxAllowedAmount;
    }

    public void setMaxAllowedAmount(BigDecimal maxAllowedAmount) {
        this.maxAllowedAmount = maxAllowedAmount;
    }

    public void setAccreditationTime(Integer accreditationTime) {
        this.accreditationTime = accreditationTime;
    }

    public Integer getAccreditationTime() {
        return accreditationTime;
    }

    public String getMerchantAccountId() {
        return merchantAccountId;
    }

    public void setMerchantAccountId(String merchantAccountId) {
        this.merchantAccountId = merchantAccountId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(String paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public List<Setting> getSettings() {
        return settings;
    }

    public void setSettings(List<Setting> settings) {
        this.settings = settings;
    }

    protected PaymentMethod(Parcel in) {
        additionalInfoNeeded = in.createStringArrayList();
        id = in.readString();
        name = in.readString();
        paymentTypeId = in.readString();
        status = in.readString();
        secureThumbnail = in.readString();
        deferredCapture = in.readString();
        settings = in.createTypedArrayList(Setting.CREATOR);
        if (in.readByte() == 0) {
            accreditationTime = null;
        } else {
            accreditationTime = in.readInt();
        }
        merchantAccountId = in.readString();
        financialInstitutions = in.createTypedArrayList(FinancialInstitution.CREATOR);
        minAllowedAmount = new BigDecimal(in.readString());
        maxAllowedAmount = new BigDecimal(in.readString());
    }

    public static final Creator<PaymentMethod> CREATOR = new Creator<PaymentMethod>() {
        @Override
        public PaymentMethod createFromParcel(Parcel in) {
            return new PaymentMethod(in);
        }

        @Override
        public PaymentMethod[] newArray(int size) {
            return new PaymentMethod[size];
        }
    };

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeStringList(additionalInfoNeeded);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(paymentTypeId);
        dest.writeString(status);
        dest.writeString(secureThumbnail);
        dest.writeString(deferredCapture);
        dest.writeTypedList(settings);
        if (accreditationTime == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(accreditationTime);
        }
        dest.writeString(merchantAccountId);
        dest.writeTypedList(financialInstitutions);
        dest.writeString(minAllowedAmount.toString());
        dest.writeString(maxAllowedAmount.toString());
    }
}
