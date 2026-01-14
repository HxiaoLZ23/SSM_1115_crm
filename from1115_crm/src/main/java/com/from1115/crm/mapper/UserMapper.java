package com.from1115.crm.mapper;

import com.from1115.crm.domain.User;
import com.from1115.crm.dto.UserDto;
import com.from1115.crm.dto.UserSearchDto;

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


    /**
     * 根据id删除单个客户信息
     * @param id 被删除的客户id
     * @return 受影响的行数
     */
    int deleteUserById(Long id);

    /**
     * 根据ID删除多个用户
     * @param id 多个ID
     * @return 受影响的行数
     */
    int deleteManyUser(String id);

    /**
     * 动态搜索客户名称
     * @param userSearchDto 搜索参数
     * @return
     */
    List<User> selectUserBySearch(UserSearchDto userSearchDto);

    int updateUserById(UserDto userDto);

}
