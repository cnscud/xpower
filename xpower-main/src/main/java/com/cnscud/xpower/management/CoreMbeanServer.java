package com.cnscud.xpower.management;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cnscud.xpower.configcenter.SystemConfig;

public final class CoreMbeanServer {

    final static Logger log = LoggerFactory.getLogger(CoreMbeanServer.class);
    

    private MBeanServer mbserver = null;

    private static CoreMbeanServer instance = new CoreMbeanServer();

    private JMXConnectorServer connectorServer;

    private CoreMbeanServer() {
        initialize();
    }

    private void initialize() {
        if (mbserver != null && connectorServer != null && connectorServer.isActive()) {
            return;
        }
        String hostName = null;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            hostName = addr.getHostName();
        } catch (IOException e) {
            log.error("Get HostName Error", e);
            hostName = "localhost";
        }
        String host = System.getProperty("hostName", hostName);
        try {
            boolean enableJMX = SystemConfig.getInstance().getBoolean("suc.core.jmx", false);
            if (enableJMX) {
                mbserver = ManagementFactory.getPlatformMBeanServer();
                int port = SystemConfig.getInstance().getInt("suc.core.jmx.port", 7077);
                String rmiName = SystemConfig.getInstance().getString("suc.core.jmx.name", "sucJmx");
                Registry registry = null;
                try {
                    registry = LocateRegistry.getRegistry(port);
                    registry.list();
                } catch (Exception e) {
                    registry = null;
                }
                if (null == registry) {
                    registry = LocateRegistry.createRegistry(port);
                }
                registry.list();
                String serverURL = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/" + rmiName;
                JMXServiceURL url = new JMXServiceURL(serverURL);
                connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbserver);
                connectorServer.start();
                Runtime.getRuntime().addShutdownHook(new Thread() {

                    @Override
                    public void run() {
                        try {

                            if (connectorServer.isActive()) {
                                connectorServer.stop();
                                log.warn("JMXConnector stop");
                            }
                        } catch (IOException e) {
                            log.error("Shutdown MBean server error", e);
                        }
                    }
                });
                log.warn("jmx url: " + serverURL);
            }
        } catch (Exception e) {
            log.error("create MBServer error", e);
        }
    }

    public static CoreMbeanServer getInstance() {
        return instance;
    }

    public final void shutdown() {
        try {
            if (connectorServer != null && connectorServer.isActive()) {
                connectorServer.stop();
                log.warn("JMXConnector stop");
            }
        } catch (IOException e) {
            log.error("Shutdown MBean server error", e);
        }
    }

    public boolean isRegistered(String name) {
        try {
            return mbserver != null && mbserver.isRegistered(new ObjectName(name));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isActive() {
        return mbserver != null && connectorServer != null && connectorServer.isActive();
    }

    public int getMBeanCount() {
        if (mbserver != null) {
            return mbserver.getMBeanCount();
        } else {
            return 0;
        }
    }

    public void registMBean(Object o, String name) {
        if (isRegistered(name)) {
            return;
        }
        if (mbserver != null) {
            try {
                mbserver.registerMBean(o, new ObjectName(name));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void unregistMBean(String name) {
        if (isRegistered(name)) {
            if (mbserver != null) {
                try {
                    mbserver.unregisterMBean(new ObjectName(name));
                } catch (MBeanRegistrationException e) {
                    e.printStackTrace();
                } catch (InstanceNotFoundException e) {
                    e.printStackTrace();
                } catch (MalformedObjectNameException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}