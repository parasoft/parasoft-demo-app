package com.parasoft.demoapp.model.industry;

public enum OrderStatus {

    SUBMITTED("Submitted", 0),
    PROCESSED("Processed", 1),
    CANCELED("Canceled", 1),
    APPROVED("Approved", 2),
    DECLINED("Declined", 2);

    private String status;

    private int priority;

    OrderStatus(String status, int priority) {
        this.status = status;
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public int getPriority() {
        return priority;
    }
}
