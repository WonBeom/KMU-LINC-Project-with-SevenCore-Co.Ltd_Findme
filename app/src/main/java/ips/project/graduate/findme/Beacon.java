package ips.project.graduate.findme;

import java.util.HashMap;

/*the class which contains geo-local information of beacon devices*/
public class Beacon {
    private static HashMap<String, Coord> deviceInfo = new HashMap<>();

    static {
        //TODO WRITE CODE : PARSE HASH TABLE FROM FILE.
        //TODO IN TEST, USE STATIC HASH TABLE (NOT FROM FILE.)

        deviceInfo.put("E3:1C:E5:65:1C:D4", new Coord(37.510226, 127.038467)); // left down
        deviceInfo.put("FD:C4:09:46:2D:CD", new Coord(37.5102439, 127.0385442)); // right down

        deviceInfo.put("F5:09:B6:45:B7:53", new Coord(37.5102675, 127.0384435)); // left top
        deviceInfo.put("E7:A5:1B:2E:4D:0B", new Coord(37.5102854, 127.0385207)); // right top

        //deviceInfo.put("D0:5F:B8:17:E8:EF", new Coord(-1, -1));
        //deviceInfo.put("D0:5F:B8:17:E5:F4", new Coord(-1, 1));
    }

    public static Coord lookup(String string) {
        return deviceInfo.get(string);
    }

    public static boolean existKey(String string) {
        return deviceInfo.containsKey(string);
    }
}
