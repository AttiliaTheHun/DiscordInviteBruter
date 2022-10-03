# Discord Invite Bruter
A simple Java program to brute force discord invite links.
## ⚠️ Under Construction
The project is not finished. There are many bugs to be fixed, features to be implemented, lines of code to be improved. This is a first stable version, which made me decide to make this repository public.
## ⚠️ Disclaimer
This project (all code located in this repository) was made as a *proof of concept* without any malicious intent. The project is 100% open-source, without any warranty or liability regarding its usage.
## TODO
• Items marked as "TODO" (mostly methods I did not feel like implementing)<br/>
• Cut off undesired information from dumps<br/>
• Fix the bugs<br/>
• Make it possible to start/load a session using command line arguments<br/>
• Complete the JavaDoc<br/>
• Make a `.jar` from it and drop it in releases so the [Features](#features) don't lie in item #1 already<br/>
• Remove the residual debug commented code<br/>
• <br/>
## Features
• No setup/configuration/installation needed<br/>
• Supports multithrea*ding*<br/>
• Outputs a single dump file with the harvested invite links<br/>
## How to build
1) Clone this repo: `git clone https://github.com/AttiliaTheHun/DiscordInviteBruter.git`</br>
2) Compile: 
```shell
cd ˜(where your cloned repo is)/src/attilathehun/invitebruter
javac *.java
```
### Configuration
See `Launcher.java`</br>
</br>
`MAX_THREAD_COUNT` determines the maximum number of threads that will be used for the session. It does **not** state the actual number of threads that will be used.</br>
</br>
`ESTIMATED_MB_OF_MEMORY_PER_THREAD` determines how much of RAM should the program keep free for each thread. Setting it too low may result in [OutOfMemoryError](https://docs.oracle.com/javase/7/docs/api/java/lang/OutOfMemoryError.html). Setting it too high may result in decreased number of threads for the session, because the thread count is calculated as free memory divided by this estimate and compared to the given maximum.
## How to run
Obviously, you need to [build](#how-to-build) it first.</br>
```shell
cd ˜(where your cloned repo is)/src
java attilathehun.invitebruter.Main
```
