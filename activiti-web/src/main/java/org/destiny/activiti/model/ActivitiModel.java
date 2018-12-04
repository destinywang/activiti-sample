package org.destiny.activiti.model;

import lombok.Data;

import java.util.Date;

@Data
public class ActivitiModel {

    private String id;
    private String name;
    private String key;
    private String description;
    private Date createTime;
    private Date lastUpdateTime;
    private int version;

}
