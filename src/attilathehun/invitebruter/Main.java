package attilathehun.invitebruter;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static final Launcher launcher = new Launcher();
    private static Thread logThread = new Thread(launcher.getLogger());
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        boolean sessionRunning = false;
        logThread.start();
        printInfo();
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

            if (input.startsWith("start")) {
                launcher.init();
                String start = "";
                String end = "";
                if (input.contains("-begin")) {
                    String substring = input.substring(input.indexOf("-begin") + "-begin".length());
                    int spaceIndex = substring.trim().indexOf(" ");
                    if (spaceIndex == -1) {
                        start = substring.trim();
                    } else {
                        start = substring.substring(0, spaceIndex).trim();
                    }
                    launcher.setStart(start);
                }
                if (input.contains("-end")) {
                    String substring = input.substring(input.indexOf("-end") + "-end".length());
                    int spaceIndex = substring.trim().indexOf(" ");
                    if (spaceIndex == -1) {
                        end = substring.trim();
                    } else {
                        end = substring.substring(0, spaceIndex).trim();
                    }
                    launcher.setEnd(end);
                }

                launcher.launch();
                System.out.println("Session started");
                sessionRunning = true;
            }

            isItClear:
            if (input.startsWith("clear")) {
                if (sessionRunning) {
                    System.out.println("Cannot perform clearing when a session is running");
                    break isItClear;
                }
                launcher.getLogger().close();
                Logger.clear();
                Logger.refresh();
                launcher.refresh();
                logThread = new Thread(launcher.getLogger());
                logThread.start();
                if (input.contains("-s")) {
                    launcher.clearSessions();
                }
                System.out.println("All clear");
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
                case "saved":
                case "sessions":
                    System.out.println("Available local sessions: ");
                    ArrayList<String> sessions = launcher.savedSessionsList();
                    for (String session : sessions) {
                        System.out.println("> " + session);
                    }
                    break;
            }
        }

    }

    private static void printInfo() {
        System.out.println("Welcome to Discord Invite Bruter");
        System.out.println("start -[\"begin xKjdf8P\"] -[\"end ZZZZZZZ\"]: start a new session");
        System.out.println("exit: close the session and the program");
        System.out.println("save -[n]: save and close the current session, -n to save it to a new file");
        System.out.println("load -[f] [\"session3\"]: resumes the latest session, -f to load a specific session");
        System.out.println("clear -[s]: clear log and dump files from previous sessions, -s to clear saved sessions");
        System.out.println("list: list saved sessions");
    }
}
