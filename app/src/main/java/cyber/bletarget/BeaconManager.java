package cyber.bletarget;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import cyber.bletarget.ui.home.HomeViewModel;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

public class BeaconManager {

    ArrayList<Beacon> beacons = new ArrayList<>();
    ConnectionManager connectionManager = new ConnectionManager(beacons);
    String BEN = "d5:61:6b:fb:8d:e3";
    String CARL = "d6:82:a5:47:bf:ac";
    String DAVE = "c5:7c:30:e4:a5:66";
    //    String FERRANTE = "f7:a2:7a:d2:40:1c";
    String SIMO = "f3:4e:e8:df:11:bc";
    String ANDREA = "dc:45:a3:e5:90:41";
    String ALE = "c2:e3:b0:2b:b9:bf";
    //    String WEI = "";
    List<String> names = Arrays.asList("BEN", "CARL", "DAVE", "SIMO", "ANDREA", "ALE");
    List<String> addresses = Arrays.asList(BEN, CARL, DAVE, SIMO, ANDREA, ALE);
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
        //connectBeaconsInternal();
        startScan();
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(final int callbackType, final ScanResult result) {
            // do nothing
        }

        @Override
        public void onBatchScanResults(final List<ScanResult> results) {

            for (final ScanResult result : results) {
                String line = "";
                try {
                    BluetoothDevice device = result.getDevice();
                    String name = result.getScanRecord() != null ? result.getScanRecord().getDeviceName() : "";
                    int rssi = result.getRssi();
                    String addr = device.getAddress();

                    line = addr + "," + rssi + "," + name + "," + result.getTimestampNanos() / 1000;
                    mqttRssi.publish("cyber/rssi", line);
                } catch (Exception ex) {
                    Log.e("TAG1", "exception!", ex);
                }
                Log.i("TAG1", line);
            }


        }

        @Override
        public void onScanFailed(final int errorCode) {
            // should never be called
        }
    };

    private void startScan() {
        mqttRssi.connect();
        final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        final ScanSettings settings = new ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(500).setUseHardwareBatchingIfSupported(false).build();
        final List<ScanFilter> filters = new ArrayList<>();
        ParcelUuid mUuid = new ParcelUuid(UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb"));
        filters.add(new ScanFilter.Builder().setServiceUuid(mUuid).build());
        scanner.startScan(filters, settings, scanCallback);

    }

    public synchronized void connectBeaconsInternal() {

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
        for (int i = 0; i < addresses.size(); i++) {
            String addr = addresses.get(i);
            String address = addr.toUpperCase();
            BluetoothDevice device = mBTAdapter.getRemoteDevice(address);
            Beacon beacon = new Beacon(addr, device, applicationContext, names.get(i));
            beacons.add(beacon);
        }

        int index = 0;
        long counter = 0;

        while (Thread.currentThread().isAlive()) {
            connectionManager.pollConnection();
            counter++;
            long curr = System.currentTimeMillis();

            index++;
            if (index >= beacons.size())
                index = 0;


            Beacon beacon = beacons.get(index);
            if (beacon.connected())
                beacon.readRemoteRssi();
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
                connstat.append(b.name);
                connstat.append(" ");
                connstat.append(b.connected() ? "C" : " ");
                connstat.append("\n");
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
