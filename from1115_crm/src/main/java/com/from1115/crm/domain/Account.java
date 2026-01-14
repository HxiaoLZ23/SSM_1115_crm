package com.from1115.crm.domain;

import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName account
 */
@Data
public class Account {
    /**
     * 主键
     */
    private Long id;

    /**
     * 姓名
     */
    private String username;

    /**
     * 密码
     */
    private String pwd;

    /**
     * 头像地址
     */
    private String imgUrl;

    /**
     * 注册时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}