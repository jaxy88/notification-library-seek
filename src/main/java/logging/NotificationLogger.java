package logging;

public interface NotificationLogger {
    void log(String message);
    void error(String message, Throwable throwable);
}