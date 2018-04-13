package com.mercadopago.testcheckout.input;


import java.util.ArrayList;
import java.util.List;

public abstract class FakeCard extends Card {

    public FakeCard(CardState cardState, String cardNumber) {
        super("123", cardState.toString(), cardNumber, "11234567", "1121");
    }

    public enum CardState {
        APRO,
        CONT,
        CALL,
        FUND,
        SECU,
        EXPI,
        FORM,
        OTHE;

        public static List<CardState> all() {
            List<CardState> states = new ArrayList<>();
            states.add(APRO);
            states.add(CONT);
            states.add(CALL);
            states.add(FUND);
            states.add(SECU);
            states.add(EXPI);
            states.add(FORM);
            states.add(OTHE);
            return states;
        }
    }
}
