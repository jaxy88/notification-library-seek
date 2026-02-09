package strategies;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import config.NotificationConfigurator;
import records.Notification;
import records.SmsConfig;

public final class SmsStrategy implements NotificationStrategy {

    private final SmsConfig config;

    public SmsStrategy() {
        this.config = NotificationConfigurator.getSmsConfig();
        initializeTwilio();
    }

    private void initializeTwilio() {
        Twilio.init(config.accountSid(), config.authToken());
    }

    @Override
    public void send(Notification notification) {
        var logger = NotificationConfigurator.getLogger();

        try {
            logger.log("Enviando SMS vía Twilio a: " + notification.recipient());

            Message message = Message.creator(
                    new PhoneNumber(notification.recipient()),
                    new PhoneNumber(config.fromNumber()),
                    notification.body()
            ).create();

            logger.log("SMS enviado correctamente. SID: " + message.getSid());

        } catch (Exception e) {
            logger.error("Error enviando SMS vía Twilio", e);
            throw new RuntimeException("Fallo en la entrega del SMS", e);
        }
    }
}
