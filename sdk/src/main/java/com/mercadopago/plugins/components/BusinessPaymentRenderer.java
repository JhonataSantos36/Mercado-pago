package com.mercadopago.plugins.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.mercadopago.R;
import com.mercadopago.components.Button;
import com.mercadopago.components.CompactComponent;
import com.mercadopago.components.Footer;
import com.mercadopago.components.HelpComponent;
import com.mercadopago.components.PaymentMethodComponent;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.components.TotalAmount;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentTypes;
import com.mercadopago.paymentresult.components.Header;
import com.mercadopago.paymentresult.props.HeaderProps;
import com.mercadopago.plugins.model.BusinessPayment;
import com.mercadopago.plugins.model.ExitAction;

import java.math.BigDecimal;

public class BusinessPaymentRenderer extends Renderer<BusinessPaymentContainer> {

    @Override
    protected View render(@NonNull final BusinessPaymentContainer component,
                          @NonNull final Context context,
                          @Nullable final ViewGroup parent) {

        final LinearLayout mainContentContainer = CompactComponent.createLinearContainer(context);
        final ScrollView scrollView = CompactComponent.createScrollContainer(context);
        scrollView.addView(mainContentContainer);

        final View header = renderHeader(component, mainContentContainer);
        final ViewTreeObserver vto = scrollView.getViewTreeObserver();

        if (component.props.hasHelp()) {
            View helpView = new HelpComponent(component.props.getHelp()).render(mainContentContainer);
            mainContentContainer.addView(helpView);
            vto.addOnGlobalLayoutListener(bodyCorrection(mainContentContainer, scrollView, helpView));
        }

        if (component.props.shouldShowPaymentMethod()) {
            View paymentMethodView = renderPaymentMethod(component.props, mainContentContainer);
            if (!component.props.hasHelp()) {
                vto.addOnGlobalLayoutListener(bodyCorrection(mainContentContainer, scrollView, paymentMethodView));
            }
        }

        if (mainContentContainer.getChildCount() == 0) {
            vto.addOnGlobalLayoutListener(noBodyCorrection(mainContentContainer, scrollView, header));
        }

        renderFooter(component, mainContentContainer);

        return scrollView;
    }

    private View renderPaymentMethod(final BusinessPayment props, final LinearLayout mainContentContainer) {
        //TODO
        PaymentMethod pm = new PaymentMethod("123", "asd", PaymentTypes.CREDIT_CARD);
        TotalAmount.TotalAmountProps totalAmountProps = new TotalAmount.TotalAmountProps("ARS", new BigDecimal(100), null, null);
        PaymentMethodComponent paymentMethodComponent = new PaymentMethodComponent(new PaymentMethodComponent.PaymentMethodProps(pm, "1234", "ASD das", totalAmountProps));
        RendererFactory.create(mainContentContainer.getContext(), paymentMethodComponent).render(mainContentContainer);
        return mainContentContainer.findViewById(R.id.mpsdkPaymentMethodContainer);
    }

    private ViewTreeObserver.OnGlobalLayoutListener bodyCorrection(final LinearLayout mainContentContainer,
                                                                   final ScrollView scrollView,
                                                                   final View toCorrect) {
        return new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int diffHeight = calculateDiff(mainContentContainer, scrollView);
                if (diffHeight > 0) {
                    toCorrect.setPadding(toCorrect.getPaddingLeft(), (int) Math.ceil(diffHeight / 2f), toCorrect.getPaddingRight(),
                            (int) Math.ceil(diffHeight / 2f));
                }
            }
        };
    }

    private void renderFooter(@NonNull final BusinessPaymentContainer component, final LinearLayout linearLayout) {
        Button.Props primaryButtonProps = getButtonProps(component.props.getPrimaryAction());
        Button.Props secondaryButtonProps = getButtonProps(component.props.getSecondaryAction());
        Footer footer = new Footer(new Footer.Props(primaryButtonProps, secondaryButtonProps), component.getDispatcher());
        View footerView = footer.render(linearLayout);
        linearLayout.addView(footerView);
    }

    @Nullable
    private Button.Props getButtonProps(final ExitAction action) {
        if (action != null) {
            String label = action.getName();
            return new Button.Props(label, action);
        }
        return null;
    }

    @NonNull
    private View renderHeader(@NonNull final BusinessPaymentContainer component, @NonNull final LinearLayout linearLayout) {
        Context context = linearLayout.getContext();
        Header header = new Header(HeaderProps.from(component.props, context), component.getDispatcher());
        View render = RendererFactory.create(context, header).render(linearLayout);
        return render.findViewById(R.id.mpsdkPaymentResultContainerHeader);
    }

    @NonNull
    private ViewTreeObserver.OnGlobalLayoutListener noBodyCorrection(final LinearLayout mainContentContainer,
                                                                     final ScrollView scrollView,
                                                                     final View header) {
        return new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int diffHeight = calculateDiff(mainContentContainer, scrollView);
                if (diffHeight > 0) {
                    header.setPadding(header.getPaddingLeft(), header.getPaddingTop(), header.getPaddingRight(),
                            (header.getPaddingBottom() + diffHeight));
                }
            }
        };
    }

    private int calculateDiff(final LinearLayout mainContentContainer, final ScrollView scrollView) {
        int linearHeight = mainContentContainer.getMeasuredHeight();
        int scrollHeight = scrollView.getMeasuredHeight();
        return scrollHeight - linearHeight;
    }
}
