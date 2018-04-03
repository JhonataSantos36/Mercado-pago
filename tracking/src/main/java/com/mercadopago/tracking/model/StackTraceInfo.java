package com.mercadopago.tracking.model;

/**
 * Created by vaserber on 6/5/17.
 */

public class StackTraceInfo {

    private String file;
    private Integer lineNumber;
    private Integer columnNumber;
    private String method;

    public StackTraceInfo(String file, Integer lineNumber, Integer columnNumber, String method) {
        this.file = file;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.method = method;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Integer getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(Integer columnNumber) {
        this.columnNumber = columnNumber;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
