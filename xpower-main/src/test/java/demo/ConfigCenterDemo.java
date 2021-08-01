/**
 * 
 */
package demo;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import com.cnscud.xpower.configcenter.ConfigCenter;
import com.cnscud.xpower.configcenter.GlobalConfig;
import com.cnscud.xpower.configcenter.GlobalConfig.GlobalConfigKey;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2015年5月19日
 */
public class ConfigCenterDemo {

    /**
     * @param args
     */
    public static void main(String[] args) {

        System.out.println(ConfigCenter.isProduct());
        GlobalConfig gc = ConfigCenter.getGlobalConfig();
        for (int i = 0; i < 30; i++) {
            System.out.println(gc.is(GlobalConfigKey.product, false) + " -> " + gc.get(GlobalConfigKey.passport_maxLoginCountPerHour, 10));
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
        }
    }

}
