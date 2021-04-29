package attilathehun.invitebruter;

public interface AttemptResultListener {
    void attemptResulted(String messageToLog, boolean toDump);
    void logMessage(String message);
}
