package com.mercadopago.utils;

import java.util.List;

/**
 * Created by marlanti on 7/6/17.
 */

public class MVPStructure <P,R,V,T>{

    private P presenter;
    private V view;
    private R provider;
    private List<T> objects;

    public P getPresenter() {
        return presenter;
    }

    public void setPresenter(P presenter) {
        this.presenter = presenter;
    }

    public V getView() {
        return view;
    }

    public void setView(V view) {
        this.view = view;
    }

    public R getProvider() {
        return provider;
    }

    public void setProvider(R provider) {
        this.provider = provider;
    }

    public List<T> getObjects() {
        return objects;
    }

    public void setObjects(List<T> objects) {
        this.objects = objects;
    }
}