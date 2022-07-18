package com.crudtest.service;

import com.crudtest.domain.Email;
import com.crudtest.domain.Post;
import com.crudtest.exception.InvalidKey;
import com.crudtest.exception.PostNotFound;
import com.crudtest.repository.EmailRepository;
import com.crudtest.request.EmailCreate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final EmailRepository emailRepository;

    public Long emailWrite(EmailCreate request) {
        String uuid = UUID.randomUUID().toString().substring(0,8);
        Email email = Email.builder()
                .email(request.getEmail())
                .key(uuid)
                .build();

        emailRepository.save(email);
        return email.getId();
    }
    public void keyValid(String key) {
        emailRepository.findByKey(key)
                .orElseThrow(InvalidKey::new);
    }
}
