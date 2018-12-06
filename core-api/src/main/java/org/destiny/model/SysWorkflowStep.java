package org.destiny.model;

import lombok.Data;

import java.util.Date;

public class SysWorkflowStep {

    private Long id;
    private Date createTime;
    private Long workflowId;
    private String rolePkno;    // 该步骤的审核角色
    private Integer type;       // 1-会签, 2-普通流转


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    public String getRolePkno() {
        return rolePkno;
    }

    public void setRolePkno(String rolePkno) {
        this.rolePkno = rolePkno;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
