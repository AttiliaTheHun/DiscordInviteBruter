package attilathehun.invitebruter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Bruter implements Runnable {

    private final transient List<BruteListener> listeners = new ArrayList<>();

    private static final String BASE_URL = "https://discord.gg/";
    final static String CHARSET = "aAbBcCdDeEfFgGiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789";
    private SourceArrayVault currentPosition; // Holds data for the upcoming attempt
    private int[] endPosition; // Data for the last attempt of this particular Bruter
    private int name;
    private boolean running = true;

    private InviteManager manager = new InviteManager(this);

    /**
     * Default constructor for multithrea*ding* support
     * @param name Bruter's name to differentiate between the individual Bruters
     * @param start data for the starting position of this Bruter
     * @param end data for the final position of this Bruter
     */
    public Bruter(int name, SourceArrayVault start, int[] end) {
        this.currentPosition = start;
        this.endPosition = end;
        this.name = name;
    }

    private Bruter() {}

    //TODO: skip 9-characters long invites for Discord uses no such thing
    @Override
    public void run() {
        logMessage("Thread #" + name + " started\n");
        while (running) {
            if(currentPosition.equals(endPosition)){
                finish();
            }
            attempt();
            currentPosition.increase();
        }
    }

    /**
     * Tries whether invite link of the currentPosition is valid and creates proper record
     */
    private void attempt() {
        try {
            InviteManager.Invite invite = InviteManager.resolve(InviteManager.generateLink(currentPosition));
            String signature = " #" + name + " " + invite.responseCode() + "\n";
            if (invite.isValid()) {
                String message = "Server: " + invite.name() + " - Invite: " + invite.link() + signature;
                logAttemptSuccessful(message);
            } else {
                String message = "Fail - Invite: " + invite.link() + signature;
                logAttemptUnsuccessful(message);
            }
        } catch (IOException e) {
            logMessage(e.getMessage());
        }
    }

    /**
     * Adds a BruteListener to the list, making it feel the events
     * @param toAdd the BruteListener that will be added
     */
    public void addListener(BruteListener toAdd) {
        listeners.add(toAdd);
    }

    /**
     * Notifies all the BruteListeners of our successful attempt
     * @param message record of the attempt
     */
    private void logAttemptSuccessful(String message) {
        for (BruteListener bl : listeners){
            bl.attemptSuccessful(message);
        }
    }

    /**
     * Notifies all the BruteListeners of our not so successful attempt
     * @param message record of the attempt
     */
    private void logAttemptUnsuccessful(String message) {
        for (BruteListener bl : listeners){
            bl.attemptUnsuccessful(message);
        }
    }

    /**
     * Internal function to log a message through the Logger
     * @param message the message to log
     */
    private void logMessage(String message){
        for (BruteListener arl : listeners){
            arl.logMessage(message);
        }
    }

    /**
     * Terminating (closing) means the thread was force ended and there may be untried
     * possibilities left
     */
    public void terminate(){
        logMessage("Thread #" + name + " closed\n");
        this.running = false;
    }

    /**
     * Finishing is natural way of the thread to close itself when all possibilities
     * in it's range were tried out
     */
    private void finish(){
        logMessage("Thread #" + name + " finished\n");
        this.running = false;
    }

    public int[] getAttemptBase() {
        return currentPosition.array();
    }

    public int[] getEndPosition() {
        return endPosition;
    }

    public static String getCharset() {
        return Bruter.CHARSET;
    }

    public static int getCharsetLength() {
        return Bruter.CHARSET.length();
    }

    public static String getBaseURL() {
        return Bruter.BASE_URL;
    }

}
