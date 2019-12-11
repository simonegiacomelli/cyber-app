package cyber.bletarget;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    HashMap<String, String> address2name = new HashMap<>();
    //    List<String> addresses = Arrays.asList(BEN);
    private BluetoothAdapter mBTAdapter;
    private Context applicationContext;
    private HomeViewModel homeViewModel;
    private Thread thread;

    public BeaconManager(Context applicationContext, HomeViewModel homeViewModel) {
        this.applicationContext = applicationContext;
        this.homeViewModel = homeViewModel;
        for (int i = 0; i < addresses.size(); i++) {
            address2name.put(addresses.get(i), names.get(i));
        }
    }

    public synchronized void connectBeacons() {
        //connectBeaconsInternal();
        //startScan();
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
                    homeViewModel.mText.postValue(line);



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



    MqttRssi mqttRssi = new MqttRssi();


}

