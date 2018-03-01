package com.mercadopago.test;

import com.mercadopago.util.HttpClientUtil;
import com.mercadopago.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Created by mreverter on 1/7/16.
 */
public class FakeAPI {

    private List<QueuedResponse> queuedResponses;
    private FakeInterceptor fakeInterceptor;

    public QueuedResponse getNextResponse() {

        if (hasQueuedResponses()) {
            return queuedResponses.remove(0);
        } else {
            return new QueuedResponse("", 401, "");
        }
    }

    public <T> void addResponseToQueue(T response, int statusCode, String reason) {
        if (queuedResponses == null) {
            queuedResponses = new ArrayList<>();
        }
        String jsonResponse = JsonUtil.getInstance().toJson(response);
        addResponseToQueue(jsonResponse, statusCode, reason);
    }

    public void addResponseToQueue(String jsonResponse, int statusCode, String reason) {
        if (queuedResponses == null) {
            queuedResponses = new ArrayList<>();
        }
        QueuedResponse queuedResponse = new QueuedResponse(jsonResponse, statusCode, reason);
        queuedResponses.add(queuedResponse);
    }

    public void addResponseToQueue(String jsonResponse, int statusCode, String reason, int delayMilliseconds) {
        if (queuedResponses == null) {
            queuedResponses = new ArrayList<>();
        }
        QueuedResponse queuedResponse = new QueuedResponse(jsonResponse, statusCode, reason, delayMilliseconds);
        queuedResponses.add(queuedResponse);
    }

    public void start() {
        HttpClientUtil.setCustomClient(createClient());
    }

    public void stop() {
        HttpClientUtil.removeCustomClient();
        cleanQueue();
    }

    public void cleanQueue() {
        if (this.hasQueuedResponses()) {
            queuedResponses.clear();
        }
    }

    private OkHttpClient createClient() {

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .addInterceptor(getFakeInterceptor());

        return okHttpClientBuilder.build();
    }

    private Interceptor getFakeInterceptor() {
        if (fakeInterceptor == null) {
            fakeInterceptor = new FakeInterceptor(this);
        }
        return fakeInterceptor;
    }

    public boolean hasQueuedResponses() {
        return queuedResponses != null && !queuedResponses.isEmpty();
    }

    public static class QueuedResponse {
        private String jsonResponse;
        private int statusCode;
        private String reason;
        private Integer delayMilliseconds;

        public QueuedResponse(String jsonResponse, int statusCode, String reason) {
            this.jsonResponse = jsonResponse;
            this.reason = reason;
            this.statusCode = statusCode;
        }

        public QueuedResponse(String jsonResponse, int statusCode, String reason, int delayMilliseconds) {
            this.jsonResponse = jsonResponse;
            this.reason = reason;
            this.statusCode = statusCode;
            this.delayMilliseconds = delayMilliseconds;
        }

        public String getBodyAsJson() {
            return jsonResponse;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public Integer getDelay() {
            return delayMilliseconds;
        }

        public boolean hasDelay() {
            return delayMilliseconds != null;
        }
    }
}
