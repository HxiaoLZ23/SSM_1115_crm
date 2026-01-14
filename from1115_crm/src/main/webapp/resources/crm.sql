create database crm character set utf8;
use crm;
create table account(
    id bigint(20) primary key auto_increment comment '主键',
    username varchar(30) comment '姓名',
    pwd varchar(40) comment '密码',
    img_url varchar(200) comment '头像地址',
    create_time datetime comment '注册时间',
    update_time datetime comment '更新时间'
);

create table dept(
    id bigint(20) primary key auto_increment comment '主键',
    name varchar(20) comment '部门名称',
    loc varchar(200) comment '部门地址',
	create_time datetime comment '创建时间',
    update_time datetime comment '更新时间'
);

create table user(
    id bigint(20) primary key auto_increment comment '主键',
    username varchar(20) comment '客户姓名',
    birthday datetime comment '生日',
    gender varchar(2) comment '1男 0女',
    tel varchar(20) comment '手机',
    sal float(10,2) comment '工资',
    profession varchar(2) comment '客户职业',
    address varchar(200) comment '客户住址',
    remark varchar(200) comment '备注',
    dept_id bigint(20) comment '部门编号',
	create_time datetime comment '创建时间',
    update_time datetime comment '更新时间'
);