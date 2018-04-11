package com.mercadopago.model;

import android.text.TextUtils;

import java.util.List;

public class InstructionReference {
    private String label;
    private List<String> fieldValue;
    private String separator;
    private String comment;

    public String getLabel() {
        return label;
    }

    public List<String> getFieldValue() {
        return fieldValue;
    }

    public String getSeparator() {
        return separator;
    }

    public String getComment() {
        return comment;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setFieldValue(List<String> fieldValue) {
        this.fieldValue = fieldValue;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFormattedReference() {
        StringBuilder stringBuilder = new StringBuilder();
        if (fieldValue != null) {
            for (String string : fieldValue) {
                stringBuilder.append(string);
                if (fieldValue.indexOf(string) != fieldValue.size() - 1 && !TextUtils.isEmpty(separator)) {
                    stringBuilder.append(separator);
                }
            }
        }
        return stringBuilder.toString();
    }

    public boolean hasLabel() {
        return getLabel() != null && !getLabel().isEmpty();
    }

    public boolean hasValue() {
        return getFieldValue() != null && getFieldValue().size() != 0;
    }

    public boolean hasComment() {
        return comment != null && !comment.isEmpty();
    }

    public boolean isNumericReference() {
        for (String text : fieldValue) {
            text = text.replace(":", "");
            text = text.replace("-", "");

            if (!TextUtils.isDigitsOnly(text)) {
                return false;
            }
        }
        return true;
    }
}
