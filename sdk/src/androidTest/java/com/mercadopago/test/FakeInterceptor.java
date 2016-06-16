package com.mercadopago.test;

import com.mercadopago.util.JsonUtil;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by mreverter on 17/5/16.
 */
public class FakeInterceptor implements Interceptor {

    private List<QueuedResponse> queuedResponses;

    public FakeInterceptor() {
        queuedResponses = new ArrayList<>();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = null;
        if(BuildConfig.DEBUG) {
            QueuedResponse nextResponse = getNextResponse();
            String responseString = nextResponse.jsonResponse;
            response = new Response.Builder()
                    .code(nextResponse.statusCode)
                    .message(responseString)
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_0)
                    .body(ResponseBody.create(MediaType.parse("application/json"), responseString.getBytes()))
                    .addHeader("content-type", "application/json")
                    .build();
        }
        else {
            response = chain.proceed(chain.request());
        }

        return response;
    }

    private QueuedResponse getNextResponse() {

        if(!queuedResponses.isEmpty()) {
            return queuedResponses.remove(0);
        }
        else {
            return new QueuedResponse("", 401, "");
        }
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

    public void cleanQueue() {
        this.queuedResponses.clear();
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
