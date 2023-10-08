package com.fyp.dhumbal.global.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendOtpEmail(String userId, String name, String email, String otp) throws MessagingException;
}
