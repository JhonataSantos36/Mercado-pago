package com.mercadopago.testcheckout.input;


public class Visa extends FakeCard {

    public Visa(final CardState cardState, final Country country) {
        super(cardState, getNumber(country));
    }

    private static String getNumber(Country country) {
        String cardNumber = null;

        switch (country) {
            case ARGENTINA:
                cardNumber = "4509953566233704";
                break;
            case BRASIL:
                cardNumber = "4235647728025682";
                break;
            case CHILE:
                cardNumber = "4168818844447115";
                break;
            case COLOMBIA:
                cardNumber = "4013540682746260";
                break;
            case MEXICO:
                cardNumber = "4075595716483764";
                break;
            case PERU:
                cardNumber = "4009175332806176";
                break;
            case URUGUAY:
                cardNumber = "4014682387532428";
                break;
            case VENEZUELA:
                cardNumber = "4966382331109310";
                break;
            default:
                cardNumber = null;
                break;
        }

        return cardNumber;
    }
}
