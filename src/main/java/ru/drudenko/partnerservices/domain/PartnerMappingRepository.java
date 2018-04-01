package ru.drudenko.partnerservices.domain;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface PartnerMappingRepository {
    /**
     * Поиск PartnerMapping по идентификатору
     * @param partnerMappingId
     * @return
     */
    PartnerMapping getPartnerMappingById(Long partnerMappingId);

    /**
     * Создание PartnerMapping
     * @param customerId
     * @param accountIdentity
     * @param fullName
     * @return
     */
    PartnerMapping createPartnerMapping(Long customerId, AccountIdentity accountIdentity, String fullName);

    /**
     * Удаление PartnerMapping по идентификатору
     * @param partnerMappingId
     */
    void deletePartnerMappingById(Long partnerMappingId);

    /**
     * Создание Photo
     * @param partnerMappingId
     * @param bytes
     * @param MIMEType
     * @return
     */
    Photo createPhoto(Long partnerMappingId, byte[] bytes, String MIMEType);

    /**
     * Поиск Photo по идентификатору
     * @param photoId
     * @return
     */
    Photo getPhoto(Long photoId);

    /**
     * Удаление Photo по идентификатору
     * @param photoId
     */
    void deletePhoto(Long photoId);

    void persist(Object entity);

}
