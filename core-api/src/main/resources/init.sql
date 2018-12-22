-- auto-generated definition
create table ACT_CREATION
(
  ID int auto_increment primary key,
  PROCESS_DEFINITION_ID varchar(255) null comment '流程定义 id',
  DOUSERID varchar(255) null comment '操作人 id',
  ACT_ID varchar(255) null,
  PROCESS_INSTANCE_ID varchar(255) default '0' not null comment '流程实例 id',
  PROPERTIES_TEXT varchar(2000) null comment '参数',
  STATE_ tinyint default '0' not null comment '0 有效 1 无效',
  create_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
)
engine = InnoDB;