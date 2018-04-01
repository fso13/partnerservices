package ru.drudenko.partnerservices.domain;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface CustomerRepository {
    /**
     * Поиск ru.drudenko.partnerservices.domain.Customer по числовому идентификатору
     *
     * @param id
     * @return ru.drudenko.partnerservices.domain.Customer
     * если не найден возвращается NULL
     */
    Customer getCustomerById(Long id);
}
