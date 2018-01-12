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

public abstract class PluginComponent<T> extends Component<PluginComponent.Props, T> {

    static {
        RendererFactory.register(PluginComponent.class, PluginRenderer.class);
    }

    public PluginComponent(@NonNull final PluginComponent.Props props) {
        super(props);
    }

    public ToolbarComponent getToolbarComponent() {
        final ToolbarComponent.Props props = new ToolbarComponent.Props.Builder()
                .setToolbarTitle(this.props.toolbarTitle)
                .setToolbarVisible(this.props.toolbarVisible)
                .build();
        return new ToolbarComponent(props);
    }

    public static class Props {

        public final Map<String, Object> data;
        public final PaymentData paymentData;
        public final String toolbarTitle;
        public final boolean toolbarVisible;

        public Props(@NonNull final PluginComponent.Props.Builder builder) {
            this.data = builder.data;
            this.toolbarTitle = builder.toolbarTitle;
            this.toolbarVisible = builder.toolbarVisible;
            this.paymentData = builder.paymentData;
        }

        public Builder toBuilder() {
            return new Builder()
                .setData(this.data)
                .setToolbarVisible(this.toolbarVisible)
                .setToolbarTitle(this.toolbarTitle)
                .setPaymentData(this.paymentData);
        }

        public static class Builder {
            public Map<String, Object> data;
            public PaymentData paymentData;
            public String toolbarTitle = "";
            public boolean toolbarVisible = true;

            public PluginComponent.Props.Builder setData(@NonNull final Map<String, Object> data) {
                this.data = data;
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
}