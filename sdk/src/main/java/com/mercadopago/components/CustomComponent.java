package com.mercadopago.components;

/**
 * Created by mromar on 11/22/17.
 */

public class CustomComponent extends Component<Object, Object> {

    public CustomComponent() {
        //Pass empty object as props, external objects should not have props because are not known from the SDK side.
        super(new Object());
    }
}
