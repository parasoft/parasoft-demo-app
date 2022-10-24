package com.parasoft.demoapp.model.industry;

public enum OrderStatus {

    CREATED("Created", 0),
    FAILED("Failed", 1),
    SUBMITTED("Submitted", 1),
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
