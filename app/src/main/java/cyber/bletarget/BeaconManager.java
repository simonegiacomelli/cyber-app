package cyber.bletarget;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cyber.bletarget.ui.home.HomeViewModel;

public class BeaconManager {

    ArrayList<Beacon> beacons = new ArrayList<>();
    String BEN = "d5:61:6b:fb:8d:e3";
    String CARL = "d6:82:a5:47:bf:ac";
    String DAVE = "c5:7c:30:e4:a5:66";
    String FERRANTE = "f7:a2:7a:d2:40:1c";
    String SIMO = "f3:4e:e8:df:11:bc";
    List<String> addresses = Arrays.asList(FERRANTE, BEN, SIMO);
    private BluetoothAdapter mBTAdapter;
    private Context applicationContext;
    private HomeViewModel homeViewModel;
    private Thread thread;
    private Activity activity;

    public BeaconManager(Activity activity, Context applicationContext, HomeViewModel homeViewModel) {
        this.activity = activity;
        this.applicationContext = applicationContext;
        this.homeViewModel = homeViewModel;
    }
    //"d5:61:6b:fb:8d:e3");
//        ,
//                "d6:82:a5:47:bf:ac",
//                "c5:7c:30:e4:a5:66");

    public synchronized void connectBeacons() {

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        // Ask for location permission if not already allowed
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        if (thread != null)
            return;

        beacons.clear();
        thread = new Thread() {
            public void run() {
                internalRun();

            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    MqttRssi mqttRssi = new MqttRssi();

    private void internalRun() {

        mqttRssi.connect();

        addresses.forEach(addr -> {
            String address = addr.toUpperCase();
            BluetoothDevice device = mBTAdapter.getRemoteDevice(address);
            Beacon beacon = new Beacon(addr);

            BluetoothGatt gatt = device.connectGatt(applicationContext, true, beacon);
            beacon.gatt = gatt;
            beacon.device = device;
            beacons.add(beacon);
            try {
                Thread.sleep(900);
            } catch (InterruptedException e) {

            }

        });
        int index = 0;
        long counter = 0;
        long prev = System.currentTimeMillis();
        while (Thread.currentThread().isAlive()) {

            counter++;
            long curr = System.currentTimeMillis();

            index++;
            if (index >= beacons.size())
                index = 0;

            beacons.get(index).gatt.readRemoteRssi();
            StringBuilder sb = new StringBuilder();
            for (Beacon b : beacons) {
                sb.append(b.rssi);
                sb.append(",");
            }
            sb.append(counter);


            String line = sb.toString();
            mqttRssi.publish("cyber/rssi", line);
            homeViewModel.mText.postValue(line);
            Log.i("CYBER", line);
            try {
                Thread.sleep(90);
            } catch (InterruptedException e) {

            }
        }
    }
}