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
    ConnectionManager connectionManager = new ConnectionManager(beacons);
    String BEN = "d5:61:6b:fb:8d:e3";
    String CARL = "d6:82:a5:47:bf:ac";
    String DAVE = "c5:7c:30:e4:a5:66";
    String FERRANTE = "f7:a2:7a:d2:40:1c";
    String SIMO = "f3:4e:e8:df:11:bc";
    List<String> addresses = Arrays.asList(FERRANTE, BEN, SIMO);
    //    List<String> addresses = Arrays.asList(BEN);
    private BluetoothAdapter mBTAdapter;
    private Context applicationContext;
    private HomeViewModel homeViewModel;
    private Thread thread;

    public BeaconManager(Context applicationContext, HomeViewModel homeViewModel) {
        this.applicationContext = applicationContext;
        this.homeViewModel = homeViewModel;
    }

    public synchronized void connectBeacons() {

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

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
            Beacon beacon = new Beacon(addr, device, applicationContext);
            beacons.add(beacon);
        });

        int index = 0;
        long counter = 0;

        while (Thread.currentThread().isAlive()) {
            connectionManager.pollConnection();
            counter++;
            long curr = System.currentTimeMillis();

            index++;
            if (index >= beacons.size())
                index = 0;

            beacons.get(index).readRemoteRssi();
            StringBuilder rssi = new StringBuilder();
            for (Beacon b : beacons) {
                rssi.append(b.rssi);
                rssi.append(",");
            }

            for (Beacon b : beacons) {
                rssi.append(b.age());
                rssi.append(",");
            }
            rssi.append(counter);


            String line = rssi.toString();
            mqttRssi.publish("cyber/rssi", line);

            StringBuilder connstat = new StringBuilder();
            for (Beacon b : beacons) {
                connstat.append(b.connected());
                connstat.append(",");
            }
            homeViewModel.mText.postValue(line + "\n" + connstat);

            //Log.i("TAG1", line);
            try {
                Thread.sleep(90);
            } catch (InterruptedException e) {

            }
        }
    }
}
