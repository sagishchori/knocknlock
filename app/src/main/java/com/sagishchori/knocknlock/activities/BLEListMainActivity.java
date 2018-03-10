package com.sagishchori.knocknlock.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sagishchori.knocknlock.adapters.BLEListItemAdapter;
import com.knocknlock.sagishchori.knocknlock.R;

public class BLEListMainActivity extends AppCompatActivity implements BLEListItemAdapter.OnBLEDeviceClickListener
{
    /**
     * A class tag
     */
    public final static String TAG = BLEListMainActivity.class.getSimpleName();

    /**
     * A request code for indicating a request made for enabling bluetooth
     */
    public final static int REQUEST_ENABLE_BT = 12;

    /**
     * The scan period in ms.
     */
    private final static int SCAN_PERIOD = 10000;

    private Toolbar toolbar;
    private View includedView;
    private BluetoothManager bluetoothManager = null;
    private boolean mScanning;
    private BLEListItemAdapter mLeDeviceListAdapter;
    private RecyclerView bleRecyclerView;
    private boolean mEnabled = false;
    private BluetoothAdapter mBluetoothAdapter;
    private ProgressBar progressBar;
    private FloatingActionButton fab;
    private BluetoothGatt bluetoothGatt;
    private int selectedItemPosition;
    private int bluetoothState = BluetoothProfile.STATE_DISCONNECTED;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.blelist_activity_main);

        setupViews();

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        isBlueToothAvailableAndEnabled();

        startScan(true);
    }

    private void setupViews()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startScan(true);
            }
        });

        includedView = (View) findViewById(R.id.ble_recycleView);
        bleRecyclerView = (RecyclerView) includedView.findViewById(R.id.ble_recycleView);

        mLeDeviceListAdapter = new BLEListItemAdapter(this, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        bleRecyclerView.setLayoutManager(linearLayoutManager);
        bleRecyclerView.setAdapter(mLeDeviceListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_blelist_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_scan:
                startScan(true);
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A method to check if Bluetooth available on the device and if it is enabled.
     *
     * @return      true - if Bluetooth is available and enabled, false - if Bluetooth is not available
     *              or Bluetooth is not enabled
     */
    private boolean isBlueToothAvailableAndEnabled()
    {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            mEnabled = false;
            return false;
        }
        else
        {
            mEnabled = true;
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == REQUEST_ENABLE_BT)
            {
                isBlueToothAvailableAndEnabled();
                startScan(!mScanning);
            }
        }
    }

    /**
     * A method for enabling the device's Bluetooth in case it is disabled.
     */
    private void enableBlueTooth()
    {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    /**
     * A method to start scanning for BLE devices or to stop scanning when needed.
     *
     * @param enable    A boolean for start/stop the scan
     */
    private void startScan(final boolean enable)
    {
        // First, check if Bluetooth is enabled.
        if (!mEnabled)
        {
            // In case the Bluetooth is not enabled, enable it and return. The next call
            // to startScan() will be after the system will be notified that Bluetooth was enabled.
            enableBlueTooth();
            return;
        }

        // If Bluetooth is enabled start the scan for a period of time defined in SCAN_PERIOD field.
        if (enable) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);

                    // Show the fab in case the user will want to re-scan for BLE devices
                    fab.setVisibility(View.VISIBLE);

                    progressBar.setVisibility(View.GONE);
                }
            }, SCAN_PERIOD);

            // Hide the fab in case the user start to re-scan for BLE devices
            fab.setVisibility(View.GONE);

            progressBar.setVisibility(View.VISIBLE);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);

            // Show the fab in case the user will want to re-scan for BLE devices
            fab.setVisibility(View.VISIBLE);

            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * @see android.bluetooth.BluetoothAdapter.LeScanCallback
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("Device found:", device.getName() + " " + device.getAddress());
                            mLeDeviceListAdapter.addDevice(device, rssi);
                        }
                    });
                }
            };

    /**
     * @see BluetoothGattCallback
     */
    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback()
    {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
            bluetoothState = newState;

            if (newState == BluetoothProfile.STATE_CONNECTED)
            {
                mLeDeviceListAdapter.updateListItemState(true, selectedItemPosition);

                Toast.makeText(BLEListMainActivity.this, getString(R.string.connected_to) + " " + bluetoothGatt.getDevice().getName(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Connected to GATT server.");

                if (bluetoothGatt != null)
                {
                    // After connecting to a device we are trying to discover the services of the GATT server
                    bluetoothGatt.discoverServices();
                }
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED)
            {
                mLeDeviceListAdapter.updateListItemState(false, selectedItemPosition);

                Toast.makeText(BLEListMainActivity.this, getString(R.string.disconnected), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            super.onServicesDiscovered(gatt, status);
        }
    };

    @Override
    public void onBLEDeviceClicked(BluetoothDevice device, int position)
    {
        selectedItemPosition = position;

        // Before trying to connect to/disconnect from device we need to stop the scan
        startScan(false);

        // If the Bluetooth is in connecting or in disconnecting state return and do nothing to prevent multiple clicks by the user.
        if (bluetoothState == BluetoothProfile.STATE_CONNECTING || bluetoothState == BluetoothProfile.STATE_DISCONNECTING)
            return;
        // If the user clicks on a list item and it is connected we disconnect the client from GATT server.
        else if (bluetoothState == BluetoothProfile.STATE_CONNECTED && bluetoothGatt != null)
            bluetoothGatt.disconnect();
        // If the user clicks on a list item and it is disconnected we connect the client to GATT server.
        else if (bluetoothState == BluetoothProfile.STATE_DISCONNECTED)
            bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        close();
    }

    public void close() {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.disconnect();
        bluetoothGatt.close();
        bluetoothGatt = null;
    }
}
