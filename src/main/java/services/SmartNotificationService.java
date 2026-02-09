package services;

import config.NotificationConfigurator;
import enums.Priority;
import records.Notification;
import strategies.EmailStrategy;
import strategies.FcmStrategy;
import strategies.NotificationStrategy;
import strategies.SlackStrategy;
import strategies.SmsStrategy;

public class SmartNotificationService {

    public void dispatch(Notification notification) {
        var logger = NotificationConfigurator.getLogger();

        try {
            NotificationStrategy strategy = selectStrategy(notification.priority());
            strategy.send(notification);
        } catch (Exception e) {
            logger.error("Fallo el canal principal (" + notification.priority() + ")." +
                    " Intentando backup por Email...", e);

            try {
                new EmailStrategy().send(notification);
            } catch (Exception fatal) {
                logger.error("FALLO CRÍTICO: El respaldo también falló.", fatal);
            }
        }
    }

    NotificationStrategy selectStrategy(Priority priority) {
        return switch (priority) {
            case LOW      -> (NotificationStrategy) new EmailStrategy();
            case MEDIUM   -> (NotificationStrategy) new SlackStrategy();
            case HIGH     -> (NotificationStrategy) new FcmStrategy();
            case CRITICAL -> (NotificationStrategy) new SmsStrategy();
        };
    }
}