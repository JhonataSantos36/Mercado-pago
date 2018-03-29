package com.mercadopago.constants;

public class PaymentMethods {

    /**
     * @deprecated ACCOUNT_MONEY is deprecated, will be removed in future releases as part
     * of payment methods plugins implementation.
     */
    @Deprecated
    public static final String ACCOUNT_MONEY = "account_money";

    public class ARGENTINA {
        public static final String VISA = "visa";
        public static final String MASTER = "master";
        public static final String AMEX = "amex";
        public static final String NARANJA = "naranja";
        public static final String NATIVA = "nativa";
        public static final String TARSHOP = "tarshop";
        public static final String CENCOSUD = "cencosud";
        public static final String DINERS = "diners";
        public static final String CABAL = "cabal";
        public static final String ARGENCARD = "argencard";
        public static final String PAGOFAIL = "pagofacil";
        public static final String RAPIPAGO = "rapipago";
        public static final String CARGA_VIRTUAL = "cargavirtual";
        public static final String CORDOBESA = "cordobesa";
        public static final String CMR = "cmr";
        public static final String MERCADOPAGO_CC = "mercadopago_cc";
        public static final String REDLINK = "redlink";
        public static final String BAPROPAGOS = "bapropagos";
        public static final String CORDIAL = "cordial";
        public static final String MAESTRO = "maestro";
        public static final String DEBMASTER = "debmaster";
        public static final String DEBCABAL = "debcabal";
        public static final String DEBVISA = "debvisa";
    }

    public class BRASIL {
        public static final String VISA = "visa";
        public static final String MASTER = "master";
        public static final String AMEX = "amex";
        public static final String HIPERCARD = "hipercard";
        public static final String DINERS = "diners";
        public static final String ELO = "elo";
        public static final String MELICARD = "melicard";
        public static final String BOLBRADESCO = "bolbradesco";
    }

    public class MEXICO {
        public static final String VISA = "visa";
        public static final String MASTER = "master";
        public static final String AMEX = "amex";
        public static final String DEBVISA = "debvisa";
        public static final String DEBMASTER = "debmaster";
        public static final String MERCADOPAGOCARD = "mercadopagocard";
        public static final String BANCOMER = "bancomer";
        public static final String SERFIN = "serfin";
        public static final String BANAMEX = "banamex";
        public static final String OXXO = "oxxo";
    }

    public class VENEZUELA {
        public static final String VISA = "visa";
        public static final String MASTER = "master";
        public static final String MERCANTIL = "mercantil";
        public static final String PROVINCIAL = "provincial";
        public static final String BANESCO = "banesco";
    }

    public class COLOMBIA {
        public static final String VISA = "visa";
        public static final String AMEX = "amex";
        public static final String MASTER = "master";
        public static final String DINERS = "diners";
        public static final String CODENSA = "codensa";
        public static final String EFECTY = "efecty";
        public static final String DAVIVIENDA = "davivienda";
        public static final String PSE = "pse";
    }

    public class PERU {
        public static final String VISA = "visa";
        public static final String DEBVISA = "debvisa";
        public static final String PAGOEFECTIVO_ATM = "pagoefectivo_atm";
    }

    public class CHILE {
        public static final String VISA = "visa";
        public static final String AMEX = "amex";
        public static final String MASTER = "master";
        public static final String MAGNA = "magna";
        public static final String PRESTO = "presto";
        public static final String CMR = "cmr";
        public static final String DINERS = "diners";
        public static final String WEBPAY = "webpay";
        public static final String SERVIPAG = "servipag";
        public static final String KHIPU = "khipu";
    }

    private PaymentMethods(){}
}
