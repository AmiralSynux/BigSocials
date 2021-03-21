package socialNetwork.utils;


import java.time.LocalTime;
import java.util.Map;
import java.util.TreeMap;

public class MyTimer{
    private final Map<String, LocalTime> map;

    public MyTimer(){
        map = new TreeMap<>();
    }

    /**
     * starts the timer for a certain key
     * @param key - the key used for identifying the timer. It is also used to stop the timer.
     */
    public void start(String key){
        map.put(key,LocalTime.now());
    }

    /**
     * displays the key, start time, end time and the duration of it.
     * the duration is calculated by the formula: stop - start
     * the key is also removed.
     * @param key - the key for which the timer stops
     */
    public void stopLong(String key){
        LocalTime endT = LocalTime.now();
        LocalTime startT = map.get(key);
        System.out.println(" - - - - - " + key + " - - - - - ");
        System.out.println("Start: " + startT);
        System.out.println("End: " + endT);
        endT = endT.minusNanos(startT.getNano());
        endT = endT.minusSeconds(startT.getSecond());
        endT = endT.minusMinutes(startT.getMinute());
        endT = endT.minusHours(startT.getHour());
        System.out.println("Duration: " + endT);
        System.out.println("-------------------------------------------------");
        map.remove(key);
    }

    /**
     * displays the key and the duration of it.
     * the duration is calculated by the formula: stop - start
     * the key is also removed.
     * @param key - the key for which the timer stops
     */
    public void stop(String key){
        LocalTime endT = LocalTime.now();
        LocalTime startT = map.get(key);
        endT = endT.minusNanos(startT.getNano());
        endT = endT.minusSeconds(startT.getSecond());
        endT = endT.minusMinutes(startT.getMinute());
        endT = endT.minusHours(startT.getHour());
        System.out.println(key + " -> " + endT);
        map.remove(key);
    }
}

