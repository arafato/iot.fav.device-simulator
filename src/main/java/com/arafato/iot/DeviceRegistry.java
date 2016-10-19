package com.arafato.iot;

import com.microsoft.azure.iot.service.exceptions.IotHubException;
import com.microsoft.azure.iot.service.sdk.Device;
import com.microsoft.azure.iot.service.sdk.RegistryManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class DeviceRegistry {
    private static final String CONNECTION_STRING = "HostName=arafato-iothub.azure-devices.net;SharedAccessKeyName=iothubowner;SharedAccessKey=oqAl5nl3FiacSAXYJnzvgj78zbtxE/+wD4dBizs4908=";
    private static final String DEVICE_ID = "Device-%1$d";
    private static final int NUMBER_OF_DEVICES = 100;
    private static DeviceRegistry _instance;

    private Map<String, Device> registry;

    private DeviceRegistry() throws Exception {
        this.registry = new HashMap<String, Device>();
        this.initDevices(NUMBER_OF_DEVICES);
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

    public List<String> getDeviceIds() {
        return new ArrayList(this.registry.keySet());
    }

    private void initDevices(int n) throws Exception {
        RegistryManager registryManager = RegistryManager.createFromConnectionString(CONNECTION_STRING);
        for(int i = 1; i <= n; ++i) {
            Device device = Device.createFromId(String.format(DEVICE_ID, i), null, null);
            try {
                device = registryManager.addDevice(device);
            } catch (IotHubException iote) {
                try {
                    device = registryManager.getDevice(DEVICE_ID);
                } catch (IotHubException iotf) {
                    iotf.printStackTrace();
                    throw iotf;
                }
            }

            this.registry.put(device.getDeviceId(), device);
        }
    }
}