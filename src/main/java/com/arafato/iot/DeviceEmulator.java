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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class DeviceEmulator {
    private int numOfThreads;
    private IotHubClientProtocol protocol;
    private ExecutorService executor;
    private List<BeaconControllerSender> senders = new ArrayList<BeaconControllerSender>();
    
    public DeviceEmulator(int numOfDevices, IotHubClientProtocol protocol) {
        this.numOfThreads = numOfDevices / 2 + 1;
        this.executor = Executors.newFixedThreadPool(this.numOfThreads);
        this.protocol = protocol;
    }

    public void addBeaconController(String deviceId, String authKey) throws URISyntaxException {
    	this.senders.add(new BeaconControllerSender(deviceId, authKey, this.protocol));
    }
    
    public void start() {
    	for(BeaconControllerSender sender: senders) {
    		executor.execute(sender);
    	}
    }
    
    public void stop() throws IOException {
    	for(BeaconControllerSender sender: senders) {
    		sender.shutdown();
    	}
    	this.executor.shutdown();
    }


    private static class BeaconControllerSender implements Runnable {
        private String connString = "HostName=%1s;DeviceId=%2s;SharedAccessKey=%3s";
        private DeviceClient client;
        private ControllerEventGenerator messageGenerator;

        public volatile boolean stopThread = false;
        
        public BeaconControllerSender(String deviceId, String authKey, IotHubClientProtocol protocol) throws URISyntaxException {
            this.connString = String.format(this.connString, Config.IOT_HUB_ENDPOINT, deviceId, authKey);
            this.client = new DeviceClient(this.connString, protocol);
            this.messageGenerator = new ControllerEventGenerator(deviceId);
        }

        public void shutdown() throws IOException {
        	this.client.close();
        }
        
        public void run() {
        	try {
        		while(!stopThread) {
            		String msgJson = messageGenerator.generateRandomEventMessage();
            		Message msg = new Message(msgJson); 
            		this.client.sendEventAsync(msg, null, null);
            		Thread.sleep(5000);
            	}	
        	} catch (InterruptedException e) {
        		System.err.println("Interrupted: " + e.getMessage());
        	}
        }
    }

}