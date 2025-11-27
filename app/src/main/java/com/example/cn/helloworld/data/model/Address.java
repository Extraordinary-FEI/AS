package com.example.cn.helloworld.data.model;

public class Address {

    private String id;
    private String contactName;
    private String phone;
    private String detail;

    public Address(String id, String contactName) {
        this(id, contactName, "", "");
    }

    public Address(String id, String contactName, String phone, String detail) {
        this.id = id;
        this.contactName = contactName;
        this.phone = phone;
        this.detail = detail;
    }

    public String getId() {
        return id;
    }

    public String getContactName() {
        return contactName;
    }

    public String getPhone() {
        return phone;
    }

    public String getDetail() {
        return detail;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
