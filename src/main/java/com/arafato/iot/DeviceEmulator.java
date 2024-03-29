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

    public void addBeaconController(String deviceId, String authKey) throws URISyntaxException, IOException {
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


    private static class EventCallback implements IotHubEventCallback {
        public void execute(IotHubStatusCode status, Object context) {
            // System.out.println("IoT Hub responded to message with status: " + status.name());
        }
    }
    
    private static class BeaconControllerSender implements Runnable {
        private String connString = "HostName=%1s;DeviceId=%2s;SharedAccessKey=%3s";
        private DeviceClient client;
        private ControllerEventGenerator messageGenerator;
        private String deviceId;
        private String authKey;
        
        public volatile boolean stopThread = false;
        
        public BeaconControllerSender(String deviceId, String authKey, IotHubClientProtocol protocol) throws URISyntaxException, IOException {
        	this.deviceId = deviceId;
        	this.authKey = authKey;
            this.connString = String.format(this.connString, Config.IOT_HUB_ENDPOINT, deviceId, authKey);
            this.client = new DeviceClient(this.connString, protocol);
            this.client.open();
            this.messageGenerator = new ControllerEventGenerator(deviceId);
        }

        public void shutdown() throws IOException {
        	System.out.println("Closing " + this.deviceId);
        	this.stopThread = true;
        	this.client.close();
        }
        
        public void run() {
        	try {
        		// Spreading the initial startups of individual senders by [500;5000]ms
	        	Random rand = new Random();
	        	Thread.sleep(rand.nextInt(4500) + 500);
        	}
        	catch(InterruptedException e) {
        		System.err.println("Interrupted during initial startup: " + e.getMessage());
        	}
        	try {
        		while(!stopThread) {
            		String msgJson = messageGenerator.generateRandomEventMessage();
            		
            		System.out.println(deviceId + " sending:");
            		System.out.println(msgJson);
            		
            		Message msg = new Message(msgJson); 
            		this.client.sendEventAsync(msg, new EventCallback(), null);
            		Thread.sleep(5000);
            	}	
        	} catch (InterruptedException e) {
        		System.err.println("Interrupted: " + e.getMessage());
        	}
        }
    }

}