package attilathehun.invitebruter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.ArrayList;

public class SessionManager {

    private int[] START = null;
    private int[] END = null;
    private int threadCount = -1;
    private int charsetLength = -1;
    private Logger logger;

    private static final String DEFAULT_FILE_NAME = "session";

    public static void save(Session s) {
        save(s, DEFAULT_FILE_NAME);
    }

    public static String saveN(Session s) {
        String fileName = DEFAULT_FILE_NAME;
        int i = 0;
        while (true) {
            File file = new File(fileName + i);
            if(file.exists()) {
                i++;
                continue;
            } else {
                break;
            }
        }
        fileName += i;
        save(s, fileName);
        return fileName;
    }

    private static void save(Session s, String filename) {
        try {
            File dataFile = new File(filename);
            dataFile.createNewFile();
            FileOutputStream dataFileOutputStream = new FileOutputStream(dataFile, false);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(dataFileOutputStream);
            objectOutputStream.writeObject(s);
            objectOutputStream.close();
            dataFileOutputStream.close();
        }catch (Exception e){
            System.out.println("Can not save the data: " + e.getMessage());
        } finally {
            s.stop();
        }
    }

    public static ArrayList<String> savedSessions() {
        ArrayList<String> saves = new ArrayList<String>();
        File file  = new File(".");
        String[] allFiles = file.list();

        if (allFiles != null) {
            for (String filename : allFiles) {
                if (filename.startsWith("session")) {
                    saves.add(filename);
                }
            }
        }

        return saves;

    }

    public static Session load() {
        return load(DEFAULT_FILE_NAME);
    }

