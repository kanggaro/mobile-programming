package com.example.listview.data;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

public class Contact {

    public Contact(Integer id, String name, String contact, @Nullable Bitmap contactProfile) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.contactProfile = contactProfile;
    }

    private Integer id;

    private String name;
    private String contact;
    private Bitmap contactProfile;

    public Integer getId() { return id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Bitmap getContactProfile() {
        return contactProfile;
    }

    public void setContactProfile(Bitmap contactProfile) {
        this.contactProfile = contactProfile;
    }
}