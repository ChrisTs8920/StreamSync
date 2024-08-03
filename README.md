# StreamSync

## Description

A Video streaming server - client application made with Java, JavaFX and FFmpeg.

>*This project was made during my Multimedia course in University.*

### Server

The application responsible for managing a list of available files that can be streamed, for converting the original files into a set of files with different formats and resolutions, for communicating with the client, as well as for the transmission of the files to the Client.

**Supported formats:** avi, mkv, mp4\
**Supported resolutions:** 240p, 360p, 480p, 720p, 1080p\
**Supported protocols:** tcp, udp, rtp/udp

### Client

The application that runs on the end user side and is responsible for communicating with the Server and downloading and playing the video to the end user.

## A more in depth look

### Server details

Consists of 4 menus:

- **Home:** Welcomes the user to the Server application and gives us a small explanation about the functions of the next menus.
- **Console:** Contains all the functionality of the Server. It simulates a console, and the Server user can choose to run commands.
- **Client:** Contains information about the current connected client.
- **About:** A simple menu that contains information about the application.

>*The user can select any of these menus at anytime.*

Available console commands:

- **generatefiles:** Reads 'videos' directory and generates all supported formats and resolutions for each video file read.
- **listfiles:** Lists all the available files.
- **awaitconnection:** Waits for a client connection on port 5000.
- **closeconnection:** Closes the current connection, if one exists.
- **listclientfiles:** Lists all files that the Client can support, based on his speed.
- **clear:** Clears the console.
- **help:** Displays a help message about all available commands and their descriptions.
- **exit:** Exits the Server application.

>The server does not generate a higher resolution than the one given.

### Client details

Consists of 4 Screens:

- **First Screen:** The first screen that the user sees upon opening the Client application. It starts a 5 second speedtest to determine the Client speed. It is also the screen were the user selects a format. The user can go to the next screen if the server is up and waiting for a connection.
- **Second Screen:** In this screen there is already a connection established. This screen displays a list of available videos that the Server provided. The user can select a video and a protocol (optional).
- **Third Screen:** The Media player that plays the selected video (using VLCJ). The user can play/pause the video using the button or by clicking the video screen.
- **Fourth Screen:** A simple about screen that can be opened anytime from the third screen.

Ruleset for protocol selection (if user did not select a protocol):


|Resolutions|240p|360p|480p|720p|1080p|
|-----------|----|----|----|----|-----|
|Protocol|TCP|UDP|UDP|RTP/UDP|RTP/UDP|

Ruleset for video availability:

|Resolutions|240p|360p|480p|720p|1080p|
|-----------|----|----|----|----|-----|
|User speed test|>400Kbps|>750Kbps|>1000Kbps|>2500Kbps|>4500Kbps|


### Usage

Place preffered video files inside 'videos' directory: '***src\main\resources\org\streamsync\multimedia_server\videos***'

Start the server and run 'generatefiles' (otherwise the Server won't know about the files in the 'videos' directory). After generating the files, run 'awaitconnection' (you can also use any other command you like during that time).

While the Server waits for a connection, run the client to start the 5 second speed test. After the speed test, proceed to the next screen and select a video and protocol (optional). Lastly the streaming begins.

## Technologies and tools used

- JavaFX
- [SceneBuilder for JavaFX](https://gluonhq.com/products/scene-builder/)
- [FFmpeg](https://ffmpeg.org/download.html)
- [FFmpeg Wrapper for Java](https://github.com/bramp/ffmpeg-cli-wrapper)
- [JSpeedTest](https://github.com/bertrandmartel/speed-test-lib)
- [VLCJ](https://github.com/caprica/vlcj)
- Log4j2 Logger

Icons provided by:\
[Google Material Icons](https://fonts.google.com/icons)

Theme builder used:\
[Google Material theme builder](https://material-foundation.github.io/material-theme-builder/)

## Requirements

- Java JDK 11+ (Used 17)
- JavaFX 2+ (Used 21)
- [VLC 64bit](https://www.videolan.org/vlc/download-windows.html) (Required by VLCJ)

## Current limitations - bugs

- **While using rtp/udp:** Audio is not streamed.
- **While using rtp/udp:** video.sdp file is located locally inside the Server project.
- **While using rtp/udp:** Video timer stays at 0 (Client media player screen).

- When the client disconnects abruptly, the server cannot detect it automatically.\
Execution of 'closeconnection' is required.
- If we want to place different - new videos inside 'videos' directory, we need to delete the old ones manually. We **also** need to delete a flag file called 'flag.flag'. This file is created when the first generation of files occurs.

## Screenshots

### Server - Home

![Server - Home](https://github.com/ChrisTs8920/StreamSync/blob/main/screenshots/Home.jpg?raw=true)

### Server - Console

![Server - Console](https://github.com/ChrisTs8920/StreamSync/blob/main/screenshots/Console.jpg?raw=true)

### Server - Client menu

![Server - Client menu](https://github.com/ChrisTs8920/StreamSync/blob/main/screenshots/Client.jpg?raw=true)

### Server - About

![Server - About](https://github.com/ChrisTs8920/StreamSync/blob/main/screenshots/About.jpg?raw=true)

### Client - Speed test

![Client - Speed test](https://github.com/ChrisTs8920/StreamSync/blob/main/screenshots/Speedtest.jpg?raw=true)

### Client - Video selection

![Client - Video selection](https://github.com/ChrisTs8920/StreamSync/blob/main/screenshots/VideoSelection.jpg?raw=true)

### Client - Video player

![Client - Video player](https://github.com/ChrisTs8920/StreamSync/blob/main/screenshots/VideoPlayer.jpg?raw=true)

### Client - About

![Client - About](https://github.com/ChrisTs8920/StreamSync/blob/main/screenshots/About_c.jpg?raw=true)
