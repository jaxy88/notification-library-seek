package logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultNotificationLogger implements NotificationLogger {

    private final Logger logger = LoggerFactory.getLogger("Notification");

    @Override
    public void log(String message) {
        logger.info(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        if (throwable != null) {
            logger.error(message, throwable);
        } else {
            logger.error(message);
        }
    }
}
