package cyber.bletarget;

public class Beacon  {
    public String name;
    String address;
    int rssi = 0;
    long millis = System.currentTimeMillis();

    public Beacon(String address, String name, int rssi) {
        this.address = address;
        this.name = name;
        this.rssi = rssi;
    }


    public long ageMillis() {
        return (System.currentTimeMillis() - millis) ;
    }

}
