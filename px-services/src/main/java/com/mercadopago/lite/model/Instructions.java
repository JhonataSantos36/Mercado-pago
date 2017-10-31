package com.mercadopago.lite.model;
import java.util.List;

/**
 * Created by mromar on 10/20/17.
 */

public class Instructions {

    private AmountInfo amountInfo;
    private List<Instruction> instructions;

    public AmountInfo getAmountInfo() {
        return amountInfo;
    }

    public void setAmountInfo(AmountInfo amountInfo) {
        this.amountInfo = amountInfo;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<Instruction> instructions) {
        this.instructions = instructions;
    }
}
