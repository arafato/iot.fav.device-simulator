package com.arafato.iot;

import com.microsoft.azure.iot.service.exceptions.IotHubException;
import com.microsoft.azure.iot.service.sdk.Device;
import com.microsoft.azure.iot.service.sdk.RegistryManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class DeviceRegistry {
    private static final String DEVICE_ID = "Device-%1$d";
    private static DeviceRegistry _instance;

    private Map<String, Device> registry;

    private DeviceRegistry() throws Exception {
        this.registry = new HashMap<String, Device>();
    }

    static {
        try {
            _instance = new DeviceRegistry();
        } catch (Exception e) {
            throw new RuntimeException("exception occured in creation of DeviceRegistry: " + e.getMessage());
        }
    }

    public static DeviceRegistry instance() {
        return _instance;
    }

    public Device getDevice(String deviceId) {
        return this.registry.get(deviceId);
    }

    public String getAuthKey(String deviceId) {
        Device device = this.getDevice(deviceId);
        if (device != null) {
            return device.getPrimaryKey();
        }
        throw new NoSuchElementException(deviceId);
    }

    public List<String> getDeviceIds(int numberOfDevices) {
        return new ArrayList(this.registry.keySet()).subList(0, numberOfDevices);
    }

    public void initDevices(int n) throws Exception {
        RegistryManager registryManager = RegistryManager.createFromConnectionString(Config.IOT_HUB_CONNECTION_STRING);
        for(int i = 1; i <= n; ++i) {
            String deviceId = String.format(DEVICE_ID, i);
            Device device = Device.createFromId(deviceId, null, null);
            try {
                device = registryManager.addDevice(device);
            } catch (IotHubException iote) {
                try {
                    device = registryManager.getDevice(deviceId);
                } catch (IotHubException iotf) {
                    iotf.printStackTrace();
                    throw iotf;
                }
            }

            this.registry.put(device.getDeviceId(), device);
        }
    }
}