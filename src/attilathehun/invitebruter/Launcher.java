package attilathehun.invitebruter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Launcher {

    private final int MAX_THREAD_COUNT = 50;
    private final int ESTIMATED_MB_OF_MEMORY_PER_THREAD = 60;
    private final int CHARSET_LENGTH = Bruter.getCharsetLength();

    private Session session;

    private final Logger logger = new Logger();


    public void launch() {
        session.start();
    }

    public void shutdown() {
        session.stop();
        System.exit(0);
    }

    public void init() {
        SourceArrayVault.setBoundary(CHARSET_LENGTH - 1);
        session = new Session();
        session.setCharsetLength(CHARSET_LENGTH);
        session.setThreadCount(calculateThreadCount());
        session.setLogger(getLogger());
    }

    /**
     * Calculates the number of Bruter threads to use
     * @return the count of Bruter threads to be used
     */
    private int calculateThreadCount(){
        int memoryMegaBytes = (int) Runtime.getRuntime().freeMemory() / 1024 / 1024;
        int threadCount = memoryMegaBytes / ESTIMATED_MB_OF_MEMORY_PER_THREAD;
        if(threadCount > MAX_THREAD_COUNT){
            return MAX_THREAD_COUNT;
        }
        return threadCount;
    }

    public void load(String filename) {
        session = SessionManager.load(filename);
        getLogger().logMessage("Session restore from file \"" + filename + "\" - " +  DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()));
        loadInit();
    }

    public void load() {
        session = SessionManager.load();
        getLogger().logMessage("Session restored - " +  DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()));
        loadInit();
    }

    private void loadInit() {
        SourceArrayVault.setBoundary(CHARSET_LENGTH - 1);
        session.setLogger(getLogger());
        session.refresh();
    }

    public void save() {
        SessionManager.save(session);
        getLogger().logMessage("Session paused - " +  DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()));
    }

    public void saveN() {
        String filename = SessionManager.saveN(session);
        getLogger().logMessage("Session paused into file \"" + filename + "\" - " +  DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()));
    }

    public Logger getLogger() {
        return logger;
    }

}