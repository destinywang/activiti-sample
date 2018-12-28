package org.destiny.model;

import lombok.Data;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2018-12-26 16:11
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2018
 */
@Data
public class DeliveryHistory {

    private Long id;
    private String taskId;      // 关联 Task 表 id
    private String srcAssignee; // 转交人
    private String dstAssignee; // 被转交人
    private String srcTaskId;   // 原始 taskId (涉及多次转交, 原始 TaskId 相同)
    private Long createTime;    // 转交时间

}
