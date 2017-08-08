package com.mercadopago.util;



/**
 * Created by vaserber on 6/5/17.
 */

public class TrackingUtil {

    //Screen IDs
    public static final String SCREEN_ID_CHECKOUT = "/checkout_off/init";
    public static final String SCREEN_ID_PAYMENT_VAULT = "/checkout_off/payment_option";
    public static final String SCREEN_ID_REVIEW_AND_CONFIRM = "/checkout_off/review";
    public static final String SCREEN_ID_PAYMENT_RESULT_APPROVED = "/checkout_off/congrats/approved";
    public static final String SCREEN_ID_PAYMENT_RESULT_PENDING = "/checkout_off/congrats/in_process";
    public static final String SCREEN_ID_PAYMENT_RESULT_REJECTED = "/checkout_off/congrats/rejected";
    public static final String SCREEN_ID_PAYMENT_RESULT_INSTRUCTIONS = "/checkout_off/congrats/instructions";
    public static final String SCREEN_ID_BANK_DEALS = "/checkout_off/bank_deals";
    public static final String SCREEN_ID_CARD_FORM = "/checkout_off/card/";
    public static final String SCREEN_ID_ERROR = "/checkout_off/failure";
    public static final String SCREEN_ID_PAYMENT_TYPES = "/checkout_off/card/payment_types";
    public static final String SCREEN_ID_IDENTIFICATION = "/checkout_off/identification";
    public static final String SCREEN_ID_ISSUERS = "/checkout_off/card/issuer";
    public static final String SCREEN_ID_INSTALLMENTS = "/checkout_off/card/installments";

    //Screen Names
    public static final String SCREEN_NAME_CHECKOUT = "INIT_CHECKOUT";
    public static final String SCREEN_NAME_PAYMENT_VAULT = "PAYMENT_METHOD_SEARCH";
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

    //Sufix
    public static final String CARD_NUMBER = "/number";
    public static final String CARD_HOLDER_NAME = "/name";
    public static final String CARD_EXPIRATION_DATE = "/expiration";
    public static final String CARD_SECURITY_CODE = "/cvv";
    public static final String CARD_SECURITY_CODE_VIEW = "/security_code";

    //Additional Info Keys
    public static final String METADATA_PAYMENT_METHOD_ID = "payment_method";
    public static final String METADATA_PAYMENT_TYPE_ID = "payment_type";
    public static final String METADATA_ISSUER_ID = "issuer";
    public static final String METADATA_SHIPPING_INFO = "has_shipping";
    public static final String METADATA_PAYMENT_STATUS = "payment_status";
    public static final String METADATA_PAYMENT_ID = "payment_id";
    public static final String METADATA_PAYMENT_STATUS_DETAIL = "payment_status_detail";
    public static final String METADATA_PAYMENT_IS_EXPRESS = "is_express";
    public static final String METADATA_ERROR_STATUS = "error_status";
    public static final String METADATA_ERROR_CODE = "error_code";
    public static final String METADATA_ERROR_REQUEST = "error_request_origin";

    //Default values
    public static final String HAS_SHIPPING_DEFAULT_VALUE = "false";
    public static final String IS_EXPRESS_DEFAULT_VALUE = "false";
}
