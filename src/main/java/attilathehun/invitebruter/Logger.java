package attilathehun.invitebruter;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class Logger implements Runnable,AttemptResultListener{

    private File logFile;
    private File dumpFile;
    private FileOutputStream logFileOutputStream;
    private FileOutputStream dumpFileOutputStream;

    public Logger(){
        init();
    }

    @Override
    public void run() {
        while(true){

        }
    }

    private void log(String s){
        try {
            logFileOutputStream.write(s.getBytes(StandardCharsets.UTF_8));
        }catch (Exception e){

        }
    }

    private void dump(String s){
        try {
            dumpFileOutputStream = new FileOutputStream(dumpFile, true);
            dumpFileOutputStream.write(s.getBytes(StandardCharsets.UTF_8));
            dumpFileOutputStream.close();
        }catch(Exception e){

        }
    }

    private void init(){
        try{
            logFile = new File("log");
            dumpFile = new File("dump");
            logFile.createNewFile();
            logFileOutputStream = new FileOutputStream(logFile, true);
            if(dumpFile.createNewFile()){
                log("Dump file created\n");
            }else{
                log("Dump file found (" + dumpFile.length() / 1024 + " kb)\n");
            }
        }catch(Exception e){
            System.out.println("Unable to create Logger");
        }
    }

    @Override
    public void attemptResulted(String messageToLog, boolean toDump) {
        log(messageToLog);
        if(toDump){
            dump(messageToLog);
        }
    }

    @Override
    public void logMessage(String message) {
        log(message);
    }
}
