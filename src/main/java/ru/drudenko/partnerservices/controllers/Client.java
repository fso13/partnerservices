package ru.drudenko.partnerservices.controllers;

import java.math.BigDecimal;

public class Client {
    private String fullName;
    private BigDecimal balance;
    private boolean isActive;

    public Client(String fullName, BigDecimal balance, boolean isActive) {
        this.fullName = fullName;
        this.balance = balance;
        this.isActive = isActive;
    }

    public String getFullName() {
        return fullName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public boolean isActive() {
        return isActive;
    }
}
