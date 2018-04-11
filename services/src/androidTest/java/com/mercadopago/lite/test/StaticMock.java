package com.mercadopago.lite.test;

import android.content.Context;

import com.mercadopago.model.CardToken;
import com.mercadopago.model.Cardholder;
import com.mercadopago.model.Identification;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.lite.util.JsonUtil;

import java.io.InputStream;

public class StaticMock {

    // * Merchant public key
    public static final String DUMMY_PRIVATE_KEY = "MOCKED_AT";
    public static final String DUMMY_MERCHANT_PUBLIC_KEY = "444a9ef5-8a6b-429f-abdf-587639155d88";
    public static final String DUMMY_TEST_PUBLIC_KEY = "TEST-ad365c37-8012-4014-84f5-6c895b3f8e0a";
    public static final String DUMMY_TEST_MX_PUBLIC_KEY = "TEST-9eb0be69-329a-417f-9dd5-aad772a4d50b";
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
    public final static String DUMMY_CARD_NUMBER_AMEX = "371180303257522";
    public final static String DUMMY_CARD_NUMBER_VISA = "4509953566233704";
    public final static String DUMMY_CARDHOLDER_NAME = "JOHN";
    public final static int DUMMY_EXPIRATION_MONTH = 11;
    public final static int DUMMY_EXPIRATION_YEAR_SHORT = 25;
    public final static int DUMMY_EXPIRATION_YEAR_LONG = 2025;
    public final static String DUMMY_IDENTIFICATION_NUMBER = "12345678";
    public final static String DUMMY_IDENTIFICATION_NUMBER_WITH_MASK = "12.345.678";
    public final static String DUMMY_IDENTIFICATION_TYPE = "DNI";
    public final static String DUMMY_SECURITY_CODE = "123";
    public final static String DUMMY_SECURITY_CODE_FOUR_DIGITS = "1234";
    public final static String DUMMY_EXPIRATION_DATE = "1225";
    public final static int DUMMY_EXPIRATION_DATE_ONLY_MONTH = 12;
    public final static int DUMMY_EXPIRATION_DATE_ONLY_YEAR = 25;
    public final static String DUMMY_EXPIRATION_DATE_WITH_MASK = "12/25";
    public final static String DUMMY_CI_NUMBER = "123456789";
    public final static String DUMMY_LC_NUMBER = "1234567";
    public final static String DUMMY_LE_NUMBER = "1234567";
    public final static String DUMMY_OTHER_NUMBER = "1234567890";

    // * Identification type
    public final static String DUMMI_IDENTIFICATION_TYPE_ID = "DNI";
    public final static String DUMMI_IDENTIFICATION_TYPE_NAME = "DNI";
    public final static String DUMMI_IDENTIFICATION_TYPE_TYPE = "number";
    public final static Integer DUMMI_IDENTIFICATION_TYPE_MIN_LENGTH = 7;
    public final static Integer DUMMI_IDENTIFICATION_TYPE_MAX_LENGTH = 8;

    // * Saved cards token
    public final static String DUMMY_CARD_ID = "11";

    // * Security code holder
    public final static String SECURITY_CODE_FRONT_HOLDER = "••••";
    public final static String SECURITY_CODE_BACK_HOLDER = "•••";

    // * Discount
    public final static String DUMMY_DISCOUNT_CODE = "PRUEBA";

    public static CardToken getCardToken() {
        CardToken cardToken = new CardToken();
        Cardholder cardholder = new Cardholder();
        Identification identification = new Identification();

        identification.setType(DUMMY_IDENTIFICATION_TYPE);
        identification.setNumber(DUMMY_IDENTIFICATION_NUMBER);

        cardholder.setName(DUMMY_CARDHOLDER_NAME);
        cardholder.setIdentification(identification);

        cardToken.setCardholder(cardholder);
        cardToken.setSecurityCode(DUMMY_SECURITY_CODE);
        cardToken.setExpirationMonth(DUMMY_EXPIRATION_MONTH);
        cardToken.setExpirationYear(DUMMY_EXPIRATION_YEAR_LONG);
        cardToken.setCardNumber(DUMMY_CARD_NUMBER);

        return cardToken;
    }

    public static PaymentMethod getPaymentMethod(Context context) {
        return getPaymentMethod(context, "");
    }

    public static PaymentMethod getPaymentMethod(Context context, String flavor) {
        return JsonUtil.getInstance().fromJson(getFile(context, "mocks/payment_method_on" + flavor + ".json"), PaymentMethod.class);
    }

