package strategies;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import config.NotificationConfigurator;
import enums.Priority;
import logging.NotificationLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import records.Notification;
import records.SmsConfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SmsStrategyTest {

    private SmsStrategy smsStrategy;
    private NotificationLogger loggerMock;
    private MockedStatic<NotificationConfigurator> configuratorMock;

    @BeforeEach
    void setUp() {
        // Mock del SmsConfig
        SmsConfig mockConfig = mock(SmsConfig.class);
        when(mockConfig.accountSid()).thenReturn("fakeSid");
        when(mockConfig.authToken()).thenReturn("fakeToken");
        when(mockConfig.fromNumber()).thenReturn("+1234567890");

        // Mock estático de NotificationConfigurator
        configuratorMock = Mockito.mockStatic(NotificationConfigurator.class);
        configuratorMock.when(NotificationConfigurator::getSmsConfig).thenReturn(mockConfig);

        // Mock del logger
        loggerMock = mock(NotificationLogger.class);
        configuratorMock.when(NotificationConfigurator::getLogger).thenReturn(loggerMock);

        // Instancia la estrategia después de mockear todo
        smsStrategy = new SmsStrategy();
    }

    @Test
    void send_shouldCallTwilioMessageCreator() {
        Notification notification = new Notification("+0987654321", "Hola desde test",
                "Asunto test", Priority.MEDIUM);

        try (MockedStatic<Message> messageMock = Mockito.mockStatic(Message.class)) {
            // Mock del mensaje devuelto por create()
            Message messageObjMock = mock(Message.class);
            when(messageObjMock.getSid()).thenReturn("fakeSID");

            // Mock del método estático Message.creator
            messageMock.when(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), any(String.class)))
                    .thenAnswer(invocation -> new Object() {
                        public Message create() {
                            return messageObjMock;
                        }
                    });

            // Ejecuta el método real
            smsStrategy.send(notification);

            // Verifica que Message.creator fue llamado con los parámetros correctos
            messageMock.verify(() -> Message.creator(
                    new PhoneNumber(notification.recipient()),
                    new PhoneNumber("+1234567890"),
                    notification.body()
            ));

            // Verifica que el logger fue llamado
            verify(loggerMock).log("Enviando SMS vía Twilio a: " + notification.recipient());
            verify(loggerMock).log("SMS enviado correctamente. SID: fakeSID");
        }
    }
}
