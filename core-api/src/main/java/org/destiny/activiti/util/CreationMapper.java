package org.destiny.activiti.util;

import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * create table ACT_CREATION
 * (
 *   ID int auto_increment primary key,
 *   PROCESS_DEFINITION_ID varchar(255) null comment '流程定义 id',
 *   DOUSERID varchar(255) null comment '操作人 id',
 *   ACT_ID varchar(255) null,
 *   PROCESS_INSTANCE_ID varchar(255) default '0' not null comment '流程实例 id',
 *   PROPERTIES_TEXT varchar(2000) null comment '参数',
 *   STATE_ tinyint default '0' not null comment '0 有效 1 无效',
 *   create_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
 * )
 * engine = InnoDB;
 * ------------------------------------------------------------------
 * design by 2018/12/22 15:50
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@Mapper
public interface CreationMapper {

    @Select("select * from act_creation where STATE_ = 0")
    @Results({
            @Result(property = "id", column = "ID"),
            @Result(property = "processDefinitionId", column = "PROCESS_DEFINITION_ID"),
            @Result(property = "doUserId", column = "DOUSERID"),
            @Result(property = "actId", column = "ACT_ID"),
            @Result(property = "processInstanceId", column = "PROCESS_INSTANCE_ID"),
            @Result(property = "propertiesText", column = "PROPERTIES_TEXT"),
            @Result(property = "state", column = "STATE_"),
            @Result(property = "createTime", column = "create_time"),
    })
    List<ActCreation> find();

    @Insert("insert into act_creation(PROCESS_DEFINITION_ID, PROCESS_INSTANCE_ID, PROPERTIES_TEXT, create_time) values(#{processDefinitionId}, #{processInstanceId}, #{propertiesText}, #{createTime})")
    int insert(ActCreation actCreation);
}
