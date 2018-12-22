package org.destiny.activiti.util;

import lombok.Data;

import java.sql.Date;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/22 15:50
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@Data
public class ActCreation {

    private Integer id;
    private String processDefinitionId;
    private String doUserId;
    private String actId;
    private String processInstanceId;
    private String propertiesText;
    private Integer state;
    private Date createTime;
}
