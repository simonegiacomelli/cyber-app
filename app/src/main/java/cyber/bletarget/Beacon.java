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

    public Beacon(String address) {
        this.address = address;
//            this.device = device;
    }


    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.i("CYBER", gatt.getDevice().getAddress() + " " + address + " connected");

            gatt.readRemoteRssi();
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.i("CYBER", address + " disconnected");
        }
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        super.onReadRemoteRssi(gatt, rssi, status);
        counter++;
//            if (counter % 100 == 0)
        //Log.i("CYBER", gatt.getDevice().getAddress() + " " + this.gatt.getDevice().getAddress() + " " + address + " rssi " + rssi);
//            this.gatt.readRemoteRssi();
        this.rssi = rssi;
    }
}
