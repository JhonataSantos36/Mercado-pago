package com.mercadopago.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mreverter on 16/2/16.
 */
public class Instruction implements Serializable {
    private List<InstructionReference> references;
    private String title;
    private List<String> info;
    private  List<String> secondaryInfo;
    private List<String> tertiaryInfo;
    private String accreditationMessage;
    private List<InstructionActionInfo> actions;

    public List<InstructionReference> getReferences() {
        return references;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getInfo() {
        return info;
    }

    public List<String> getSecondaryInfo() {
        return secondaryInfo;
    }

    public List<String> getTertiaryInfo() {
        return tertiaryInfo;
    }

    public String getAcreditationMessage() {
        return accreditationMessage;
    }

    public List<InstructionActionInfo> getActions() {
        return actions;
    }
}
