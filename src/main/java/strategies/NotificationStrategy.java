package strategies;

import records.Notification;


public sealed interface NotificationStrategy
        permits EmailStrategy, SlackStrategy, SmsStrategy, FcmStrategy {

    void send(Notification notification);

    default String getChannelName() {
        return this.getClass().getSimpleName();
    }
}