    public static Session load(String filename) {
        try {
            File file = new File(filename);
            if(!file.exists()) {
                System.out.println("File not found: " + filename);
                return null;
            }
            FileInputStream dataFileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(dataFileInputStream);
            Session session = (Session) objectInputStream.readObject();
            objectInputStream.close();
            dataFileInputStream.close();
            return session;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void clear() {
        for (String name : SessionManager.savedSessions()) {
            File file = new File(name);
            file.delete();
        }
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public void setCharsetLength(int charsetLength) {
        this.charsetLength = charsetLength;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
    public void setStart(int[] start) {
        this.START = start;
    }

    public void setEnd(int[] end) {
        this.END = end;
    }

    private boolean parameterCheck() {
        if (this.logger == null) {
            return false;
        }

        if (this.charsetLength == -1) {
            return false;
        }

        if (this.threadCount == -1) {
            return false;
        }

        if (START != null) {
            if (SourceArrayVault.isSourceArray(START) == false) {
                return false;
            }
        }

        if (END != null) {
            if (SourceArrayVault.isSourceArray(END) == false) {
                return false;
            }
        }

        return true;
    }

    public Session create() {
        if(!parameterCheck()) {
            throw new IllegalArgumentException();
        }
        if (END == null) {
            if (START == null) {
                return new Session(charsetLength, threadCount, logger);
            } else {
                return new Session(START, charsetLength, threadCount, logger);
            }
        } else {
            return new Session(START, END, charsetLength, threadCount, logger);
        }
    }

    public static class Session implements Serializable {

        private int threadCount;
        private int[] START = new int[InviteManager.Invite.getAvailableLengths()[0]];
        private int[] END = new int[InviteManager.Invite.getAvailableLengths()[InviteManager.Invite.getAvailableLengths().length - 1]];
        private int charsetLength;
        private transient Logger logger;

        private Bruter[] bruters;
        private transient Thread[] threads;

        private Session(int[] start, int[] end, int charsetLength, int threadCount, Logger logger) {
            this.START = start;
            this.END = end;
            this.charsetLength = charsetLength;
            this.threadCount = threadCount;
            this.logger = logger;
        }

        private Session(int charsetLength, int threadCount, Logger logger) {
            this.charsetLength = charsetLength;
            this.threadCount = threadCount;
            this.logger = logger;
            Arrays.fill(END, charsetLength - 1);
        }

        private Session(int[] start, int charsetLength, int threadCount, Logger logger) {
            this.START = start;
            this.charsetLength = charsetLength;
            this.threadCount = threadCount;
            this.logger = logger;
            this.END = SourceArrayVault.fromNumber(SourceArrayVault.summarizeMaxSourceArrayOfLength(InviteManager.Invite.getAvailableLengths()[InviteManager.Invite.getAvailableLengths().length - 1])).array();
        }

        public void start() {
            initThreads();
            startThreads();
        }

        public void stop() {
            stopThreads();
        }


        private void startThreads(){
            logger.logMessage("\nSession started with " + threads.length + " threads " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "\n");
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
            int[][] endingPositions = getEndingPositions(startingPositions);
            this.bruters = new Bruter[getThreadCount()];
            this.threads = new Thread[getThreadCount()];
            for(int i = 0; i < bruters.length; i++){
                //System.out.println("#" + i + ": " + Arrays.toString(startingPositions[i].array()) + " - " + Arrays.toString(endingPositions[i]));
                bruters[i] = new Bruter(i, startingPositions[i], endingPositions[i]);
                threads[i] = new Thread(bruters[i]);
                bruters[i].addListener(logger);
            }
        }

        /**
         * Recreates the Bruter Threads, use this when restoring a session
         */
        public void refresh() {
            this.threads = new Thread[getThreadCount()];
            for(int i = 0; i < bruters.length; i++){
                threads[i] = new Thread(bruters[i]);
                bruters[i].refresh();
                bruters[i].addListener(logger);
            }
        }

        private SourceArrayVault[] getStartingPositions() {
            long BASE_POSSIBILITIES = SourceArrayVault.summarizeSourceArray(getStart());
            SourceArrayVault[] startingPositions = new SourceArrayVault[getThreadCount()];
            long possibilitiesPerThread = calculateTotalPossibilities() / getThreadCount();
            //System.out.println("SESSION_END: " + Arrays.toString(getEnd()) + "\n SESSION_END sum: " + SourceArrayVault.summarizeSourceArray(getEnd()));
            //System.out.println("BASE_POSSIBILITIES: " + BASE_POSSIBILITIES + "\ntotal possibilites: " + calculateTotalPossibilities() + "\npossibilities per thread: " + possibilitiesPerThread);
            for (int i = 0; i < getThreadCount(); i++) {
                startingPositions[i] = SourceArrayVault.fromNumber(possibilitiesPerThread * i + BASE_POSSIBILITIES);
                if (startingPositions[i].length() < START.length) {
                    startingPositions[i] = new SourceArrayVault(SourceArrayVault.prolongArray(startingPositions[i].array(), START.length));
                }
                //System.out.println(Arrays.toString(startingPositions[i].array()));
            }
            return startingPositions;
        }

        //TODO:
        private int[][] getEndingPositions() {
            long END_POSSIBILITIES = SourceArrayVault.summarizeSourceArray(getEnd());
            int[][] endingPositions = new int[getThreadCount()][];
            long possibilitiesPerThread = calculateTotalPossibilities() / getThreadCount();
            for (int i = 0; i < getThreadCount(); i++) {
                endingPositions[i] = SourceArrayVault.fromNumber(END_POSSIBILITIES - possibilitiesPerThread * i).array();
            }
            return endingPositions;
        }

        private int[][] getEndingPositions(SourceArrayVault[] startingPositions) {
            long END_POSSIBILITIES = SourceArrayVault.summarizeSourceArray(getEnd());
            int[][] endingPositions = new int[getThreadCount()][];
            long possibilitiesPerThread = calculateTotalPossibilities() / getThreadCount();
            for (int i = 0; i < getThreadCount() - 1; i++) {
                endingPositions[i] = startingPositions[i + 1].array();
            }
            endingPositions[endingPositions.length - 1] = getEnd();
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

}
