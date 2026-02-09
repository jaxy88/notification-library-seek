package config;



public final class NotificationProvider {

    private static boolean isInitialized = false;

    private NotificationProvider() {}

    public static void initialize(NotificationProviderConfig config) {

        if (isInitialized) {
            throw new IllegalStateException(
                    "La librería ya fue inicializada previamente."
            );
        }

        if (config == null || config.noChannelsConfigured()) {
            throw new IllegalArgumentException(
                    "Debe configurarse al menos un canal de notificación."
            );
        }

        NotificationConfigurator.setConfigs(
                config.getFcm(),
                config.getEmail(),
                config.getSlack(),
                config.getSms(),
                config.getLogger()
        );



        isInitialized = true;

        NotificationConfigurator.getLogger()
                .log("Sistema de Notificaciones inicializado correctamente.");
    }
}

