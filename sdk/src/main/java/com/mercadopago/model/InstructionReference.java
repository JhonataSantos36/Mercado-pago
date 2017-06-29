package com.mercadopago.model;

import android.text.TextUtils;

import java.util.List;


/**
 * Created by mreverter on 16/2/16.
 */
public class InstructionReference {
    private String label;
    private List<String> fieldValue;
    private String separator;

    public String getLabel() {
        return label;
    }

    public List<String> getFieldValue() {
        return fieldValue;
    }

    public String getSeparator() {
        return separator;
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

    public String getFormattedReference() {
        StringBuilder stringBuilder = new StringBuilder();
        if (fieldValue != null) {
            for (String string : fieldValue) {
                stringBuilder.append(string);
                if (fieldValue.indexOf(string) != fieldValue.size() - 1 && !TextUtils.isEmpty(this.separator)) {
                    stringBuilder.append(this.separator);
                }
            }
        }
        return stringBuilder.toString();
    }

    public boolean hasLabel() {
        return this.getLabel() != null && !this.getLabel().isEmpty();
    }

    public boolean hasValue() {
        return this.getFieldValue() != null && this.getFieldValue().size() != 0;
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
