# Notification Library

Java library para enviar notificaciones por **Firebase (FCM)**, **Twilio (SMS)**, **SendGrid (Email)** y **Slack**.

Incluye un **Quick Start**, configuración y cómo ejecutar tu proyecto dentro de Docker.

---

## Quick Start

1. Clona el repositorio:

```bash
git clone https://github.com/jaxy88/notification-library-seek.git
cd notification-library

## Requisitos

- Java 21
- Gradle
- Docker (opcional)
- Conexión a Internet para APIs externas (Twilio, SendGrid, Firebase, Slack)

---

## Dependencias (Gradle)

```gradle
plugins {
    id 'java-library'
}

group = 'com.notification-lib'
version = '1.0-0'

repositories {
    mavenCentral()
}

dependencies {
    testImplementation platform('org.junit:junit-bom:5.10.0')
    testImplementation 'org.junit.jupiter:junit-jupiter'

    testImplementation 'org.mockito:mockito-core:5.5.0'
    testImplementation 'org.mockito:mockito-junit-jupiter:5.5.0'

    implementation 'com.google.firebase:firebase-admin:9.2.0'
    implementation 'com.twilio.sdk:twilio:10.1.0'
    implementation 'com.sendgrid:sendgrid-java:4.10.2'
    implementation 'ch.qos.logback:logback-classic:1.5.13'

    compileOnly 'org.projectlombok:lombok:1.18.30'
    annotationProcessor 'org.projectlombok:lombok:1.18.30'
}

test {
    useJUnitPlatform()
}

Quick Start
Inicialización de NotificationProvider

NotificationProvider.initialize(
    NotificationProviderConfig.builder()
        .fcm(new FcmConfig(Constants.SERVICE_ACCOUNT_PATH))
        .sms(new SmsConfig(Constants.TWILIO_ACCOUNT_SID, Constants.TWILIO_AUTH_TOKEN, Constants.TWILIO_NUMBER))
        .email(new EmailConfig(Constants.API_KEY_SENDGRID, Constants.SENDGRID_EMAIL_CONFIG, Constants.SENDER_NAME))
        .logger(new DefaultNotificationLogger())
        .build()
);


Enviar notificaciones

/* FCM */
new FcmStrategy().send(
    new Notification(Constants.TOKEN_FCM, "Mensaje FCM", "Asunto FCM", Priority.HIGH)
);

/* SMS */
new SmsStrategy().send(
    new Notification("+573239432406", "Mensaje SMS", "Asunto SMS", Priority.HIGH)
);

/* Email */
new EmailStrategy().send(
    new Notification("correo@dominio.com", "Mensaje Email", "Asunto Email", Priority.HIGH)
);

/* Slack */
new SlackStrategy().send(
    new Notification(Constants.SLACK_CHANNEL, "Mensaje Slack", "Asunto Slack", Priority.MEDIUM)
);

/* SmartNotificationService con fallback automático */
SmartNotificationService service = new SmartNotificationService();
service.dispatch(new Notification(Constants.TOKEN_FCM, "Mensaje FCM con fallback", "Asunto", Priority.HIGH));


Configuración de Constantes

public class Constants {

    public static String SERVICE_ACCOUNT_PATH = "src/main/resources/service-account.json";
    public static String TOKEN_FCM = "c7EKVpi2hkW2l0iR2at8_K:APA91bHfNh_buTTn1mu8-6SkMsJOY5Q200UxqASpgvmnA-6bvkpBff0fZERxT02pWebDIYlSPlfSeNkXkHsxzBbct4pXVnQWgsHXHUeoDsKJzKki9hgotB8";

    public static String SLACK_WEBHOOK = "https://hooks.slack.com/";
    public static String SLACK_CHANNEL = "#dev";

    public static String API_KEY_SENDGRID = "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";
    public static String SENDGRID_EMAIL_CONFIG = "juancasares2030@gmail.com";
    public static String SENDER_NAME = "JHON JAVIER RENTERIA";
    public static String SUBJECT_OF_THE_EMAIL = "task created with due date";

    public static String TWILIO_ACCOUNT_SID = "ACe41b8fd77c066b69f1cb1665c93798b5";
    public static String TWILIO_AUTH_TOKEN = "64e062c1affdc55b8d0e6fb3963d7546";
    public static String TWILIO_NUMBER = "+15594911977";
}


Construir y ejecutar el contenedor

# Construir imagen
docker build -t notification-lib .

# Ejecutar contenedor
docker run --rm notification-lib


Testing
./gradlew test


Notas

Prioridades de notificación:

LOW → Email

MEDIUM → Slack

HIGH → FCM

CRITICAL → SMS

SmartNotificationService maneja fallback automático si falla el canal principal.

Ajustar los valores de Constants.java antes de producción.