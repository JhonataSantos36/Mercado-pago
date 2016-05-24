package com.mercadopago.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.model.PaymentMethod;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil {

    private static JsonUtil mInstance = null;
    private Gson mGson;

    protected JsonUtil() {
        mGson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).serializeNulls().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
    }

    public static JsonUtil getInstance() {
        if (mInstance == null) {
            mInstance = new JsonUtil();
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

    public <T> List<T> listFromJson(String json, Class<T> tClass) {

        Gson gson = new Gson();

        Type listType = new TypeToken<List<JsonObject>>() {
        }.getType();

        List<JsonObject> jsonItems = gson.fromJson(json, listType);

        List<T> listObjects = new ArrayList<>();

        for (JsonObject jsonItem : jsonItems) {
            listObjects.add(fromJson(jsonItem.toString(), tClass));
        }

        return listObjects;
    }

    public static <T> String parseList(List<T> list) {

        if (list != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<T>>(){}.getType();
            return gson.toJson(list, listType);
        }
        return null;
    }
}
