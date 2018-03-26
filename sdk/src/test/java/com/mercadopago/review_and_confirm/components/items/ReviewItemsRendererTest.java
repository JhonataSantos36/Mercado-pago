package com.mercadopago.review_and_confirm.components.items;

import com.mercadopago.R;
import com.mercadopago.review_and_confirm.models.ItemsModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReviewItemsRendererTest {

    public static final int RESOURCE_ID = 1;
    @Mock ReviewItems.Props props;

    @Mock ItemsModel itemsModel;

    private ReviewItemsRenderer renderer;

    @Before
    public void setUp() {
        renderer = new ReviewItemsRenderer();
        when(props.getItemsModel()).thenReturn(itemsModel);
    }

    @Test
    public void whenIconIsAvailableInPreferenceAndIsUniqueItemThenShowIt() throws Exception {
        when(itemsModel.hasUniqueItem()).thenReturn(true);
        when(props.getCollectorIcon()).thenReturn(RESOURCE_ID);

        final int icon = renderer.getIcon(props);
        assertEquals(RESOURCE_ID, icon);
    }

    @Test
    public void whenIconIsNotAvailableInPreferenceThenShowDefaultIcon() throws Exception {
        when(itemsModel.hasUniqueItem()).thenReturn(true);
        when(props.getCollectorIcon()).thenReturn(null);

        final int icon = renderer.getIcon(props);
        assertEquals(R.drawable.mpsdk_review_item_default, icon);
    }

    @Test
    public void whenModelHasMultipleItemsThenShowDefaultIcon() throws Exception {
        when(itemsModel.hasUniqueItem()).thenReturn(false);

        final int icon = renderer.getIcon(props);
        assertEquals(R.drawable.mpsdk_review_item_default, icon);
    }
}