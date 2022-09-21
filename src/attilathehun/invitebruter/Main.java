package attilathehun.invitebruter;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static final Launcher launcher = new Launcher();
    private static final Thread logThread = new Thread(launcher.getLogger());
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        logThread.start();
        System.out.println("Welcome to Discord Invite Bruter");
        System.out.println("start -[\"begin xKjdf8P\"] -[\"end ZZZZZZZ\"]: start a new session");
        System.out.println("exit: close the session and the program");
        System.out.println("save -[n]: save and close the current session, -n to save it to a new file");
        System.out.println("load -[f] [\"session3\"]: resumes the latest session, -f to load a specific session");
        System.out.println("clear: clear log and dump files from previous sessions");
        System.out.println("list: list saved sessions");
        while (true) {
            String input = sc.nextLine();

            if (input.startsWith("load")) {
                if (input.contains("-f")) {
                    String filename = input.substring(input.indexOf("-f") + "-f".length()).trim();
                    launcher.load(filename);
                    System.out.println("Session started from file \"" + filename + "\"");
                } else {
                    launcher.load();
                    System.out.println("Session started from default save");
                }
                launcher.launch();
                continue;
            }

            if (input.startsWith("save")) {
                if (input.contains("-n")) {
                    String filename = launcher.saveN();
                    System.out.println("Session saved into the file \"" + filename + "\"");
                } else {
                    launcher.save();
                    System.out.println("Session saved");
                }
                launcher.shutdown();
                continue;
            }
            //TODO: make it possible to "start" a new session with a specific START END positions
            if (input.startsWith("start")) {
                String start = "";
                String end = "";
                if (input.contains("-begin")) {

                }
                if (input.contains("-end")) {
                    end = input.substring(input.indexOf("-end") + "-end".length()).trim();
                }

                if (start.equals("")) {


                } else if (end.equals("")) {
                    launcher.init();
                    launcher.launch();
                } else {

                }

            }

            switch (input) {
                case "stop":
                case "exit":
                case "close":
                    launcher.shutdown();
                    System.out.println("Session interrupted by command");
                    break;
                case "list":
                case "saves":
                case "sessions":
                    System.out.println("Available local sessions: ");
                    ArrayList<String> sessions = launcher.list();
                    for (String session : sessions) {
                        System.out.println("> " + session);
                    }
                    break;
                case "clear":
                    Logger.clear();
                    System.out.println("All clear");
            }

        }

    }
}
