package org.destiny.activiti.addsign1;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.destiny.activiti.addsign1.model.AddSign;

import java.util.List;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2019-03-04 20:28
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2019
 */
public interface AddSignMapper {

    @Select("select * from ACT_ADD_SIGN where STATE_ = 0")
    @Results({
            @Result(property = "id", column = "ID_"),
            @Result(property = "processDefinitionId", column = "PROCESS_DEFINITION_ID_"),
            @Result(property = "assignee", column = "ASSIGNEE_"),
            @Result(property = "processInstanceId", column = "PROCESS_INSTANCE_ID_"),
            @Result(property = "propertiesText", column = "PROPERTIES_TEXT_"),
            @Result(property = "state", column = "STATE_"),
            @Result(property = "createTime", column = "CREATE_TIME_"),
    })
    List<AddSign> find();

    @Insert("insert into ACT_ADD_SIGN(PROCESS_DEFINITION_ID_, PROCESS_INSTANCE_ID_, PROPERTIES_TEXT_, STATE_, CREATE_TIME_) values(#{processDefinitionId}, #{processInstanceId}, #{propertiesText}, 0, #{createTime})")
    int insert(AddSign addSign);

    @Select("select * from ACT_ADD_SIGN where STATE_ = 0 AND PROCESS_DEFINITION_ID_ = #{procDefId}")
    @Results({
            @Result(property = "id", column = "ID_"),
            @Result(property = "processDefinitionId", column = "PROCESS_DEFINITION_ID_"),
            @Result(property = "assignee", column = "ASSIGNEE_"),
            @Result(property = "processInstanceId", column = "PROCESS_INSTANCE_ID_"),
            @Result(property = "propertiesText", column = "PROPERTIES_TEXT_"),
            @Result(property = "state", column = "STATE_"),
            @Result(property = "createTime", column = "CREATE_TIME_"),
    })
    List<AddSign> findByProcDefId(String procDefId);
}
