package com.example.blebluetoothtest;

import java.util.Objects;

public class BLEDevice {
    private String mac;
    private String name;

    public BLEDevice(String mac, String name) {
        this.mac = mac;
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BLEDevice bleDevice = (BLEDevice) o;
        return mac.equals(bleDevice.mac);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mac);
    }
}
