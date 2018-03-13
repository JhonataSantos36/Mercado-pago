package com.mercadopago.review_and_confirm.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.uicontrollers.payercosts.PayerCostColumn;
import com.mercadopago.util.CurrenciesUtil;
import java.math.BigDecimal;

import static com.mercadopago.util.TextUtils.isEmpty;

/**
 * Created by mromar on 2/28/18.
 */

public class FullSummaryRenderer extends Renderer<FullSummary> {

    @Override
    public View render(@NonNull final FullSummary component, @NonNull final Context context, final ViewGroup parent) {
        final View summaryView = inflate(R.layout.mpsdk_full_summary_component, parent);
        final MPTextView totalAmountTextView = summaryView.findViewById(R.id.mpsdkReviewSummaryTotalText);
        final FrameLayout payerCostContainer = summaryView.findViewById(R.id.mpsdkReviewSummaryPayerCostContainer);
        final MPTextView disclaimerTextView = summaryView.findViewById(R.id.mpsdkDisclaimer);
        final LinearLayout summaryDetailsContainer = summaryView.findViewById(R.id.mpsdkSummaryDetails);
        final LinearLayout reviewSummaryPayContainer = summaryView.findViewById(R.id.mpsdkReviewSummaryPay);
        final View firstSeparator = summaryView.findViewById(R.id.mpsdkFirstSeparator);
        final LinearLayout totalAmountContainer = summaryView.findViewById(R.id.mpsdkReviewSummaryTotal);
        final View secondSeparator = summaryView.findViewById(R.id.mpsdkSecondSeparator);
        final LinearLayout disclaimerLinearLayout = summaryView.findViewById(R.id.disclaimer);

        //summaryDetails list
        for (AmountDescription amountDescription : component.getAmountDescriptionComponents()) {
            final Renderer amountDescriptionRenderer = RendererFactory.create(context, amountDescription);
            final View amountView = amountDescriptionRenderer.render();
            summaryDetailsContainer.addView(amountView);
        }

        if (component.hasToRenderPayerCost()) {
            //payer cost
            PayerCostColumn payerCostColumn = new PayerCostColumn(context, component.props.summaryModel.currencyId,
                component.props.summaryModel.siteId, component.props.summaryModel.getInstallmentsRate(),
                component.props.summaryModel.getInstallments(), component.props.summaryModel.getPayerCostTotalAmount(),
                component.props.summaryModel.getInstallmentAmount());
            payerCostColumn.inflateInParent(payerCostContainer, true);
            payerCostColumn.initializeControls();
            payerCostColumn.drawPayerCostWithoutTotal();
        } else {
            reviewSummaryPayContainer.setVisibility(View.GONE);
            firstSeparator.setVisibility(View.GONE);
        }

        //disclaimer
        if (!isEmpty(component.props.summaryModel.cftPercent)) {
            String disclaimer = getDisclaimer(component, context);
            final Renderer disclaimerRenderer = RendererFactory.create(context, component.getDisclaimerComponent(disclaimer));
            final View disclaimerView = disclaimerRenderer.render();
            disclaimerLinearLayout.addView(disclaimerView);
        }

        //total
        setText(totalAmountTextView,
            getFormattedAmount(component.getTotalAmount(), component.props.summaryModel.currencyId));
        totalAmountContainer.setVisibility(component.getTotalAmount() == null ? View.GONE : View.VISIBLE);
        secondSeparator.setVisibility(component.getTotalAmount() == null ? View.GONE : View.VISIBLE);

        //disclaimer
        setText(disclaimerTextView, component.getSummary().getDisclaimerText());

        return summaryView;
    }

    private Spanned getFormattedAmount(BigDecimal amount, String currencyId) {
        return amount != null && !isEmpty(currencyId) ? CurrenciesUtil.getFormattedAmount(amount, currencyId) : null;
    }

    public String getDisclaimer(FullSummary component, Context context) {
        StringBuilder stringBuilder = new StringBuilder();

        if (!isEmpty(component.props.summaryModel.cftPercent)) {
            stringBuilder.append(context.getString(R.string.mpsdk_installments_cft));
            stringBuilder.append(" ");
            stringBuilder.append(component.props.summaryModel.cftPercent);
        }

        return stringBuilder.toString();
    }
}
