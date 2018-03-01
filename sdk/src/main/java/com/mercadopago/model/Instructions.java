package com.mercadopago.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mreverter on 3/8/16.
 */
public class Instructions {
    private List<Instruction> instructions;

    public List<Instruction> getInstructions() {
        return instructions;
    }

    private class AmountInfo {
        private BigDecimal amount;
        private String symbol;
        private int decimalPlaces;
        private Character decimalSeparator;
        private Character thousandsSeparator;

        public BigDecimal getAmount() {
            return amount;
        }

        public String getSymbol() {
            return symbol;
        }

        public int getDecimalPlaces() {
            return decimalPlaces;
        }

        public Character getDecimalSeparator() {
            return decimalSeparator;
        }

        public Character getThousandsSeparator() {
            return thousandsSeparator;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public void setDecimalPlaces(int decimalPlaces) {
            this.decimalPlaces = decimalPlaces;
        }

        public void setDecimalSeparator(Character decimalSeparator) {
            this.decimalSeparator = decimalSeparator;
        }

        public void setThousandsSeparator(Character thousandsSeparator) {
            this.thousandsSeparator = thousandsSeparator;
        }
    }
}
