package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.lite.constants.ProcessingModes;
import com.mercadopago.paymentresult.props.InstructionsContentProps;
import com.mercadopago.paymentresult.props.InstructionsProps;
import com.mercadopago.paymentresult.props.InstructionsSecondaryInfoProps;
import com.mercadopago.paymentresult.props.InstructionsSubtitleProps;

import java.util.List;

/**
 * Created by vaserber on 11/13/17.
 */

public class Instructions extends Component<InstructionsProps, Void> {

    public Instructions(@NonNull final InstructionsProps props,
                        @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public InstructionsSubtitle getSubtitleComponent() {
        final InstructionsSubtitleProps subtitleProps = new InstructionsSubtitleProps.Builder()
                .setSubtitle(props.instruction.getSubtitle())
                .build();

        return new InstructionsSubtitle(subtitleProps, getDispatcher());
    }

    public InstructionsContent getContentComponent() {
        final InstructionsContentProps contentProps = new InstructionsContentProps.Builder()
                .setInstruction(props.instruction)
                .build();

        return new InstructionsContent(contentProps, getDispatcher());
    }

    public InstructionsSecondaryInfo getSecondaryInfoComponent() {
        final InstructionsSecondaryInfoProps secondaryInfoProps = new InstructionsSecondaryInfoProps.Builder()
                .setSecondaryInfo(props.instruction.getSecondaryInfo())
                .build();

        return new InstructionsSecondaryInfo(secondaryInfoProps, getDispatcher());
    }

    public boolean hasSubtitle() {
        final String subtitle = props.instruction.getSubtitle();
        return subtitle != null && !subtitle.isEmpty();
    }

    public boolean hasSecondaryInfo() {
        final List<String> secondaryInfoList = props.instruction.getSecondaryInfo();
        return secondaryInfoList != null && !secondaryInfoList.isEmpty();
    }

    public boolean shouldShowEmailInSecondaryInfo() {
        return props.processingMode.equals(ProcessingModes.AGGREGATOR);
    }

}
