package ru.drudenko.partnerservices.domain;

import ru.drudenko.partnerservices.controllers.PartnerAccount;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@SequenceGenerator(name = "create_seq", sequenceName = "seq_entity", allocationSize = 1)
@Table(name = "partnermapping")
public class PartnerMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "create_seq")
    @Column(name = "id")
    private Long id;
    @Column(name = "accountidentity")
    private String accountIdentity;
    @Column(name = "applicationidentity")
    private String applicationIdentity;
    @Column(name = "fullname")
    private String fullName;
    @ManyToOne
    private Customer customer;
    @OneToOne
    private Photo avatar;

    public PartnerMapping() {
    }

    public PartnerMapping(Customer customer, AccountIdentity accountIdentity, String fullName) {
        this.applicationIdentity = accountIdentity.getApplicationIdentity().getIdentityAsString();
        this.accountIdentity = accountIdentity.getIdentityAsString();
        this.fullName = fullName;
        this.customer = customer;
    }

    public void addAvatar(Photo avatar) {
        this.avatar = avatar;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public Photo getAvatar() {
        return avatar;
    }

    public String getAccountIdentity() {
        return accountIdentity;
    }

    public String getApplicationIdentity() {
        return applicationIdentity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartnerMapping that = (PartnerMapping) o;

        if (!Objects.equals(id, that.id)) return false;
        if (!accountIdentity.equals(that.accountIdentity)) return false;
        if (!applicationIdentity.equals(that.applicationIdentity)) return false;
        if (!fullName.equals(that.fullName)) return false;
        return customer.equals(that.customer);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + accountIdentity.hashCode();
        result = 31 * result + applicationIdentity.hashCode();
        result = 31 * result + fullName.hashCode();
        result = 31 * result + customer.hashCode();
        return result;
    }

    public PartnerAccount toPartnerAccount() {
        return new PartnerAccount(id, accountIdentity, applicationIdentity, fullName, customer.getId(), avatar == null ? null : avatar.toAvatar());
    }

    public void update(Account account) {
        accountIdentity = account.getAccountIdentity() != null ? account.getAccountIdentity() : accountIdentity;
        applicationIdentity = account.getApplicationIdentity() != null ? account.getApplicationIdentity() : applicationIdentity;
        fullName = account.getFullName() != null ? account.getFullName() : fullName;
    }
}
