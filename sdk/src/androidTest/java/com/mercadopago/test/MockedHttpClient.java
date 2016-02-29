package com.mercadopago.test;

import com.mercadopago.util.HttpClientUtil;
import com.mercadopago.util.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import retrofit.client.Client;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by mreverter on 24/2/16.
 */
public class MockedHttpClient implements Client {

    private static final String MIME_TYPE = "application/json";

    private List<QueuedResponse> queuedResponses;

    public MockedHttpClient() {
        this.queuedResponses = new ArrayList<>();
    }

    @Override
    public Response execute(Request request) throws IOException {
        QueuedResponse queuedResponse = getNextResponse();
        return createDummyJsonResponse(request.getUrl(), queuedResponse.statusCode, queuedResponse.reason, queuedResponse.jsonResponse);
    }

    private QueuedResponse getNextResponse() {

        if(!queuedResponses.isEmpty()) {
            return queuedResponses.remove(0);
        }
        else {
            return new QueuedResponse("", 401, "");
        }
    }
    
    private Response createDummyJsonResponse(String url, int responseCode, String reason, String json) {
        return new Response(url, responseCode, reason, Collections.EMPTY_LIST,
                new TypedByteArray(MIME_TYPE, json.getBytes()));
    }

    public <T> void addResponseToQueue(T response, int statusCode, String reason) {
        String jsonResponse = JsonUtil.getInstance().toJson(response);
        QueuedResponse queuedResponse = new QueuedResponse(jsonResponse, statusCode, reason);
        this.queuedResponses.add(queuedResponse);
    }

    public void addResponseToQueue(String jsonResponse, int statusCode, String reason) {
        QueuedResponse queuedResponse = new QueuedResponse(jsonResponse, statusCode, reason);
        this.queuedResponses.add(queuedResponse);
    }

    private class QueuedResponse {
        private String jsonResponse;
        private int statusCode;
        private String reason;

        public QueuedResponse(String jsonResponse, int statusCode, String reason) {
            this.jsonResponse = jsonResponse;
            this.reason = reason;
            this.statusCode = statusCode;
        }
    }
}
