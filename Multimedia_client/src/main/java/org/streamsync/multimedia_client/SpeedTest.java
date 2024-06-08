package org.streamsync.multimedia_client;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class SpeedTest {
    private final SpeedTestSocket socket;
    private final String URI;
    private int downSpeed = 0;
    private boolean testCompleted = false;

    public SpeedTest(String URI) {
        this.URI = URI;

        socket = new SpeedTestSocket();
        socket.addSpeedTestListener(new ISpeedTestListener() {
            @Override
            public void onCompletion(SpeedTestReport speedTestReport) {
                downSpeed = speedTestReport.getTransferRateBit().intValue() / 1_000_000;
                testCompleted = true;
            }

            @Override
            public void onProgress(float v, SpeedTestReport speedTestReport) {
                downSpeed = speedTestReport.getTransferRateBit().intValue() / 1_000_000;
            }

            @Override
            public void onError(SpeedTestError speedTestError, String s) {
                System.out.println("[ERROR] error : " + s);
            }
        });
    }

    public int getSpeed() {
        return downSpeed;
    }

    public boolean isTestCompleted() {
        return testCompleted;
    }

    public void start() {
        int REPORT_INTERVAL = 1000;
        int TEST_DUR = 5000;
        socket.startFixedDownload(this.URI, TEST_DUR, REPORT_INTERVAL);
    }

    public void close() {
        socket.closeSocket();
    }
}