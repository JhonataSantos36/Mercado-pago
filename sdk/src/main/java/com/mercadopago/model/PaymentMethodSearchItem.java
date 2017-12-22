package com.mercadopago.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.plugins.model.PaymentMethodInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mreverter on 15/1/16.
 */

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
    private @DrawableRes int icon;

    public PaymentMethodSearchItem() {

    }

    public PaymentMethodSearchItem(@NonNull final PaymentMethodInfo info) {
        id = info.id;
        type = PaymentTypes.PLUGIN;
        description = info.name;
        comment = info.description;
        children = new ArrayList<>();
        childrenHeader = null;
        showIcon = true;
        icon = info.icon;
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
        return this.childrenHeader;
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
        return type != null && type.equals(TYPE_PAYMENT_TYPE);
    }

    public boolean isPaymentMethod() {
        return type != null && type.equals(TYPE_PAYMENT_METHOD);
    }

    public boolean isGroup() {
        return type != null && type.equals(TYPE_GROUP);
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