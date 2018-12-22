package org.destiny.activiti.util;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/22 15:34
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@Data
public class TmpActivityModel implements Serializable {

    private String activityIds; // 加签的节点 Id, 逗号分隔
    private String start;
    private String end;
    private List<TaskModel> activity;

}
