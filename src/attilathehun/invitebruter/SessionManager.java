package attilathehun.invitebruter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SessionManager {

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
            System.out.println("Can not save the data");
        } finally {
            s.stop();
        }
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

}
