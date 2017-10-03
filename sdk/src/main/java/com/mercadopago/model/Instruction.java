package com.mercadopago.model;

import java.util.List;

/**
 * Created by mreverter on 16/2/16.
 */
public class Instruction {
    private List<InstructionReference> references;
    private String title;
    private String subtitle;
    private List<String> info;
    private List<String> secondaryInfo;
    private List<String> tertiaryInfo;
    private String accreditationMessage;
    private List<String> accreditationComments;
    private List<InstructionActionInfo> actions;
    private String type;

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

    public List<InstructionActionInfo> getActions() {
        return actions;
    }

    public String getType() {
        return type;
    }
}
