package com.mercadopago.model;

import java.util.List;

public class Issuer {

    private Long id;
    private String name;

    private List<String> labels;

    public Issuer() {
    }

    public Issuer(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(final List<String> labels) {
        this.labels = labels;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
