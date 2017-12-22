package com.mercadopago.plugins;

import android.support.annotation.NonNull;

import com.mercadopago.components.Component;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.components.ToolbarComponent;
import com.mercadopago.model.PaymentData;

import java.util.Map;

/**
 * Created by nfortuna on 12/13/17.
 */

public abstract class PluginComponent extends Component<PluginComponent.Props> {

    static {
        RendererFactory.register(PluginComponent.class, PluginRenderer.class);
    }

    public PluginComponent(@NonNull Props props) {
        super(props);
    }

    public static class Props {

        public final Map<String, Object> data;
        public final String toolbarTitle;
        public final boolean toolbarVisible;

        public Props(@NonNull final PluginComponent.Props.Builder builder) {
            this.data = builder.data;
            this.toolbarTitle = builder.toolbarTitle;
            this.toolbarVisible = builder.toolbarVisible;
        }

        public PluginComponent.Props.Builder toBuilder() {
            return new PluginComponent.Props.Builder()
                    .setData(this.data)
                    .setToolbarTitle(this.toolbarTitle)
                    .setToolbarVisible(this.toolbarVisible);
        }

        public static class Builder {
            public Map<String, Object> data;
            public String paymentTypeId;
            public PaymentData paymentData;
            public String toolbarTitle = "";
            public boolean toolbarVisible = true;

            public PluginComponent.Props.Builder setData(@NonNull final Map<String, Object> data) {
                this.data = data;
                return this;
            }

            public PluginComponent.Props.Builder setPaymentTypeId(@NonNull final String paymentTypeId) {
                this.paymentTypeId = paymentTypeId;
                return this;
            }

            public PluginComponent.Props.Builder setPaymentData(@NonNull final PaymentData paymentData) {
                this.paymentData = paymentData;
                return this;
            }

            public PluginComponent.Props.Builder setToolbarTitle(@NonNull final String toolbarTitle) {
                this.toolbarTitle = toolbarTitle;
                return this;
            }

            public PluginComponent.Props.Builder setToolbarVisible(@NonNull final boolean toolbarVisible) {
                this.toolbarVisible = toolbarVisible;
                return this;
            }

            public PluginComponent.Props build() {
                return new PluginComponent.Props(this);
            }
        }
    }

    public ToolbarComponent getToolbarComponent() {
        final ToolbarComponent.Props props = new ToolbarComponent.Props.Builder()
                .setToolbarTitle(this.props.toolbarTitle)
                .setToolbarVisible(this.props.toolbarVisible)
                .build();
        return new ToolbarComponent(props);
    }
}