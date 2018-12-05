package org.destiny.activiti.model;

import lombok.Data;

@Data
public class ProcessModel {

    private String id;
    private String deploymentId;
    private String key;
    private String resourceName;
    private int version;
    private String diagramResourceName;

}
