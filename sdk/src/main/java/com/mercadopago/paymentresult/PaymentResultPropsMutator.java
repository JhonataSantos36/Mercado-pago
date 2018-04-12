package com.mercadopago.paymentresult;

import android.support.annotation.NonNull;

import com.mercadopago.components.Mutator;
import com.mercadopago.components.MutatorPropsListener;
import com.mercadopago.model.Instruction;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.paymentresult.props.HeaderProps;
import com.mercadopago.paymentresult.props.PaymentResultProps;

public class PaymentResultPropsMutator implements Mutator, PaymentResultPropsView {

    private MutatorPropsListener propsListener;

    //Component props with default values
    private PaymentResultProps props = new PaymentResultProps.Builder().build();

    @Override
    public void setPropsListener(MutatorPropsListener listener) {
        propsListener = listener;
    }

    @Override
    public void setPropPaymentResult(@NonNull final String currencyId,
                                     @NonNull final PaymentResult paymentResult,
                                     final boolean showLoading) {

        props = props.toBuilder()
                .setPaymentResult(paymentResult)
                .setCurrencyId(currencyId)
                .setHeaderMode(HeaderProps.HEADER_MODE_WRAP)
                .setLoading(showLoading)
                .build();
    }

    @Override
    public void setPropInstruction(@NonNull final Instruction instruction,
                                   @NonNull final String processingModeString,
                                   final boolean showLoading) {
        props = props.toBuilder()
                .setInstruction(instruction)
                .setLoading(showLoading)
                .setProcessingMode(processingModeString)
                .build();
    }

    /**
     * @deprecated Este método tiene que ser privado, se hizo publico para poder hacer
     * notificaciones condicionales. Esta no es la forma adecuada de hacerlo hay que definir algún
     * mecanismo de transacciones para eso. Mejor no tener el feature a tener algo mal implementado.
     */
    @Override
    @Deprecated
    public void notifyPropsChanged() {
        if (propsListener != null) {
            propsListener.onProps(props);
        }
    }

    /**
     * @deprecated Eliminar este método, no tiene sentido, los valores por default de un componente
     * se define al construir el componente, cualquier otra cosa es inaceptable.
     * Esto es código viejo.
     */
    @Deprecated
    public void renderDefaultProps() {
        notifyPropsChanged();
    }

}
