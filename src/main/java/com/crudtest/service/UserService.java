package com.crudtest.service;

import com.crudtest.domain.User;
import com.crudtest.exception.InvalidKey;
import com.crudtest.exception.InvalidRequest;
import com.crudtest.repository.UserRepository;
import com.crudtest.request.MailRequest;
import com.crudtest.request.UserCreate;
import com.querydsl.core.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    public Long emailWrite(UserCreate request) {
        String uuid = UUID.randomUUID().toString().substring(0,8);
        User user = User.builder()
                .email(request.getEmail())
                .key(uuid)
                .build();

        if(userRepository.findByEmail(user.getEmail()).stream().count() == 1){
            throw new InvalidRequest("email","이미 등록된 이메일입니다.");
        }

        userRepository.save(user);

        sendMail(user.getKey(),user.getEmail());

        return user.getId();
    }
    public void keyValid(String key) {
        if(StringUtils.isNullOrEmpty(key))
        {
            throw new InvalidKey("Key","Key 를 입력해주세요!");
        }
        userRepository.findByKey(key)
                .orElseThrow(() -> new InvalidKey("Key","Key 값을 확인해주세요."));
    }

    public void sendMail(String key,String email) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        MailRequest mailRequest = MailRequest.builder()
                .key(key)
                .email(email)
                .build();

        simpleMailMessage.setTo(mailRequest.getEmail());
        simpleMailMessage.setSubject(mailRequest.getTitle());
        simpleMailMessage.setText(mailRequest.getContent());
        javaMailSender.send(simpleMailMessage);
    }
}
