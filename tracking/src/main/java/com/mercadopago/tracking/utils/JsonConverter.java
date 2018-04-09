package com.mercadopago.tracking.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by vaserber on 6/5/17.
 */

public class JsonConverter {

    private static JsonConverter mInstance = null;
    private Gson mGson;

    protected JsonConverter() {
        mGson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).serializeNulls().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
    }

    public static JsonConverter getInstance() {
        if (mInstance == null) {
            mInstance = new JsonConverter();
        }
        return mInstance;
    }

    public <T> T fromJson(String json, Class<T> classOfT) {

        return mGson.fromJson(json, classOfT);
    }

    public String toJson(Object src) {

        return mGson.toJson(src);
    }

    public Gson getGson() {

        return mGson;
    }
}
