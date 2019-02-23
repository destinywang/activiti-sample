-- auto-generated definition
create table ACT_CREATION
(
  ID                    int auto_increment primary key,
  PROCESS_DEFINITION_ID varchar(255) null comment '流程定义 id',
  DOUSERID              varchar(255) null comment '操作人 id',
  ACT_ID                varchar(255) null,
  PROCESS_INSTANCE_ID   varchar(255) default '0'               not null comment '流程实例 id',
  PROPERTIES_TEXT       varchar(2000) null comment '参数',
  STATE_                tinyint      default '0'               not null comment '0 有效 1 无效',
  create_time           timestamp    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
)
  engine = InnoDB;

CREATE TABLE `ACT_HI_DELIVERY`
(
  `id`          bigint(20) NOT NULL AUTO_INCREMENT,
  `taskId`      varchar(255) NOT NULL DEFAULT '' COMMENT '关联 ACT_RU_TASK 表 id',
  `srcAssignee` varchar(255) NOT NULL DEFAULT '' COMMENT '转交人',
  `dstAssignee` varchar(255) NOT NULL DEFAULT '' COMMENT '被转交人',
  `srcTaskId`   varchar(255)          DEFAULT '' COMMENT '原始 TaskId, 涉及多次转交时, 该字段始终表示第一次转交的 TaskId',
  `createTime`  bigint(20) NOT NULL DEFAULT '0' COMMENT '转交时间',
  PRIMARY KEY (`id`),
  KEY           `idx_task_id` (`taskId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='转交历史表, 原始信息需要回到 task 表中去查询'