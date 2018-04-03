package com.mercadopago.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.mercadopago.R;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.plugins.model.PaymentMethodInfo;

import java.math.BigDecimal;
import java.util.List;

public class PaymentMethod {

    private List<String> additionalInfoNeeded;
    private String id;
    private String name;
    private String paymentTypeId;
    private String status;
    private String secureThumbnail;
    private String deferredCapture;
    private List<Setting> settings;
    private BigDecimal minAllowedAmount;
    private BigDecimal maxAllowedAmount;
    private Integer accreditationTime;
    private String merchantAccountId;
    @DrawableRes private Integer icon = R.drawable.mpsdk_none;

    public PaymentMethod() {

    }

    public PaymentMethod(@NonNull final PaymentMethodInfo info) {
        id = info.id;
        name = info.name;
        paymentTypeId = PaymentTypes.PLUGIN;
    }

    public List<String> getAdditionalInfoNeeded() {
        return additionalInfoNeeded;
    }

    public void setAdditionalInfoNeeded(List<String> additionalInfoNeeded) {
        this.additionalInfoNeeded = additionalInfoNeeded;
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

    @Override
    public String toString() {
        return name;
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

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
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
}
