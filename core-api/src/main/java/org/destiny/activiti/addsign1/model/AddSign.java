package org.destiny.activiti.addsign1.model;

import lombok.Data;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2019-03-04 20:29
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2019
 */
@Data
public class AddSign {

    private long id;
    private String processDefinitionId;
    private String assignee;
    private String activityId;
    private String processInstanceId;
    private String propertiesText;
    private int state;
    private long createTime;

}
