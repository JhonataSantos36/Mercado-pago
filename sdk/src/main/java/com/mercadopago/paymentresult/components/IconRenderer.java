package com.mercadopago.paymentresult.components;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.paymentresult.components.Icon;
import com.mercadopago.util.CircleTransform;
import com.mercadopago.util.ScaleUtil;
import com.squareup.picasso.Picasso;

/**
 * Created by vaserber on 10/23/17.
 */

public class IconRenderer extends Renderer<Icon> {

    @Override
    public View render() {
        final View iconView = LayoutInflater.from(context).inflate(R.layout.mpsdk_icon, null);
        final ImageView iconImageView = iconView.findViewById(R.id.mpsdkIconProduct);
        final ImageView iconBadgeView = iconView.findViewById(R.id.mpsdkIconBadge);
        renderIcon(iconImageView);
        renderBadge(iconBadgeView);
        return iconView;
    }

    private void renderIcon(@NonNull final ImageView imageView) {
        final int dimen = ScaleUtil.getPxFromDp(90, context);
        Picasso.with(context)
                .load(component.props.iconImage)
                .transform(new CircleTransform())
                .resize(dimen, dimen)
                .centerInside()
                .into(imageView);
    }

    private void renderBadge(@NonNull final ImageView imageView) {
        if (component.props.badgeImage == 0) {
            imageView.setVisibility(View.INVISIBLE);
        } else {
            final Drawable badgeImage = ContextCompat.getDrawable(context,
                    component.props.badgeImage);
            imageView.setImageDrawable(badgeImage);
            imageView.setVisibility(View.VISIBLE);
        }
    }

}
