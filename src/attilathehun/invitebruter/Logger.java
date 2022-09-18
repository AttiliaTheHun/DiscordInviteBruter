/**
 * The Bruter's Logger, logs results of individual attempts
 */
package attilathehun.invitebruter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Logger implements Runnable, BruteListener{

    private volatile boolean running = true;

    private File dumpFile;
    private FileOutputStream logFileOutputStream;

    private boolean consoleMode = false;

    public Logger(){
        init();
    }

    /**
     * Runnable#run(), keeping the Logger online
     */
    @Override
    public void run() {
        while (running) {
            Thread.onSpinWait();
            // Runs indefinitely, the Logger is closed via an external call to Logger#close()
            // All communication is done through events
        }
    }
    /**
     * Class' internal command to write a String inside "log" file
     * @param s the String to log
     */
    private void log(String s){
        if(consoleMode) {
            return; // in case of console mode we do not want to get spammed by regular logs
            // we only care about successful attempts
            // sadly, we lose "Thread #n finished" quotes, but covering this would need to
            // redesign the logging architecture even more
        }
        try {
            logFileOutputStream.write(s.getBytes(StandardCharsets.UTF_8));
        }catch (Exception e){
            e.printStackTrace(System.out);
        }
    }
    /**
     * Class' internal command to write a String inside "dump" file
     * @param s the String to log
     */
    private void dump(String s){
        if(consoleMode) {
            System.out.println(s);
            return;
        }
        try {
            FileOutputStream dumpFileOutputStream = new FileOutputStream(dumpFile, true);
            dumpFileOutputStream.write(s.getBytes(StandardCharsets.UTF_8));
            dumpFileOutputStream.close();  //The dumps will not be so frequent to keep the file open
        }catch(Exception e){
            e.printStackTrace(System.out);
        }
    }

    /**
     * Initializes Logger necessary resources
     */
    private void init(){
        try{
            initLogFile();
            initDumpFile();
        }catch(Exception e){
            System.out.println("Unable to create Logger");
            System.out.println("The dump will now continue in the console");
            consoleMode = true;
        }
    }

    /**
     * Creates "log" file if doesn't exist and setups logFileOutputStream
     */
    private void initLogFile() throws IOException {
        File logFile = new File("log");
        logFile.createNewFile();
        logFileOutputStream = new FileOutputStream(logFile, true);
    }

    /**
     * Creates "dump" file if doesn't exist and logs its size if it does
     */
    private void initDumpFile() throws IOException {
        dumpFile = new File("dump");

        if(dumpFile.createNewFile()){
            log("Dump file created\n");
        }else{
            log("Dump file found (" + dumpFile.length() / 1024 + " kb)\n");
        }
    }

    /**
     * Public method to log successful attempt results
     * @param message the message to be logged and dumped
     * @see BruteListener#attemptSuccessful(String)
     */
    @Override
    public void attemptSuccessful(String message) {
        log(message);
        dump(message);
    }

    /**
     * Public method to log results of an attempt that did not hit the hole
     * @param message the message to be logged
     * @see BruteListener#attemptUnsuccessful(String)
     */
    @Override
    public void attemptUnsuccessful(String message) {
        log(message);
    }

    /**
     * Public method to log messages into the "log" file
     * @param message the message to be logged
     * @see BruteListener#logMessage(String)
     */
    @Override
    public void logMessage(String message) {
        log(message);
    }

    /**
     * Finishes the thread by setting "running" to false,
     * effectively closing the Logger
     * @see Logger#run()
     */
    public void close() {
        this.running = false;
    }
}