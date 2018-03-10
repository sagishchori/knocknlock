package com.sagishchori.knocknlock.models;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Sagi Shchori on 09/03/2018.
 */

public class BLEBlueToothDevice
{
    private BluetoothDevice bluetoothDevice;
    private int rssi;
    private boolean connected;

    public BLEBlueToothDevice(BluetoothDevice bluetoothDevice, int rssi)
    {
        this.bluetoothDevice = bluetoothDevice;
        this.rssi = rssi;
    }

    public BLEBlueToothDevice copy (BLEBlueToothDevice bleBlueToothDevice)
    {
        BLEBlueToothDevice device = new BLEBlueToothDevice(bleBlueToothDevice.getBluetoothDevice(), bleBlueToothDevice.getRSSI());
        device.setConnected(bleBlueToothDevice.isConnected());

        return device;
    }

    public BluetoothDevice getBluetoothDevice()
    {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice)
    {
        this.bluetoothDevice = bluetoothDevice;
    }

    public int getRSSI()
    {
        return rssi;
    }

    public void setRSSI(int rssi)
    {
        this.rssi = rssi;
    }

    public boolean isConnected()
    {
        return connected;
    }

    public void setConnected(boolean connected)
    {
        this.connected = connected;
    }

    public String toString()
    {
        return bluetoothDevice.getName() + " " + bluetoothDevice.getAddress();
    }
}
