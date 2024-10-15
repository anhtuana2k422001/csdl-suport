package vn.name.hoanhtuan.csdlsuport.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendEmail(String to, String subject, String body){
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setFrom("hoanhtuan.it.dev.test@gmail.com");
        message.setSubject(subject);
        message.setText(body);

        javaMailSender.send(message);

    }
}
