/**
 * $Id: Main.java 1331 2012-04-01 08:39:59Z adyliu $
 * (C)2011 Sohu Inc.
 */
package com.cnscud.xpower.ddd.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.IllegalAddException;
import org.dom4j.io.SAXReader;

import com.cnscud.xpower.configcenter.ConfigCenter;
import com.cnscud.xpower.ddd.importer.Task.TaskType;
import com.cnscud.xpower.ddd.schema.Instance;
import com.cnscud.xpower.ddd.schema.SchemaInstanceHelper;
import com.cnscud.xpower.ddd.schema.Type;

/**
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-6-14
 */
public class Main {

    private SAXReader reader = new SAXReader();

    private final File file;

    private final Map<String, Instance> data = new LinkedHashMap<String, Instance>();

    private String namespace;

    private boolean force = false;

    final Set<Task> tasks = new TreeSet<Task>();

    final List<String> supportedNamespaces = Arrays.asList("/xpower/ddd");

    /** delete old configuration */
    final static boolean realDelete = Boolean.getBoolean("realDelete");

    private Main(File file) {
        this.file = file;
    }

    private String name2Path(String name) {
        return namespace + "/" + name;
    }

    public void parse() throws Exception {
        Document doc = reader.read(file);
        Element root = doc.getRootElement();
        parseElement(root);
    }

    public void buildTasks() throws Exception {
        // read instance from zookeeper
        final Map<String, Instance> dbInstances = new LinkedHashMap<String, Instance>();
        for (String name : ConfigCenter.getInstance().getChildren(namespace)) {
            final String fullpath = name2Path(name);
            String oldData = ConfigCenter.getInstance().getDataAsString(fullpath);
            try {
                Instance oldInstance = SchemaInstanceHelper.unserialize(oldData);
                dbInstances.put(name, oldInstance);
            } catch (RuntimeException ex) {
                throw new IllegalStateException("[" + fullpath + "] parse failed:" + ex.getMessage(), ex);
            }
        }
        for (Map.Entry<String, Instance> e : data.entrySet()) {
            Instance newInstance = e.getValue();
            Instance oldInstance = dbInstances.remove(newInstance.getName());
            tasks.add(new Task(newInstance.getName(), oldInstance, newInstance, force));
        }
        for (Map.Entry<String, Instance> ed : dbInstances.entrySet()) {
            tasks.add(new Task(ed.getKey(), ed.getValue(), null, false));
        }
        log("================================");
        TaskType lastTaskType = null;
        for (Task task : tasks) {
            final Instance oldInstance = task.oldInstance;
            final Instance instance = task.newInstance;
            String message = "";
            switch (task.taskType) {
            case NONE:
                message = (String.format("[none] instance '%s' not update! old time=%s, new time=%s.",//
                        instance.getName(), getTimeDesc(oldInstance.getUpdateTime()),//
                        getTimeDesc(instance.getUpdateTime())));
                break;
            case UPDATE:
                final int m = Math.max("[update]".length(), "<old>".length());
                final int n = Math.max(instance.getName().length(), "==>".length());
                String fx = "%<m>s %<n>s: %s\n%<m>s %<n>s: %s".replaceAll("<m>", "" + m).replaceAll("<n>", "" + n);
                message = String.format(fx, "[update]", instance.getName(), instance,//
                        "<old>", "==>", oldInstance//
                        );
                break;
            case CREATE:
                message = (String.format("[create] %s: %s", instance.getName(), instance));
                break;
            case DELETE:
                message = (String.format("[delete] %s: %s", oldInstance.getName(), oldInstance));
                break;
            default:
                break;
            }
            if (lastTaskType != task.taskType) {
                log("");
                lastTaskType = task.taskType;
            }
            log(message);
        }
    }

