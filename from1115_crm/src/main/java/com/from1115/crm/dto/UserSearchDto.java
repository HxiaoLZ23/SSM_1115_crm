package com.from1115.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserSearchDto extends BaseDto {
    /**
     * 客户姓名
     */
    @Pattern(regexp = "[\\u4e00-\\u9fa5]{2,10}",message = "请输入中文名称")
    private String username;

    /**
     * 手机
     */
    @Pattern(regexp = "[1][35678]\\d{9}",message = "手机号格式有误")
    private String tel;

    /**
     * 部门名称
     */
    @Pattern(regexp = ".{2,20}",message = "部门名称格式有误")
    private String deptName;

    /**
     * 1男0女
     */
    @NotNull(message = "请选择性别")
    @Pattern(regexp = "[10]",message = "性别格式有误")
    private String gender;
}
