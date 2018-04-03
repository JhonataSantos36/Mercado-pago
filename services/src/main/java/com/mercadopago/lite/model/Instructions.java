package com.mercadopago.lite.model;

import java.util.List;

public class Instructions {

    private AmountInfo amountInfo;

    private List<Instruction> instructions;

    public AmountInfo getAmountInfo() {
        return amountInfo;
    }

    public void setAmountInfo(final AmountInfo amountInfo) {
        this.amountInfo = amountInfo;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(final List<Instruction> instructions) {
        this.instructions = instructions;
    }
}
