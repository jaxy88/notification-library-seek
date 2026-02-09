package records;

public record EmailConfig(
        String apiKey,
        String fromEmail,
        String senderName
) {
}
