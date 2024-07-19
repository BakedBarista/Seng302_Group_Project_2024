package nz.ac.canterbury.seng302.gardenersgrove.service;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;

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
        sendEmail(formatNameAddr(to), subject, body);
    }

    /**
     * Generates a URL string for the reset password link
     * @param request the HTTP request
     * @return the URL string
     */
    public String generateUrlString(HttpServletRequest request, String token) {
        // Get the URL they requested from (not the localhost)
        StringBuilder url = new StringBuilder();
        url.append(request.getScheme()).append("://").append(request.getServerName());

        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            url.append(":").append(request.getServerPort());
        }

        url.append(request.getContextPath()); // This is the /test or /prod
        url.append("/users/reset-password/callback?token=").append(token);
        return url.toString();
    }

    private String formatNameAddr(GardenUser user) {
        StringBuilder sb = new StringBuilder();
        sb.append(user.getFname());
        if (user.getLname() != null) {
            sb.append(" ").append(user.getLname());
        }
        sb.append(" <").append(user.getEmail()).append(">");
        return sb.toString();
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

        // Construct a new SimpleMailMessage object
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_HEADER);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        // Send the email using the emailSender object
        emailSender.send(message);

        logger.info("Message sent successfully");
    }

}
