package ru.drudenko.partnerservices.controllers;

public class Avatar {
    private long id;
    private byte[] photo;
    private String MIMEType;

    public Avatar(long id, byte[] photo, String MIMEType) {
        this.id = id;
        this.photo = photo;
        this.MIMEType = MIMEType;
    }

    public long getId() {
        return id;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public String getMIMEType() {
        return MIMEType;
    }
}
