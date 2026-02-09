package main;


import config.NotificationProvider;
import config.NotificationProviderConfig;
import constant.Constants;
import enums.Priority;
import logging.DefaultNotificationLogger;
import records.EmailConfig;
import records.FcmConfig;
import records.Notification;
import records.SmsConfig;
import services.SmartNotificationService;
import strategies.EmailStrategy;
import strategies.FcmStrategy;
import strategies.SmsStrategy;

public class Application {

    public static void main(String[] args) {
        NotificationProvider.initialize(
                NotificationProviderConfig.builder()
                        .fcm( new FcmConfig(Constants.SERVICE_ACCOUNT_PATH))
                        .sms( new SmsConfig(Constants.TWILIO_ACCOUNT_SID, Constants.TWILIO_AUTH_TOKEN, Constants.TWILIO_NUMBER))
                        .email(new EmailConfig(Constants.API_KEY_SENDGRID, Constants.SENDGRID_EMAIL_CONFIG, Constants.SENDER_NAME))
                        .logger(new DefaultNotificationLogger())
                .build()
        );


        /*new FcmStrategy().send(
                new records.Notification(
                        Constants.TOKEN_FCM, "Salir con Krisff ...", "Ayer no la saque",
                        Priority.HIGH
                )
        );*/

        /*new SmsStrategy().send(
                new records.Notification(
                        "+573239432406", "Test sms to user", "Test send sms",
                        Priority.HIGH
                )
        );*/

        /*new EmailStrategy().send(
                new records.Notification(
                        "codebydevs@gmail.com", "Test email using sendgrid", "Test send email",
                        Priority.HIGH
                )
        );*/


        /*SmartNotificationService service = new SmartNotificationService();
        service.dispatch(new Notification(Constants.TOKEN_FCM, "Salir con Krisff ...", "Ayer no la saque",
                Priority.HIGH));*/

        /*SmartNotificationService service = new SmartNotificationService();
        service.dispatch(new Notification("codebydevs@gmail.com", "Test email using sendgrid", "Test send email",
                Priority.LOW));*/
    }
}
