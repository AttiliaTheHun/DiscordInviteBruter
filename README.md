# DiscordInviteBruter
A simple Java program to brute force discord invite links.
## TODO
• Items marked as "TODO" (mostly methods I did not feel like implementing)<br/>
• Cut off undesired information from dumps<br/>
• Fix the bugs<br/>
• Make it possible to start/load a session using command line arguments<br/>
• Complete the JavaDoc<br/>
• Make a `.jar` from it and drop it in releases so the [Features](#features) don't lie in item #1 already<br/>
• Remove the residual debug commented code<br/>
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
## How to run
```shell
cd ˜(where your cloned repo is)/src
java attilathehun.invitebruter.Main
```
