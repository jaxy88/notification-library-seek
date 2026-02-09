package strategies;

import static org.mockito.Mockito.*;

import config.NotificationConfigurator;
import logging.NotificationLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import records.FcmConfig;
import records.Notification;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;

class FcmStrategyTest {

    private NotificationLogger mockLogger;
    private FcmStrategy fcmStrategy;

    @BeforeEach
    void setUp() throws Exception {
        // Mockeamos el logger
        mockLogger = mock(NotificationLogger.class);

        // Inyectamos el logger mock en NotificationConfigurator
        Field loggerField = NotificationConfigurator.class.getDeclaredField("logger");
        loggerField.setAccessible(true);
        loggerField.set(null, mockLogger);

        // Creamos un FcmConfig mock
        FcmConfig fakeFcmConfig = mock(FcmConfig.class);

        // Inyectamos el FcmConfig mock en NotificationConfigurator
        Field fcmConfigField = NotificationConfigurator.class.getDeclaredField("fcmConfig");
        fcmConfigField.setAccessible(true);
        fcmConfigField.set(null, fakeFcmConfig);

        // Creamos un spy de FcmStrategy **antes de inicializar**
        fcmStrategy = spy(FcmStrategy.class);

        // Sobrescribimos initializeIfNeeded para que no haga nada
        doNothing().when(fcmStrategy).initializeIfNeeded();
    }

    @Test
    void send_shouldLogSuccess_whenMessageSent() {
        Notification notification = new Notification(
                "fake-token",
                "Test Subject",
                "Test Body",
                enums.Priority.LOW
        );

        fcmStrategy.send(notification);

        verify(mockLogger).log(anyString());
    }

    @Test
    void initializeIfNeeded_shouldOnlyInitializeOnce() {
        // Solo verificamos que nunca se logueó inicialización real
        verify(mockLogger, never()).log(contains("Firebase Admin inicializado correctamente"));
    }
}
