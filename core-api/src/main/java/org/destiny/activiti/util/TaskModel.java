package org.destiny.activiti.util;

import lombok.Data;

import java.io.Serializable;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/22 15:27
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@Data
public class TaskModel implements Serializable {

    private String id;
    private String name;
    private String doUserId;
    private int type = 1;   // 默认 UserTask

    public TaskModel(String id, String name, String doUserId) {
        this.id = id;
        this.name = name;
        this.doUserId = doUserId;
    }
}
