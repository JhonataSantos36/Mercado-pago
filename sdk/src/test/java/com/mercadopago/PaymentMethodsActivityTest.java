package com.mercadopago;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.PaymentMethod;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;

/**
 * Created by mreverter on 1/2/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk=19)
public class PaymentMethodsActivityTest {


    @Test
    public void testText() {
        PaymentMethodsActivity activity = Robolectric.buildActivity(PaymentMethodsActivity.class).attach().get();
        PaymentMethodsActivity spyActivity = Mockito.spy(activity);

        final List<PaymentMethod> list = new ArrayList<>();
        MercadoPago mercadoPago = getMercadoPago(spyActivity.getApplicationContext());

        MercadoPago spyMercadoPago = Mockito.spy(mercadoPago);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Callback<List<PaymentMethod>>) invocation.getArguments()[0]).success(list, null);
                return null;
            }
        }).when(spyMercadoPago).getPaymentMethods(
                Mockito.any(Callback.class));

        Mockito.when(spyActivity.createMercadoPago("6c0d81bc - 99c1-4de8-9976-c8d1d62cd4f2")).thenReturn(spyMercadoPago);

        spyActivity.onCreate(null);

        RecyclerView.Adapter adapter = ((RecyclerView) activity.findViewById(R.id.payment_methods_list)).getAdapter();
        Assert.assertEquals(adapter.getItemCount(), 0);


        PaymentMethod paymentMethodMock = Mockito.mock(PaymentMethod.class);
        Mockito.when(paymentMethodMock.getId()).thenReturn("Id de mentira");
        paymentMethodMock.getId();

    }
    @Test
    public void testt() {
        PaymentMethodsActivity activity = Robolectric.buildActivity(PaymentMethodsActivity.class).setup().get();
        RecyclerView.Adapter adapter = ((RecyclerView) activity.findViewById(R.id.payment_methods_list)).getAdapter();
        Assert.assertTrue(adapter.getItemCount() != 0);
    }

    private MercadoPago getMercadoPago(Context context) {
        return new MercadoPago.Builder()
                .setContext(context)
                .setPublicKey("6c0d81bc - 99c1-4de8-9976-c8d1d62cd4f2")
                .build();
    }
}
