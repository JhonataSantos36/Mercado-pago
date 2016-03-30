package com.mercadopago.test;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.mercadopago.PaymentVaultActivity;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.CheckoutPreference;
import com.mercadopago.model.Customer;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Instruction;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodRow;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.util.JsonUtil;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

public class StaticMock {

    // * Merchant public key
    public static final String DUMMY_MERCHANT_PUBLIC_KEY = "444a9ef5-8a6b-429f-abdf-587639155d88";
    public static final String DUMMY_MX_MERCHANT_PUBLIC_KEY = "6c0d81bc-99c1-4de8-9976-c8d1d62cd4f2";

    // DUMMY_MERCHANT_PUBLIC_KEY_AR = "444a9ef5-8a6b-429f-abdf-587639155d88";
    // DUMMY_MERCHANT_PUBLIC_KEY_BR = "561ebf1c-d45a-4b44-99f5-cb5311697a60";
    // DUMMY_MERCHANT_PUBLIC_KEY_MX = "6c0d81bc-99c1-4de8-9976-c8d1d62cd4f2";
    // DUMMY_MERCHANT_PUBLIC_KEY_VZ = "ba25c9fc-863b-4100-a122-99d458df9ddc";

    // * Merchant server vars
    public static final String DUMMY_MERCHANT_BASE_URL = "https://www.mercadopago.com";
    public static final String DUMMY_MERCHANT_GET_CUSTOMER_URI = "/checkout/examples/getCustomer";
    public static final String DUMMY_MERCHANT_CREATE_PAYMENT_URI = "/checkout/examples/doPayment";
    public static final String DUMMY_MERCHANT_GET_DISCOUNT_URI = "/checkout/examples/getDiscounts";

    // * Merchant access token
    public static final String DUMMY_MERCHANT_ACCESS_TOKEN = "mla-cards-data";
    // DUMMY_MERCHANT_ACCESS_TOKEN_AR = "mla-cards-data";
    // DUMMY_MERCHANT_ACCESS_TOKEN_BR = "mlb-cards-data";
    // DUMMY_MERCHANT_ACCESS_TOKEN_MX = "mlm-cards-data";
    // DUMMY_MERCHANT_ACCESS_TOKEN_VZ = "mlv-cards-data";
    // DUMMY_MERCHANT_ACCESS_TOKEN_NO_CCV = "mla-cards-data-tarshop";

    // * Card token
    public final static String DUMMY_CARD_NUMBER = "4444444444440008";
    public final static String DUMMY_CARD_NUMBER_MASTER = "5444444444440008";
    public final static String DUMMY_CARDHOLDER_NAME = "john";
    public final static int DUMMY_EXPIRATION_MONTH = 11;
    public final static int DUMMY_EXPIRATION_YEAR_SHORT = 18;
    public final static int DUMMY_EXPIRATION_YEAR_LONG = 2018;
    public final static String DUMMY_IDENTIFICATION_NUMBER = "12345678";
    public final static String DUMMY_IDENTIFICATION_TYPE = "DNI";
    public final static String DUMMY_SECURITY_CODE = "123";

    // * Identification type
    public final static String DUMMI_IDENTIFICATION_TYPE_ID = "DNI";
    public final static String DUMMI_IDENTIFICATION_TYPE_NAME = "DNI";
    public final static String DUMMI_IDENTIFICATION_TYPE_TYPE = "number";
    public final static Integer DUMMI_IDENTIFICATION_TYPE_MIN_LENGTH = 7;
    public final static Integer DUMMI_IDENTIFICATION_TYPE_MAX_LENGTH = 8;

    // * Saved cards token
    public final static String DUMMY_CARD_ID = "11";

    public static CardToken getCardToken() {

        return new CardToken(DUMMY_CARD_NUMBER, DUMMY_EXPIRATION_MONTH,
                DUMMY_EXPIRATION_YEAR_SHORT, DUMMY_SECURITY_CODE, DUMMY_CARDHOLDER_NAME,
                DUMMY_IDENTIFICATION_TYPE, DUMMY_IDENTIFICATION_NUMBER);
    }

    public static CardToken getCardToken(Context context, String flavor) {

        return JsonUtil.getInstance().fromJson(getFile(context, "mocks/card_token" + flavor + ".json"), CardToken.class);
    }

    public static SavedCardToken getSavedCardToken() {

        return new SavedCardToken(DUMMY_CARD_ID, DUMMY_SECURITY_CODE);
    }

    public static PaymentMethod getPaymentMethod(Context context) {

        return getPaymentMethod(context, "");
    }

    public static PaymentMethod getPaymentMethod(Context context, String flavor) {

        return JsonUtil.getInstance().fromJson(getFile(context, "mocks/payment_method" + flavor + ".json"), PaymentMethod.class);
    }

    public static IdentificationType getIdentificationType() {

        return new IdentificationType(DUMMI_IDENTIFICATION_TYPE_ID, DUMMI_IDENTIFICATION_TYPE_NAME,
                DUMMI_IDENTIFICATION_TYPE_TYPE, DUMMI_IDENTIFICATION_TYPE_MIN_LENGTH,
                DUMMI_IDENTIFICATION_TYPE_MAX_LENGTH);
    }

