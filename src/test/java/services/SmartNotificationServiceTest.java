package services;

import config.NotificationConfigurator;
import enums.Priority;
import logging.NotificationLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import records.Notification;
import strategies.EmailStrategy;
import strategies.FcmStrategy;
import strategies.NotificationStrategy;
import strategies.SlackStrategy;
import strategies.SmsStrategy;

import static org.mockito.Mockito.*;

public class SmartNotificationServiceTest {

    private SmartNotificationService service;
    private NotificationLogger loggerMock;

    @BeforeEach
    void setUp() {
        service = new SmartNotificationService();

        // Mock del logger
        loggerMock = mock(NotificationLogger.class);

        // Mock estático de NotificationConfigurator.getLogger()
        MockedStatic<NotificationConfigurator> configuratorMock = Mockito.mockStatic(NotificationConfigurator.class);
        configuratorMock.when(NotificationConfigurator::getLogger).thenReturn(loggerMock);
    }

    @Test
    void dispatch_shouldUseFcmStrategyForHighPriority() throws Exception {
        // Creamos un mock de FcmStrategy
        FcmStrategy fcmMock = mock(FcmStrategy.class);

        Notification notification = new Notification("token", "Mensaje", "Asunto", Priority.HIGH);

        // Mock estático de selectStrategy para inyectar nuestro mock
        SmartNotificationService spyService = spy(service);
        doReturn(fcmMock).when(spyService).selectStrategy(Priority.HIGH);

        // Ejecutamos
        spyService.dispatch(notification);

        // Verificamos que la estrategia se llamó
        verify(fcmMock).send(notification);
        verifyNoInteractions(loggerMock); // no se debe loguear error
    }

    @Test
    void dispatch_shouldFallbackToEmailIfPrimaryFails() throws Exception {
        Notification notification = new Notification("token", "Mensaje", "Asunto", Priority.HIGH);

        // Mock de estrategia principal que lanza excepción
        NotificationStrategy failingStrategy = mock(FcmStrategy.class);
        doThrow(new RuntimeException("Fallo principal")).when(failingStrategy).send(notification);

        // Mock de EmailStrategy
        EmailStrategy emailMock = mock(EmailStrategy.class);

        // Spy para interceptar selectStrategy y crear EmailStrategy
        SmartNotificationService spyService = spy(service);
        doReturn(failingStrategy).when(spyService).selectStrategy(Priority.HIGH);
        doReturn(emailMock).when(spyService).selectStrategy(Priority.LOW); // fallback

        spyService.dispatch(notification);

        verify(failingStrategy).send(notification);
        verify(emailMock).send(notification);
        verify(loggerMock).error(contains("Fallo el canal principal"), any());
    }

    @Test
    void dispatch_shouldLogCriticalIfFallbackEmailFails() throws Exception {
        Notification notification = new Notification("token", "Mensaje", "Asunto", Priority.HIGH);

        // Estrategia principal y backup que fallan
        NotificationStrategy failingPrimary = mock(FcmStrategy.class);
        doThrow(new RuntimeException("Fallo principal")).when(failingPrimary).send(notification);

        EmailStrategy failingEmail = mock(EmailStrategy.class);
        doThrow(new RuntimeException("Fallo email")).when(failingEmail).send(notification);

        SmartNotificationService spyService = spy(service);
        doReturn(failingPrimary).when(spyService).selectStrategy(Priority.HIGH);
        doReturn(failingEmail).when(spyService).selectStrategy(Priority.LOW);

        spyService.dispatch(notification);

        verify(loggerMock).error(contains("Fallo el canal principal"), any());
        verify(loggerMock).error(contains("FALLO CRÍTICO"), any());
    }
}
