package com.mercadopago.providers;

/**
 * Created by vaserber on 2/2/17.
 */

public class CheckoutProviderImpl {

//    private MercadoPagoServices mercadoPago;
//    private Context mContext;
//    private String mMerchantPublicKey;
//
//    public CheckoutProviderImpl(Context context, String publicKey) throws IllegalStateException {
//        if (publicKey == null) {
//            throw new IllegalStateException("public key not set");
//        } else if (context == null) {
//            throw new IllegalStateException("context not set");
//        }
//        mContext = context;
//        mMerchantPublicKey = publicKey;
//        mercadoPago = createMercadoPago(context, publicKey);
//    }
//
//    protected MercadoPagoServices createMercadoPago(Context context, String publicKey) {
//        return new MercadoPagoServices.Builder()
//                .setContext(context)
//                .setPublicKey(publicKey)
//                .build();
//    }
//
//    @Override
//    public void getPaymentMethodSearch(final OnResourcesRetrievedCallback<PaymentMethodSearch> resourcesRetrievedCallback) {
//        CheckoutPreference checkoutPreference = MercadoPagoContext.getInstance().getCheckoutPreference();
//        mercadoPago.getPaymentMethodSearch(checkoutPreference.getAmount(), checkoutPreference.getExcludedPaymentTypes(),
//                checkoutPreference.getExcludedPaymentMethods(), checkoutPreference.getPayer(), false,
//                new Callback<PaymentMethodSearch>() {
//                    @Override
//                    public void success(PaymentMethodSearch paymentMethodSearch) {
//                        resourcesRetrievedCallback.onSuccess(paymentMethodSearch);
//                    }
//
//                    @Override
//                    public void failure(ApiException apiException) {
////                        MPException exception = new MPException(apiException);
//                        MercadoPagoError mercadoPagoError = new MercadoPagoError(apiException);
//                        resourcesRetrievedCallback.onFailure(mercadoPagoError);
//                    }
//                });
//    }
//
//    @Override
//    public void getCustomer(final OnResourcesRetrievedCallback<Customer> resourcesRetrievedCallback) {
//        ServicePreference servicePreference = MercadoPagoContext.getInstance().getServicePreference();
//        String getCustomerURL = servicePreference.getGetCustomerURL();
//        String getCustomerURI = servicePreference.getGetCustomerURI();
//        Map<String, String> additionalInfo = servicePreference.getGetCustomerAdditionalInfo();
//
//        CustomServiceHandler.getCustomer(mContext, getCustomerURL, getCustomerURI, additionalInfo, new Callback<Customer>() {
//            @Override
//            public void success(Customer customer) {
//                resourcesRetrievedCallback.onSuccess(customer);
//            }
//
//            @Override
//            public void failure(ApiException apiException) {
//                MercadoPagoError mercadoPagoError = new MercadoPagoError(apiException);
//                resourcesRetrievedCallback.onFailure(mercadoPagoError);
//            }
//        });
//
//    }
//
//    @Override
//    public void getCheckoutPreference(String checkoutPreferenceId, OnResourcesRetrievedCallback<CheckoutPreference> onResourcesRetrievedCallback) {
//
//    }
}