package com.mercadopago.model;

import com.mercadopago.lite.exceptions.ApiException;
import com.mercadopago.lite.model.Cause;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class ApiExceptionTest {

    @Test
    public void whenCauseIsNullThenContainsCauseShouldBeFalse() {
        ApiException apiException = new ApiException();
        assertFalse(apiException.containsCause(null));
    }

    @Test
    public void whenCauseIsEmptyThenContainsCauseShouldBeFalse() {
        ApiException apiException = new ApiException();
        apiException.setCause(new ArrayList<Cause>());
        assertFalse(apiException.containsCause("Some cause"));
    }

    @Test
    public void whenCauseIsNotInListOfCausesThenContainsCauseShouldBeFalse() {
        final Cause cause1 = new Cause();
        cause1.setCode("1");

        final Cause cause2 = new Cause();
        cause2.setCode("2");

        List<Cause> causeList = new ArrayList<Cause>() {{
            add(cause1);
            add(cause2);
        }};

        ApiException apiException = new ApiException();
        apiException.setCause(causeList);

        assertFalse(apiException.containsCause("Some cause"));
    }

    @Test
    public void whenCauseIsInListOfCausesThenContainsCauseShouldBeTrue() {
        final Cause cause1 = new Cause();
        cause1.setCode("1");

        final Cause cause2 = new Cause();
        cause2.setCode("Some cause");

        List<Cause> causeList = new ArrayList<Cause>() {{
            add(cause1);
            add(cause2);
        }};

        ApiException apiException = new ApiException();
        apiException.setCause(causeList);

        assertTrue(apiException.containsCause("Some cause"));
    }

}
