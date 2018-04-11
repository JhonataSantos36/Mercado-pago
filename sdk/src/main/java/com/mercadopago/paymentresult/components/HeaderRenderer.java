package com.mercadopago.paymentresult.components;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.paymentresult.props.HeaderProps;

public class HeaderRenderer extends Renderer<Header> {

    @Override
    public View render(@NonNull final Header component, @NonNull final Context context, final ViewGroup parent) {
        final View headerView = inflate(R.layout.mpsdk_payment_result_header, parent);
        final ViewGroup headerContainer = headerView.findViewById(R.id.mpsdkPaymentResultContainerHeader);
        final MPTextView titleTextView = headerView.findViewById(R.id.mpsdkHeaderTitle);
        final ViewGroup iconParentViewGroup = headerView.findViewById(R.id.iconContainer);
        final MPTextView labelTextView = headerView.findViewById(R.id.mpsdkHeaderLabel);
        final int background = ContextCompat.getColor(context, component.props.background);
        final int statusBarColor = ContextCompat.getColor(context, component.props.statusBarColor);

        //Render height
        if (component.props.height.equals(HeaderProps.HEADER_MODE_WRAP)) {
            wrapHeight(headerContainer);
        } else if (component.props.height.equals(HeaderProps.HEADER_MODE_STRETCH)) {
            stretchHeight(headerContainer);
        }

        headerContainer.setBackgroundColor(background);
        setStatusBarColor(statusBarColor, context);
        setText(labelTextView, component.props.label);

        //Render icon
        RendererFactory.create(context, component.getIconComponent()).render(iconParentViewGroup);

        //Render title
        setText(titleTextView, component.props.title);

        return headerView;
    }

    private void setStatusBarColor(final int statusBarColor, final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = ((Activity) context).getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(statusBarColor);
        }
    }
}
