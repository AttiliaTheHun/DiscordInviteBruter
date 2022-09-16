package attilathehun.invitebruter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
            tryInvite(generateLink());
            currentPosition.increase();
        }
    }

    /**
     * Tries an invite link and logs attempt result
     * @param inviteLink the URL address to try
     */
    private void tryInvite(String inviteLink) {

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(inviteLink).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            InputStream response = connection.getInputStream();
            String input =  new String(response.readAllBytes());
            response.close();
            String title = input.substring(input.indexOf("<title>") + "<title>".length(), input.indexOf("</title>"));
            int responseCode = connection.getResponseCode();
            String messageToLog;
            boolean successful = false;
            if(title.equals("Discord")) {
                messageToLog = "Fail - Invite: " + inviteLink;
            }else {
                messageToLog = "Server: " + title + " - Invite: " + inviteLink;
                successful = true;
            }
            String signature = " #" + name + " " + responseCode + "\n";
            messageToLog += signature;
            if(successful) {
                logAttemptSuccessful(messageToLog);
            } else {
                logAttemptUnsuccessful(messageToLog);
            }

            /**
             * @catch MalformedURLException
             * @catch ProtocolException
             */
        }catch(Exception e) {
            logMessage(e.getMessage());
        }
    }

    /**
     * Generates invite link for currentPosition
     * @return invite link based on current attempt base
     */
    private String generateLink(){
        StringBuilder builder = new StringBuilder();
        for(int number : currentPosition.array()) {
            builder.append(Bruter.getCharset().charAt(number));
        }
        return Bruter.getBaseURL().concat(new String(builder));
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
