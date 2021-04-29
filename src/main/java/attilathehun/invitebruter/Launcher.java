<<<<<<< HEAD
/*
 * Program entry-point, Main class. This class launches and initializes the Bruters
 */

package attilathehun.invitebruter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class Launcher {
    private final int MAX_THREAD_COUNT = 20;
    private final int CHARSET_LENGTH = Bruter.getCharsetLength();

    private Bruter[] bruters;
    private Thread[] threads;
    private int[] globalLowerLimit = fillIntArrayWithValue(0, 6);
    private int[] globalUpperLimit = fillIntArrayWithValue(CHARSET_LENGTH, 6);
    private int[][] allLowerLimits;
    private int[][] allUpperLimits;

    private final Logger logger = new Logger();

    public static void main(String[] args){
        Launcher launcher = new Launcher();
        Thread logThread = new Thread(launcher.getLogger());
        logThread.start();
        System.out.println("Type \"start\" for default value range or use \"-start\" for the default value and \"-end\" for the final value");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine().trim();
        Model dataModel = launcher.loadData();
        launcher.setAllLowerLimits(dataModel.getLowerRanges());
        launcher.setAllUpperLimits(dataModel.getUpperRanges());
        if(input.equals("start")){
            if(launcher.getAllLowerLimits() == null){
                launcher.initThreadLowerRanges(launcher.getThreadCount());
            }
            if(launcher.getAllLowerLimits() == null){
                launcher.initThreadUpperRanges(launcher.getThreadCount());
            }

            launcher.initThreads(launcher.getThreadCount());
            launcher.startThreads();
        }else if(input.startsWith("start")){

        }
        while(true) {
            input = sc.nextLine().trim();
            if(input.equals("stop")) {
                launcher.stopThreads();
                String message = "Session closed by command\n";
                launcher.getLogger().logMessage(message);
                logThread.interrupt();
                System.out.print(message);
                System.exit(0);
            }
        }

    }

    public int getThreadCount(){
        int memoryMegaBytes = (int) Runtime.getRuntime().freeMemory() / 1024 / 1024;
        int threadCount = memoryMegaBytes / 25;
        if(threadCount > MAX_THREAD_COUNT){
            return MAX_THREAD_COUNT;
        }
        return threadCount;
    }

    public void initThreadLowerRanges(int threadCount){
        int[][] threadLowerRanges = new int[threadCount][];
        final long totalPossibilities = getTotalPossibilities();
        final long threadPossibilities = totalPossibilities / threadCount;

        threadLowerRanges[0] = globalLowerLimit;

        for(int i = 1; i < threadLowerRanges.length; i++){
            threadLowerRanges[i] = shiftByPossibilitiesUp(threadLowerRanges[i -1], threadPossibilities);
        }
        allLowerLimits = threadLowerRanges;
    }

    public void initThreadUpperRanges(int threadCount){
        int[][] threadUpperRanges = new int[threadCount][];
        for(int i = 0; i < threadUpperRanges.length - 1; i++){
            threadUpperRanges[i] = shiftByPossibilitiesDown(allLowerLimits[i + 1], (long) 1);
        }
        threadUpperRanges[threadUpperRanges.length -1] = globalUpperLimit;
        allUpperLimits = threadUpperRanges;
    }

    public void setAllLowerLimits(int[][] allLowerLimits) {
        this.allLowerLimits = allLowerLimits;
    }

    public void setAllUpperLimits(int[][] allUpperLimits) {
        this.allUpperLimits = allUpperLimits;
    }

    public int[][] getAllLowerLimits() {
        return allLowerLimits;
    }

    public int[][] getAllUpperLimits() {
        return allUpperLimits;
    }

    public long getTotalPossibilities(){
        int[] difference = new int[globalLowerLimit.length];
        for(int i = 0; i < globalLowerLimit.length; i++){
            difference[i] = globalUpperLimit[i] - globalLowerLimit[i];
        }
        long possibilities = difference[difference.length - 1];
        for(int i = difference.length - 2; i > 0; i--){
            if(difference[i] == 0){
                continue;
            }
           possibilities = possibilities * difference[i];
        }
        return possibilities;
    }

    public int[] shiftByPossibilitiesUp(int[] source, long possibilityCount){
        return null;
    }

    public int[] shiftByPossibilitiesDown(int[] source, long possibilityCount){
        return null;
    }

    public void startThreads(){
        logger.logMessage("\nSession started with " + threads.length + "threads " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()));
        for(Thread t : threads){
            t.start();
        }
   }

    public void stopThreads(){
        int[][] currentPositions = new int[bruters.length][];
        int[][] endPositions = new int[bruters.length][];
        for(int i = 0; i < bruters.length; i++){
            currentPositions[i] = bruters[i].getAttemptBase();
            endPositions[i] = bruters[i].getEndPosition();
            bruters[i].exit();
        }
        if(saveData(currentPositions, endPositions)){
            logger.logMessage("Session saved inside \"data\"\n");
        } else {
            logger.logMessage("Attempt to save the session failed\n");
        }
        logger.logMessage("Session interrupted by command " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "\n");
    }

    public void initThreads(int threadCount){
        this.bruters = new Bruter[threadCount];
        for(int i = 0; i < bruters.length; i++){
            bruters[i] = new Bruter(i,allLowerLimits[i], allUpperLimits[i]);
            threads[i] = new Thread(bruters[i]);
            bruters[i].addListener(logger);
        }
        this.threads = new Thread[threadCount];

    }

    public Logger getLogger() {
        return logger;
    }

    public Model loadData() {
        try {
            File dataFile = new File("data");
            if (!dataFile.exists()) {
                return null;
            }
            FileInputStream dataFileInputStream = new FileInputStream(dataFile);
            ObjectInputStream objectInputStream = new ObjectInputStream(dataFileInputStream);
            Model model = (Model) objectInputStream.readObject();
            objectInputStream.close();
            dataFileInputStream.close();
            return model;
        }catch (Exception e){
            System.out.println("Died when loading data");
            return null;
        }
    }

    public boolean saveData(int[][] lowerRanges, int[][] upperRanges){
        try {
            File dataFile = new File("data");
            dataFile.createNewFile();
            FileOutputStream dataFileOutputStream = new FileOutputStream(dataFile, false);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(dataFileOutputStream);
            Model dataModel = new Model(lowerRanges, upperRanges);
            objectOutputStream.writeObject(dataModel);
            objectOutputStream.close();
            dataFileOutputStream.close();
            return true;
        }catch (Exception e){
            System.out.println("Can not save the data");
            return false;
        }
    }

    public int[] fillIntArrayWithValue(int value, int arrayLength){
        int[] output = new int[arrayLength];
        for(int i : output){
            i = value;
        }
        return output;
    }
}
=======
package attilathehun.invitebruter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


