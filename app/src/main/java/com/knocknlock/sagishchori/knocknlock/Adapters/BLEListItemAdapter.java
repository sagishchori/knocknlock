package com.knocknlock.sagishchori.knocknlock.Adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.knocknlock.sagishchori.knocknlock.R;
import com.knocknlock.sagishchori.knocknlock.Models.BLEBlueToothDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Sagi Shchori on 08/03/2018.
 */

public class BLEListItemAdapter extends RecyclerView.Adapter<BLEListItemAdapter.BLEListItem>
{
    private ArrayList<BLEBlueToothDevice> deviceList;
    private OnBLEDeviceClickListener onBLEDeviceClickListener;
    private Context context;

    public BLEListItemAdapter(Context context, OnBLEDeviceClickListener onBLEDeviceClickListener)
    {
        deviceList = new ArrayList<>();
        this.context = context;
        setOnBLEDeviceClickListener(onBLEDeviceClickListener);
    }

    @Override
    public BLEListItem onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ble_list_item, parent, false);
        return new BLEListItem(view);
    }

    @Override
    public void onBindViewHolder(BLEListItem holder, final int position)
    {
        final BLEBlueToothDevice bleBlueToothDevice  = deviceList.get(position);
        holder.bindDevice(bleBlueToothDevice);
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onBLEDeviceClickListener != null)
                    onBLEDeviceClickListener.onBLEDeviceClicked(bleBlueToothDevice.getBluetoothDevice(), position);
            }
        });

        if (bleBlueToothDevice.isConnected())
            holder.setEnabled(true);
        else
            holder.setEnabled(false);
    }

    @Override
    public int getItemCount()
    {
        if (deviceList != null)
            return deviceList.size();

        return 0;
    }

    /**
     * Each time a {@link BluetoothDevice} is found it is added to the adapter to be shown to
     * the user.
     *
     * @param device    The {@link BluetoothDevice} That was found in the scan
     * @param rssi      The {@param rssi} value
     */
    public void addDevice(BluetoothDevice device, int rssi)
    {
        BLEBlueToothDevice bleBlueToothDevice = new BLEBlueToothDevice(device, rssi);
        if (deviceList != null)
        {
            if (!deviceList.contains(bleBlueToothDevice))
                deviceList.add(bleBlueToothDevice);

            // Sorting the {@link BluetoothDevice} list in descending order
            Collections.sort(deviceList, new RSSIComparator());
        }
    }

    public void updateListItemState(boolean state, int selectedItemPosition)
    {
        if (deviceList != null && deviceList.size() > 0)
        {
            deviceList.get(selectedItemPosition).setConnected(state);
            notifyItemChanged(selectedItemPosition);
        }
    }

    class BLEListItem extends RecyclerView.ViewHolder
    {
        private TextView deviceName;
        private TextView deviceMACAddress;
        private TextView deviceRSSI;

        public BLEListItem(View itemView)
        {
            super(itemView);

            deviceName = (TextView) itemView.findViewById(R.id.device_name);
            deviceMACAddress = (TextView) itemView.findViewById(R.id.device_mac_address);
            deviceRSSI = (TextView) itemView.findViewById(R.id.device_rssi);
        }

        public void bindDevice(BLEBlueToothDevice bleBlueToothDevice)
        {
            BluetoothDevice bluetoothDevice = bleBlueToothDevice.getBluetoothDevice();
            deviceName.setText(bluetoothDevice.getName());
            deviceMACAddress.setText(bluetoothDevice.getAddress());
            deviceRSSI.setText(bleBlueToothDevice.getRSSI());
        }

        /**
         * A method to indicate if the BLE device is connected.
         *
         * @param enabled   true - The device is connected, false - The device is disconnected
         */
        public void setEnabled(boolean enabled)
        {
            if (enabled)
                deviceName.setTextColor(context.getResources().getColor(R.color.connected));
            else
                deviceName.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    /**
     * A Class to compare between 2 {@link BLEBlueToothDevice} according
     * to their {@param rssi}.
     */
    private class RSSIComparator implements Comparator<BLEBlueToothDevice>
    {
        @Override
        public int compare(BLEBlueToothDevice o1, BLEBlueToothDevice o2)
        {
            return o1.getRSSI() - o2.getRSSI();
        }
    }

    public void setOnBLEDeviceClickListener(OnBLEDeviceClickListener onBLEDeviceClickListener)
    {
        this.onBLEDeviceClickListener = onBLEDeviceClickListener;
    }

    /**
     * An interface for passing the clicked device
     */
    public interface OnBLEDeviceClickListener
    {
        void onBLEDeviceClicked(BluetoothDevice device, int position);
    }
}
