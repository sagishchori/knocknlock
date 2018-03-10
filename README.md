# knocknlock

This app allows you to:
1. Scan for BLE (Bluetooth Low Energy) device nearby.
* Scan period - 10 second (can be modified).
* The list is being refreshed each time a device is found.
2. Show the devices in list (RecyclerView).
3. Sort the list by RSSI key.
4. Connecting to/ Disconnecting from any selected device.
* By clicking on RecyclerView item the user is trying to connect to/disconnect from GATT server.
5. After connecting to a device discover device's services.
* Nothing is being done with the list.
