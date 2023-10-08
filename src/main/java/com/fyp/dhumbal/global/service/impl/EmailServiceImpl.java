package com.fyp.dhumbal.global.service.impl;

import com.fyp.dhumbal.global.service.EmailService;
import com.fyp.dhumbal.global.util.ResourceUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${dhumbal.server.url}")
    private String baseUrl;

    public enum EmailType {
        OTP("OTP", "otp_email.html");

        @Getter
        private final String subject;
        @Getter
        private final String fileName;

        EmailType(String subject, String fileName) {
            this.subject = subject;
            this.fileName = fileName;
        }
    }


    private final JavaMailSender emailSender;

    @Override
    public void sendOtpEmail(String userId, String name, String email, String otp) throws MessagingException {
        Map<String, String> value = new HashMap<>();
        value.put("name", name);
        value.put("link", String.format("%s/v1/user/verify/%s/%s",baseUrl, userId, otp));
        sendEmail(EmailType.OTP, email, value);
    }

    private void sendEmail(EmailType type, String to, Map<String, String> value) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(type.subject);
        helper.setText(getBody(type, value), true);
        emailSender.send(message);
    }


    private String getBody(EmailType type, Map<String, String> values) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:email/" + type.fileName);
        String body = ResourceUtil.asString(resource);
        for (Map.Entry<String, String> entry : values.entrySet()) {
            body = body.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return body;
    }

}
