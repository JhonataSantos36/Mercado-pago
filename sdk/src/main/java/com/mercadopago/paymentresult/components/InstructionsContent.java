package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.model.InstructionActionInfo;
import com.mercadopago.model.InstructionReference;
import com.mercadopago.paymentresult.props.InstructionsActionsProps;
import com.mercadopago.paymentresult.props.InstructionsContentProps;
import com.mercadopago.paymentresult.props.InstructionsInfoProps;
import com.mercadopago.paymentresult.props.InstructionsReferencesProps;
import com.mercadopago.paymentresult.props.InstructionsTertiaryInfoProps;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaserber on 11/14/17.
 */

public class InstructionsContent extends Component<InstructionsContentProps> {

    public InstructionsContent(@NonNull final InstructionsContentProps props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public boolean hasInfo() {
        final List<String> info = props.instruction.getInfo();
        return info != null && !info.isEmpty();
    }

    public boolean hasReferences() {
        final List<InstructionReference> references = props.instruction.getReferences();
        return references != null && !references.isEmpty();
    }

    public boolean hasAccreditationTime() {
        final String accreditationMessage = props.instruction.getAcreditationMessage();
        final boolean hasMessage = accreditationMessage != null && !accreditationMessage.isEmpty();
        final List<String> accreditationComments = props.instruction.getAccreditationComments();
        final boolean hasComments = accreditationComments != null && !accreditationComments.isEmpty();
        return hasMessage || hasComments;
    }

    public boolean hasActions() {
        final List<InstructionActionInfo> instructionActionInfoList = props.instruction.getActions();
        if (instructionActionInfoList != null && !instructionActionInfoList.isEmpty()) {
            for (InstructionActionInfo actionInfo : instructionActionInfoList) {
                if (actionInfo.getTag().equals(InstructionActionInfo.Tags.LINK)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasTertiaryInfo() {
        final List<String> tertiaryInfoList = props.instruction.getTertiaryInfo();
        return tertiaryInfoList != null && !tertiaryInfoList.isEmpty();
    }

    public boolean needsBottomMargin() {
        return hasInfo() || hasReferences() || hasAccreditationTime();
    }

    public InstructionsInfo getInfoComponent() {
        List<String> content = new ArrayList<>();
        final List<String> info = props.instruction.getInfo();

        String title = "";
        boolean hasTitle = false;
        if (info.size() == 1 || (info.size() > 1 && info.get(1).isEmpty())) {
            title = info.get(0);
            hasTitle = true;
        }

        boolean firstSpaceFound = false;
        boolean secondSpaceFound = false;
        boolean hasBottomDivider = false;
        for (String text : info) {
            if (text.isEmpty()) {
                if (firstSpaceFound) {
                    secondSpaceFound = true;
                } else {
                    firstSpaceFound = true;
                }
            } else {
                if (!hasTitle || (firstSpaceFound && !secondSpaceFound)) {
                    content.add(text);
                } else if (firstSpaceFound && secondSpaceFound) {
                    hasBottomDivider = true;
                }
            }
        }

        final InstructionsInfoProps infoProps = new InstructionsInfoProps.Builder()
                .setInfoTitle(title)
                .setInfoContent(content)
                .setBottomDivider(hasBottomDivider)
                .build();

        return new InstructionsInfo(infoProps, getDispatcher());
    }

    public InstructionsReferences getReferencesComponent() {
        final List<String> info = props.instruction.getInfo();
        int spacesFound = 0;
        String title = "";
        for (String text : info) {
            if (text.isEmpty()) {
                spacesFound++;
            } else if (spacesFound == 2) {
                title = text;
                break;
            }
        }

        final InstructionsReferencesProps referencesProps = new InstructionsReferencesProps.Builder()
                .setTitle(title)
                .setReferences(props.instruction.getReferences())
                .build();

        return new InstructionsReferences(referencesProps, getDispatcher());
    }


    public InstructionsTertiaryInfo getTertiaryInfoComponent() {
        final InstructionsTertiaryInfoProps tertiaryInfoProps = new InstructionsTertiaryInfoProps.Builder()
                .setTertiaryInfo(props.instruction.getTertiaryInfo())
                .build();

        return new InstructionsTertiaryInfo(tertiaryInfoProps, getDispatcher());
    }

    public AccreditationTime getAccreditationTimeComponent() {
        final AccreditationTime.Props accreditationTimeProps = new AccreditationTime.Props.Builder()
                .setAccreditationMessage(props.instruction.getAcreditationMessage())
                .setAccreditationComments(props.instruction.getAccreditationComments())
                .build();

        return new AccreditationTime(accreditationTimeProps, getDispatcher());
    }

    public InstructionsActions getActionsComponent() {
        final InstructionsActionsProps instructionsActionsProps = new InstructionsActionsProps.Builder()
                .setInstructionsActions(props.instruction.getActions())
                .build();

        return new InstructionsActions(instructionsActionsProps, getDispatcher());
    }
}
