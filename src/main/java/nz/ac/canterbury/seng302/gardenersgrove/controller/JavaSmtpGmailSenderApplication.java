package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.service.JavaSmtpGmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class JavaSmtpGmailSenderApplication {

    private final JavaSmtpGmailSenderService senderService;

    public JavaSmtpGmailSenderApplication() {
        this.senderService = new JavaSmtpGmailSenderService(); // Manually instantiate the service
    }

    @EventListener(ApplicationReadyEvent.class)
    public void sendMail(){
        senderService.sendEmail("lce26@uclive.ac.nz","SpringBoot Automated Email","Morgan English is a legend");
    }
}