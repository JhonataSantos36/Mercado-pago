package com.mercadopago.plugins.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mercadopago.R;
import com.mercadopago.components.Button;
import com.mercadopago.components.Footer;
import com.mercadopago.components.PaymentMethod;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.components.TotalAmount;
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

        final LinearLayout mainContentContainer = createMainContainer(context);
        final ScrollView scrollView = createScrollContainer(mainContentContainer);
        final View header = renderHeader(component, mainContentContainer);
        final ViewTreeObserver vto = scrollView.getViewTreeObserver();

        if (component.props.hasHelp() || component.props.shouldShowPaymentMethod()) {
            ViewGroup help = addHelp(component.props.getHelp(), mainContentContainer);
            vto.addOnGlobalLayoutListener(helpCorrectionListener(mainContentContainer, scrollView, help));
        } else {
            vto.addOnGlobalLayoutListener(noHelpCorrectionListener(mainContentContainer, scrollView, header));
        }

        if (component.props.shouldShowPaymentMethod()) {
            renderPaymentMethod(component.props, mainContentContainer);
        }

        renderFooter(component, mainContentContainer);

        return scrollView;
    }

    private void renderPaymentMethod(final BusinessPayment props, final LinearLayout mainContentContainer) {
        //TODO
        com.mercadopago.model.PaymentMethod pm = new com.mercadopago.model.PaymentMethod("123", "asd", PaymentTypes.CREDIT_CARD);
        TotalAmount.TotalAmountProps totalAmountProps = new TotalAmount.TotalAmountProps("ARS", new BigDecimal(100), null, null);
        PaymentMethod paymentMethod = new PaymentMethod(new PaymentMethod.PaymentMethodProps(pm, "1234", "ASD das", totalAmountProps));
        RendererFactory.create(mainContentContainer.getContext(), paymentMethod).render(mainContentContainer);
    }

    private ViewTreeObserver.OnGlobalLayoutListener helpCorrectionListener(final LinearLayout mainContentContainer,
                                                                           final ScrollView scrollView,
                                                                           final ViewGroup help) {
        return new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int diffHeight = calculateDiff(mainContentContainer, scrollView);
                if (diffHeight > 0) {
                    help.setPadding(help.getPaddingLeft(), (int) Math.ceil(diffHeight / 2f), help.getPaddingRight(),
                            (int) Math.ceil(diffHeight / 2f));
                }
            }
        };
    }

    @NonNull
    private ViewTreeObserver.OnGlobalLayoutListener noHelpCorrectionListener(final LinearLayout mainContentContainer,
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

    private void renderFooter(@NonNull final BusinessPaymentContainer component, final LinearLayout linearLayout) {

        ExitAction primaryAction = component.props.getPrimaryAction();
        ExitAction secondaryAction = component.props.getSecondaryAction();
        Button.Props primaryButtonProps = null;
        Button.Props secondaryButtonProps = null;

        if (primaryAction != null) {
            String primaryLabel = primaryAction.getName();
            primaryButtonProps = new Button.Props(primaryLabel, primaryAction);
        }

        if (secondaryAction != null) {
            String secondaryLabel = secondaryAction.getName();
            secondaryButtonProps = new Button.Props(secondaryLabel, secondaryAction);
        }

        Footer footer = new Footer(new Footer.Props(primaryButtonProps, secondaryButtonProps), component.getDispatcher());
        View footerView = footer.render(linearLayout);
        linearLayout.addView(footerView);
    }

    private ViewGroup addHelp(final String help, final ViewGroup parent) {
        final View bodyErrorView = inflate(R.layout.mpsdk_payment_result_body_error, parent);
        ViewGroup helpContainer = bodyErrorView.findViewById(R.id.bodyErrorContainer);
        TextView errorTitle = bodyErrorView.findViewById(R.id.paymentResultBodyErrorTitle);
        TextView errorDescription = bodyErrorView.findViewById(R.id.paymentResultBodyErrorDescription);
        bodyErrorView.findViewById(R.id.paymentResultBodyErrorSecondDescription).setVisibility(View.GONE);
        errorTitle.setText(parent.getContext().getString(R.string.mpsdk_what_can_do));
        errorDescription.setText(help);
        return helpContainer;
    }

    private View renderHeader(@NonNull final BusinessPaymentContainer component, @NonNull final LinearLayout linearLayout) {
        Context context = linearLayout.getContext();
        Header header = new Header(HeaderProps.from(component.props, context), component.getDispatcher());
        View render = RendererFactory.create(context, header).render(linearLayout);
        return render.findViewById(R.id.mpsdkPaymentResultContainerHeader);
    }

    @NonNull
    private ScrollView createScrollContainer(final LinearLayout linearLayout) {
        ScrollView scrollView = new ScrollView(linearLayout.getContext());
        scrollView.setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        scrollView.addView(linearLayout);
        scrollView.setBackgroundColor(scrollView
                .getContext()
                .getResources()
                .getColor(R.color.mpsdk_white_background));
        return scrollView;
    }

    @NonNull
    private LinearLayout createMainContainer(@NonNull final Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(context.getResources().getColor(R.color.mpsdk_white));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);
        return linearLayout;
    }
}
