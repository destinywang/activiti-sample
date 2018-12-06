package org.destiny.model;

import lombok.Data;

import java.util.Date;

public class SysWorkflow {

    private Long id;
    private Date createTime;
    private String name;        // 工作流名称
    private String content;     // 工作流描述
    private Integer skipLevel;


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getSkipLevel() {
        return skipLevel;
    }

    public void setSkipLevel(Integer skipLevel) {
        this.skipLevel = skipLevel;
    }
}
