package attilathehun.invitebruter;

public interface BruteListener {
    public void attemptSuccessful(String message);
    public void attemptUnsuccessful(String message);
    public void logMessage(String message);
}
