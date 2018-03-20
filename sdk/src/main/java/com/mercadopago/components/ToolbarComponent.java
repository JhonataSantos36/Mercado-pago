package com.mercadopago.components;

import android.support.annotation.NonNull;

public class ToolbarComponent extends Component<ToolbarComponent.Props, Void> {

    static {
        RendererFactory.register(ToolbarComponent.class, ToolbarRenderer.class);
    }

    public ToolbarComponent(@NonNull final Props props) {
        super(props);
    }

    public static class Props {

        public final String toolbarTitle;
        public final boolean toolbarVisible;

        public Props(@NonNull final Builder builder) {
            toolbarTitle = builder.toolbarTitle;
            toolbarVisible = builder.toolbarVisible;
        }

        public Builder toBuilder() {
            return new Builder()
                .setToolbarTitle(toolbarTitle)
                .setToolbarVisible(toolbarVisible);
        }

        public static class Builder {
            public String toolbarTitle = "";
            public boolean toolbarVisible = true;

            public Builder setToolbarTitle(@NonNull final String toolbarTitle) {
                this.toolbarTitle = toolbarTitle;
                return this;
            }

            public Builder setToolbarVisible(@NonNull final boolean toolbarVisible) {
                this.toolbarVisible = toolbarVisible;
                return this;
            }

            public Props build() {
                return new Props(this);
            }
        }
    }
}