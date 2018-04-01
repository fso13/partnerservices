package ru.drudenko.partnerservices.domain.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.drudenko.partnerservices.domain.AccountIdentity;
import ru.drudenko.partnerservices.domain.Customer;
import ru.drudenko.partnerservices.domain.CustomerRepository;
import ru.drudenko.partnerservices.domain.PartnerMapping;
import ru.drudenko.partnerservices.domain.PartnerMappingRepository;
import ru.drudenko.partnerservices.domain.Photo;
import ru.drudenko.partnerservices.domain.Token;
import ru.drudenko.partnerservices.domain.TokenRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class DomainRepositoryImpl implements TokenRepository, CustomerRepository, PartnerMappingRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @PersistenceContext
    private final EntityManager entityManager;

    public DomainRepositoryImpl(@Autowired EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Token findTokenById(String token) {
        try {
            return entityManager.find(Token.class, token);
        } catch (IllegalArgumentException e) {
            logger.warn("Token with id: {} not found!", token);
            return null;
        }
    }

    @Override
    public Customer getCustomerById(Long id) {
        try {
            return entityManager.find(Customer.class, id);
        } catch (IllegalArgumentException e) {
            logger.warn("Customer with id: {} not found!", id);
            return null;
        }
    }

    @Override
    public PartnerMapping getPartnerMappingById(Long partnerMappingId) {
        try {
            return entityManager.find(PartnerMapping.class, partnerMappingId);
        } catch (IllegalArgumentException e) {
            logger.warn("PartnerMapping with id: {} not found!", partnerMappingId);
            return null;
        }
    }

    @Override
    public PartnerMapping createPartnerMapping(Long customerId, AccountIdentity accountIdentity, String fullName) {
        Customer customer = getCustomerById(customerId);
        if (customer != null) {
            PartnerMapping partnerMapping = new PartnerMapping(customer, accountIdentity, fullName);
            entityManager.persist(partnerMapping);
            return partnerMapping;
        }
        return null;
    }

    @Override
    public void deletePartnerMappingById(Long partnerMappingId) {
        entityManager.remove(entityManager.find(PartnerMapping.class, partnerMappingId));
    }

    @Override
    public Photo createPhoto(Long partnerMappingId, byte[] bytes, String MIMEType) {
        PartnerMapping partnerMapping = getPartnerMappingById(partnerMappingId);
        if (partnerMapping != null) {
            Photo photo = new Photo(bytes.clone(), MIMEType);
            partnerMapping.addAvatar(photo);
            entityManager.persist(photo);
            return photo;
        }
        return null;
    }

    @Override
    public Photo getPhoto(Long photoId) {
        try {
            return entityManager.find(Photo.class, photoId);
        } catch (IllegalArgumentException e) {
            logger.warn("Photo with id: {} not found!", photoId);
            return null;
        }
    }

    @Override
    public void deletePhoto(Long photoId) {
        entityManager.remove(entityManager.find(Photo.class, photoId));
    }

    @Override
    public void persist(Object entity) {
        entityManager.persist(entity);
    }
}
