package com.mercadopago.lite.model;
import java.util.List;

/**
 * Created by mromar on 10/20/17.
 */

public class Instruction {

    private String title;
    private String subtitle;
    private String accreditationMessage;
    private List<String> accreditationComments;
    private List<InstructionAction> actions;
    private String type;
    private List<InstructionReference> references;
    private List<String> info;
    private List<String> secondaryInfo;
    private List<String> tertiaryInfo;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getAccreditationMessage() {
        return accreditationMessage;
    }

    public void setAccreditationMessage(String accreditationMessage) {
        this.accreditationMessage = accreditationMessage;
    }

    public List<String> getAccreditationComments() {
        return accreditationComments;
    }

    public void setAccreditationComments(List<String> accreditationComments) {
        this.accreditationComments = accreditationComments;
    }

    public List<InstructionAction> getActions() {
        return actions;
    }

    public void setActions(List<InstructionAction> actions) {
        this.actions = actions;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<InstructionReference> getReferences() {
        return references;
    }

    public void setReferences(List<InstructionReference> references) {
        this.references = references;
    }

    public List<String> getInfo() {
        return info;
    }

    public void setInfo(List<String> info) {
        this.info = info;
    }

    public List<String> getSecondaryInfo() {
        return secondaryInfo;
    }

    public void setSecondaryInfo(List<String> secondaryInfo) {
        this.secondaryInfo = secondaryInfo;
    }

    public List<String> getTertiaryInfo() {
        return tertiaryInfo;
    }

    public void setTertiaryInfo(List<String> tertiaryInfo) {
        this.tertiaryInfo = tertiaryInfo;
    }
}
