package com.example.cn.helloworld.data.model;

import java.util.UUID;

public class Address {

    private final String id;
    private final String contactName;
    private final String phone;
    private final String detail;

    public Address(String id, String contactName, String phone, String detail) {
        this.id = id;
        this.contactName = contactName;
        this.phone = phone;
        this.detail = detail;
    }

    public static Address create(String contactName, String phone, String detail) {
        return new Address(UUID.randomUUID().toString(), contactName, phone, detail);
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

    public Address withContent(String newName, String newPhone, String newDetail) {
        return new Address(id, newName, newPhone, newDetail);
    }
}
