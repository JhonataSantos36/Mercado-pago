package com.mercadopago.review_and_confirm.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.util.CurrenciesUtil;

import static com.mercadopago.util.TextUtils.isEmpty;
import static com.mercadopago.util.TextUtils.isNotEmpty;

public class CompactSummaryRenderer extends Renderer<CompactSummary> {

    @Override
    public View render(@NonNull final CompactSummary component, @NonNull final Context context, final ViewGroup parent) {
        final View summaryView = inflate(R.layout.mpsdk_compact_summary_component, parent);
        final MPTextView totalAmountTextView = summaryView.findViewById(R.id.mpsdkTotalAmount);
        final MPTextView itemTitleTextView = summaryView.findViewById(R.id.mpsdkItemTitle);
        final LinearLayout disclaimerLinearLayout = summaryView.findViewById(R.id.disclaimer);

        setText(totalAmountTextView, CurrenciesUtil.getSpannedAmountWithCurrencySymbol(component.props.getTotalAmount(), component.props.currencyId));
        setText(itemTitleTextView, getItemTitle(component.props.title, context));

        if (isNotEmpty(component.props.cftPercent)) {
            String disclaimer = getDisclaimer(component, context);
            final Renderer disclaimerRenderer = RendererFactory.create(context, component.getDisclaimerComponent(disclaimer));
            final View disclaimerView = disclaimerRenderer.render();
            disclaimerLinearLayout.addView(disclaimerView);
        }

        return summaryView;
    }

    private String getItemTitle(String itemTitle, Context context) {
        return isEmpty(itemTitle) ? getDefaultTitle(context) : itemTitle;
    }

    private String getDefaultTitle(Context context) {
        return context.getString(R.string.mpsdk_review_summary_product);
    }

    private String getDisclaimer(CompactSummary component, Context context) {
        StringBuilder stringBuilder = new StringBuilder();

        if (!isEmpty(component.props.cftPercent)) {
            stringBuilder.append(context.getString(R.string.mpsdk_installments_cft));
            stringBuilder.append(" ");
            stringBuilder.append(component.props.cftPercent);
        }

        return stringBuilder.toString();
    }
}
