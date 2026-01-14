package com.from1115.crm.mapper;

import com.from1115.crm.domain.User;
import com.from1115.crm.dto.UserDto;

import java.util.List;

/**
* @author 86156
* @description 针对表【user】的数据库操作Mapper
* @createDate 2026-01-13 22:33:20
* @Entity com.com.com.com.com.from1115.crm.domain.User
*/
public interface UserMapper {

    /* 查询部门是否存在*/
    int selectDeptIsExist(UserDto userDto);

    /* 查询客户是否已经添加*/
    int selectUserIsExist(UserDto userDto);

    /*添加客户信息*/
    int insertUser(UserDto userDto);

    /*根据ID查询客户信息*/
    User selectByPrimaryKey(Long id);

    /*结合PageHelper分页查询客户信息*/
    List<User> selectUsersBypage();



}
