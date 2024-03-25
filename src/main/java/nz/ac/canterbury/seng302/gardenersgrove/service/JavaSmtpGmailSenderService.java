package nz.ac.canterbury.seng302.gardenersgrove.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
public class JavaSmtpGmailSenderService {

    private JavaMailSender emailSender;

    public JavaSmtpGmailSenderService() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Set host
        mailSender.setHost("smtp.gmail.com");
        
        // Set Port
        mailSender.setPort(587);
        
        // Provide your Gmail address
        mailSender.setUsername("team800.garden@gmail.com"); 
        
        // Provide your Gmail password
        mailSender.setPassword("ujsk xhhn fydf kmeu");

        // Enables TLS (Transport Layer Security) when connecting to the smtp server 
        mailSender.getJavaMailProperties().put("mail.smtp.starttls.enable", "true");

        // Disables authentication for sender email when connecting to the smtp server
        mailSender.getJavaMailProperties().put("mail.smtp.auth", "false");
        emailSender = mailSender;
    }
    
    public void sendEmail(String toEmail, String subject, String body){
        // Create a new SimpleMailMessage object to compose the email
        SimpleMailMessage message = new SimpleMailMessage();

        // Set the sender's email address
        message.setFrom("team800.garden@gmail.com");

        // Set the recipient's email address
        message.setTo(toEmail);

        // Set the subject of the email
        message.setSubject(subject);

        // Set the body of the email
        message.setText(body);

        // Send the email using the emailSender object
        emailSender.send(message);

        // Print a success message to the console
        System.out.println("Message sent successfully");
    }
}
