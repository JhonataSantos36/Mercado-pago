package com.mercadopago.test;

import com.mercadopago.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mreverter on 1/7/16.
 */
public class FakeAPI {

    private static List<QueuedResponse> queuedResponses;

    public static QueuedResponse getNextResponse() {

        if(queuedResponses != null && !queuedResponses.isEmpty()) {
            return queuedResponses.remove(0);
        }
        else {
            return new QueuedResponse("", 401, "");
        }
    }

    public static <T> void addResponseToQueue(T response, int statusCode, String reason) {
        if(queuedResponses == null) {
            queuedResponses = new ArrayList<>();
        }
        String jsonResponse = JsonUtil.getInstance().toJson(response);
        QueuedResponse queuedResponse = new QueuedResponse(jsonResponse, statusCode, reason);
        queuedResponses.add(queuedResponse);
    }

    public static void addResponseToQueue(String jsonResponse, int statusCode, String reason) {
        if(queuedResponses == null) {
            queuedResponses = new ArrayList<>();
        }
        QueuedResponse queuedResponse = new QueuedResponse(jsonResponse, statusCode, reason);
        queuedResponses.add(queuedResponse);
    }

    public static void cleanQueue() {
        queuedResponses.clear();
    }

    public static class QueuedResponse {
        private String jsonResponse;
        private int statusCode;
        private String reason;

        public QueuedResponse(String jsonResponse, int statusCode, String reason) {
            this.jsonResponse = jsonResponse;
            this.reason = reason;
            this.statusCode = statusCode;
        }

        public String getBodyAsJson() {
            return jsonResponse;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }
}
