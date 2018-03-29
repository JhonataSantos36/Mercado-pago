package com.mercadopago.lite.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Campaign {

    private Long id;
    private String code;
    private String name;
    private String discountType;
    private BigDecimal value;
    private Date endDate;
    private BigDecimal minPaymentAmount;
    private BigDecimal maxPaymentAmount;
    private BigDecimal maxCouponAmount;
    private BigDecimal totalAmountLimit;
    private Long maxCoupons;
    private Integer maxCouponsByCode;
    private Integer maxRedeemPerUser;
    private String siteId;
    private String marketplace;
    private String codeType;
    private BigDecimal maxUserAmountPerCampaign;
    private List<String> labels;
    private List<String> paymentMethods;
    private List<String> paymentTypes;
    private List<String> cardIssuers;
    private List<String> shippingModes;
    private Long clientId;
    private List<String> tags;
    private Integer multipleCodeLimit;
    private Integer codeCount;
    private BigDecimal couponAmount;
    private List<Long> collectors;

    private static final String CODE_TYPE_SINGLE = "single";
    private static final String CODE_TYPE_NONE = "none";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getMinPaymentAmount() {
        return minPaymentAmount;
    }

    public void setMinPaymentAmount(BigDecimal minPaymentAmount) {
        this.minPaymentAmount = minPaymentAmount;
    }

    public BigDecimal getMaxPaymentAmount() {
        return maxPaymentAmount;
    }

    public void setMaxPaymentAmount(BigDecimal maxPaymentAmount) {
        this.maxPaymentAmount = maxPaymentAmount;
    }

    public BigDecimal getMaxCouponAmount() {
        return maxCouponAmount;
    }

    public void setMaxCouponAmount(BigDecimal maxCouponAmount) {
        this.maxCouponAmount = maxCouponAmount;
    }

    public BigDecimal getTotalAmountLimit() {
        return totalAmountLimit;
    }

    public void setTotalAmountLimit(BigDecimal totalAmountLimit) {
        this.totalAmountLimit = totalAmountLimit;
    }

    public Long getMaxCoupons() {
        return maxCoupons;
    }

    public void setMaxCoupons(Long maxCoupons) {
        this.maxCoupons = maxCoupons;
    }

    public Integer getMaxCouponsByCode() {
        return maxCouponsByCode;
    }

    public void setMaxCouponsByCode(Integer maxCouponsByCode) {
        this.maxCouponsByCode = maxCouponsByCode;
    }

    public Integer getMaxRedeemPerUser() {
        return maxRedeemPerUser;
    }

    public void setMaxRedeemPerUser(Integer maxRedeemPerUser) {
        this.maxRedeemPerUser = maxRedeemPerUser;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(String marketplace) {
        this.marketplace = marketplace;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public BigDecimal getMaxUserAmountPerCampaign() {
        return maxUserAmountPerCampaign;
    }

    public void setMaxUserAmountPerCampaign(BigDecimal maxUserAmountPerCampaign) {
        this.maxUserAmountPerCampaign = maxUserAmountPerCampaign;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<String> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<String> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public List<String> getPaymentTypes() {
        return paymentTypes;
    }

    public void setPaymentTypes(List<String> paymentTypes) {
        this.paymentTypes = paymentTypes;
    }

    public List<String> getCardIssuers() {
        return cardIssuers;
    }

    public void setCardIssuers(List<String> cardIssuers) {
        this.cardIssuers = cardIssuers;
    }

    public List<String> getShippingModes() {
        return shippingModes;
    }

    public void setShippingModes(List<String> shippingModes) {
        this.shippingModes = shippingModes;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getMultipleCodeLimit() {
        return multipleCodeLimit;
    }

    public void setMultipleCodeLimit(Integer multipleCodeLimit) {
        this.multipleCodeLimit = multipleCodeLimit;
    }

    public Integer getCodeCount() {
        return codeCount;
    }

    public void setCodeCount(Integer codeCount) {
        this.codeCount = codeCount;
    }

    public BigDecimal getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(BigDecimal couponAmount) {
        this.couponAmount = couponAmount;
    }

    public List<Long> getCollectors() {
        return collectors;
    }

    public void setCollectors(List<Long> collectors) {
        this.collectors = collectors;
    }

    public Boolean isCodeDiscountCampaign() {
        return codeType.contains(CODE_TYPE_SINGLE);
    }

    public Boolean isDirectDiscountCampaign() {
        return codeType.contains(CODE_TYPE_NONE);
    }
}
