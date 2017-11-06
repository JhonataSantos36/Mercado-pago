package com.mercadopago.lite.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by mromar on 10/20/17.
 */

public class Payment {

    private Boolean binaryMode;
    private String callForAuthorizeId;
    private Boolean captured;
    private Card card;
    private Integer collectorId;
    private BigDecimal couponAmount;
    private String currencyId;
    private Date dateApproved;
    private Date dateCreated;
    private Date dateLastUpdated;
    private String description;
    private Long differentialPricingId;
    private String externalReference;
    private List<FeeDetail> feeDetails;
    private Long id;
    private Integer installments;
    private String issuerId;
    private Boolean liveMode;
    private Map metadata;
    private Date moneyReleaseDate;
    private String notificationUrl;
    private String operationType;
    private Order order;
    private Payer payer;
    private String paymentMethodId;
    private String paymentTypeId;
    private List<Refund> refunds;
    private String statementDescriptor;
    private String status;
    private String statusDetail;
    private BigDecimal transactionAmount;
    private BigDecimal transactionAmountRefunded;
    private TransactionDetails transactionDetails;
    private String tokenId;

    public Boolean getBinaryMode() {
        return binaryMode;
    }

    public void setBinaryMode(Boolean binaryMode) {
        this.binaryMode = binaryMode;
    }

    public String getCallForAuthorizeId() {
        return callForAuthorizeId;
    }

    public void setCallForAuthorizeId(String callForAuthorizeId) {
        this.callForAuthorizeId = callForAuthorizeId;
    }

    public Boolean getCaptured() {
        return captured;
    }

    public void setCaptured(Boolean captured) {
        this.captured = captured;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Integer getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(Integer collectorId) {
        this.collectorId = collectorId;
    }

    public BigDecimal getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(BigDecimal couponAmount) {
        this.couponAmount = couponAmount;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public Date getDateApproved() {
        return dateApproved;
    }

    public void setDateApproved(Date dateApproved) {
        this.dateApproved = dateApproved;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateLastUpdated() {
        return dateLastUpdated;
    }

    public void setDateLastUpdated(Date dateLastUpdated) {
        this.dateLastUpdated = dateLastUpdated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDifferentialPricingId() {
        return differentialPricingId;
    }

    public void setDifferentialPricingId(Long differentialPricingId) {
        this.differentialPricingId = differentialPricingId;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public List<FeeDetail> getFeeDetails() {
        return feeDetails;
    }

    public void setFeeDetails(List<FeeDetail> feeDetails) {
        this.feeDetails = feeDetails;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public String getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(String issuerId) {
        this.issuerId = issuerId;
    }

    public Boolean getLiveMode() {
        return liveMode;
    }

    public void setLiveMode(Boolean liveMode) {
        this.liveMode = liveMode;
    }

    public Map getMetadata() {
        return metadata;
    }

    public void setMetadata(Map metadata) {
        this.metadata = metadata;
    }

    public Date getMoneyReleaseDate() {
        return moneyReleaseDate;
    }

    public void setMoneyReleaseDate(Date moneyReleaseDate) {
        this.moneyReleaseDate = moneyReleaseDate;
    }

    public String getNotificationUrl() {
        return notificationUrl;
    }

    public void setNotificationUrl(String notificationUrl) {
        this.notificationUrl = notificationUrl;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Payer getPayer() {
        return payer;
    }

    public void setPayer(Payer payer) {
        this.payer = payer;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(String paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public List<Refund> getRefunds() {
        return refunds;
    }

    public void setRefunds(List<Refund> refunds) {
        this.refunds = refunds;
    }

    public String getStatementDescriptor() {
        return statementDescriptor;
    }

    public void setStatementDescriptor(String statementDescriptor) {
        this.statementDescriptor = statementDescriptor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public BigDecimal getTransactionAmountRefunded() {
        return transactionAmountRefunded;
    }

    public void setTransactionAmountRefunded(BigDecimal transactionAmountRefunded) {
        this.transactionAmountRefunded = transactionAmountRefunded;
    }

    public TransactionDetails getTransactionDetails() {
        return transactionDetails;
    }

    public void setTransactionDetails(TransactionDetails transactionDetails) {
        this.transactionDetails = transactionDetails;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public class Status {
        public static final String APPROVED = "approved";
        public static final String IN_PROCESS = "in_process";
        public static final String REJECTED = "rejected";
        public static final String PENDING = "pending";
    }

    public class StatusDetail {
        public static final String INVALID_ESC = "invalid_esc";
        public static final String ACCREDITED = "accredited";
        public static final String REJECTED_CALL_FOR_AUTHORIZE = "cc_rejected_call_for_authorize";
        public static final String PENDING_CONTINGENCY = "pending_contingency";
        public static final String PENDING_REVIEW_MANUAL = "pending_review_manual";
        public static final String PENDING_WAITING_PAYMENT = "pending_waiting_payment";
        public static final String REJECTED_OTHER_REASON = "cc_rejected_other_reason";
        public static final String REJECTED_BAD_FILLED_OTHER = "cc_rejected_bad_filled_other";
        public static final String REJECTED_BAD_FILLED_CARD_NUMBER = "cc_rejected_bad_filled_card_number";
        public static final String REJECTED_BAD_FILLED_SECURITY_CODE = "cc_rejected_bad_filled_security_code";
        public static final String REJECTED_BAD_FILLED_DATE = "cc_rejected_bad_filled_date";
        public static final String REJECTED_HIGH_RISK = "rejected_high_risk";
        public static final String REJECTED_INSUFFICIENT_AMOUNT = "cc_rejected_insufficient_amount";
        public static final String REJECTED_MAX_ATTEMPTS = "cc_rejected_max_attempts";
        public static final String REJECTED_DUPLICATED_PAYMENT = "cc_rejected_duplicated_payment";
        public static final String REJECTED_CARD_DISABLED = "cc_rejected_card_disabled";
        public static final String REJECTED_REJECTED_BY_BANK = "rejected_by_bank";
        public static final String REJECTED_REJECTED_INSUFFICIENT_DATA = "rejected_insufficient_data";
    }
}
