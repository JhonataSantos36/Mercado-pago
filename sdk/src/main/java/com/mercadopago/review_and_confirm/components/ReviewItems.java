package com.mercadopago.review_and_confirm.components;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.mercadopago.R;
import com.mercadopago.components.ActionDispatcher;
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

    public ReviewItems(@NonNull final Props props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public int getIcon() {
        return props.collectorIcon == null ? R.drawable.mpsdk_review_item_default : props.collectorIcon;
    }

    public static class Props {

        final ItemsModel itemsModel;
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
            this.unitPriceLabel= unitPriceLabel;
        }
    }

}
