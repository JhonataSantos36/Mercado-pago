package com.mercadopago.paymentresult.components;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.paymentresult.components.Header;
import com.mercadopago.paymentresult.props.HeaderProps;

/**
 * Created by vaserber on 10/20/17.
 */

public class HeaderRenderer extends Renderer<Header> {

    @Override
    public View render() {

        final View headerView = LayoutInflater.from(context).inflate(R.layout.mpsdk_payment_result_header, null, false);
        final ViewGroup headerContainer = headerView.findViewById(R.id.mpsdkPaymentResultContainerHeader);
        final MPTextView titleTextView = headerView.findViewById(R.id.mpsdkHeaderTitle);
        final ViewGroup iconParentViewGroup = headerView.findViewById(R.id.iconContainer);
        final MPTextView labelTextView = headerView.findViewById(R.id.mpsdkHeaderLabel);
        final int background = ContextCompat.getColor(context, component.props.background);
        final int statusBarColor = ContextCompat.getColor(context, component.props.statusBarColor);

        renderHeight(headerContainer);
        headerContainer.setBackgroundColor(background);
        setStatusBarColor(statusBarColor);
        setText(labelTextView, component.props.label);
        renderIcon(iconParentViewGroup);
        renderTitle(titleTextView);

        return headerView;
    }

    private void setStatusBarColor(final int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = ((Activity) context).getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(statusBarColor);
        }
    }

    private void renderIcon(@NonNull final ViewGroup parent) {
        final Renderer iconRenderer = RendererFactory.create(context, component.getIconComponent());
        View icon = iconRenderer.render();
        parent.addView(icon);
    }

    private void renderTitle(@NonNull final MPTextView titleTextView) {
        if (component.props.amountFormat == null) {
            setText(titleTextView, component.props.title);
        } else {
            titleTextView.setText(component.props.amountFormat.formatTextWithAmount(component.props.title));
        }
    }

    private void renderHeight(ViewGroup viewGroup) {
        if (component.props.height.equals(HeaderProps.HEADER_MODE_WRAP)) {
            wrapHeight(viewGroup);
        } else if (component.props.height.equals(HeaderProps.HEADER_MODE_STRETCH)) {
            stretchHeight(viewGroup);
        }
    }
}
