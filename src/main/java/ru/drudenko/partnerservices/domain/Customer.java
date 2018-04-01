package ru.drudenko.partnerservices.domain;

import ru.drudenko.partnerservices.controllers.Client;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@SequenceGenerator(name = "create_seq", sequenceName = "seq_entity", allocationSize = 1)
@Table(name = "customer")
public class Customer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "create_seq")
    @Column(name = "id")
    private Long id;
    @Column(name = "fullname")
    private String fullName;
    private BigDecimal balance;
    @Column(name = "isactive")
    private boolean isActive;
    private String login;
    private String password;

    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
    private Set<PartnerMapping> partnerMappings = new HashSet<>();

    public Long getId() {
        return id;
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

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Set<PartnerMapping> getPartnerMappings() {
        return partnerMappings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        return Objects.equals(id, customer.id) && fullName.equals(customer.fullName) && login.equals(customer.login) && password.equals(customer.password);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + fullName.hashCode();
        result = 31 * result + login.hashCode();
        result = 31 * result + password.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", balance=" + balance +
                ", isActive=" + isActive +
                '}';
    }

    public Client toClient() {
        return new Client(fullName, balance, isActive);
    }
}
