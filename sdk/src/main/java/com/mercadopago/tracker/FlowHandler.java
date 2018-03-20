package com.mercadopago.tracker;

import java.util.UUID;

public class FlowHandler {
    private static FlowHandler INSTANCE;
    private String flowId;

    public static synchronized FlowHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FlowHandler();
        }
        return INSTANCE;
    }

    public void generateFlowId() {
        flowId = UUID.randomUUID().toString();
    }

    public String getFlowId() {
        return flowId;
    }
}
