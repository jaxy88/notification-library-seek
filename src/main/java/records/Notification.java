package records;

import enums.Priority;

public record Notification(String recipient, String subject, String body, Priority priority) {}
