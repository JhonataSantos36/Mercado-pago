package com.mercadopago.review_and_confirm.components.items;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.mercadopago.components.Component;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.review_and_confirm.models.ItemsModel;

public class ReviewItems extends Component<ReviewItems.Props, Void> {

    static {
        RendererFactory.register(ReviewItems.class, ReviewItemsRenderer.class);
    }

    public ReviewItems(@NonNull final Props props) {
        super(props);
    }

    public static class Props {

        public ItemsModel getItemsModel() {
            return itemsModel;
        }

        final ItemsModel itemsModel;

        public Integer getCollectorIcon() {
            return collectorIcon;
        }

        final @DrawableRes Integer collectorIcon;
        final String quantityLabel;
        final String unitPriceLabel;

        public Props(final ItemsModel itemsModel,
            final @DrawableRes Integer collectorIcon,
            final String quantityLabel,
            final String unitPriceLabel) {
            this.itemsModel = itemsModel;
            this.collectorIcon = collectorIcon;
            this.quantityLabel = quantityLabel;
            this.unitPriceLabel = unitPriceLabel;
        }
    }
}
