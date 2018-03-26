package com.mercadopago.model;

public class IdentificationType {

    private String id;
    private String name;
    private String type;
    private Integer minLength;
    private Integer maxLength;

    public IdentificationType() {
    }

    public IdentificationType(String id, String name, String type,
                              Integer minLength, Integer maxLength) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public String getId() {
        return id;
    }

    public void setId(String Id) {
        id = Id;
    }

    public String getName() {
        return name;
    }

    public void setName(String Name) {
        name = Name;
    }

    public String getType() {
        return type;
    }

    public void setType(String Type) {
        type = Type;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer MinLength) {
        minLength = MinLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer MaxLength) {
        maxLength = MaxLength;
    }
}
