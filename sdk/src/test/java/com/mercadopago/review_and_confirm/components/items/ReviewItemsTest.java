package com.mercadopago.review_and_confirm.components.items;

import com.mercadopago.R;
import com.mercadopago.model.Item;
import com.mercadopago.review_and_confirm.components.ReviewItems;
import com.mercadopago.review_and_confirm.models.ItemsModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ReviewItemsTest {

    @Mock
    private Item item;

    private List<Item> itemList;

    private final static int DEFAULT_ICON = R.drawable.mpsdk_review_item_default;
    private final static int ITEM_ICON = 1;
    private final static String ITEM_CURRENCY_ID = "item_currency_id";

    @Before
    public void setUp() {
        itemList = new ArrayList<>();
        itemList.add(item);
    }

    @Test
    public void whenIconIsAvailableInPreferenceThenShowIt() {
        ItemsModel model = new ItemsModel(ITEM_CURRENCY_ID, itemList);

        ReviewItems component = new ReviewItems(new ReviewItems.Props(model, ITEM_ICON, null, null));

        Assert.assertEquals(ITEM_ICON, component.getIcon());
    }

    @Test
    public void whenIconIsNotAvailableInPreferenceThenShowDefault() {
        ItemsModel model = new ItemsModel(ITEM_CURRENCY_ID, itemList);

        ReviewItems component = new ReviewItems(new ReviewItems.Props(model, null, null, null));

        Assert.assertEquals(DEFAULT_ICON, component.getIcon());
    }
}
