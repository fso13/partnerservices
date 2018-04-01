package ru.drudenko.partnerservices.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "token")
public class Token {
    private transient final Logger logger = LoggerFactory.getLogger(getClass());
    @Id
    private String token;
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    private Instant created = Instant.now();
    private Instant expired = created.plus(Duration.ofDays(1));

    public Token() {
    }

    public Token(Customer customer) {
        try {
            this.token = hash(created + ":" + customer.getLogin());
            this.customer = customer;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            logger.error("Error created new token.", e);
            throw new RuntimeException(e);
        }
    }

    public String getToken() {
        return token;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Instant getCreated() {
        return created;
    }

    public Instant getExpired() {
        return expired;
    }

    private String hash(String s) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] bytes = md.digest(s.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (final byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}
