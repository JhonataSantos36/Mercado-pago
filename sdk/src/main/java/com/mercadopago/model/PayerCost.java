package com.mercadopago.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayerCost {

    public static final String CFT = "CFT";
    public static final String TEA = "TEA";
    private Integer installments;
    private BigDecimal installmentRate;
    private List<String> labels;
    private BigDecimal minAllowedAmount;
    private BigDecimal maxAllowedAmount;
    private String recommendedMessage;
    private BigDecimal installmentAmount;
    private BigDecimal totalAmount;

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

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
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

    public String getRecommendedMessage() {
        return recommendedMessage;
    }

    public void setRecommendedMessage(String recommendedMessage) {
        this.recommendedMessage = recommendedMessage;
    }

    public BigDecimal getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(BigDecimal installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTEAPercent() {
        return getRates().get(TEA);
    }

    public String getCFTPercent() {
        return getRates().get(CFT);
    }

    public Map<String, String> getRates() {
        Map<String, String> ratesMap = new HashMap<>();

        if (isValidLabels()){
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
        return labels != null && labels.size()>0;
    }

    @Override
    public String toString() {
        return installments.toString();
    }
}
