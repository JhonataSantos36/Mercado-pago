package com.mercadopago.review_and_confirm.components.items;

import com.mercadopago.review_and_confirm.components.ReviewItem;
import com.mercadopago.review_and_confirm.models.ItemModel;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class ReviewItemTest {

    private final static String ITEM_IMAGE_URL = "item_image_url";
    private final static String ITEM_TITLE = "item_title";
    private final static String ITEM_SUBTITLE = "item_subtitle";
    private final static String ITEM_CURRENCY_ID = "item_currency_id";
    private final static BigDecimal ITEM_UNIT_PRICE = new BigDecimal(100);
    private final static Integer ITEM_ICON = 1;


    private ItemModel getItemModel(String imageUrl,
                                   String title,
                                   String subtitle,
                                   Integer quantity,
                                   String currencyId,
                                   BigDecimal unitPrice) {
        return new ItemModel(imageUrl, title, subtitle, quantity, currencyId, unitPrice);
    }

    @Test
    public void whenItemImageIsAvailableThenShowIt() {
        ItemModel model = getItemModel(ITEM_IMAGE_URL, ITEM_TITLE, ITEM_SUBTITLE, 1, ITEM_CURRENCY_ID, ITEM_UNIT_PRICE);
        ReviewItem component = new ReviewItem(new ReviewItem.Props(model, ITEM_ICON, null, null));

        Assert.assertTrue(component.hasItemImage());
    }

    @Test
    public void whenItemImageIsNotAvailableThenShowIcon() {
        ItemModel model = getItemModel(null, ITEM_TITLE, ITEM_SUBTITLE, 1, ITEM_CURRENCY_ID, ITEM_UNIT_PRICE);
        ReviewItem component = new ReviewItem(new ReviewItem.Props(model, ITEM_ICON, null, null));

        Assert.assertFalse(component.hasItemImage());
        Assert.assertTrue(component.hasIcon());
    }

}