    public static IdentificationType getIdentificationType() {
        IdentificationType identificationType = new IdentificationType();
        identificationType.setId(DUMMI_IDENTIFICATION_TYPE_ID);
        identificationType.setName(DUMMI_IDENTIFICATION_TYPE_NAME);
        identificationType.setType(DUMMI_IDENTIFICATION_TYPE_TYPE);
        identificationType.setMinLength(DUMMI_IDENTIFICATION_TYPE_MIN_LENGTH);
        identificationType.setMaxLength(DUMMI_IDENTIFICATION_TYPE_MAX_LENGTH);

        return identificationType;
    }

//    public static String getIdentificationTypeList() {
//
//        try {
//            return getFile(InstrumentationRegistry.getContext(), "mocks/identification_types.json");
//
//        } catch (Exception ex) {
//            return null;
//        }
//
//    }

//    public static String getIdentificationTypeCPF() {
//
//        try {
//            return getFile(InstrumentationRegistry.getContext(), "mocks/identification_type_cpf.json");
//
//        } catch (Exception ex) {
//            return null;
//        }
//
//    }
//
//    public static String getIdentificationTypeCNPJ() {
//
//        try {
//            return getFile(InstrumentationRegistry.getContext(), "mocks/identification_type_cnpj.json");
//
//        } catch (Exception ex) {
//            return null;
//        }
//
//    }
//
//    public static String getPaymentMethodListMLM() {
//        try {
//            return getFile(InstrumentationRegistry.getContext(), "mocks/payment_method_list_mlm.json");
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static List<PayerCost> getPayerCosts() {
//
//        Installment installment = JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/installment.json"), Installment.class);
//        return installment.getPayerCosts();
//    }
//
//
//    public static PayerCost getPayerCostWithInterests() {
//        return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/payerCostWithInterest.json"), PayerCost.class);
//    }
//
//    public static PayerCost getPayerCostWithoutInterests() {
//        return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/payerCostWithoutInterest.json"), PayerCost.class);
//    }
//
//    public static Issuer getIssuer() {
//        return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/issuer.json"), Issuer.class);
//    }
//
//    public static String getIssuersJson() {
//
//        try {
//            return getFile(InstrumentationRegistry.getContext(), "mocks/issuers.json");
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static List<Issuer> getIssuersList() {
//        List<Issuer> issuerList;
//        String json = getIssuersJson();
//        try {
//            Type listType = new TypeToken<List<Issuer>>() {
//            }.getType();
//            issuerList = JsonUtil.getInstance().getGson().fromJson(json, listType);
//        } catch (Exception ex) {
//            issuerList = null;
//        }
//        return issuerList;
//    }
//
//    public static PaymentPreference getEmptyPaymentPreference() {
//        return new PaymentPreference();
//    }
//
//    public static String getInstallmentsJson() {
//
//        try {
//            return getFile(InstrumentationRegistry.getContext(), "mocks/installments.json");
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static String getInstallmentsWithUniquePayerCostJson() {
//
//        try {
//            return getFile(InstrumentationRegistry.getContext(), "mocks/installments_unique.json");
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static String getMultipleInstallmentsJson() {
//
//        try {
//            return getFile(InstrumentationRegistry.getContext(), "mocks/installments_multiple.json");
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//
//    public static String getPayerCostsJson() {
//
//        try {
//            return getFile(InstrumentationRegistry.getContext(), "mocks/payerCosts.json");
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static String getPaymentMethodList() {
//        try {
//            return getFile(InstrumentationRegistry.getContext(), "mocks/payment_method_list.json");
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static String getPaymentMethodListMaster() {
//        try {
//            return getFile(InstrumentationRegistry.getContext(), "mocks/payment_method_list_master.json");
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static String getPaymentTypesListMaster() {
//        try {
//            return getFile(InstrumentationRegistry.getContext(), "mocks/payment_types_list_master.json");
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static String getInvalidPayerCostsJson() {
//
//        try {
//            return getFile(InstrumentationRegistry.getContext(), "mocks/payerCostsInvalid.json");
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Token getToken() {
//
//        try {
//            return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/token.json"), Token.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Token getClonedToken() {
//
//        try {
//            return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/cloned_token.json"), Token.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Token getTokenAmex() {
//
//        try {
//            return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/token_amex.json"), Token.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Token getClonedTokenAmex() {
//
//        try {
//            return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/cloned_token_amex.json"), Token.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Token getTokenMasterIssuers() {
//
//        try {
//            return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/token_master_issuers.json"), Token.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Customer getCustomer(int cardsAmount) {
//        try {
//            if (cardsAmount == 1) {
//                return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/customer_one_card.json"), Customer.class);
//            } else if (cardsAmount == 2) {
//                return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/customer_two_cards.json"), Customer.class);
//            } else if (cardsAmount == 3) {
//                return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/customer_three_cards.json"), Customer.class);
//            }
//        } catch (Exception ex) {
//            return null;
//        }
//        return null;
//    }
//
//    public static List<Card> getCards() {
//
//        try {
//            Customer customer = JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/customer_three_cards.json"), Customer.class);
//            return customer.getCards();
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Card getCard() {
//
//        try {
//            List<Card> cards = getCards();
//            return cards.get(0);
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Payment getPayment(Context context) {
//
//        try {
//            return JsonUtil.getInstance().fromJson(getFile(context, "mocks/payment.json"), Payment.class);
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Payment getPayment() {
//
//        try {
//            return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/payment.json"), Payment.class);
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Payment getPaymentApprovedVisa() {
//
//        try {
//            return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/payment_approved_visa.json"), Payment.class);
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Payment getPaymentRejectedCallForAuthorize() {
//
//        try {
//            return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/payment_rejected_call_for_authorize.json"), Payment.class);
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Payment getPaymentRejectedBadFilledSecurityCode() {
//
//        try {
//            return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/payment_rejected_bad_filled_security_code.json"), Payment.class);
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Payment getAmexPaymentRejectedBadFilledSecurityCode() {
//
//        try {
//            return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/payment_rejected_bad_filled_security_code_amex.json"), Payment.class);
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static String getPaymentMethodSearchWithoutCustomOptionsAsJson() {
//        try {
//            return getFile(InstrumentationRegistry.getContext(), "mocks/payment_method_search_all.json");
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static String getPaymentMethodSearchWithOnlyAccountMoneyAndNewCard() {
//        try {
//            return getFile(InstrumentationRegistry.getContext(), "mocks/payment_method_search_account_money_and_new_card.json");
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static String getPaymentMethodSearchWithOnlyAccountMoney() {
//        try {
//            return getFile(InstrumentationRegistry.getContext(), "mocks/payment_method_search_only_account_money.json");
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static String getPaymentMethodSearchWithCardsAsJson() {
//        try {
//            return getFile(InstrumentationRegistry.getContext(), "mocks/payment_method_search_with_cards");
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static PaymentMethodSearch getPaymentMethodSearchWithUniquePaymentMethodOff() {
//        try {
//            return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/payment_method_search_unique_item_off.json"), PaymentMethodSearch.class);
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static PaymentMethodSearch getPaymentMethodSearchWithUniqueItemCreditCard() {
//        try {
//            return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/payment_method_search_unique_item_credit_card.json"), PaymentMethodSearch.class);
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static List<PaymentMethod> getPaymentMethods() {
//        try {
//            Type listType = new TypeToken<List<PaymentMethod>>(){}.getType();
//            String json = getFile(InstrumentationRegistry.getContext(), "mocks/payment_methods.json");
//            return JsonUtil.getInstance().getGson().fromJson(json, listType);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//
//    public static PaymentMethod getPaymentMethodOff() {
//        try {
//            return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/payment_method_off.json"), PaymentMethod.class);
//        } catch (Exception ex) {
//
//            return null;
//        }
//    }
//
//    public static PaymentMethod getPaymentMethodOn() {
//        try {
//            return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/payment_method_on.json"), PaymentMethod.class);
//        } catch (Exception ex) {
//
//            return null;
//        }
//    }
//
//    public static PaymentMethod getAmexPaymentMethodOn() {
//        try {
//            return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/payment_method_on_amex.json"), PaymentMethod.class);
//        } catch (Exception ex) {
//
//            return null;
//        }
//    }
//
//    public static CheckoutPreference getCheckoutPreference() {
//        try {
//            String json = getFile(InstrumentationRegistry.getContext(), "mocks/preference_without_exclusions.json");
//            return JsonUtil.getInstance().fromJson(json, CheckoutPreference.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static CheckoutPreference getPreferenceWithExclusions() {
//        try {
//            String json = getFile(InstrumentationRegistry.getContext(), "mocks/preference_with_exclusions.json");
//            return JsonUtil.getInstance().fromJson(json, CheckoutPreference.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static CheckoutPreference getPreferenceWithOffUniquePaymentMethod() {
//        try {
//            String json = getFile(InstrumentationRegistry.getContext(), "mocks/preference_with_one_off_payment_method.json");
//            return JsonUtil.getInstance().fromJson(json, CheckoutPreference.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static PaymentMethod getPaymentMethodWithIdentificationNotRequired() {
//        try {
//            return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/payment_method_identification_not_required.json"), PaymentMethod.class);
//        } catch (Exception ex) {
//
//            return null;
//        }
//    }
//
//    public static PaymentMethod getPaymentMethodWithIdentificationAndCVVNotRequired() {
//        try {
//            return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/payment_method_id_number_sec_code_not_required.json"), PaymentMethod.class);
//        } catch (Exception ex) {
//
//            return null;
//        }
//    }
//
//    public static CheckoutPreference getPreferenceWithoutItem() {
//        try {
//            String json = getFile(InstrumentationRegistry.getContext(), "mocks/preference_without_item.json");
//            return JsonUtil.getInstance().fromJson(json, CheckoutPreference.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Instructions getInstructions() {
//        try {
//            String json = getFile(InstrumentationRegistry.getContext(), "mocks/instructions_many.json");
//            return JsonUtil.getInstance().fromJson(json, Instructions.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Instructions getInstructionWithoutActions() {
//        try {
//            String json = getFile(InstrumentationRegistry.getContext(), "mocks/instructions_cash.json");
//            return JsonUtil.getInstance().fromJson(json, Instructions.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Instructions getInstructionWithAction() {
//        try {
//            String json = getFile(InstrumentationRegistry.getContext(), "mocks/instructions_actions.json");
//            return JsonUtil.getInstance().fromJson(json, Instructions.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Instructions getInstructionWithInvalidAction() {
//        try {
//            String json = getFile(InstrumentationRegistry.getContext(), "mocks/instructions_actions_invalid_tag.json");
//            return JsonUtil.getInstance().fromJson(json, Instructions.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Instructions getInstructionWithActionButNullUrl() {
//        try {
//            String json = getFile(InstrumentationRegistry.getContext(), "mocks/instructions_action_without_url.json");
//            return JsonUtil.getInstance().fromJson(json, Instructions.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Instructions getInstructionWithActionButEmptyUrl() {
//        try {
//            String json = getFile(InstrumentationRegistry.getContext(), "mocks/instructions_action_with_empty_url.json");
//            return JsonUtil.getInstance().fromJson(json, Instructions.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Instructions getInstructionWithoutLabels() {
//        try {
//            String json = getFile(InstrumentationRegistry.getContext(), "mocks/instructions_cash_without_labels.json");
//            return JsonUtil.getInstance().fromJson(json, Instructions.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Instructions getInstructionWithoutPrimaryInfo() {
//        try {
//            String json = getFile(InstrumentationRegistry.getContext(), "mocks/instructions_cash_no_primary_info.json");
//            return JsonUtil.getInstance().fromJson(json, Instructions.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Instructions getInstructionWithNullInfo() {
//        try {
//            String json = getFile(InstrumentationRegistry.getContext(), "mocks/instructions_cash_null_info.json");
//            return JsonUtil.getInstance().fromJson(json, Instructions.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Instructions getInstructionWithoutSecondaryInfo() {
//        try {
//            String json = getFile(InstrumentationRegistry.getContext(), "mocks/instructions_cash_no_secondary_info.json");
//            return JsonUtil.getInstance().fromJson(json, Instructions.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Instructions getInstructionWithoutTertiaryInfo() {
//        try {
//            String json = getFile(InstrumentationRegistry.getContext(), "mocks/instructions_cash_no_tertiary_info.json");
//            return JsonUtil.getInstance().fromJson(json, Instructions.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Instructions getInstructionWithInvalidReference() {
//        try {
//            String json = getFile(InstrumentationRegistry.getContext(), "mocks/instruction_with_invalid_reference.json");
//            return JsonUtil.getInstance().fromJson(json, Instructions.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static String getBankDealsJson() {
//        try {
//            return getFile(InstrumentationRegistry.getContext(), "mocks/bank_deals.json");
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static List<BankDeal> getBankDeals() {
//        try {
//            String json = getFile(InstrumentationRegistry.getContext(), "mocks/bank_deals.json");
//            Type listType = new TypeToken<List<BankDeal>>(){}.getType();
//            return JsonUtil.getInstance().getGson().fromJson(json, listType);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Discount getPercentOffDiscount() {
//        try {
//            String json = getFile(InstrumentationRegistry.getContext(), "mocks/discount_percent_off.json");
//            return JsonUtil.getInstance().fromJson(json, Discount.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static Discount getAmountOffDiscount() {
//        try {
//            String json = getFile(InstrumentationRegistry.getContext(), "mocks/discount_amount_off.json");
//            return JsonUtil.getInstance().fromJson(json, Discount.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static ApiException getApiExceptionDiscountCodeNotMatch() {
//        try {
//            String json = getFile(InstrumentationRegistry.getContext(), "mocks/discount_error_campaign_code_does_not_match.json");
//            return JsonUtil.getInstance().fromJson(json, ApiException.class);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static ApiException getApiExceptionWithoutStatus() {
//
//        try {
//            return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/api_exception_without_status.json"), ApiException.class);
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//
//    public static ApiException getApiExceptionNotFound() {
//        try {
//            return JsonUtil.getInstance().fromJson(getFile(InstrumentationRegistry.getContext(), "mocks/api_exception_not_found.json"), ApiException.class);
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }

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

//    public static PaymentMethod getPaymentMethodOnWithoutRequiredIdentification() {
//        return null;
//    }
}
