package strategies;

import records.Notification;

public final class SlackStrategy implements NotificationStrategy {
    @Override
    public void send(Notification notification) {
        System.out.println("Publicando en Slack: " + notification.body());
        // Lógica de Webhook aquí
    }
}