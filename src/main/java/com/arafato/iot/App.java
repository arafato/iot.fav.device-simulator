//  mvn exec:java -Dexec.mainClass="com.arafato.iot.App"

package com.arafato.iot;

import com.microsoft.azure.iot.service.exceptions.IotHubException;
import com.microsoft.azure.iot.service.sdk.Device;
import com.microsoft.azure.iot.service.sdk.RegistryManager;

import java.io.IOException;
import java.net.URISyntaxException;

public class App {

    public static void main(String[] args) throws IOException, URISyntaxException, Exception {
        if (args.length < 1) {
            System.out.println("Please supply the number of simulated devices.");
            System.exit(1);
        }
        int numberOfDevices = Integer.parseInt(args[0]);
        registerDevices(numberOfDevices);


    }

    private static void registerDevices(int numberOfDevices) throws Exception {
        System.out.println(String.format("Registering %1d devices... ", numberOfDevices));
        DeviceRegistry.instance().initDevices(numberOfDevices);
        System.out.println(String.format("Successfully registered all devices... ", numberOfDevices));
    }
}