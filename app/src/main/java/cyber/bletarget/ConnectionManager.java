package cyber.bletarget;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConnectionManager {
    private ArrayList<Beacon> beacons;
    long last = 0;
    int index = 0;

    public ConnectionManager(ArrayList<Beacon> beacons) {
        this.beacons = beacons;
    }

    public void pollConnection() {
        List<Beacon> connected = this.beacons.stream().filter(beacon1 -> beacon1.connected()).collect(Collectors.toList());
        if (connected.size() >= 2)
            return;

        if (System.currentTimeMillis() - last > 2000) {
            List<Beacon> disconnected = this.beacons.stream().filter(Beacon::connectable).collect(Collectors.toList());
            if (disconnected.size() > 0) {
                index++;
                if (index >= disconnected.size())
                    index = 0;
                Beacon beacon = disconnected.get(index);
                Log.i("TAG1", "Connecting to "+ beacon.name);
                beacon.connect();
                last = System.currentTimeMillis();
            }
        }
    }
}
