package ru.drudenko.partnerservices.controllers;

public class PartnerAccount {
    private long id;
    private String accountIdentity;
    private String applicationIdentity;
    private String fullName;
    private Long customerId;
    private Avatar avatar;

    public PartnerAccount(long id, String accountIdentity, String applicationIdentity, String fullName, Long customerId, Avatar avatar) {
        this.id = id;
        this.accountIdentity = accountIdentity;
        this.applicationIdentity = applicationIdentity;
        this.fullName = fullName;
        this.customerId = customerId;
        this.avatar = avatar;
    }

    public long getId() {
        return id;
    }

    public String getAccountIdentity() {
        return accountIdentity;
    }

    public String getApplicationIdentity() {
        return applicationIdentity;
    }

    public String getFullName() {
        return fullName;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Avatar getAvatar() {
        return avatar;
    }
}
