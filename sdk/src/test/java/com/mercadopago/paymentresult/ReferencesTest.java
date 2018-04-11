package com.mercadopago.paymentresult;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.mocks.Instructions;
import com.mercadopago.model.Instruction;
import com.mercadopago.paymentresult.components.InstructionReferenceComponent;
import com.mercadopago.paymentresult.components.InstructionsReferences;
import com.mercadopago.paymentresult.props.InstructionsReferencesProps;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.mock;

/**
 * Created by vaserber on 23/11/2017.
 */

public class ReferencesTest {

    private ActionDispatcher dispatcher;

    @Before
    public void setup() {
        dispatcher = mock(ActionDispatcher.class);
    }

    @Test
    public void testCreateReferenceComponentsList() {
        final Instruction instruction = Instructions.getBanamexBankTransferInstruction();
        final InstructionsReferencesProps props = new InstructionsReferencesProps.Builder()
                .setReferences(instruction.getReferences())
                .build();
        final InstructionsReferences component = new InstructionsReferences(props, dispatcher);

        Assert.assertNotNull(instruction.getReferences());
        Assert.assertFalse(instruction.getReferences().isEmpty());

        final List<InstructionReferenceComponent> referenceComponentList = component.getReferenceComponents();

        Assert.assertNotNull(referenceComponentList);
        Assert.assertFalse(referenceComponentList.isEmpty());
        Assert.assertEquals(instruction.getReferences().size(), referenceComponentList.size());
    }

    @Test
    public void testReferenceComponentsListPropsAreValid() {
        final Instruction instruction = Instructions.getBanamexBankTransferInstruction();
        final InstructionsReferencesProps props = new InstructionsReferencesProps.Builder()
                .setReferences(instruction.getReferences())
                .build();
        final InstructionsReferences component = new InstructionsReferences(props, dispatcher);

        final List<InstructionReferenceComponent> referenceComponentList = component.getReferenceComponents();

        for (InstructionReferenceComponent instructionReferenceComponent: referenceComponentList) {
            Assert.assertNotNull(instructionReferenceComponent.props.reference);
        }
    }
}