public class Launcher {
    private final int MAX_THREAD_COUNT = 20;
    private final int CHARSET_LENGTH = Bruter.charSet.length();

    private Bruter[] bruters;
    private Thread[] threads;
    private int[] globalLowerLimit = {0, 0, 0, 0, 0, 0};
    private int[] globalUpperLimit= {CHARSET_LENGTH, CHARSET_LENGTH, CHARSET_LENGTH, CHARSET_LENGTH, CHARSET_LENGTH, CHARSET_LENGTH};;

    private Logger logger = new Logger();

    public static void main(String[] args){
        Launcher launcher = new Launcher();
        Thread logThread = new Thread(launcher.getLogger());
        logThread.start();
        System.out.println("Type \"start\" for default value range or use \"-start\" for the default value and \"-end\" for the final value");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine().trim();
        if(input.equals("start")){
            launcher.initThreads(launcher.getThreadCount());
            launcher.startThreads();
        }else if(input.startsWith("start")){

        }
        while(true) {
            input = sc.nextLine().trim();
            if(input.equals("stop")) {
                launcher.stopThreads();
                String message = "Session closed by command\n";
                launcher.getLogger().logMessage(message);
                logThread.interrupt();
                System.out.print(message);
                System.exit(0);
            }
        }

    }

    public int getThreadCount(){
        int memoryMegaBytes = (int) Runtime.getRuntime().freeMemory() / 1024 /1024;
        int threadCount = memoryMegaBytes / 25;
        if(threadCount > MAX_THREAD_COUNT){
            return MAX_THREAD_COUNT;
        }
        return threadCount;
    }

    public int[][] getThreadLowerRanges(int threadCount){
        int[][] threadLowerRanges;
        final int totalPossibilities = getTotalPossibilities();
        final int threadPossibilities = totalPossibilities / threadCount;

        int[] difference = null;


        return null;
    }

    public int[][] getThreadUpperRanges(int threadCount){
        return null;
    }

    public int getTotalPossibilities(){
        int[] difference = new int[globalLowerLimit.length];
        for(int i = 0; i < globalLowerLimit.length; i++){
            difference[i] = globalUpperLimit[i] - globalLowerLimit[i];
        }
        int possibilities = difference[difference.length - 1];
        for(int i = difference.length - 2; i > 0; i--){
            if(difference[i] == 0){
                continue;
            }
           possibilities = possibilities * difference[i];
        }
        return possibilities;
    }

    public void startThreads(){
        logger.logMessage("\nSession started with " + threads.length + "threads " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()));
        for(Thread t : threads){
            t.start();
        }
   }

    public void stopThreads(){
        for(Bruter b : bruters){
            b.exit();
        }
        logger.logMessage("Session interrupted by command " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()));
    }

    public void initThreads(int threadCount){
        this.bruters = new Bruter[threadCount];
        int[][] threadLowerRanges = getThreadLowerRanges(threadCount);
        int[][] threadupperRanges = getThreadUpperRanges(threadCount);
        for(int i = 0; i < bruters.length; i++){
            bruters[i] = new Bruter(i,threadLowerRanges[i], threadupperRanges[i]);
            threads[i] = new Thread(bruters[i]);
            bruters[i].addListener(logger);
        }
        this.threads = new Thread[threadCount];

    }

    public Logger getLogger() {
        return logger;
    }
}
>>>>>>> 02c222fe7bfdc69c01f2b58d027fe37fe080bbd8
