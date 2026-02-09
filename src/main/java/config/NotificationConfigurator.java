package config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import logging.NotificationLogger;
import records.EmailConfig;
import records.FcmConfig;
import records.SlackConfig;
import records.SmsConfig;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationConfigurator {

    private static FcmConfig fcmConfig;
    private static EmailConfig emailConfig;
    private static SlackConfig slackConfig;
    private static SmsConfig smsConfig;
    private static NotificationLogger logger;


    public static FcmConfig getFcmConfig() {
        return check(fcmConfig, "FCM");
    }

    public static EmailConfig getEmailConfig() {
        return check(emailConfig, "Email");
    }

    public static SlackConfig getSlackConfig() {
        return check(slackConfig, "Slack");
    }

    public static SmsConfig getSmsConfig() {
        return check(smsConfig, "SMS");
    }

    public static NotificationLogger getLogger() {
        return logger != null ? logger : NOOP_LOGGER;
    }


    static void setConfigs(
            FcmConfig fcm,
            EmailConfig email,
            SlackConfig slack,
            SmsConfig sms,
            NotificationLogger log
    ) {
        fcmConfig = fcm;
        emailConfig = email;
        slackConfig = slack;
        smsConfig = sms;
        logger = (log != null) ? log : NOOP_LOGGER;
    }


    private static <T> T check(T config, String name) {
        if (config == null) {
            throw new IllegalStateException(
                    "El canal " + name + " no ha sido configurado."
            );
        }
        return config;
    }

    private static final NotificationLogger NOOP_LOGGER =
            new NotificationLogger() {
                @Override public void log(String message) {}
                @Override public void error(String message, Throwable t) {}
            };
}
