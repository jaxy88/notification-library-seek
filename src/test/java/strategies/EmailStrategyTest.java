package strategies;

import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import config.NotificationConfigurator;
import logging.NotificationLogger;
import records.EmailConfig;
import records.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;

class EmailStrategyTest {

    private SendGrid mockSendGrid;
    private NotificationLogger mockLogger;
    private EmailConfig testConfig;
    private EmailStrategy emailStrategy;

    @BeforeEach
    void setUp() throws Exception {
        testConfig = new EmailConfig(
                "fake-api-key",
                "from@test.com",
                "Test Sender"
        );

        mockSendGrid = mock(SendGrid.class);
        mockLogger = mock(NotificationLogger.class);

        emailStrategy = new EmailStrategy(testConfig);

        Field sendGridField = EmailStrategy.class.getDeclaredField("sendGrid");
        sendGridField.setAccessible(true);
        sendGridField.set(emailStrategy, mockSendGrid);

        Field loggerField = NotificationConfigurator.class.getDeclaredField("logger");
        loggerField.setAccessible(true);
        loggerField.set(null, mockLogger);
    }

    @Test
    void send_shouldCallSendGridApi_andLogMessages() throws Exception {
        Response fakeResponse = new Response();
        fakeResponse.setStatusCode(202);
        when(mockSendGrid.api(any(Request.class))).thenReturn(fakeResponse);

        Notification notification = new Notification(
                "to@test.com",
                "Test Subject",
                "Test Body",
                enums.Priority.LOW
        );

        emailStrategy.send(notification);

        verify(mockLogger).log("Enviando Email vía SendGrid a: " + notification.recipient());
        verify(mockLogger).log("Email enviado correctamente. Status: 202");
        verify(mockSendGrid).api(any(Request.class));
    }

    @Test
    void send_shouldThrowException_whenSendGridFails() throws Exception {
        when(mockSendGrid.api(any(Request.class))).thenThrow(new IOException("Error de red"));

        Notification notification = new Notification(
                "to@test.com",
                "Test Subject",
                "Test Body",
                enums.Priority.LOW
        );

        try {
            emailStrategy.send(notification);
        } catch (RuntimeException e) {
            verify(mockLogger).error(eq("Error de red al conectar con SendGrid"), any(IOException.class));
            return;
        }
        throw new AssertionError("Se esperaba RuntimeException");
    }
}
