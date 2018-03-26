package com.mercadopago.review_and_confirm.components.items;

import com.mercadopago.review_and_confirm.models.ItemModel;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class ItemModelTest {

    private final static String ITEM_IMAGE_URL = "item_image_url";
    private final static String ITEM_TITLE = "item_title";
    private final static String ITEM_SUBTITLE = "item_subtitle";
    private final static String ITEM_CURRENCY_ID = "item_currency_id";
    private final static BigDecimal ITEM_UNIT_PRICE = new BigDecimal(100);

    @Test
    public void whenQuantityIsUniqueThenHideIt() {
        ItemModel model = new ItemModel(ITEM_IMAGE_URL, ITEM_TITLE, ITEM_SUBTITLE, 1, ITEM_CURRENCY_ID, ITEM_UNIT_PRICE);
        Assert.assertFalse(model.hasToShowQuantity());
    }

    @Test
    public void whenQuantityIsMultipleThenShowIt() {
        ItemModel model = new ItemModel(ITEM_IMAGE_URL, ITEM_TITLE, ITEM_SUBTITLE, 3, ITEM_CURRENCY_ID, ITEM_UNIT_PRICE);
        Assert.assertTrue(model.hasToShowQuantity());
    }
}
