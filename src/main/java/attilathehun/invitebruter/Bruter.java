<<<<<<< HEAD
/*
 * This class is responsible for the actual brute-force process
 */
package attilathehun.invitebruter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Bruter implements Runnable{

    private List<AttemptResultListener> listeners = new ArrayList<AttemptResultListener>();

    private final String baseURL = "https://discord.gg/";
    final static String charSet = "aAbBcCdDeEfFgGiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789";
    private int[] attemptBase;
    private int[] endPosition;
    private int name;
    private boolean RUN = true;

    public Bruter(int name, int[] start, int[] end){
        this.attemptBase = start;
        this.endPosition = end;
        this.name = name;
    }

    @Override
    public void run() {
        logMessage("Thread #" + name + " started\n");
        while (RUN) {
            if(attemptBase == endPosition){
                finish();
            }
            for (int i = 0; i < charSet.length(); i++) {
                attemptBase[attemptBase.length - 1]++;
                tryInvite(generateLink());
                transitions:
                for (int t = 1; t < attemptBase.length; t++) {
                    if (t == attemptBase.length - 1 && attemptBase[t] == charSet.length() - 1) {
                        attemptBase[i] = 0;
                        attemptBase[i - 1]++;
                    } else if (attemptBase[i] == charSet.length() - 1 && attemptBase[i - 1] != charSet.length() - 1 && attemptBase[i + 1] == charSet.length() - 1) { //we need to make sure we don't dump any options
                        attemptBase[i] = 0;
                        attemptBase[i - 1]++;
                    }
                    tryInvite(generateLink());
                }
                switch (attemptBase.length) {
                    case 8:
                        attemptBase = new int[10];
                        break;
                    case 10:
                        finish();
                        break;
                    default:
                        attemptBase = new int[attemptBase.length + 1];
                }
                for (int a : attemptBase) {
                    attemptBase[a] = 0;
                }

            }
        }
    }

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
            boolean toDump = false;
            if(title.equals("Discord")) {
                messageToLog = "Fail - Invite: " + inviteLink;
            }else {
                messageToLog = "Server: " + title + " - Invite: " + inviteLink;
                toDump = true;
            }
            String signature = " #" + name + " " + responseCode + "\n";
            messageToLog += signature;
            resultAttempt(responseCode, messageToLog, toDump);

        /*
        * @catch MalformedURLException
        * @catch ProtocolException
        */
        }catch(Exception e) {
            logMessage(e.getMessage());
        }
    }

    private String generateLink(){
        String invite = "";
        for(int number : attemptBase) {
            invite += charSet.charAt(number);
        }
        return baseURL.concat(invite);
    }

    public void addListener(AttemptResultListener toAdd) {
        listeners.add(toAdd);
    }

    private void resultAttempt(int responseCode, String messageToLog, boolean toDump){
        for (AttemptResultListener arl : listeners){
            arl.attemptResulted(messageToLog, toDump);
        }
    }

    private void logMessage(String message){
        for (AttemptResultListener arl : listeners){
            arl.logMessage(message);
        }
    }

    /*
     * Exiting (closing) means the thread was ended by force and there may be untried
     * possibilities left
     */
    public void exit(){
        logMessage("Thread #" + name + " closed\n");
        this.RUN = false;
    }

    /*
    * Finishing is natural way of the thread to close itself when all possibilities
    * in his range were tried
    */
    private void finish(){
        logMessage("Thread #" + name + " finished\n");
        this.RUN = false;
    }

    public int[] getAttemptBase() {
        return attemptBase;
    }

    public int[] getEndPosition() {
        return endPosition;
    }

    public static int getCharsetLength(){
        return charSet.length();
    }
}
=======
package attilathehun.invitebruter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Bruter implements Runnable{

    private List<AttemptResultListener> listeners = new ArrayList<AttemptResultListener>();

    private final String baseURL = "https://discord.gg/";
    final static String charSet = "aAbBcCdDeEfFgGiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789";
    private int[] attemptBase;
    private int[] endPosition;
    private int name;
    private boolean RUN = true;

    public Bruter(int name, int[] start, int[] end){
        this.attemptBase = start;
        this.endPosition = end;
        this.name = name;
    }

    @Override
    public void run() {
        logMessage("Thread #" + name + " started\n");
        while (RUN) {
            if(attemptBase == endPosition){
                finish();
            }
            for (int i = 0; i < charSet.length(); i++) {
                attemptBase[attemptBase.length - 1]++;
                tryInvite(generateLink());
                transitions:
                for (int t = 1; t < attemptBase.length; t++) {
                    if (t == attemptBase.length - 1 && attemptBase[t] == charSet.length() - 1) {
                        attemptBase[i] = 0;
                        attemptBase[i - 1]++;
                    } else if (attemptBase[i] == charSet.length() - 1 && attemptBase[i - 1] != charSet.length() - 1 && attemptBase[i + 1] == charSet.length() - 1) { //we need to make sure we don't dump any options
                        attemptBase[i] = 0;
                        attemptBase[i - 1]++;
                    }
                    tryInvite(generateLink());
                }
                switch (attemptBase.length) {
                    case 8:
                        attemptBase = new int[10];
                        break;
                    case 10:
                        finish();
                        break;
                    default:
                        attemptBase = new int[attemptBase.length + 1];
                }
                for (int a : attemptBase) {
                    attemptBase[a] = 0;
                }

            }
        }
    }

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
            boolean toDump = false;
            if(title.equals("Discord")) {
                messageToLog = "Fail - Invite: " + inviteLink;
            }else {
                messageToLog = "Server: " + title + " - Invite: " + inviteLink;
                toDump = true;
            }
            String signature = " #" + name + " " + responseCode + "\n";
            messageToLog += signature;
            resultAttempt(responseCode, messageToLog, toDump);

        /*
        * @catch MalformedURLException
        * @catch ProtocolException
        */
        }catch(Exception e) {
            logMessage(e.getMessage());
        }
    }

    private String generateLink(){
        String invite = "";
        for(int number : attemptBase) {
            invite += charSet.charAt(number);
        }
        return baseURL.concat(invite);
    }

    public void addListener(AttemptResultListener toAdd) {
        listeners.add(toAdd);
    }

    private void resultAttempt(int responseCode, String messageToLog, boolean toDump){
        for (AttemptResultListener arl : listeners){
            arl.attemptResulted(messageToLog, toDump);
        }
    }

    private void logMessage(String message){
        for (AttemptResultListener arl : listeners){
            arl.logMessage(message);
        }
    }

    public void exit(){
        logMessage("Thread #" + name + " closed\n");
        this.RUN = false;
    }

    private void finish(){
        logMessage("Thread #" + name + " finished\n");
        this.RUN = false;
    }

}
>>>>>>> 02c222fe7bfdc69c01f2b58d027fe37fe080bbd8
