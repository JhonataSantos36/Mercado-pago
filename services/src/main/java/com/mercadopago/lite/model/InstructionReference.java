package com.mercadopago.lite.model;
import java.util.List;

/**
 * Created by mromar on 10/20/17.
 */

public class InstructionReference {

    private String label;
    private List<String> fieldValue;
    private String separator;
    private String comment;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(List<String> fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
