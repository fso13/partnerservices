package ru.drudenko.partnerservices.domain;

public final class ApplicationIdentity {
    private String applicationIdentity;

    private ApplicationIdentity(String applicationIdentity) {
        this.applicationIdentity = applicationIdentity;
    }

    public static ApplicationIdentity fromString(String identity) throws IllegalArgumentException {
        if (identity != null && !identity.trim().isEmpty()) {
            return new ApplicationIdentity(identity.trim());
        } else {
            throw new IllegalArgumentException("Partner applicationIdentity is mandatory!");
        }
    }

    public AccountIdentity createAccountIdentity(String account) {
        return new AccountIdentity(this, account);
    }

    public String getIdentityAsString() {
        return applicationIdentity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplicationIdentity that = (ApplicationIdentity) o;

        return applicationIdentity.equals(that.applicationIdentity);
    }

    @Override
    public int hashCode() {
        return applicationIdentity.hashCode();
    }
}
