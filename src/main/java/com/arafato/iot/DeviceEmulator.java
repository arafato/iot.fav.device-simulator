package com.arafato.iot;

import com.microsoft.azure.iothub.DeviceClient;
import com.microsoft.azure.iothub.IotHubClientProtocol;
import com.microsoft.azure.iothub.Message;
import com.microsoft.azure.iothub.IotHubStatusCode;
import com.microsoft.azure.iothub.IotHubEventCallback;
import com.microsoft.azure.iothub.IotHubMessageResult;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class DeviceEmulator {
    private int numOfThreads;
    private IotHubClientProtocol protocol;

    private ExecutorService executor;
    
    public DeviceEmulator(int numOfDevices, IotHubClientProtocol protocol) {
        this.numOfThreads = numOfThreads / 2 + 1;
        this.executor = Executors.newFixedThreadPool(this.numOfThreads);
        this.protocol = protocol;
    }

    // public void addBeaconController

    private static class TelemetryDataPoint {
        public String deviceId;
        public double windSpeed;

        public String serialize() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }

    private static class BeaconController implements Runnable {
        private String connString = "HostName=arafato-iothub.azure-devices.net;DeviceId=%1s;SharedAccessKey=%2s";
        private DeviceClient client;

        public BeaconController(String deviceId, String authKey, IotHubClientProtocol protocol) throws URISyntaxException {
            this.connString = String.format(this.connString, deviceId, authKey);
            client = new DeviceClient(this.connString, protocol);    
        }

        public void run() {

        }
    }

}