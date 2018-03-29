package com.mercadopago.utils;

import com.mercadopago.lite.exceptions.ApiException;
import com.mercadopago.lite.model.Discount;
import com.mercadopago.util.JsonUtil;

public class Discounts {

    private static String discountWithPercentOffMLA = "{\"id\":66,\"name\":\"TestChoNativo\",\"percent_off\":5,\"amount_off\":0,\"coupon_amount\":50,\"currency_id\":\"ARS\"}";

    private static String discountWithAmountOffMLA = "{\"id\":66,\"name\":\"TestChoNativo\",\"percent_off\":0,\"amount_off\":15,\"coupon_amount\":15,\"currency_id\":\"ARS\"}";

    private static String doNotFindCampaignApiExeption = "{\"message\":\"doesn't find a campaign\",\"error\":\"campaign-doesnt-match\",\"cause\":[]}";

    private Discounts() {}

    public static Discount getDiscountWithPercentOffMLA() {
        return JsonUtil.getInstance().fromJson(discountWithPercentOffMLA, Discount.class);
    }

    public static Discount getDiscountWithAmountOffMLA() {
        return JsonUtil.getInstance().fromJson(discountWithAmountOffMLA, Discount.class);
    }

    public static ApiException getDoNotFindCampaignApiException() {
        return JsonUtil.getInstance().fromJson(doNotFindCampaignApiExeption, ApiException.class);
    }
}
