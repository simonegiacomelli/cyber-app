package cyber.bletarget;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

public class Beacon extends BluetoothGattCallback {
    String address;
    BluetoothDevice device;
    BluetoothGatt gatt;
    long counter = 0;
    int rssi = 0;
    long millis = System.currentTimeMillis();
    boolean connected;

    public Beacon(String address) {
        this.address = address;
    }


    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            connected = true;
            Log.i("CYBER", gatt.getDevice().getAddress() + " " + address + " connected");

            gatt.readRemoteRssi();
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            connected = false;
            Log.i("CYBER", address + " disconnected");
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
        return System.currentTimeMillis() - millis;
    }

    public boolean connected() {
        return connected;
    }
}
