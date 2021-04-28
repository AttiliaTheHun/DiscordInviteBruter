import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        System.out.println("Type \"scan [optional parameters]\" to start and \"stop\" to stop");
    Scanner sc = new Scanner(System.in);
    String input = sc.nextLine().trim();
    Bruter bruter;
    if(input.equals("scan")){ //start bruting from scratch
         bruter = new Main.Bruter(null);
    }else{ //start bruting from target position
         bruter = new Main.Bruter(input.substring("scan".length()).trim());
    }
    Thread bthread = new Thread(bruter);
    bthread.start(); //start the bruter thread
    while(true){
        input = sc.nextLine();
        if(input.contains("stop")){ //check if user wants to stop
            try {
                bruter.stop(); //stop the bruter
            }catch(Exception e){
                e.printStackTrace();
            }
            System.exit(0); //exit
        }
    }
    }
public static class Bruter implements Runnable{
 boolean run; //boolean to control when to stop the bruter
  final  String base = "https://discord.gg/"; //base discord link url
   final String chars = "aAbBcCdDeEfFgGiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789"; //character set for the string generation
    int n = chars.length() - 1; //length of the char set
    String invite; //the link url holder
    String data = null; //arguments to start bruting from, null by default
    int[] num = {0, 0, 0, 0, 0, 0, 0}; //array representing the invite code as number = position in chars string
    File log; //log file
    File dump; //dump file
    FileOutputStream fos = null;
    FileOutputStream dos;
public Bruter(String data){
    this.run = true; //run instantly after creation
    if(data != null) { //check if user provided arguments
        this.data = data;
        String[] sarr = data.split(","); //split the arguments into string array
        num = new int[sarr.length]; //update the num array for the length of arguments
for(int i = 0; i < num.length; i++){ //convert string array to int array num
    num[i] = Integer.parseInt(sarr[i]);
}
    }
}
    @Override
    public void run(){
        try {
            log = new File("log");
            dump = new File("dump");
            log.createNewFile(); //create log file, we don't really care if it exists or not
                fos = new FileOutputStream(log, true);
                fos.write(("\nSession started: " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "\n").getBytes(StandardCharsets.UTF_8));
                System.out.println("Session started: " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()));
          if (dump.createNewFile()) { //log if the file was made
                fos.write("Dump file created\n".getBytes(StandardCharsets.UTF_8));
            }else{ //log there was previous dump found
                fos.write(("Dump file found (" + dump.length() / 1024 + " kb)\n").getBytes(StandardCharsets.UTF_8));
            }
          if(data != null){ //log if arguments were used
              fos.write(("Loaded from: " + data + "\n").getBytes(StandardCharsets.UTF_8));
          }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
while(run) {  //loop used to modify values between force: iterations
   force:
    while(true) { //where the brute forcing happens
        num[num.length - 1]++; //increase last element
        force(); //try the link
        check:
        for(int m = 0; m < num.length; m++){ //check if there are options left
            if(num[m] != n){ //there are options left
                break check; //no need to check further
            }
            if(m == num.length - 1){ //if the loop isn't stopped yet, it means there are no options left
                break force; //stop the brute forcing cycle
            }
        }
        transitions: //fix transitions between positions in the link
        for(int i = 1; i < num.length; i++){
            if(i == num.length - 1 && num[i] == n){ //we can't find next element when already on the last
                num[i] = 0; //reset the value
                num[i - 1]++; //increase the previous one
                force(); //link was changed, try the link
            }else if(num[i] == n && num[i - 1] != n && num[i + 1] == n){ //we need to make sure we don't dump any options
                num[i] = 0;
                num[i - 1]++;
                force(); //link was changed, try the link
            }
        }
    }
    if(num.length == 8) { //all 7 character long options were tried
        num = new int[10]; // there are no 9 characters long invites
        for(int i : num){ //set all elements to zero
            num[i] = 0;
        }
    }else if(num.length == 10){ //no invite is longer than 10 characters, all options were tried
        run = false; //close the loop to let the thread finish itself
    }
        num = new int[num.length + 1];
        for(int i : num){
            num[i] = 0;
        }
}
try{
    fos.write("Thread finished\n".getBytes(StandardCharsets.UTF_8)); //log thread finishment
}catch (IOException ie){
ie.printStackTrace();
}
System.out.println("Thread finished"); //notify the console

    }
    public void stop() throws IOException{ //stops the process
     generate(true); //generate string from raw array data
    System.out.println("Stopped at values: " + invite); //print the values to the console
    fos.write("Session interrupted by command\n".getBytes(StandardCharsets.UTF_8)); //log the reason of stopping
    fos.write(("Exit values: " + invite + "\n").getBytes(StandardCharsets.UTF_8)); //log exit values
    this.run = false; //finally do the loop stopping
    }
    public void force(){ //tries the invite
    try {
        generate(false); //generate string from the array
        HttpURLConnection connection = (HttpURLConnection) new URL(base.concat(invite)).openConnection(); //connect to the invite address
        connection.setRequestMethod("GET");
        fos.write(("Sent GET request to " + base + invite + "\n").getBytes(StandardCharsets.UTF_8)); //log connection attempt
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        fos.write(("Response code: " + connection.getResponseCode() + "\n").getBytes(StandardCharsets.UTF_8)); //log if there were connection/server errors
        InputStream response = connection.getInputStream(); //get the http response
        byte[] input = new byte[2048]; //array to read the response in
        String stringify = ""; //string to hold stringified array
        String title; //hold site title in this one
        while (response.read(input) != -1) { //read the response while there is any
        stringify = stringify.concat(new String(input).trim()); //concat it to the string
          }
        title = stringify.substring(stringify.indexOf("<title>") + "<title>".length(), stringify.indexOf("</title>")); //get the HTML <title> value, indicates server name
            if(title.equals("Discord")) { //this one means link is invalid
                fos.write(("Fail - Invite: " + base.concat(invite) + "\n").getBytes(StandardCharsets.UTF_8)); //log failed invite
            }else{ //if link is valid
                fos.write(("Server: " + title + " - Invite: " + base.concat(invite) + "\n").getBytes(StandardCharsets.UTF_8)); //log successful invite
                dos = new FileOutputStream(dump, true); //open the dump file, we don't wanna destroy previous dumps so we need the 'true' to append
                dos.write(("Server: " + title + " - Invite: " + base.concat(invite) + "\n").getBytes(StandardCharsets.UTF_8)); //log working invite to the dump file
                dos.close(); //close the dump file
            }
    }catch(IOException ioexp){
        try {
            fos.write(ioexp.getMessage().getBytes(StandardCharsets.UTF_8)); //log exceptions
        }catch(Exception e5){ //if fails lol
            e5.printStackTrace(); //annoy the console
        }
    }
    }
    public void generate(boolean values){ //generates strings from the num array
    invite = "";
    if(!values){ //if false, we are making invite link
    for(int number : num) {
        invite += chars.charAt(number); //convert the int value to character
    }
        }else{ //if true, we are making string from the int values
            for(int n : num){
                invite = invite.concat("," + n); //add the values to the string
            }
            invite = invite.substring(1); //eliminate the fist coma
    }
    }
}
}