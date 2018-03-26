package com.mercadopago.mvp;

import java.lang.ref.WeakReference;

/**
 * Base class for all <code>MvpPresenter</code> implementations.
 *
 * All <code>MvpPresenter</code>'s implementations MUST NOT contain references to Android library
 * or api calls, that's what it is <code>ResourcesProvider</code> made for.
 *
 * See also {@link ResourcesProvider}
 * See also {@link MvpView}
 */

public abstract class MvpPresenter<V extends MvpView, R extends ResourcesProvider> {

    private transient WeakReference<V> mView;
    private transient R resourcesProvider;

    public void attachResourcesProvider(final R resourcesProvider){
        this.resourcesProvider = resourcesProvider;
    }

    public void attachView(V view) {
        mView = new WeakReference<>(view);
    }

    public void detachView() {
        if (mView != null) {
            mView.clear();
            mView = null;
        }
    }

    public boolean isViewAttached() {
        return mView != null && mView.get() != null;
    }

    public V getView() {
        return mView == null ? null : mView.get();
    }

    public R getResourcesProvider(){
        return resourcesProvider;
    }

    public void detachResourceProvider() {
        resourcesProvider = null;
    }
}








