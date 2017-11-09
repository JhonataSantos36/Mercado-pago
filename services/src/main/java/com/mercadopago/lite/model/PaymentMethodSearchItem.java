package com.mercadopago.lite.model;
import java.util.List;

/**
 * Created by mromar on 10/20/17.
 */

public class PaymentMethodSearchItem {

    private String id;
    private String type;
    private String description;
    private String comment;
    private List<PaymentMethodSearchItem> children;
    private String childrenHeader;
    private Boolean showIcon;

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

    public void setChildrenHeader(String childrenHeader) {
        this.childrenHeader = childrenHeader;
    }

    public Boolean getShowIcon() {
        return showIcon;
    }

    public void setShowIcon(Boolean showIcon) {
        this.showIcon = showIcon;
    }
}
