package attilathehun.invitebruter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class Session implements Serializable {

    private int threadCount;
    private int[] START = new int[7];
    private int[] END = new int[10];
    private int charsetLength;
    private Logger logger;

    private Bruter[] bruters;
    private transient Thread[] threads;

    {

    }

    public Session() {
        Arrays.fill(END, charsetLength - 1);
    }

    public Session(int[] start, int[] end) {
        this.START = start;
        this.END = end;
    }

    public void start() {
        startThreads();
    }

    public void stop() {
        stopThreads();
    }


    private void startThreads(){
        logger.logMessage("\nSession started with " + threads.length + " threads " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()));
        for(Thread t : threads){
            t.start();
        }
    }


    private void stopThreads(){
        for (Bruter bruter : bruters) {
            bruter.terminate();
        }
        logger.logMessage("Session interrupted by command " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "\n");
    }

    /**
     * Initializes the Bruter threads
     */
    private void initThreads(){
        SourceArrayVault[] startingPositions = getStartingPositions();
        int[][] endingPositions = getEndingPositions();
        this.bruters = new Bruter[getThreadCount()];
        this.threads = new Thread[getThreadCount()];
        for(int i = 0; i < bruters.length; i++){
            System.out.println("#" + i + ": " + Arrays.toString(startingPositions[i].array()) + " - " + Arrays.toString(endingPositions[i]));
            bruters[i] = new Bruter(i, startingPositions[i], endingPositions[i]);
            threads[i] = new Thread(bruters[i]);
            bruters[i].addListener(logger);
        }
    }

    public void refresh() {
        this.threads = new Thread[getThreadCount()];
        for(int i = 0; i < bruters.length; i++){
            threads[i] = new Thread(bruters[i]);
            bruters[i].addListener(logger);
        }
    }

    private SourceArrayVault[] getStartingPositions() {
        long BASE_POSSIBILITIES = SourceArrayVault.summarizeSourceArray(getStart());
        SourceArrayVault[] startingPositions = new SourceArrayVault[getThreadCount()];
        long possibilitiesPerThread = calculateTotalPossibilities() / getThreadCount();
        System.out.println("SESSION_END: " + Arrays.toString(getEnd()) + "\n SESSION_END sum: " + SourceArrayVault.summarizeSourceArray(getEnd()));
        System.out.println("BASE_POSSIBILITIES: " + BASE_POSSIBILITIES + "\ntotal possibilites: " + calculateTotalPossibilities() + "\npossibilities per thread: " + possibilitiesPerThread);
        for (int i = 0; i < getThreadCount(); i++) {
            startingPositions[i] = SourceArrayVault.fromNumber(possibilitiesPerThread * i + BASE_POSSIBILITIES);
            //System.out.println(Arrays.toString(startingPositions[i].array()));
        }
        return startingPositions;
    }

    private int[][] getEndingPositions() {
        long END_POSSIBILITIES = SourceArrayVault.summarizeSourceArray(getEnd());
        int[][] endingPositions = new int[getThreadCount()][];
        long possibilitiesPerThread = calculateTotalPossibilities() / getThreadCount();
        for (int i = 0; i < getThreadCount(); i++) {
            endingPositions[i] = SourceArrayVault.fromNumber(END_POSSIBILITIES - possibilitiesPerThread * i).array();
        }
        return endingPositions;
    }

    public int[] getEnd() {
        return END;
    }

    public int[] getStart() {
        return START;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    private long calculateTotalPossibilities() {
        return SourceArrayVault.summarizeSourceArray(getEnd()) - SourceArrayVault.summarizeSourceArray(getStart());
    }

    public void setCharsetLength(int charsetLength) {
        this.charsetLength = charsetLength;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
