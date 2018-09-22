package com.laundry.laundry.models;

/**
 * @author Aditya Kulkarni
 */

public class BagModel {
    private int pk, count;
    private String uid, service_type, current_status;
    private boolean completed;
    private int customer;

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getService_type() {
        return service_type;
    }

    public String getCurrent_status() {
        return current_status;
    }

    public void setCurrent_status(String current_status) {
        this.current_status = current_status;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public int getCustomerModel() {
        return customer;
    }

    public void setCustomerModel(int customerModel) {
        this.customer = customerModel;
    }
}
