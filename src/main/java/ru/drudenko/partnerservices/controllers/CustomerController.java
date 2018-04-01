package ru.drudenko.partnerservices.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.drudenko.partnerservices.domain.Account;
import ru.drudenko.partnerservices.domain.AccountIdentity;
import ru.drudenko.partnerservices.domain.ApplicationIdentity;
import ru.drudenko.partnerservices.domain.PartnerMapping;
import ru.drudenko.partnerservices.domain.PartnerMappingRepository;
import ru.drudenko.partnerservices.domain.Photo;
import ru.drudenko.partnerservices.domain.Token;
import ru.drudenko.partnerservices.domain.TokenRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

@Transactional
@RestController
@RequestMapping("/customer")
public class CustomerController {
    private final PartnerMappingRepository partnerMappingRepository;
    private final TokenRepository tokenRepository;

    public CustomerController(@Autowired PartnerMappingRepository partnerMappingRepository,
                              @Autowired TokenRepository tokenRepository) {
        this.partnerMappingRepository = partnerMappingRepository;
        this.tokenRepository = tokenRepository;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{userId}")
    ResponseEntity<Client> getCustomer(@PathVariable(required = true) Long userId,
                                       @RequestHeader(name = "Authorization", required = true) String token) {
        Token t = tokenRepository.findTokenById(parseToken(token));
        if (Objects.equals(t.getCustomer().getId(), userId)) {
            return ResponseEntity.ok(t.getCustomer().toClient());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/@me")
    ResponseEntity<Client> getCurrentCustomer(@RequestHeader(name = "Authorization", required = true) String token) {
        Token t = tokenRepository.findTokenById(parseToken(token));
        return ResponseEntity.ok(t.getCustomer().toClient());
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{userId}/partnermapping/{partnerMappingId}")
    ResponseEntity<Void> deletePartnerMapping(@PathVariable Long userId,
                                              @PathVariable Long partnerMappingId,
                                              @RequestHeader(name = "Authorization", required = true) String token) {
        Token t = tokenRepository.findTokenById(parseToken(token));
        if (!Objects.equals(t.getCustomer().getId(), userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return deletePartnerMapping(partnerMappingId, t);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/@me/partnermapping/{partnerMappingId}")
    ResponseEntity<Void> deleteCurrentCustomerPartnerMapping(@PathVariable Long partnerMappingId,
                                                             @RequestHeader(name = "Authorization", required = true) String token) {
        Token t = tokenRepository.findTokenById(parseToken(token));
        return deletePartnerMapping(partnerMappingId, t);
    }

    private ResponseEntity<Void> deletePartnerMapping(Long partnerMappingId, Token t) {
        PartnerMapping partnerMapping = partnerMappingRepository.getPartnerMappingById(partnerMappingId);
        if (partnerMapping == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!partnerMapping.getCustomer().getId().equals(t.getCustomer().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            partnerMappingRepository.deletePartnerMappingById(partnerMappingId);
            return ResponseEntity.ok().build();
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{userId}/partnermapping/{partnerMappingId}")
    ResponseEntity<PartnerAccount> getPartnerMapping(@PathVariable Long userId,
                                                     @PathVariable Long partnerMappingId,
                                                     @RequestHeader(name = "Authorization", required = true) String token) {
        Token t = tokenRepository.findTokenById(parseToken(token));
        if (!Objects.equals(t.getCustomer().getId(), userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return getPartnerAccountResponseEntity(partnerMappingId, t);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/@me/partnermapping/{partnerMappingId}")
    ResponseEntity<PartnerAccount> getCurrentCustomerPartnerMapping(@PathVariable Long partnerMappingId,
                                                                    @RequestHeader(name = "Authorization", required = true) String token) {
        Token t = tokenRepository.findTokenById(parseToken(token));

        return getPartnerAccountResponseEntity(partnerMappingId, t);
    }

    private ResponseEntity<PartnerAccount> getPartnerAccountResponseEntity(Long partnerMappingId, Token t) {
        PartnerMapping partnerMapping = partnerMappingRepository.getPartnerMappingById(partnerMappingId);
        if (partnerMapping == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!partnerMapping.getCustomer().getId().equals(t.getCustomer().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            return ResponseEntity.ok(partnerMapping.toPartnerAccount());
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/{userId}/partnermapping")
    ResponseEntity<PartnerAccount> createPartnerMapping(@PathVariable Long userId,
                                                        @RequestBody Account account,
                                                        @RequestHeader(name = "Authorization", required = true) String token) {
        Token t = tokenRepository.findTokenById(parseToken(token));
        if (!Objects.equals(t.getCustomer().getId(), userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return createPartnerMapping(account, t);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/@me/partnermapping")
    ResponseEntity<PartnerAccount> createCurrentCustomerPartnerMapping(@RequestBody Account account,
                                                                       @RequestHeader(name = "Authorization", required = true) String token) {
        Token t = tokenRepository.findTokenById(parseToken(token));
        return createPartnerMapping(account, t);
    }

    private ResponseEntity<PartnerAccount> createPartnerMapping(Account account, Token t) {
        AccountIdentity accountIdentity = ApplicationIdentity.fromString(account.getApplicationIdentity()).createAccountIdentity(account.getAccountIdentity());
        PartnerMapping partnerMapping = partnerMappingRepository.createPartnerMapping(t.getCustomer().getId(), accountIdentity, account.getFullName());
        return ResponseEntity.status(HttpStatus.CREATED).body(partnerMapping.toPartnerAccount());
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/{userId}/partnermapping/{partnerMappingId}")
    ResponseEntity<PartnerAccount> updatePartnerMapping(@PathVariable Long userId,
                                                        @PathVariable Long partnerMappingId,
                                                        @RequestBody Account account,
                                                        @RequestHeader(name = "Authorization", required = true) String token) {
        Token t = tokenRepository.findTokenById(parseToken(token));
        if (!Objects.equals(t.getCustomer().getId(), userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return updatePartnerMapping(partnerMappingId, account, t);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/@me/partnermapping/{partnerMappingId}")
    ResponseEntity<PartnerAccount> updateCurrentCustomerPartnerMapping(@PathVariable Long partnerMappingId,
                                                                       @RequestBody Account account,
                                                                       @RequestHeader(name = "Authorization", required = true) String token) {
        Token t = tokenRepository.findTokenById(parseToken(token));
        return updatePartnerMapping(partnerMappingId, account, t);
    }

    private ResponseEntity<PartnerAccount> updatePartnerMapping(Long partnerMappingId, Account account, Token t) {
        PartnerMapping partnerMapping = partnerMappingRepository.getPartnerMappingById(partnerMappingId);
        if (partnerMapping == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!Objects.equals(t.getCustomer().getId(), partnerMapping.getCustomer().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        partnerMapping.update(account);
        partnerMappingRepository.persist(partnerMapping);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.POST, path = "/{userId}/partnermapping/{partnerMappingId}/avatar")
    ResponseEntity<Object> addAvatar(@PathVariable Long userId,
                                     @PathVariable Long partnerMappingId,
                                     @RequestParam("avatar") MultipartFile file,
                                     @RequestHeader(name = "Authorization", required = true) String token) {
        Token t = tokenRepository.findTokenById(parseToken(token));
        if (!Objects.equals(t.getCustomer().getId(), userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return addAvatar(partnerMappingId, file, t);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/@me/partnermapping/{partnerMappingId}/avatar")
    ResponseEntity<Object> addCurrentCustomerAvatar(@PathVariable Long partnerMappingId,
                                                    @RequestParam("avatar") MultipartFile file,
                                                    @RequestHeader(name = "Authorization", required = true) String token) {
        Token t = tokenRepository.findTokenById(parseToken(token));
        return addAvatar(partnerMappingId, file, t);
    }

    private ResponseEntity<Object> addAvatar(Long partnerMappingId, MultipartFile file, Token t) {
        PartnerMapping partnerMapping = partnerMappingRepository.getPartnerMappingById(partnerMappingId);
        if (partnerMapping == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (!Objects.equals(t.getCustomer().getId(), partnerMapping.getCustomer().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (partnerMapping.getAvatar() != null) {
            return ResponseEntity.status(405).build();
        }

        try {
            Photo photo = partnerMappingRepository.createPhoto(partnerMappingId, file.getBytes(), file.getContentType());
            return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("id", photo.getId()));

        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }

    }

    @RequestMapping(method = RequestMethod.PUT, path = "/{userId}/partnermapping/{partnerMappingId}/avatar")
    ResponseEntity<Void> updateAvatar(@PathVariable Long userId,
                                      @PathVariable Long partnerMappingId,
                                      @RequestParam("avatar") MultipartFile file,
                                      @RequestHeader(name = "Authorization", required = true) String token) {
        Token t = tokenRepository.findTokenById(parseToken(token));
        if (!Objects.equals(t.getCustomer().getId(), userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return updateAvatar(partnerMappingId, file, t);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/@me/partnermapping/{partnerMappingId}/avatar")
    ResponseEntity<Void> updateCurrentCustomerAvatar(@PathVariable Long partnerMappingId,
                                                     @RequestParam("avatar") MultipartFile file,
                                                     @RequestHeader(name = "Authorization", required = true) String token) {
        Token t = tokenRepository.findTokenById(parseToken(token));
        return updateAvatar(partnerMappingId, file, t);
    }

    private ResponseEntity<Void> updateAvatar(Long partnerMappingId, MultipartFile file, Token t) {
        PartnerMapping partnerMapping = partnerMappingRepository.getPartnerMappingById(partnerMappingId);
        if (partnerMapping == null || partnerMapping.getAvatar() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!Objects.equals(t.getCustomer().getId(), partnerMapping.getCustomer().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            partnerMapping.getAvatar().update(file.getBytes(), file.getContentType());
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }

        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{userId}/partnermapping/{partnerMappingId}/avatar")
    ResponseEntity<Avatar> getAvatar(@PathVariable Long userId,
                                     @PathVariable Long partnerMappingId,
                                     @RequestHeader(name = "Authorization", required = true) String token) {
        Token t = tokenRepository.findTokenById(parseToken(token));
        if (!Objects.equals(t.getCustomer().getId(), userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return getAvatar(partnerMappingId, t);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/@me/partnermapping/{partnerMappingId}/avatar")
    ResponseEntity<Avatar> getCurrentCustomerAvatar(@PathVariable Long partnerMappingId,
                                                    @RequestHeader(name = "Authorization", required = true) String token) {
        Token t = tokenRepository.findTokenById(parseToken(token));
        return getAvatar(partnerMappingId, t);
    }

    private ResponseEntity<Avatar> getAvatar(Long partnerMappingId, Token t) {
        PartnerMapping partnerMapping = partnerMappingRepository.getPartnerMappingById(partnerMappingId);
        if (partnerMapping == null || partnerMapping.getAvatar() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!Objects.equals(t.getCustomer().getId(), partnerMapping.getCustomer().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(partnerMapping.getAvatar().toAvatar());
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{userId}/partnermapping/{partnerMappingId}/avatar")
    ResponseEntity<Void> deleteAvatar(@PathVariable Long userId,
                                      @PathVariable Long partnerMappingId,
                                      @RequestHeader(name = "Authorization", required = true) String token) {
        Token t = tokenRepository.findTokenById(parseToken(token));
        if (!Objects.equals(t.getCustomer().getId(), userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return deleteAvatar(partnerMappingId, t);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/@me/partnermapping/{partnerMappingId}/avatar")
    ResponseEntity<Void> deleteCurrentCustomerAvatar(@PathVariable Long partnerMappingId,
                                                     @RequestHeader(name = "Authorization", required = true) String token) {
        Token t = tokenRepository.findTokenById(parseToken(token));
        return deleteAvatar(partnerMappingId, t);
    }

    private ResponseEntity<Void> deleteAvatar(Long partnerMappingId, Token t) {
        PartnerMapping partnerMapping = partnerMappingRepository.getPartnerMappingById(partnerMappingId);
        if (partnerMapping == null || partnerMapping.getAvatar() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!Objects.equals(t.getCustomer().getId(), partnerMapping.getCustomer().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        partnerMappingRepository.deletePhoto(partnerMapping.getAvatar().getId());
        return ResponseEntity.ok().build();
    }

    private static String parseToken(String token) {
        return token.trim().substring("Bearer ".length());
    }
}
