package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaserber on 11/13/17.
 */

public class AccreditationTime extends Component<AccreditationTime.Props> {

    public AccreditationTime(@NonNull final Props props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public List<AccreditationComment> getAccreditationCommentComponents() {
        List<AccreditationComment> componentList = new ArrayList<>();

        for (String comment : props.accreditationComments) {
            final AccreditationComment.Props commentProps = new AccreditationComment.Props.Builder()
                    .setComment(comment)
                    .build();

            final AccreditationComment component = new AccreditationComment(commentProps, getDispatcher());

            componentList.add(component);
        }

        return componentList;
    }

    public boolean hasAccreditationComments() {
        return props.accreditationComments != null && !props.accreditationComments.isEmpty();
    }

    public static class Props {

        public final String accreditationMessage;
        public final List<String> accreditationComments;

        public Props(@NonNull final String accreditationMessage, final List<String> accreditationComments) {
            this.accreditationMessage = accreditationMessage;
            this.accreditationComments = accreditationComments;
        }

        public Props(@NonNull final Builder builder) {
            this.accreditationMessage = builder.accreditationMessage;
            this.accreditationComments = builder.accreditationComments;
        }

        public Builder toBuilder() {
            return new Props.Builder()
                    .setAccreditationMessage(this.accreditationMessage)
                    .setAccreditationComments(this.accreditationComments);
        }

        public static class Builder {
            public String accreditationMessage;
            public List<String> accreditationComments;

            public Builder setAccreditationMessage(String accreditationMessage) {
                this.accreditationMessage = accreditationMessage;
                return this;
            }

            public Builder setAccreditationComments(List<String> accreditationComments) {
                this.accreditationComments = accreditationComments;
                return this;
            }

            public Props build() {
                return new Props(this);
            }
        }
    }
}
