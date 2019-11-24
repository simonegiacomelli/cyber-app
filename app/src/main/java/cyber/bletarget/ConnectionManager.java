package cyber.bletarget;

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
        if (System.currentTimeMillis() - last > 1000) {
            List<Beacon> disconnected = this.beacons.stream().filter(Beacon::connectable).collect(Collectors.toList());
            if (disconnected.size() > 0) {
                index++;
                if (index >= disconnected.size())
                    index = 0;
                Beacon beacon = disconnected.get(index);
                beacon.connect();
                last = System.currentTimeMillis();
            }
        }
    }
}
