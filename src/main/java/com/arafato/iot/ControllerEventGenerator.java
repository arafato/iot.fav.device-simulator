package com.arafato.iot;

import com.google.gson.Gson;
import java.util.*;

public class ControllerEventGenerator {

    private String beaconControllerId;
    private List<String> customerWifiMacs;
    private Gson gson;
    private Random random;

    public ControllerEventGenerator(String beaconControllerId) {
        this.beaconControllerId = beaconControllerId;
        this. customerWifiMacs = Arrays.asList(
            "11:11:11:11:11:11", "22:22:22:22:22:22", "33:33:33:33:33:33", "44:44:44:44:44:44", "55:55:55:55:55:55",
            "66:66:66:66:66:66", "77:77:77:77:77:77", "88:88:88:88:88:88", "99:99:99:99:99:99", "10:10:10:10:10:10",
            "aa:aa:aa:aa:aa:aa", "bb:bb:bb:bb:bb:bb", "cc:cc:cc:cc:cc:cc", "dd:dd:dd:dd:dd:dd", "ee:ee:ee:ee:ee:ee");
        this.gson = new Gson();
        this.random = new Random();
        this.random.setSeed(new Date().getTime());
    }

    public String generateEventMessage(int numOfCustomers) {
        List<String> wifiMacs = this.getRandomWifiMacs(numOfCustomers);
        EventMessage message = new EventMessage();
        message.controllerId = this.beaconControllerId;
        long now = new Date().getTime();
        
        // In 80% of all messages changes have occurred
        if (this.random.nextInt(10) <= 7) {
	        for (String wm : wifiMacs) {
	        	message.changes.add(new EventMessage.Change(this.getRandomizedTimestamp(now), this.generateRandomRSSI(), wm));
	        }
        }

        return this.gson.toJson(message);
    }
    
    public String generateRandomEventMessage() {
        int n  = new Random().nextInt(15)+1;
        return this.generateEventMessage(n);
     }
    
    private int generateRandomRSSI() {
    	// RSSI values are between -50 and -100
    	return (this.random.nextInt(50) + 1) - 100;
    }    

    private long getRandomizedTimestamp(long now) {
        return now + (this.random.nextInt(4000) - 2000);
    }

    private List<String> getRandomWifiMacs(int numOfCustomers) {
        Collections.shuffle(this.customerWifiMacs);
        return this.customerWifiMacs.subList(0, numOfCustomers-1);
    }

    private static class EventMessage {
        public String controllerId;
        public List<Change> changes = new ArrayList<Change>();

        private static class Change {
        	public Change(long lastEventTimeUTC, int avgRSSI, String wifiMac) {
        		this.lastEventTimeUTC = lastEventTimeUTC;
        		this.avgRSSI = avgRSSI;
        		this.wifiMac = wifiMac;
        	}
            public long lastEventTimeUTC;
            public int avgRSSI;
            public String wifiMac;
        }
    }
}