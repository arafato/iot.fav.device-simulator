//  mvn exec:java -Dexec.mainClass="com.arafato.iot.App"

package com.arafato.iot;

import com.microsoft.azure.iot.service.exceptions.IotHubException;
import com.microsoft.azure.iot.service.sdk.Device;
import com.microsoft.azure.iot.service.sdk.RegistryManager;

import java.io.IOException;
import java.net.URISyntaxException;

public class App {

    public static void main(String[] args) throws IOException, URISyntaxException, Exception {
        System.out.println("Registering devices...");
        DeviceRegistry.instance();
        System.out.println("Devices registered!");
    }
}