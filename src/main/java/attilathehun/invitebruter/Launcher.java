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
