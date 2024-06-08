package org.streamsync.multimedia_server;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import shared.AppLogger;
import shared.VideoFile;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class Server {
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private ObjectOutputStream outObj = null;
    private ObjectInputStream inObj = null;

    private DataSingleton singleton;

    private final ArrayList<VideoFile> videos = new ArrayList<>();
    private ArrayList<VideoFile> clientVideos = new ArrayList<>();

    private final String PATH = "src/main/resources/org/streamsync/multimedia_server/videos/";
    private final String[] FORMATS = {"avi", "mp4", "mkv"};
    private final int[][] RESOLUTIONS = {
            {426, 640, 854, 1280, 1920},
            {240, 360, 480, 720, 1080}}; // 16:9 aspect ratio resolutions
    private final double[] RECSPEEDS =
            {0.4, 0.75, 1, 2.5, 4.5}; // YouTube recommended speeds in mbp/s for each resolution

    public void generate() {
        readAvailableFiles(new File(PATH));
        AppLogger.logger.info("Read available video files from local storage.");
        if (!filesGenerated()) {
            generateMissingFiles();
            createFlag();
        }
    }

    public ArrayList<VideoFile> getVideos() {
        return videos;
    }

    public void connect(int port) {
        try {
            clientVideos.clear();

            server = new ServerSocket(port);
            socket = server.accept();
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());
            outObj = new ObjectOutputStream(socket.getOutputStream());
            inObj = new ObjectInputStream(socket.getInputStream());
            AppLogger.logger.info("Connected to {}.", socket.getInetAddress().getHostAddress());

            // Receive client speed test and format
            int speed = Integer.parseInt(receive());
            String format = receive();
            AppLogger.logger.info("Received speed {}.", speed);
            AppLogger.logger.info("Received format {}.", format);

            // Save data to Singleton
            singleton = DataSingleton.getInstance();
            singleton.setIpaddr(socket.getInetAddress().getHostAddress());
            singleton.setPort(port);
            singleton.setClientSpeed(speed);
            singleton.setClientFormat(format);
            singleton.setConnected(true);

            // Send videos list
            int i = maxResolutionIndex();
            clientVideos = generateClientVideos(i);
            AppLogger.logger.info("Client videos generated.");
            send(clientVideos);
            AppLogger.logger.info("Sent client videos.");

            // Receive Selected file and protocol
            singleton.setSelectedFile(receiveFile());
            singleton.setProtocol(receive());
            AppLogger.logger.info("Received selected file {}.", singleton.getSelectedFile());
            AppLogger.logger.info("Received protocol {}.", singleton.getProtocol());

            stream(singleton.getSelectedFile());
        } catch (IOException e) {
            AppLogger.logger.error("Connection error.");
        }
    }

    public void stream(VideoFile file) {
        // This method begins the streaming process
        try {
            ArrayList<String> list = new ArrayList<>();
            list.add("C:\\ffmpeg\\bin\\ffmpeg.exe");
            list.add("-re");
            list.add("-i");
            list.add(file.getFile().getPath());
            if (singleton.getProtocol().equals("udp")) {
                list.add("-f");
                list.add("mpegts");
                list.add(singleton.getProtocol() + "://localhost:8080?pkt_size=1316");
            } else if (singleton.getProtocol().equals("tcp")) {
                list.add("-f");
                list.add("mpegts");
                list.add(singleton.getProtocol() + "://localhost:8080?listen");
            } else if (singleton.getProtocol().equals("rtp/udp")) {
                list.add("-an"); // Transmit without audio (rtp does not work without this option)
                list.add("-c:v");
                list.add("copy");
                list.add("-f");
                list.add("rtp");
                list.add("-sdp_file");
                list.add("video.sdp");
                list.add("rtp://127.0.0.1:8080");
            }
            ProcessBuilder build = new ProcessBuilder(list);
            //build.inheritIO();
            build.start();
            AppLogger.logger.info("Currently Streaming: {}.", singleton.getSelectedFile());
        } catch (IOException e) {
            AppLogger.logger.error("Streaming could not start.");
        }
    }

    public void close() {
        try {
            singleton.setConnected(false);
            server.close();
            socket.close();
            in.close();
            out.close();
            AppLogger.logger.info("Closed server socket.");
            AppLogger.logger.info("Disconnected from client.");
        } catch (IOException e) {
            AppLogger.logger.error("Could not close the server socket.");
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public <T> void send(T data) {
        try {
            outObj.writeObject(data);
        } catch (IOException e) {
            AppLogger.logger.error("Could not send data to the client.");
        }
    }

    public String receive() {
        try {
            return in.readUTF();
        } catch (IOException e) {
            AppLogger.logger.error("Could not receive data from client.");
            return null;
        }
    }

    public VideoFile receiveFile() {
        try {
            return (VideoFile) inObj.readObject();
        } catch (IOException | ClassNotFoundException e) {
            AppLogger.logger.error("Could not receive selected file.");
            return null;
        }
    }

    private int maxResolutionIndex() {
        // Returns an index for the array RESOLUTIONS
        // which indicates the max resolution that the client can support
        int max = -1;
        for (int i = 0; i < RECSPEEDS.length; i++) {
            if (RECSPEEDS[i] <= singleton.getClientSpeed()) {
                max = i;
            }
        }
        return max;
    }

    private ArrayList<VideoFile> generateClientVideos(int i) {
        ArrayList<VideoFile> tmp = new ArrayList<>();
        for (VideoFile f: videos) {
            if (f.getHeight() <= RESOLUTIONS[1][i] && singleton.getClientFormat().equals(f.getFormat())) {
                tmp.add(f);
            }
        }
        return tmp;
    }

    public ArrayList<VideoFile> getClientVideos() {
        return clientVideos;
    }

    private void readAvailableFiles(File folder) {
        // Reads "videos" directory
        // Adds found files to videos list
        for (File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                readAvailableFiles(fileEntry);
            } else {
                VideoFile tmpFile = new VideoFile(fileEntry);
                videos.add(tmpFile);
                setMetadata(tmpFile);
            }
        }
    }

    private void generateMissingFiles() {
        // Generates the missing formats and resolutions for each video file
        // Adds them to videos list
        try {
            FFmpeg ffmpeg = new FFmpeg();
            FFprobe ffprobe = new FFprobe();

            // Generate missing formats
            ArrayList<VideoFile> tmpList = new ArrayList<>(videos);
            for (VideoFile v : tmpList) {
                for (String format : FORMATS) {
                    String name = v.getFile().getName().substring(0, v.getFile().getName().lastIndexOf("."))
                            + "." + format;
                    if (!new File(PATH + name).exists()) {

                        // Generate video file
                        FFmpegBuilder builder = new FFmpegBuilder()
                                .setInput(v.getFile().getPath())
                                .overrideOutputFiles(true)
                                .addOutput(PATH + name)
                                .done();
                        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
                        executor.createJob(builder).run();

                        // Add file to list
                        VideoFile tmpFile = new VideoFile(new File(PATH + name));
                        videos.add(tmpFile);
                        AppLogger.logger.info("Generated video file: {}", tmpFile);
                        setMetadata(tmpFile);
                    }
                }
            }

            // Generate missing resolutions
            ArrayList<VideoFile> tmpList2 = new ArrayList<>(videos);
            for (VideoFile v : tmpList2) {
                int maxW = v.getWidth();
                int maxH = v.getHeight();
                for (int i = 0; i < 5; i++) { // five resolutions
                    if (RESOLUTIONS[0][i] < maxW && RESOLUTIONS[1][i] < maxH) {
                        String name = v.getName().substring(0, v.getName().lastIndexOf("."))
                                + "_" + RESOLUTIONS[1][i] + "p" + "." + v.getFormat();
                        if (!new File(PATH + name).exists()) {

                            // Generate video file
                            FFmpegBuilder builder = new FFmpegBuilder()
                                    .setInput(v.getFile().getPath())
                                    .overrideOutputFiles(true)
                                    .addOutput(PATH + name)
                                    .setVideoResolution(RESOLUTIONS[0][i], RESOLUTIONS[1][i])
                                    .done();
                            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
                            executor.createJob(builder).run();

                            // Add file to list
                            VideoFile tmpFile = new VideoFile(new File(PATH + name));
                            videos.add(tmpFile);
                            AppLogger.logger.info("Generated video file: {}", tmpFile);
                            setMetadata(tmpFile);
                        }
                    }
                }
            }
        } catch (IOException e) {
            AppLogger.logger.error("Video generation failed.");
        }
    }

    private void setMetadata(VideoFile video) {
        // Stores name, format and resolution for each video file
        try {
            FFprobe ffprobe = new FFprobe();
            FFmpegProbeResult probeResult = ffprobe.probe(video.getFile().getPath());
            FFmpegFormat format = probeResult.getFormat();
            FFmpegStream stream = probeResult.getStreams().get(0);

            if (format.format_name.contains("matroska")) { // mkv
                video.setName(video.getFile().getName());
                video.setFormat("mkv");
                video.setWidth(stream.width);
                video.setHeight(stream.height);
            } else if (format.format_name.contains("mp4")) { // mp4
                video.setName(video.getFile().getName());
                video.setFormat("mp4");
                video.setWidth(stream.width);
                video.setHeight(stream.height);
            }
            else { // avi
                video.setName(video.getFile().getName());
                video.setFormat(format.format_name);
                video.setWidth(stream.width);
                video.setHeight(stream.height);
            }
            AppLogger.logger.info("Metadata set for: {}.", video);
        } catch (IOException e) {
            AppLogger.logger.error("Failed to set metadata for: {}.", video);
        }
    }

    private void createFlag() {
        // Create an empty flag file that indicates that the video files have already been generated
        // from a previous execution of the server.
        try {
            new File("src/main/resources/org/streamsync/multimedia_server/flag.flag").createNewFile();
            AppLogger.logger.info("Flag file created.");
        } catch (IOException e) {
            AppLogger.logger.error("Could not create flag file.");
        }
    }

    private boolean filesGenerated() {
        // Returns true if the flag file exists, else returns false
        return new File("src/main/resources/org/streamsync/multimedia_server/flag.flag").exists();
    }
}