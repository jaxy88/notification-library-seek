package config;

import lombok.Builder;
import lombok.Getter;

import logging.NotificationLogger;
import records.EmailConfig;
import records.FcmConfig;
import records.SlackConfig;
import records.SmsConfig;

@Getter
@Builder
public class NotificationProviderConfig {

    private FcmConfig fcm;
    private EmailConfig email;
    private SlackConfig slack;
    private SmsConfig sms;
    private NotificationLogger logger;

    public boolean noChannelsConfigured() {
        return fcm == null && email == null && slack == null && sms == null;
    }
}

