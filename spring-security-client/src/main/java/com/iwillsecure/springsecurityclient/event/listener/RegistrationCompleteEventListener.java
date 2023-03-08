package com.iwillsecure.springsecurityclient.event.listener;

import com.iwillsecure.springsecurityclient.entity.User;
import com.iwillsecure.springsecurityclient.event.RegistrationCompleteEvent;
import com.iwillsecure.springsecurityclient.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class RegistrationCompleteEventListener implements
        ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        //Create the verification token for user with link.
        User user=event.getUser();
        String token= UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token,user);
        //Send Mail to user
        String url=
                event.getApplicationUrl()
                        + "/verifyRegistration?token="
                        + token;
        //sendVerificationEmail()
        log.info("Click the link to verify your account: {}",url);
    }
}
