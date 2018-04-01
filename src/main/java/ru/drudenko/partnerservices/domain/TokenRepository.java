package ru.drudenko.partnerservices.domain;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Repository
public interface TokenRepository {
    /**
     * Поиск ru.drudenko.partnerservices.domain.Token по строковому идентификатору
     *
     * @param token-авторизацтонный токен
     * @return ru.drudenko.partnerservices.domain.Token
     * если не найден возвращается NULL
     */
    @Transactional(propagation = Propagation.REQUIRED)
    Token findTokenById(String token);
}
