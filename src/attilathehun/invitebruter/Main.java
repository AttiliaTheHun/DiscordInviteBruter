package attilathehun.invitebruter;

import java.util.Scanner;

public class Main {

    private static final Launcher launcher = new Launcher();
    private static final Thread logThread = new Thread(launcher.getLogger());
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        logThread.start();
        System.out.println("Welcome to Discord Invite Bruter");
        System.out.println("start -[begin xKjdf8P] - [end ZZZZZZZ]: start a new session");
        System.out.println("exit: close the session and the program");
        System.out.println("save -[n]: save and close the current session, -n to save it to a new file");
        System.out.println("load -[f] [session3]: resumes the latest session, -f to load a specific session");
        System.out.println("clear: clear log and dump files from previous sessions");
        while (true) {
            String input = sc.nextLine();

            if (input.startsWith("load")) {
                if (input.contains("-f")) {
                    String filename = input.substring(input.indexOf("-f") + "-f".length());
                    launcher.load(filename);
                } else {
                    launcher.load();
                }
                launcher.launch();
                continue;
            }

            if (input.startsWith("save")) {
                if (input.contains("-n")) {
                    launcher.saveN();
                } else {
                    launcher.save();
                }
                launcher.shutdown();
                continue;
            }
            //TODO: make it possible to "start" a new session with a specific START END positions
            //TODO: "clear"
            switch (input) {
                case "start":
                    launcher.init();
                    launcher.launch();
                    break;
                case "stop":
                case "exit":
                case "close":
                    launcher.shutdown();
                    break;
            }

        }

    }
}
