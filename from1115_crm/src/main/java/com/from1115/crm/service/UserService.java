package com.from1115.crm.service;
import com.from1115.crm.common.Result;
import com.from1115.crm.dto.UserDto;
import com.from1115.crm.dto.UserSearchDto;

public interface UserService {

    public Result saveUser(UserDto userDto);


    /**分页查询客户信息
       * @param pageNum  页码
        *@param pageSize  每页显示的条数
        *@return 分页信息
    */
    Result findUsersBypage(Integer pageNum,Integer pageSize);

    /**
     * 根据ID删除单个客户的信息
     * @param id
     * @return
     */
    Result removeUserById(Long id);

    /**
     * 根据ID批量删除客户信息
     * @param id
     * @return
     */
    Result removeManyUser(String id);

    /**
     * 动态搜索客户信息
     * @param userSearchDto
     * @return
     */
    Result findUserBySearch(UserSearchDto userSearchDto);

    /**
     * 根据id修改客户信息
     * @param userDto
     * @return
     */
    Result modifyUserById(UserDto userDto);
}


