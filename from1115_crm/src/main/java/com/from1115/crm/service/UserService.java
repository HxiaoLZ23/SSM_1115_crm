package com.from1115.crm.service;
import com.from1115.crm.common.Result;
import com.from1115.crm.dto.UserDto;
public interface UserService {

    public Result saveUser(UserDto userDto);


    /*分页查询客户信息
       * @param pageNum  页码
        *@param pageSize  每页显示的条数
        *@return 分页信息
  */
    Result findUsersBypage(Integer pageNum,Integer pageSize);
}


