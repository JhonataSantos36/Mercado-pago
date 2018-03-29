package com.mercadopago.lite.model;

import android.support.annotation.DrawableRes;

import java.util.List;

public class PaymentMethodSearchItem {

    private static final String TYPE_PAYMENT_METHOD = "payment_method";
    private static final String TYPE_PAYMENT_TYPE = "payment_type";
    private static final String TYPE_GROUP = "group";

    private String id;
    private String type;
    private String description;
    private String comment;
    private List<PaymentMethodSearchItem> children;
    private String childrenHeader;
    private Boolean showIcon;
    @DrawableRes
    private int icon;

    public PaymentMethodSearchItem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<PaymentMethodSearchItem> getChildren() {
        return children;
    }

    public void setChildren(List<PaymentMethodSearchItem> children) {
        this.children = children;
    }

    public String getChildrenHeader() {
        return childrenHeader;
    }

    public boolean hasChildren() {
        return children != null && children.size() != 0;
    }

    public boolean hasDescription() {
        return description != null && !description.isEmpty();
    }

    public boolean isIconRecommended() {
        return showIcon != null ? showIcon : false;
    }

    public boolean hasComment() {
        return comment != null && !comment.isEmpty();
    }

    public boolean isPaymentType() {
        return TYPE_PAYMENT_TYPE.equals(type);
    }

    public boolean isPaymentMethod() {
        return TYPE_PAYMENT_METHOD.equals(type);
    }

    public boolean isGroup() {
        return TYPE_GROUP.equals(type);
    }

    public void setChildrenHeader(String childrenHeader) {
        this.childrenHeader = childrenHeader;
    }

    public Boolean getShowIcon() {
        return showIcon;
    }

    public void setShowIcon(Boolean showIcon) {
        this.showIcon = showIcon;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}