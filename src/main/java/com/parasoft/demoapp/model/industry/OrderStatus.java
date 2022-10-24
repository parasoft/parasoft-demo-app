package com.parasoft.demoapp.model.industry;

public enum OrderStatus {

    SUBMITTED("Submitted", 0),
    PROCESSED("Processed", 1),
    CANCELED("Canceled", 1),
    APPROVED("Approved", 2),
    DECLINED("Declined", 2);

    private String status;

    private int code;

    OrderStatus(String status, int code) {
        this.status = status;
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }
}
