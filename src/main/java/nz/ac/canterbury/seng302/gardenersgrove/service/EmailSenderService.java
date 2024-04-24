package nz.ac.canterbury.seng302.gardenersgrove.service;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;

/**
 * Used to send emails. Currently only support plain-text emails.
 */
@Service
public class EmailSenderService {
    private static final String FROM_HEADER = "Gardener's Grove (Team 800) <team800.garden@gmail.com>";

    private static Logger logger = LoggerFactory.getLogger(EmailSenderService.class);

    private JavaMailSender emailSender;
    private Executor executor;

    public EmailSenderService(JavaMailSender emailSender, @Qualifier("taskScheduler") Executor executor) {
        this.emailSender = emailSender;
        this.executor = executor;
    }

    /**
     * Sends a plain-text email to the specified user with the specified
     * subject and body. This method spawns a new thread to send the email.
     * 
     * @param to      The user to send the email to
     * @param subject The subject of the email
     * @param body    The plain-text body of the email
     */
    public void sendEmail(GardenUser to, String subject, String body) {
        StringBuilder recipientBuilder = new StringBuilder();
        recipientBuilder.append(to.getFname());
        if (to.getLname() != null) {
            recipientBuilder.append(" ").append(to.getLname());
        }
        recipientBuilder.append(" <").append(to.getEmail()).append(">");
        String recipient = recipientBuilder.toString();

        sendEmail(recipient, subject, body);
    }


    /**
     * Sends a plain-text email to the specified recipient with the specified
     * subject and body. This method spawns a new thread to send the email.
     * 
     * See https://datatracker.ietf.org/doc/html/rfc5322#section-3.4 for the grammar
     * of the address list.
     * 
     * @param to      A comma separated list of emails or name-addr pairs (e.g.
     *                "Alice <alice@example.com>, Bob <bob@example.com>")
     * @param subject The subject of the email
     * @param body    The plain-text body of the email
     */
    public void sendEmail(String to, String subject, String body) {
        // Send the email on a separate thread to improve responsiveness
        executor.execute(() -> sendEmailOnCurrentThread(to, subject, body));
    }

    private void sendEmailOnCurrentThread(String to, String subject, String body) {
        logger.info("Sending email to \"{}\" with subject \"{}\"", to, subject);

        // Create a new SimpleMailMessage object to compose the email
        SimpleMailMessage message = new SimpleMailMessage();

        // Set the sender's email address
        message.setFrom(FROM_HEADER);

        // Set the recipient's email address
        message.setTo(to);

        // Set the subject of the email
        message.setSubject(subject);

        // Set the body of the email
        message.setText(body);

        // Send the email using the emailSender object
        emailSender.send(message);

        // Print a success message to the console
        logger.info("Message sent successfully");
    }
}
