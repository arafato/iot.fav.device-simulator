//  mvn exec:java -Dexec.mainClass="com.arafato.iot.App"

package com.arafato.iot;

import com.microsoft.azure.iot.service.exceptions.IotHubException;
import com.microsoft.azure.iot.service.sdk.Device;
import com.microsoft.azure.iot.service.sdk.RegistryManager;
import com.microsoft.azure.iothub.IotHubClientProtocol;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class App {

    public static void main(String[] args) throws IOException, URISyntaxException, Exception {
        if (args.length < 2) {
            System.out.println("Please supply the number of simulated devices and the iot hub connection string.");
            System.exit(1);
        }
        int numberOfDevices = Integer.parseInt(args[0]);
        Config.init(args[1]);        
        registerDevices(numberOfDevices);
        
        DeviceEmulator emulator = new DeviceEmulator(10, IotHubClientProtocol.MQTT);
        List<String> deviceIds = DeviceRegistry.instance().getDeviceIds(numberOfDevices);
        for(String deviceId: deviceIds) {
        	emulator.addBeaconController(deviceId, DeviceRegistry.instance().getAuthKey(deviceId));
        }
        
        emulator.start();
        
        System.in.read();
        
        emulator.stop();
    }

    private static void registerDevices(int numberOfDevices) throws Exception {
        System.out.println(String.format("Registering %1d devices... ", numberOfDevices));
        DeviceRegistry.instance().initDevices(numberOfDevices);
        System.out.println(String.format("Successfully registered all devices... ", numberOfDevices));
    }
}