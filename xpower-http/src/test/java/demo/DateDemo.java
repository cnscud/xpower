package demo;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2016年2月23日
 */
public class DateDemo {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Calendar c = Calendar.getInstance();
        c.set(1970, 0, 1, 0, 0, 0);
        System.out.println(c.getTimeInMillis()/3600/1000.0);
        System.out.println("------------------------------------------------------");
        // Los_Angeles -8 hours
        ZonedDateTime zdt = ZonedDateTime.of(2016, 2, 25, 9, 0, 0, 0, ZoneId.of("America/Los_Angeles"));
        //
        System.out.println(zdt);
        System.out.println(zdt.toEpochSecond());
        System.out.println(zdt.toLocalDateTime());
        //
        System.out.println("------------------------------------------------------");
        //
        zdt = ZonedDateTime.now().withMinute(0).withSecond(0);
        System.out.println(zdt);
        System.out.println(zdt.toEpochSecond());
        System.out.println(zdt.toLocalDateTime());
        System.out.println("------------------------------------------------------");
        zdt = ZonedDateTime.of(2016, 2, 22, 19, 0, 0, 0, ZoneId.of("America/Los_Angeles"));
        //
        System.out.println(zdt);
        System.out.println(zdt.toEpochSecond());
        System.out.println(zdt.toLocalDateTime());
        //
        System.out.println("------------------------------------------------------");
        /*
         * https://maps.googleapis.com/maps/api/timezone/json?location=41.380936,2.12288&timestamp=1456308000&sensor=false
         {
   "dstOffset" : 0,
   "rawOffset" : 3600,
   "status" : "OK",
   "timeZoneId" : "Europe/Madrid",
   "timeZoneName" : "Central European Standard Time"
}
         */
        c = Calendar.getInstance();
        c.set(2016, 1, 25, 0, 0, 0); //2016-02-25 09:00:00
        c.add(Calendar.SECOND, (9+8)*3600);
        c.add(Calendar.SECOND, -0);
        c.add(Calendar.SECOND, -3600);
        //
        System.out.println(c.getTimeInMillis() / 1000);
        zdt = ZonedDateTime.of(2016, 2,25,9,0,0,0,ZoneId.of("Europe/Madrid")); //2016-02-25 09:00:00
        System.out.println(zdt.toEpochSecond());
    }

}
