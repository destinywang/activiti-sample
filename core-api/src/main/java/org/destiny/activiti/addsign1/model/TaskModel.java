package org.destiny.activiti.addsign1.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2019-03-04 18:19
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2019
 */
@Data
public class TaskModel implements Serializable {

    private String id;
    private String name;
    private String assignee;    // 处理人
    private int type = 1;       // 任务类型, 1-任务节点
}
