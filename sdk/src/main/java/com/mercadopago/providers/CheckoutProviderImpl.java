package com.mercadopago.providers;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.provider.FontRequest;
import android.support.v4.provider.FontsContractCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.R;
import com.mercadopago.lite.model.PaymentTypes;
import com.mercadopago.lite.model.Sites;
import com.mercadopago.core.CheckoutStore;
import com.mercadopago.core.CustomServer;
import com.mercadopago.core.MercadoPagoServicesAdapter;
import com.mercadopago.lite.exceptions.CheckoutPreferenceException;
import com.mercadopago.exceptions.ExceptionHandler;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.lite.callbacks.Callback;
import com.mercadopago.lite.exceptions.ApiException;
import com.mercadopago.lite.model.Campaign;
import com.mercadopago.lite.model.Card;
import com.mercadopago.model.Customer;
import com.mercadopago.lite.model.Discount;
import com.mercadopago.model.MerchantPayment;
import com.mercadopago.lite.model.Payer;
import com.mercadopago.lite.model.Payment;
import com.mercadopago.model.PaymentBody;
import com.mercadopago.model.PaymentData;
import com.mercadopago.lite.model.PaymentMethodSearch;
import com.mercadopago.lite.model.Site;
import com.mercadopago.mvp.TaggedCallback;
import com.mercadopago.lite.preferences.CheckoutPreference;
import com.mercadopago.lite.preferences.PaymentPreference;
import com.mercadopago.lite.preferences.ServicePreference;
import com.mercadopago.uicontrollers.FontCache;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.MercadoPagoESC;
import com.mercadopago.util.MercadoPagoESCImpl;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.util.QueryBuilder;
import com.mercadopago.util.TextUtil;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CheckoutProviderImpl implements CheckoutProvider {

    private final ServicePreference servicePreference;
    private final Context context;
    private final MercadoPagoServicesAdapter mercadoPagoServicesAdapter;
    private final String publicKey;
    private final MercadoPagoESC mercadoPagoESC;
    private Handler mHandler;

    public CheckoutProviderImpl(Context context,
                                @NonNull String publicKey,
                                @NonNull String privateKey,
                                ServicePreference servicePreference,
                                boolean escEnabled) {
        this.context = context;
        this.publicKey = publicKey;
        mercadoPagoServicesAdapter = new MercadoPagoServicesAdapter(context, publicKey, privateKey);
        this.servicePreference = servicePreference;
        mercadoPagoESC = new MercadoPagoESCImpl(context, escEnabled);
    }

    @Override
    public void fetchFonts() {
        if (!FontCache.hasTypeface(FontCache.CUSTOM_REGULAR_FONT)) {
            fetchRegularFont();
        }
        if (!FontCache.hasTypeface(FontCache.CUSTOM_MONO_FONT)) {
            fetchMonoFont();
        }
        if (!FontCache.hasTypeface(FontCache.CUSTOM_LIGHT_FONT)) {
            fetchLightFont();
        }
    }

    private void fetchRegularFont() {
        FontsContractCompat.FontRequestCallback regularFontCallback = new FontsContractCompat
                .FontRequestCallback() {
            @Override
            public void onTypefaceRetrieved(Typeface typeface) {
                FontCache.setTypeface(FontCache.CUSTOM_REGULAR_FONT, typeface);
            }

            @Override
            public void onTypefaceRequestFailed(int reason) {
                //Do nothing
            }
        };
        FontsContractCompat.requestFont(context,
                getFontRequest(FontCache.FONT_ROBOTO, QueryBuilder.WIDTH_DEFAULT,
                        QueryBuilder.WEIGHT_DEFAULT, QueryBuilder.ITALIC_DEFAULT),
                regularFontCallback,
                getHandlerThreadHandler());
    }

    private void fetchLightFont() {
        FontsContractCompat.FontRequestCallback lightFontCallback = new FontsContractCompat
                .FontRequestCallback() {
            @Override
            public void onTypefaceRetrieved(Typeface typeface) {
                FontCache.setTypeface(FontCache.CUSTOM_LIGHT_FONT, typeface);
            }

            @Override
            public void onTypefaceRequestFailed(int reason) {
                //Do nothing
            }
        };
        FontsContractCompat.requestFont(context,
                getFontRequest(FontCache.FONT_ROBOTO, QueryBuilder.WIDTH_DEFAULT,
                        QueryBuilder.WEIGHT_LIGHT, QueryBuilder.ITALIC_DEFAULT),
                lightFontCallback,
                getHandlerThreadHandler());
    }

    private void fetchMonoFont() {
        FontsContractCompat.FontRequestCallback monoFontCallback = new FontsContractCompat
                .FontRequestCallback() {
            @Override
            public void onTypefaceRetrieved(Typeface typeface) {
                FontCache.setTypeface(FontCache.CUSTOM_MONO_FONT, typeface);
            }

            @Override
            public void onTypefaceRequestFailed(int reason) {
                //Do nothing
            }
        };
        FontsContractCompat.requestFont(context,
                getFontRequest(FontCache.FONT_ROBOTO_MONO, QueryBuilder.WIDTH_DEFAULT,
                        QueryBuilder.WEIGHT_DEFAULT, QueryBuilder.ITALIC_DEFAULT),
                monoFontCallback,
                getHandlerThreadHandler());
    }

    private FontRequest getFontRequest(String fontName, int width, int weight, float italic) {
        QueryBuilder queryBuilder = new QueryBuilder(fontName)
                .withWidth(width)
                .withWeight(weight)
                .withItalic(italic)
                .withBestEffort(true);
        String query = queryBuilder.build();

        return new FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                query,
                R.array.com_google_android_gms_fonts_certs);
    }


    private Handler getHandlerThreadHandler() {
        if (mHandler == null) {
            HandlerThread handlerThread = new HandlerThread("fonts");
            handlerThread.start();
            mHandler = new Handler(handlerThread.getLooper());
        }
        return mHandler;
    }

    @Override
    public void getCheckoutPreference(String checkoutPreferenceId, final TaggedCallback<CheckoutPreference> taggedCallback) {
        mercadoPagoServicesAdapter.getCheckoutPreference(checkoutPreferenceId, new Callback<CheckoutPreference>() {
            @Override
            public void success(CheckoutPreference checkoutPreference) {
                taggedCallback.onSuccess(checkoutPreference);
            }

            @Override
            public void failure(ApiException apiException) {
                taggedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.GET_PREFERENCE));
            }
        });
    }

    @Override
    public void getDiscountCampaigns(final TaggedCallback<List<Campaign>> callback) {

        if (CheckoutStore.getInstance().getPaymentMethodPluginList().isEmpty()) {
            mercadoPagoServicesAdapter.getCampaigns(new Callback<List<Campaign>>() {
                @Override
                public void success(List<Campaign> campaigns) {
                    callback.onSuccess(campaigns);
                }

                @Override
                public void failure(ApiException apiException) {
                    callback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.GET_CAMPAIGNS));
                }
            });
        } else {
            final List<Campaign> empty = new ArrayList<>();
            callback.onSuccess(empty);
        }
    }

    @Override
    public void getDirectDiscount(BigDecimal amount, String payerEmail, TaggedCallback<Discount> taggedCallback) {
        if (servicePreference != null && servicePreference.hasGetDiscountURL()) {
            Map<String, Object> discountInfoMap = new HashMap<>();
            discountInfoMap.putAll(servicePreference.getGetDiscountAdditionalInfo());
            CustomServer.getDirectDiscount(context, amount.toString(), payerEmail, servicePreference.getGetMerchantDiscountBaseURL(), servicePreference.getGetMerchantDiscountURI(), servicePreference.getGetDiscountAdditionalInfo(), taggedCallback);
        } else {
            mercadoPagoServicesAdapter.getDirectDiscount(amount.toString(), payerEmail, taggedCallback);
        }
    }

    @Override
    public void getPaymentMethodSearch(BigDecimal amount, final List<String> excludedPaymentTypes, final List<String> excludedPaymentMethods, Payer payer, Site site, final TaggedCallback<PaymentMethodSearch> onPaymentMethodSearchRetrievedCallback, final TaggedCallback<Customer> onCustomerRetrievedCallback) {

        Set<String> excludedPaymentTypesSet = new HashSet<>(excludedPaymentTypes);
        excludedPaymentTypesSet.addAll(getUnsupportedPaymentTypes(site));

        mercadoPagoServicesAdapter.getPaymentMethodSearch(amount, new ArrayList<>(excludedPaymentTypesSet), excludedPaymentMethods, payer, site, new Callback<PaymentMethodSearch>() {
            @Override
            public void success(final PaymentMethodSearch paymentMethodSearch) {
                if (servicePreference != null && servicePreference.hasGetCustomerURL()) {
                    attachCustomerCardsFromMerchantServer(paymentMethodSearch, excludedPaymentTypes, excludedPaymentMethods, onPaymentMethodSearchRetrievedCallback, onCustomerRetrievedCallback);
                } else {
                    onPaymentMethodSearchRetrievedCallback.onSuccess(paymentMethodSearch);
                }
            }

            @Override
            public void failure(ApiException apiException) {
                onPaymentMethodSearchRetrievedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.PAYMENT_METHOD_SEARCH));
            }
        });
    }

    private void attachCustomerCardsFromMerchantServer(final PaymentMethodSearch paymentMethodSearch, final List<String> excludedPaymentTypes, final List<String> excludedPaymentMethods, final TaggedCallback<PaymentMethodSearch> onPaymentMethodSearchRetrievedCallback, final TaggedCallback<Customer> onCustomerRetrievedCallback) {
        CustomServer.getCustomer(context, servicePreference.getGetCustomerURL(), servicePreference.getGetCustomerURI(), servicePreference.getGetCustomerAdditionalInfo(), new Callback<Customer>() {
            @Override
            public void success(Customer customer) {
                PaymentPreference paymentPreference = new PaymentPreference();
                paymentPreference.setExcludedPaymentTypeIds(excludedPaymentTypes);
                paymentPreference.setExcludedPaymentMethodIds(excludedPaymentMethods);
                List<Card> savedCards = paymentPreference.getValidCards(customer.getCards());
                paymentMethodSearch.setCards(savedCards, context.getString(R.string.mpsdk_last_digits_label));

                onCustomerRetrievedCallback.onSuccess(customer);
                onPaymentMethodSearchRetrievedCallback.onSuccess(paymentMethodSearch);
            }

            @Override
            public void failure(ApiException apiException) {
                onCustomerRetrievedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.GET_CUSTOMER));

                //Return payment method search to avoid failure due to merchant server
                onPaymentMethodSearchRetrievedCallback.onSuccess(paymentMethodSearch);
            }
        });
    }


    @Override
    public String getCheckoutExceptionMessage(CheckoutPreferenceException exception) {
        return ExceptionHandler.getErrorMessage(context, exception);
    }

    @Override
    public String getCheckoutExceptionMessage(IllegalStateException exception) {
        return context.getString(R.string.mpsdk_standard_error_message);
    }

    @Override
    public void createPayment(String transactionId, CheckoutPreference checkoutPreference, PaymentData paymentData, Boolean binaryMode, String customerId, TaggedCallback<Payment> taggedCallback) {
        if (servicePreference != null && servicePreference.hasCreatePaymentURL()) {
            createPaymentInMerchantServer(transactionId, paymentData, taggedCallback);
        } else {
            createPaymentInMercadoPago(transactionId, checkoutPreference, paymentData, binaryMode, customerId, taggedCallback);
        }
    }

    private void createPaymentInMerchantServer(String transactionId, PaymentData paymentData, final TaggedCallback<Payment> taggedCallback) {
        Map<String, Object> paymentInfoMap = new HashMap<>();
        paymentInfoMap.putAll(servicePreference.getCreatePaymentAdditionalInfo());

        MerchantPayment merchantPayment = new MerchantPayment(paymentData);
        String payLoadJson = JsonUtil.getInstance().toJson(merchantPayment);

        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> paymentDataMap = new Gson().fromJson(payLoadJson, type);

        paymentInfoMap.putAll(paymentDataMap);

        CustomServer.createPayment(context, transactionId, servicePreference.getCreatePaymentURL(), servicePreference.getCreatePaymentURI(), paymentInfoMap, new HashMap<String, String>(), new Callback<Payment>() {
            @Override
            public void success(Payment payment) {
                taggedCallback.onSuccess(payment);
            }

            @Override
            public void failure(ApiException apiException) {
                taggedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.CREATE_PAYMENT));
            }
        });
    }

    private void createPaymentInMercadoPago(String transactionId,
                                            CheckoutPreference checkoutPreference,
                                            PaymentData paymentData,
                                            Boolean binaryMode, String customerId,
                                            final TaggedCallback<Payment> taggedCallback) {
        PaymentBody paymentBody = createPaymentBody(transactionId, checkoutPreference, paymentData, binaryMode, customerId);
        mercadoPagoServicesAdapter.createPayment(paymentBody, taggedCallback);
    }

    private PaymentBody createPaymentBody(String transactionId, CheckoutPreference checkoutPreference, PaymentData paymentData, Boolean binaryMode, String customerId) {
        PaymentBody paymentBody = new PaymentBody();
        paymentBody.setPrefId(checkoutPreference.getId());
        paymentBody.setPublicKey(publicKey);
        paymentBody.setPaymentMethodId(paymentData.getPaymentMethod().getId());
        paymentBody.setBinaryMode(binaryMode);

        Payer payer = paymentData.getPayer();
        if (!TextUtil.isEmpty(customerId) && MercadoPagoUtil.isCard(paymentData.getPaymentMethod().getPaymentTypeId())) {
            payer.setId(customerId);
        }
        paymentBody.setPayer(payer);

        if (paymentData.getToken() != null) {
            paymentBody.setTokenId(paymentData.getToken().getId());
        }
        if (paymentData.getPayerCost() != null) {
            paymentBody.setInstallments(paymentData.getPayerCost().getInstallments());
        }
        if (paymentData.getIssuer() != null) {
            paymentBody.setIssuerId(paymentData.getIssuer().getId());
        }

        Discount discount = paymentData.getDiscount();
        if (discount != null) {
            paymentBody.setCampaignId(discount.getId().intValue());
            paymentBody.setCouponAmount(discount.getCouponAmount().floatValue());
            paymentBody.setCouponCode(paymentData.getDiscount().getCouponCode());
        }

        paymentBody.setTransactionId(transactionId);
        return paymentBody;
    }

    private List<String> getUnsupportedPaymentTypes(Site site) {

        List<String> unsupportedTypesForSite = new ArrayList<>();
        if (Sites.CHILE.getId().equals(site.getId())
                || Sites.VENEZUELA.getId().equals(site.getId())
                || Sites.COLOMBIA.getId().equals(site.getId())) {

            unsupportedTypesForSite.add(PaymentTypes.TICKET);
            unsupportedTypesForSite.add(PaymentTypes.ATM);
            unsupportedTypesForSite.add(PaymentTypes.BANK_TRANSFER);
        }
        return unsupportedTypesForSite;
    }

    @Override
    public void deleteESC(String cardId) {
        mercadoPagoESC.deleteESC(cardId);
    }

    @Override
    public boolean saveESC(String cardId, String value) {
        return mercadoPagoESC.saveESC(cardId, value);
    }
}