    @SuppressWarnings("unchecked")
    private void parseElement(Element root) {
        String nodeName = root.getName();
        if ("instances".equals(nodeName)) {
            namespace = root.attributeValue("namespace");
            for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
                parseElement(it.next());
            }
        } else if ("instance".equals(nodeName)) {
            Instance instance = buildInstance(root);
            if (data.containsKey(instance.getName())) {
                throw new IllegalAddException("duplicate instance: " + instance.getName());
            }
            data.put(instance.getName(), instance);
        } else {
            throw new IllegalArgumentException("unknow element:" + root.getName() + ", path:" + root.getPath());
        }
    }

    private String getTimeDesc(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time));
    }

    @SuppressWarnings("unchecked")
    private Instance buildInstance(Element e) {
        Instance instance = new Instance();
        instance.setName(e.attributeValue("name"));
        instance.setType("router".equals(e.attributeValue("type")) ? Type.ROUTE : Type.CLUSTER);
        long updatetime = 0;
        final String supdate = e.attributeValue("updatetime");
        try {
            updatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(supdate).getTime();
        } catch (Exception ex) {
            log(String.format("[ERROR] updatetime [%s] is error for '%s', format is '%s'.", supdate, instance.getName(), "yyyy-MM-dd HH:mm:ss"));
            System.exit(0);
        }
        instance.setUpdateTime(updatetime);
        for (Iterator<Element> it = e.elementIterator(); it.hasNext();) {
            Map<String, String> arg = buildArgs(it.next(), instance.getName());
            if (arg != null) {
                instance.addToParams(arg);
            }
        }
        return instance;
    }

    private static void log(String msg) {
        final PrintStream out = System.out;//必须的，这是一个控制台程序
        out.println(msg);
    }

    private Map<String, String> buildArgs(Element s, String instanceName) {
        if ("server".equals(s.getName())) {
            return buildServer(s, instanceName);
        }
        if ("route".equals(s.getName())) {
            return buildOptionRouter(s, instanceName);
        }
        return null;
    }

    void checkArg(String instanceName, Map<String, String> args, String... argsName) {
        for (String argName : argsName) {
            if (!args.containsKey(argName)) {
                throw new IllegalArgumentException(argName + " must not be null for instance: " + instanceName);
            }
        }
    }

    String value(Element s, String name, String defaultValue) {
        String v = s.attributeValue(name);
        return v != null ? v.trim() : defaultValue;
    }

    String value(Element s, String name) {
        return value(s, name, "");
    }

    void addArg(Map<String, String> args, Element s, String name, String defaultValue) {
        String value = value(s, name, defaultValue);
        if (value != null) {
            args.put(name, value);
        }
    }

    private Map<String, String> buildServer(Element s, String instanceName) {
        Map<String, String> args = new HashMap<String, String>();
        //
        addArg(args, s, "type", null);
        addArg(args, s, "database", null);
        addArg(args, s, "host", null);
        addArg(args, s, "port", null);
        addArg(args, s, "user", null);
        addArg(args, s, "password", null);
        addArg(args, s, "rw", null);
        addArg(args, s, "charset", null);
        addArg(args, s, "min", "1");
        addArg(args, s, "max", "100");
        addArg(args, s, "connectTimeout", "5000");
        addArg(args, s, "socketTimeout", "1800000");
        //
        addArg(args, s, "url", null);// for hsql, oracle
        addArg(args, s, "validationquery", null);
        //
        checkArg(instanceName, args, "type", "database", "host", "rw", "charset");
        return args;
    }

    private boolean checkNamespace() {
        for (String supportNamespace : supportedNamespaces) {
            if (supportNamespace.equals(this.namespace)) {
                return true;
            }
        }
        return false;
    }

    private Map<String, String> buildOptionRouter(Element s, String instanceName) {
        Map<String, String> args = new HashMap<String, String>();
        addArg(args, s, "instance", null);
        addArg(args, s, "expression", null);
        checkArg(instanceName, args, "instance", "expression");
        return args;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            log("Usage: <file>");
            log("Usage: <file> [--force]");
            System.exit(1);
        }
        System.setProperty("config.product", "true");
        File file = new File(args[0]);
        if (!file.exists()) {
            log("NOT FOUND FILE: " + file.getCanonicalPath());
            System.exit(3);
        }
        log("update ddd from file: " + file.getCanonicalPath());
        Main main = new Main(file);
        main.parse();
        main.force = args.length > 1 && "--force".equals(args[1]);
        if (!main.checkNamespace()) {
            log("check your namespace from file:" + main.namespace);
            System.exit(4);
        }
        final String nameSpaceMsg = "Check your namespace, your setting: %s (y/n)";
        if (!confirm(String.format(nameSpaceMsg, main.namespace))) {
            log("Cancel.");
            System.exit(5);
        }
        main.buildTasks();
        if (!confirm("Are you sure to do this?(y/n): ")) {
            log("Cancel.");
            System.exit(6);
        }
        main.updateTasks();
        log("DONE");
    }

    private void updateTasks() throws Exception {
        for (Task task : tasks) {
            if (task.taskType == TaskType.CREATE) {
                ConfigCenter.getInstance().createPath(name2Path(task.name), SchemaInstanceHelper.serialize(task.newInstance));
            } else if (task.taskType == TaskType.UPDATE) {
                ConfigCenter.getInstance().updateData(name2Path(task.name), SchemaInstanceHelper.serialize(task.newInstance));
            } else if (task.taskType == TaskType.DELETE && realDelete) {
                ConfigCenter.getInstance().deletePath(name2Path(task.name));
            }
        }
    }

    private static boolean confirm(String message) {
        log(message);
        String line = null;
        try {
            if (null != System.console()) {
                line = System.console().readLine();
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        line = line != null ? line.toLowerCase().trim() : "";
        return "y".equals(line) || "yes".equals(line);
    }
}
