package com.mercadopago.paymentresult;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.mocks.Instructions;
import com.mercadopago.model.Instruction;
import com.mercadopago.paymentresult.components.AccreditationComment;
import com.mercadopago.paymentresult.components.AccreditationTime;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.mock;

/**
 * Created by vaserber on 23/11/2017.
 */

public class AccreditationTimeTest {

    private ActionDispatcher dispatcher;

    @Before
    public void setup() {
        dispatcher = mock(ActionDispatcher.class);
    }

    @Test
    public void testCreateAccreditationCommentComponentsList() {
        final Instruction instruction = Instructions.getBoletoInstructionTicket();
        final AccreditationTime.Props props = new AccreditationTime.Props.Builder()
                .setAccreditationMessage(instruction.getAcreditationMessage())
                .setAccreditationComments(instruction.getAccreditationComments())
                .build();
        final AccreditationTime component = new AccreditationTime(props, dispatcher);

        Assert.assertNotNull(instruction.getAccreditationComments());
        Assert.assertFalse(instruction.getAccreditationComments().isEmpty());

        final List<AccreditationComment> accreditationCommentList = component.getAccreditationCommentComponents();

        Assert.assertNotNull(accreditationCommentList);
        Assert.assertFalse(accreditationCommentList.isEmpty());
        Assert.assertEquals(instruction.getAccreditationComments().size(), accreditationCommentList.size());
    }

    @Test
    public void testAccreditationCommentComponentsListPropsAreValid() {
        final Instruction instruction = Instructions.getBoletoInstructionTicket();
        final AccreditationTime.Props props = new AccreditationTime.Props.Builder()
                .setAccreditationMessage(instruction.getAcreditationMessage())
                .setAccreditationComments(instruction.getAccreditationComments())
                .build();
        final AccreditationTime component = new AccreditationTime(props, dispatcher);

        final List<AccreditationComment> accreditationCommentList = component.getAccreditationCommentComponents();

        for (AccreditationComment accreditationComment: accreditationCommentList) {
            Assert.assertNotNull(accreditationComment.props.comment);
        }
    }
}