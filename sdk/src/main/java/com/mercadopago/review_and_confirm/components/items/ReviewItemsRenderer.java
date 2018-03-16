package com.mercadopago.review_and_confirm.components.items;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.review_and_confirm.models.ItemModel;

public class ReviewItemsRenderer extends Renderer<ReviewItems> {
    @Override
    protected View render(@NonNull final ReviewItems component, @NonNull final Context context,
        @Nullable final ViewGroup parent) {

        final LinearLayout linearLayout = createMainLayout(context);

        for (ItemModel itemModel : component.props.itemsModel.itemsModelList) {
            addReviewItem(
                new ReviewItem(new ReviewItem.Props(
                    itemModel,
                    getIcon(component.props),
                    component.props.quantityLabel,
                    component.props.unitPriceLabel)),
                linearLayout);
        }

        parent.addView(linearLayout);
        return linearLayout;
    }

    @VisibleForTesting
    @DrawableRes
    protected int getIcon(final ReviewItems.Props props) {
        if (props.getItemsModel().hasUniqueItem()) {
            return props.getCollectorIcon() == null ? R.drawable.mpsdk_review_item_default : props.getCollectorIcon();
        } else {
            return R.drawable.mpsdk_review_item_default;
        }
    }

    @NonNull
    private LinearLayout createMainLayout(final @NonNull Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
        return linearLayout;
    }

    private void addReviewItem(final @NonNull ReviewItem reviewItem,
        final ViewGroup container) {
        Renderer renderer = RendererFactory.create(container.getContext(), reviewItem);
        renderer.render(container);
    }
}
