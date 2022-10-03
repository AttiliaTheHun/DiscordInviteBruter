package attilathehun.invitebruter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Launcher {

    private final int MAX_THREAD_COUNT = 50;
    private final int ESTIMATED_MB_OF_MEMORY_PER_THREAD = 20;
    private final int CHARSET_LENGTH = Bruter.getCharsetLength();

    private SessionManager manager;
    private SessionManager.Session session;

    private Logger logger = Logger.getInstance();


    public void launch() {
        if (session == null) {
            session = manager.create();
        }

        session.start();
    }

    public void shutdown() {
        session.stop();
        System.exit(0);
    }

    public void init() {
        manager = new SessionManager();
        SourceArrayVault.setBoundary(CHARSET_LENGTH);
        manager.setCharsetLength(CHARSET_LENGTH);
        manager.setThreadCount(calculateThreadCount());
        //manager.setThreadCount(1);
        manager.setLogger(getLogger());
    }

    public void setStart(String start) {
        if (manager != null)
        manager.setStart(SourceArrayVault.fromString(start, Bruter.getCharset()).array());
    }

    public void setEnd(String end) {
        if (manager != null)
            manager.setEnd(SourceArrayVault.fromString(end, Bruter.getCharset()).array());
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
        if (session == null) {
            return;
        }
        getLogger().logMessage("Session restored from file \"" + filename + "\" - " +  DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()));
        loadInit();
    }

    public void load() {
        session = SessionManager.load();
        if (session == null) {
            return;
        }
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

    public String saveN() {
        String filename = SessionManager.saveN(session);
        getLogger().logMessage("Session paused into file \"" + filename + "\" - " +  DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()));
        return filename;
    }

    public ArrayList<String> savedSessionsList() {
        return SessionManager.savedSessions();
    }

    public Logger getLogger() {
        return logger;
    }

    public void refresh() {
        logger = Logger.getInstance();
    }

    public void clearSessions() {
        SessionManager.clear();
    }

}
