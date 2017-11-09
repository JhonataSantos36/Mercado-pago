package com.mercadopago.lite.model;
import java.math.BigDecimal;

/**
 * Created by mromar on 10/20/17.
 */

public class TransactionDetails {

    private String externalResourceUrl;
    private String financialInstitution;
    private BigDecimal installmentAmount;
    private BigDecimal netReceivedAmount;
    private BigDecimal overpaidAmount;
    private BigDecimal totalPaidAmount;
    private String paymentMethodReferenceId;

    public String getExternalResourceUrl() {
        return externalResourceUrl;
    }

    public void setExternalResourceUrl(String externalResourceUrl) {
        this.externalResourceUrl = externalResourceUrl;
    }

    public String getFinancialInstitution() {
        return financialInstitution;
    }

    public void setFinancialInstitution(String financialInstitution) {
        this.financialInstitution = financialInstitution;
    }

    public BigDecimal getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(BigDecimal installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public BigDecimal getNetReceivedAmount() {
        return netReceivedAmount;
    }

    public void setNetReceivedAmount(BigDecimal netReceivedAmount) {
        this.netReceivedAmount = netReceivedAmount;
    }

    public BigDecimal getOverpaidAmount() {
        return overpaidAmount;
    }

    public void setOverpaidAmount(BigDecimal overpaidAmount) {
        this.overpaidAmount = overpaidAmount;
    }

    public BigDecimal getTotalPaidAmount() {
        return totalPaidAmount;
    }

    public void setTotalPaidAmount(BigDecimal totalPaidAmount) {
        this.totalPaidAmount = totalPaidAmount;
    }

    public String getPaymentMethodReferenceId() {
        return paymentMethodReferenceId;
    }

    public void setPaymentMethodReferenceId(String paymentMethodReferenceId) {
        this.paymentMethodReferenceId = paymentMethodReferenceId;
    }
}
