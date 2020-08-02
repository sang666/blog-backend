package com.sang.blog.biz.service.impl;


import com.sang.blog.commom.utils.EmailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public class TaskService {

    @Async
    public void sendEmailVerifyCode(String verifyCode, String emailAddress) throws MessagingException {
        EmailSender.sendRegisterVerifyCode(verifyCode, emailAddress);
    }
}
