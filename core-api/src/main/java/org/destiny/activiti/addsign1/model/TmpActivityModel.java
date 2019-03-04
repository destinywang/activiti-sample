package org.destiny.activiti.addsign1.model;

import lombok.Data;
import org.destiny.activiti.addsign1.model.TaskModel;

import java.io.Serializable;
import java.util.List;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2019-03-04 18:36
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2019
 */
@Data
public class TmpActivityModel implements Serializable {
    private String activityIds;     // 加签的节点id, 多个的话逗号分隔
    private String firstId;
    private String lastId;
    private List<TaskModel> activityList;
}
