package cyber.bletarget;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

public class Beacon extends BluetoothGattCallback {
    String address;
    BluetoothDevice device;
    private final Context applicationContext;
    private BluetoothGatt gatt;
    long counter = 0;
    int rssi = 0;
    long millis = System.currentTimeMillis();
    private boolean connected;
    private boolean ack = true;

    public Beacon(String address, BluetoothDevice device, Context applicationContext) {
        this.address = address;
        this.device = device;
        this.applicationContext = applicationContext;
    }


    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        ack = true;
        check(gatt);
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            connected = true;
            Log.i("TAG1", gatt.getDevice().getAddress() + " " + address + " connected");

            gatt.readRemoteRssi();
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            connected = false;
            rssi = 0;
            Log.i("TAG1", address + " disconnected");
        }
    }

    private void check(BluetoothGatt gatt) {
        if (this.gatt != gatt) {
            Log.e("TAG1", "gatt instance id different!!!");
        }
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        super.onReadRemoteRssi(gatt, rssi, status);
        millis = System.currentTimeMillis();
        counter++;
        this.rssi = rssi;
    }

    public long age() {
        return (System.currentTimeMillis() - millis) / 1000;
    }

    public boolean connected() {
        return connected;
    }

    public synchronized void connect() {
        if (!connectable())
            return;
        Log.i("TAG1", address + " connect");
        ack = false;
        gatt = device.connectGatt(applicationContext, false, this);
    }

    public void readRemoteRssi() {
        if (connected && gatt != null)
            gatt.readRemoteRssi();
    }

    public boolean connectable() {
        return !connected && ack;
    }
}
