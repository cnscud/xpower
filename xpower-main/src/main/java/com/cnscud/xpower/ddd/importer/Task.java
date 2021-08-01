/**
 * $Id: Task.java 1331 2012-04-01 08:39:59Z adyliu $
 * (C)2011 Sohu Inc.
 */
package com.cnscud.xpower.ddd.importer;

import com.cnscud.xpower.ddd.schema.Instance;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-8-2
 */
public class Task implements Comparable<Task> {

    public static enum TaskType {
        NONE, //
        UPDATE, //
        CREATE, //
        DELETE;
    }

    final String name;

    final Instance oldInstance;

    final Instance newInstance;

    final TaskType taskType;

   
    public Task(String name, Instance oldInstance, Instance newInstance, boolean force) {
        this.name = name;
        this.oldInstance = oldInstance;
        this.newInstance = newInstance;
        if (null == oldInstance) {
            this.taskType = TaskType.CREATE;
        } else if (null == newInstance) {
            this.taskType = TaskType.DELETE;
        } else if (oldInstance.getUpdateTime() < newInstance.getUpdateTime() || force) {
            this.taskType = TaskType.UPDATE;
        } else {
            this.taskType = TaskType.NONE;
        }
    }

    public int compareTo(Task o) {
        int t = this.taskType.compareTo(o.taskType);
        if (t == 0) {
            t = this.name.compareTo(o.name);
        }
        return t;
    }

}
