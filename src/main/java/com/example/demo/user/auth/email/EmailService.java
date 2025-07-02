package com.example.demo.user.auth.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendResetLink(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Parolni tiklash");
        message.setText("Parolni tiklash uchun quyidagi havolani bosing:\n\n" + resetLink);
        message.setFrom("palankasovbirnarsa@gmail.com");  // Gmail'dagi emailingizni yozing

        mailSender.send(message);
    }
}

