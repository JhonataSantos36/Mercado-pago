package com.mercadopago.tracking.utils;

/**
 * Created by vaserber on 6/5/17.
 */

public class TrackingUtil {

    //Action IDs
    public static final String ACTION_CHECKOUT_CONFIRMED = "/checkout_confirmed";

    //Screen IDs
    public static final String SCREEN_ID_CHECKOUT = "/init";
    public static final String SCREEN_ID_PAYMENT_VAULT = "/payment_option";
    public static final String SCREEN_ID_PAYMENT_VAULT_TICKET = "/payment_option/ticket";
    public static final String SCREEN_ID_PAYMENT_VAULT_BANK_TRANSFER = "/payment_option/bank_transfer";
    public static final String SCREEN_ID_PAYMENT_VAULT_CARDS = "/payment_option/card";
    public static final String SCREEN_ID_REVIEW_AND_CONFIRM = "/review";
    public static final String SCREEN_ID_PAYMENT_RESULT_APPROVED = "/congrats/approved";
    public static final String SCREEN_ID_PAYMENT_RESULT_PENDING = "/congrats/in_process";
    public static final String SCREEN_ID_PAYMENT_RESULT_REJECTED = "/congrats/rejected";
    public static final String SCREEN_ID_PAYMENT_RESULT_INSTRUCTIONS = "/congrats/instructions";
    public static final String SCREEN_ID_BANK_DEALS = "/bank_deals";
    public static final String SCREEN_ID_CARD_FORM = "/card/";
    public static final String SCREEN_ID_ERROR = "/failure";
    public static final String SCREEN_ID_PAYMENT_TYPES = "/card/payment_types";
    public static final String SCREEN_ID_IDENTIFICATION = "/identification";
    public static final String SCREEN_ID_ISSUERS = "/card/issuer";
    public static final String SCREEN_ID_INSTALLMENTS = "/card/installments";

    //Screen Names
    public static final String SCREEN_NAME_CHECKOUT = "INIT_CHECKOUT";
    public static final String SCREEN_NAME_PAYMENT_VAULT = "PAYMENT_METHOD_SEARCH";
    public static final String SCREEN_NAME_PAYMENT_VAULT_TICKET = "PAYMENT_METHOD_SEARCH";
    public static final String SCREEN_NAME_PAYMENT_VAULT_BANK_TRANSFER = "PAYMENT_METHOD_SEARCH";
    public static final String SCREEN_NAME_PAYMENT_VAULT_CARDS = "PAYMENT_METHOD_SEARCH";
    public static final String SCREEN_NAME_REVIEW_AND_CONFIRM = "REVIEW_AND_CONFIRM";
    public static final String SCREEN_NAME_PAYMENT_RESULT_APPROVED = "RESULT";
    public static final String SCREEN_NAME_PAYMENT_RESULT_PENDING = "RESULT";
    public static final String SCREEN_NAME_PAYMENT_RESULT_REJECTED = "RESULT";
    public static final String SCREEN_NAME_PAYMENT_RESULT_CALL_FOR_AUTH = "CALL_FOR_AUTHORIZE";
    public static final String SCREEN_NAME_PAYMENT_RESULT_INSTRUCTIONS = "INSTRUCTIONS";
    public static final String SCREEN_NAME_BANK_DEALS = "BANK_DEALS";
    public static final String SCREEN_NAME_CARD_FORM = "CARD_VAULT";
    public static final String SCREEN_NAME_CARD_FORM_NUMBER = "CARD_NUMBER";
    public static final String SCREEN_NAME_CARD_FORM_NAME = "CARD_HOLDER_NAME";
    public static final String SCREEN_NAME_CARD_FORM_EXPIRY = "CARD_EXPIRY_DATE";
    public static final String SCREEN_NAME_CARD_FORM_CVV = "CARD_SECURITY_CODE";
    public static final String SCREEN_NAME_CARD_FORM_IDENTIFICATION_NUMBER = "IDENTIFICATION_NUMBER";
    public static final String SCREEN_NAME_CARD_FORM_ISSUERS = "CARD_ISSUERS";
    public static final String SCREEN_NAME_CARD_FORM_INSTALLMENTS = "CARD_INSTALLMENTS";
    public static final String SCREEN_NAME_ERROR = "ERROR_VIEW";
    public static final String SCREEN_NAME_PAYMENT_TYPES = "CARD_PAYMENT_TYPES";
    public static final String SCREEN_NAME_SECURITY_CODE = "SECURITY_CODE_CARD";

    //Payment Vault Group Ids
    public static final String GROUP_TICKET = "ticket";
    public static final String GROUP_BANK_TRANSFER = "bank_transfer";
    public static final String GROUP_CARDS = "cards";

    //Security Code Reason
    public static final String SECURITY_CODE_REASON_CALL = "call_for_auth";
    public static final String SECURITY_CODE_REASON_SAVED_CARD = "saved_card";
    public static final String SECURITY_CODE_REASON_ESC = "invalid_esc";

    //Sufix
    public static final String CARD_NUMBER = "/number";
    public static final String CARD_HOLDER_NAME = "/name";
    public static final String CARD_EXPIRATION_DATE = "/expiration";
    public static final String CARD_SECURITY_CODE = "/cvv";
    public static final String CARD_SECURITY_CODE_VIEW = "/security_code";

    //Additional Info Keys
    public static final String PROPERTY_PAYMENT_METHOD_ID = "payment_method";
    public static final String PROPERTY_PAYMENT_TYPE_ID = "payment_type";
    public static final String PROPERTY_ISSUER_ID = "issuer";
    public static final String PROPERTY_SHIPPING_INFO = "has_shipping";
    public static final String PROPERTY_PAYMENT_STATUS = "payment_status";
    public static final String PROPERTY_PAYMENT_ID = "payment_id";
    public static final String PROPERTY_PAYMENT_STATUS_DETAIL = "payment_status_detail";
    public static final String PROPERTY_PAYMENT_IS_EXPRESS = "is_express";
    public static final String PROPERTY_SECURITY_CODE_REASON = "security_code_view_reason";
    public static final String PROPERTY_ERROR_STATUS = "error_status";
    public static final String PROPERTY_ERROR_CODE = "error_code";
    public static final String PROPERTY_ERROR_REQUEST = "error_request_origin";
    public static final String PROPERTY_OPTIONS = "options";
    public static final String PROPERTY_CARD_ID = "card_id";
    public static final String PROPERTY_INSTALLMENTS = "installments";
    public static final String PROPERTY_PURCHASE_AMOUNT = "purchase_amount";

    //Default values
    public static final String HAS_SHIPPING_DEFAULT_VALUE = "false";
    public static final String IS_EXPRESS_DEFAULT_VALUE = "false";

    //Strategies
    public static final String NOOP_STRATEGY = "noop_strategy";
    public static final String REALTIME_STRATEGY = "realtime_strategy";
    public static final String BATCH_STRATEGY = "batch_strategy";
    public static final String FORCED_STRATEGY = "forced_strategy";
}