package com.mercadopago.review_and_confirm.components;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.review_and_confirm.models.ItemModel;
import com.mercadopago.util.TextUtils;

public class ReviewItem extends Component<ReviewItem.Props, Void> {

    static {
        RendererFactory.register(ReviewItem.class, ReviewItemRenderer.class);
    }

    public ReviewItem(@NonNull final Props props) {
        super(props);
    }

    public ReviewItem(@NonNull final Props props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public boolean hasItemImage() {
        return TextUtils.isNotEmpty(props.itemModel.imageUrl);
    }

    public boolean hasIcon() {
        return props.icon != null;
    }

    public static class Props {

        final ItemModel itemModel;
        final @DrawableRes
        Integer icon;
        final String quantityLabel;
        final String unitPriceLabel;

        public Props(final ItemModel itemModel,
                     final @DrawableRes Integer icon,
                     final String quantityLabel,
                     final String unitPriceLabel) {
            this.itemModel = itemModel;
            this.icon = icon;
            this.quantityLabel = quantityLabel;
            this.unitPriceLabel = unitPriceLabel;
        }
    }
}
