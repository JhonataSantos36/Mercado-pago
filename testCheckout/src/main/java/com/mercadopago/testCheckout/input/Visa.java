package com.mercadopago.testCheckout.input;


public class Visa extends FakeCard {

    public Visa(final CardState cardState, final Country country) {
        super(cardState, getNumber(country));
    }

    private static String getNumber(Country country) {
        switch (country) {
            case ARGENTINA:
                return "4509953566233704";
            case BRASIL:
                return "4235647728025682";
            case CHILE:
                return "4168818844447115";
            case COLOMBIA:
                return "4013540682746260";
            case MEXICO:
                return "4075595716483764";
            case PERU:
                return "4009175332806176";
            case URUGUAY:
                return "4014682387532428";
            case VENEZUELA:
                return "4966382331109310";
            default:
                return null;
        }
    }
}