    public static PaymentMethodRow getPaymentMethodRow(Context context) {

        return JsonUtil.getInstance().fromJson(getFile(context, "mocks/payment_method_row.json"), PaymentMethodRow.class);
    }

    public static List<PayerCost> getPayerCosts(Context context) {

        Installment installment = JsonUtil.getInstance().fromJson(getFile(context, "mocks/installment.json"), Installment.class);
        return installment.getPayerCosts();
    }

    public static Issuer getIssuer(Context context) {

        return JsonUtil.getInstance().fromJson(getFile(context, "mocks/issuer.json"), Issuer.class);
    }

    public static List<Card> getCards(Context context) {

        try {
            Customer customer = JsonUtil.getInstance().fromJson(getFile(context, "mocks/customer.json"), Customer.class);
            return customer.getCards();

        } catch (Exception ex) {

            return null;
        }
    }

    public static Card getCard(Context context) {

        try {
            List<Card> cards = getCards(context);
            return cards.get(0);

        } catch (Exception ex) {

            return null;
        }
    }

    public static Payment getPayment(Context context) {

        try {
            return JsonUtil.getInstance().fromJson(getFile(context, "mocks/payment.json"), Payment.class);

        } catch (Exception ex) {

            return null;
        }
    }

    public static Payment getPayment() {

        try {
            return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/payment.json"), Payment.class);

        } catch (Exception ex) {

            return null;
        }
    }

    public static String getCompletePaymentMethodSearchAsJson() {
        try {
            return getFile(InstrumentationRegistry.getContext(), "mocks/payment_method_search_all.json");

        } catch (Exception ex) {

            return null;
        }
    }

    public static String getCompletePaymentMethodsJson() {
        try {
            return getFile(InstrumentationRegistry.getContext(), "mocks/payment_methods.json");

        } catch (Exception ex) {

            return null;
        }
    }

    public static CheckoutPreference getPreferenceWithoutExclusions() {
        try {
            String json = getFile(InstrumentationRegistry.getContext(), "mocks/preference_without_exclusions.json");
            return JsonUtil.getInstance().fromJson(json, CheckoutPreference.class);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Instruction getInstructionWithoutActions() {
        try {
            String json = getFile(InstrumentationRegistry.getContext(), "mocks/cash_instructions.json");
            return JsonUtil.getInstance().fromJson(json, Instruction.class);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Instruction getInstructionWithAction() {
        try {
            String json = getFile(InstrumentationRegistry.getContext(), "mocks/action_instructions.json");
            return JsonUtil.getInstance().fromJson(json, Instruction.class);
        } catch (Exception ex) {
            return null;
        }
    }
    public static Instruction getInstructionWithActionButNullUrl() {
        try {
            String json = getFile(InstrumentationRegistry.getContext(), "mocks/action_instructions_without_url.json");
            return JsonUtil.getInstance().fromJson(json, Instruction.class);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Instruction getInstructionWithActionButEmptyUrl() {
        try {
            String json = getFile(InstrumentationRegistry.getContext(), "mocks/action_instructions_with_empty_url.json");
            return JsonUtil.getInstance().fromJson(json, Instruction.class);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Instruction getInstructionWithoutLabels() {
        try {
            String json = getFile(InstrumentationRegistry.getContext(), "mocks/cash_instructions_without_labels.json");
            return JsonUtil.getInstance().fromJson(json, Instruction.class);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Instruction getInstructionWithoutPrimaryInfo() {
        try {
            String json = getFile(InstrumentationRegistry.getContext(), "mocks/cash_instructions_no_primary_info.json");
            return JsonUtil.getInstance().fromJson(json, Instruction.class);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Instruction getInstructionWithNullInfo() {
        try {
            String json = getFile(InstrumentationRegistry.getContext(), "mocks/cash_instructions_null_info.json");
            return JsonUtil.getInstance().fromJson(json, Instruction.class);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Instruction getInstructionWithoutSecondaryInfo() {
        try {
            String json = getFile(InstrumentationRegistry.getContext(), "mocks/cash_instructions_no_secondary_info.json");
            return JsonUtil.getInstance().fromJson(json, Instruction.class);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Instruction getInstructionWithoutTertiaryInfo() {
        try {
            String json = getFile(InstrumentationRegistry.getContext(), "mocks/cash_instructions_no_tertiary_info.json");
            return JsonUtil.getInstance().fromJson(json, Instruction.class);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Instruction getInstructionWithInvalidReference() {
        try {
            String json = getFile(InstrumentationRegistry.getContext(), "mocks/instruction_with_invalid_reference.json");
            return JsonUtil.getInstance().fromJson(json, Instruction.class);
        } catch (Exception ex) {
            return null;
        }
    }

    private static String getFile(Context context, String fileName) {

        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            return new String(buffer);

        } catch (Exception e) {

            return "";
        }
    }
}
