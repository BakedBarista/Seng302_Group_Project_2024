package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.service.JavaSmtpGmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class JavaSmtpGmailSenderApplication {

    @Autowired
    private JavaSmtpGmailSenderService senderService;

    @EventListener(ApplicationReadyEvent.class)
    public void sendMail(){
        senderService.sendEmail("imogenkeeling@gmail.com","This is subject","This is email body");
    }
}