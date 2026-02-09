package strategies;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import config.NotificationConfigurator;
import records.FcmConfig;

import java.io.FileInputStream;
import java.io.IOException;

public final class FcmStrategy implements NotificationStrategy {

    private static volatile boolean initialized = false;

    public FcmStrategy() {
        initializeIfNeeded();
    }

    public void initializeIfNeeded() {
        if (initialized) {
            return;
        }

        synchronized (FcmStrategy.class) {
            if (initialized) {
                return;
            }

            FcmConfig config = NotificationConfigurator.getFcmConfig();
            var logger = NotificationConfigurator.getLogger();

            try (FileInputStream serviceAccount =
                         new FileInputStream(config.serviceAccountJson())) {

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                initialized = true;
                logger.log("Firebase Admin inicializado correctamente (FCM). ");
            } catch (IOException e) {
                throw new IllegalStateException(
                        "Error inicializando Firebase Admin SDK", e
                );
            }
        }
    }

    @Override
    public void send(records.Notification notification) {
        var logger = NotificationConfigurator.getLogger();

        try {
            Message message = Message.builder()
                    .setToken(notification.recipient())
                    .setNotification(
                            com.google.firebase.messaging.Notification.builder()
                                    .setTitle(notification.subject())
                                    .setBody(notification.body())
                                    .build()
                    )
                    .build();

            String response = FirebaseMessaging
                    .getInstance()
                    .send(message);

            logger.log("FCM enviado correctamente. MessageId: " + response);

        } catch (Exception e) {
            logger.error("Error enviando notificación FCM", e);
            throw new RuntimeException("Error enviando notificación FCM", e);
        }
    }

}
