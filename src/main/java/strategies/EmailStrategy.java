package strategies;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import config.NotificationConfigurator;
import records.EmailConfig;
import records.Notification;

import java.io.IOException;

public final class EmailStrategy implements NotificationStrategy {

    private final EmailConfig config;
    private final SendGrid sendGrid;

    public EmailStrategy() {
        this(NotificationConfigurator.getEmailConfig());
    }

    public EmailStrategy(EmailConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("EmailConfig no puede ser null");
        }
        this.config = config;
        this.sendGrid = new SendGrid(config.apiKey());
    }


    @Override
    public void send(Notification notification) {
        var logger = NotificationConfigurator.getLogger();

        Email from = new Email(config.fromEmail(), config.senderName());
        Email to = new Email(notification.recipient());
        Content content = new Content("text/plain", notification.body());

        Mail mail = new Mail(from, notification.subject(), to, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            logger.log("Enviando Email vía SendGrid a: " + notification.recipient());

            Response response = sendGrid.api(request);

            if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
                throw new IllegalStateException(
                        "SendGrid rechazó el envío. Status: " + response.getStatusCode()
                );
            }

            logger.log("Email enviado correctamente. Status: " + response.getStatusCode());

        } catch (IOException e) {
            logger.error("Error de red al conectar con SendGrid", e);
            throw new RuntimeException("Fallo en la entrega de Email", e);
        }
    }
}
