package ru.drudenko.partnerservices.domain;

public final class AccountIdentity {
    private final ApplicationIdentity applicationIdentity;
    private final String accountIdentity;

    AccountIdentity(ApplicationIdentity applicationIdentity, String accountIdentity) {
        this.applicationIdentity = applicationIdentity;
        this.accountIdentity = accountIdentity;
    }

    public ApplicationIdentity getApplicationIdentity() {
        return applicationIdentity;
    }

    public String getIdentityAsString() {
        return accountIdentity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountIdentity that = (AccountIdentity) o;

        if (!applicationIdentity.equals(that.applicationIdentity)) return false;
        return accountIdentity.equals(that.accountIdentity);
    }

    @Override
    public int hashCode() {
        int result = applicationIdentity.hashCode();
        result = 31 * result + accountIdentity.hashCode();
        return result;
    }
}
