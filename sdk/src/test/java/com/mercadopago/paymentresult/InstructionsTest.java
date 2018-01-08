package com.mercadopago.paymentresult;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.constants.ProcessingModes;
import com.mercadopago.mocks.Instructions;
import com.mercadopago.model.Instruction;
import com.mercadopago.paymentresult.components.InstructionsContent;
import com.mercadopago.paymentresult.components.InstructionsSecondaryInfo;
import com.mercadopago.paymentresult.components.InstructionsSubtitle;
import com.mercadopago.paymentresult.props.InstructionsProps;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by vaserber on 22/11/2017.
 */

public class InstructionsTest {

    private ActionDispatcher dispatcher;

    @Before
    public void setup() {
        dispatcher = mock(ActionDispatcher.class);
    }

    @Test
    public void testInstructionHasSubtitle(){
        final Instruction instruction = Instructions.getRapipagoInstruction();
        final com.mercadopago.paymentresult.components.Instructions component =
                getInstructionsComponent(instruction);

        Assert.assertNotNull(instruction.getSubtitle());
        Assert.assertFalse(instruction.getSubtitle().isEmpty());
        Assert.assertTrue(component.hasSubtitle());
        Assert.assertNotNull(component.getSubtitleComponent());
    }

    @Test
    public void testSubtitlePropsAreValid() {
        final Instruction instruction = Instructions.getRapipagoInstruction();
        final com.mercadopago.paymentresult.components.Instructions component =
                getInstructionsComponent(instruction);

        final InstructionsSubtitle subtitle = component.getSubtitleComponent();
        Assert.assertNotNull(subtitle.props.subtitle);
        Assert.assertEquals(subtitle.props.subtitle, instruction.getSubtitle());
    }

    @Test
    public void testContentComponentIsValid() {
        final Instruction instruction = Instructions.getRapipagoInstruction();
        final com.mercadopago.paymentresult.components.Instructions component =
                getInstructionsComponent(instruction);

        final InstructionsContent content = component.getContentComponent();
        Assert.assertNotNull(content);
        Assert.assertEquals(content.props.instruction, instruction);
    }

    @Test
    public void testInstructionHasSecondaryInfo(){
        final Instruction instruction = Instructions.getRapipagoInstruction();
        final com.mercadopago.paymentresult.components.Instructions component =
                getInstructionsComponent(instruction);

        Assert.assertNotNull(instruction.getSecondaryInfo());
        Assert.assertFalse(instruction.getSecondaryInfo().isEmpty());
        Assert.assertTrue(component.hasSecondaryInfo());
        Assert.assertNotNull(component.getSecondaryInfoComponent());
    }

    @Test
    public void testSecondaryInfoPropsAreValid() {
        final Instruction instruction = Instructions.getRapipagoInstruction();
        final com.mercadopago.paymentresult.components.Instructions component =
                getInstructionsComponent(instruction);

        final InstructionsSecondaryInfo secondaryInfo = component.getSecondaryInfoComponent();
        Assert.assertNotNull(secondaryInfo.props.secondaryInfo);
        Assert.assertFalse(secondaryInfo.props.secondaryInfo.isEmpty());
        Assert.assertEquals(secondaryInfo.props.secondaryInfo, instruction.getSecondaryInfo());
    }

    @Test
    public void testOnAggregatorThenShowEmailInSecondaryInfo() {
        final Instruction instruction = Instructions.getRapipagoInstruction();
        final com.mercadopago.paymentresult.components.Instructions component =
                getInstructionsComponent(instruction);

        Assert.assertTrue(component.shouldShowEmailInSecondaryInfo());
    }

    @Test
    public void testOnGatewayThenDontShowEmailInSecondaryInfo() {
        final Instruction instruction = Instructions.getRapipagoInstruction();
        final InstructionsProps props = new InstructionsProps.Builder()
                .setProcessingMode(ProcessingModes.GATEWAY)
                .setInstruction(instruction)
                .build();

        final com.mercadopago.paymentresult.components.Instructions component =
                new com.mercadopago.paymentresult.components.Instructions(props, dispatcher);

        Assert.assertFalse(component.shouldShowEmailInSecondaryInfo());
    }

    private com.mercadopago.paymentresult.components.Instructions getInstructionsComponent(Instruction instruction) {
        final InstructionsProps props = new InstructionsProps.Builder()
                .setProcessingMode(ProcessingModes.AGGREGATOR)
                .setInstruction(instruction)
                .build();
        final com.mercadopago.paymentresult.components.Instructions component =
                new com.mercadopago.paymentresult.components.Instructions(props, dispatcher);
        return component;
    }

}
