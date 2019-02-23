package org.destiny.activiti.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.destiny.model.DeliveryHistory;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2018-12-27 16:02
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2018
 */
@Mapper
public interface DeliveryHistoryMapper {

    @Insert("INSERT INTO ACT_HI_DELIVERY(taskId, srcAssignee, dstAssignee, srcTaskId, createTime) VALUES(#{taskId}, #{srcAssignee}, #{dstAssignee}, #{srcTaskId}, #{createTime})")
    int insert(DeliveryHistory deliveryHistory);

    @Select("SELECT taskId, srcAssignee, dstAssignee, srcTaskId, createTime FROM ACT_HI_DELIVERY where taskId = #{taskId}")
    DeliveryHistory findByTaskId(String taskId);
}
