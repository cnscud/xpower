/**
 * 
 */
package com.cnscud.xpower.configcenter;

import com.github.zkclient.IZkChildListener;
import com.github.zkclient.IZkDataListener;
import com.github.zkclient.IZkStateListener;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import java.util.List;

/**
 * Zookeeper简单的状态监听
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2015年3月17日
 */
@FunctionalInterface
public interface IZkHandler extends IZkDataListener, IZkChildListener, IZkStateListener {
    @Override
    default void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {
        handle();
    }

    @Override
    default void handleDataChange(String dataPath, byte[] data) throws Exception {
        handle();
    }

    @Override
    default void handleDataDeleted(String dataPath) throws Exception {
        handle();
    }

    @Override
    default void handleNewSession() throws Exception {
        handle();
    }

    @Override
    default void handleStateChanged(KeeperState state) throws Exception {
        handle();
    }

    /**
     * 监听状态，可以是各种数据变化，如果需要自行判断
     * 
     * @throws Exception
     *             任何异常
     */
    void handle() throws Exception;

}
