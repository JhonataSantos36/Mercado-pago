package com.mercadopago.lite.model;

import java.util.List;

public class Instruction {
    private String title;
    private String subtitle;
    private List<String> info;
    private List<String> secondaryInfo;
    private List<String> tertiaryInfo;
    private String accreditationMessage;
    private String type;
    private List<String> accreditationComments;
    private List<InstructionAction> actions;
    private List<InstructionReference> references;

    public List<InstructionReference> getReferences() {
        return references;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
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

    public List<String> getAccreditationComments() {
        return accreditationComments;
    }

    public List<InstructionAction> getActions() {
        return actions;
    }

    public String getType() {
        return type;
    }
}
