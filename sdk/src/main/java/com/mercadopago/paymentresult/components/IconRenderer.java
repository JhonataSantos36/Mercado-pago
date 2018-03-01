package com.mercadopago.paymentresult.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.paymentresult.props.IconProps;
import com.mercadopago.util.CircleTransform;
import com.mercadopago.util.ScaleUtil;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by vaserber on 10/23/17.
 */

public class IconRenderer extends Renderer<Icon> {

    @Override
    public View render(final Icon component, final Context context, final ViewGroup parent) {
        final View iconView = inflate(R.layout.mpsdk_icon, parent);
        final ImageView iconImageView = iconView.findViewById(R.id.mpsdkIconProduct);
        final ImageView iconBadgeView = iconView.findViewById(R.id.mpsdkIconBadge);

        final int size = ScaleUtil.getPxFromDp(90, context);

        //Render icon
        if (component.hasIconFromUrl()) {
            renderIconFromUrl(context, component.props, size, iconImageView);
        } else {
            renderIconFromResource(context, component.props, size, iconImageView);
        }

        //Render badge
        if (component.props.badgeImage == 0) {
            iconBadgeView.setVisibility(View.INVISIBLE);
        } else {
            final Drawable badgeImage = ContextCompat.getDrawable(context,
                    component.props.badgeImage);
            iconBadgeView.setImageDrawable(badgeImage);
            iconBadgeView.setVisibility(View.VISIBLE);
        }

        return iconView;
    }

    private void renderIconFromUrl(final Context context, final IconProps props, final int size, final ImageView iconImageView) {
        Picasso.with(context)
                .load(props.iconUrl)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .transform(new CircleTransform())
                .resize(size, size)
                .centerInside()
                .noFade()
                .error(props.iconImage)
                .into(iconImageView);
    }

    private void renderIconFromResource(final Context context, final IconProps props, final int size, final ImageView iconImageView) {
        Picasso.with(context)
                .load(props.iconImage)
                .transform(new CircleTransform())
                .resize(size, size)
                .centerInside()
                .noFade()
                .into(iconImageView);
    }
}
