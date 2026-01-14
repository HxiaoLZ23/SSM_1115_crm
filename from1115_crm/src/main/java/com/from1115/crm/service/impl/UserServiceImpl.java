package com.from1115.crm.service.impl;

import com.from1115.crm.common.Result;
import com.from1115.crm.domain.User;
import com.from1115.crm.dto.UserDto;
import com.from1115.crm.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.from1115.crm.mapper.UserMapper;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public Result saveUser(UserDto userDto) {
        // 查询部门是否存在
        int i = userMapper.selectDeptIsExist(userDto);
        if (i == 0){
            return new Result(-1, "对不起，部门不存在");
        }

        // 查询客户信息是否存在
        int j = userMapper.selectUserIsExist(userDto);
        if (j >= 1){
            return new Result(-1,"对不起，客户已经存在");
        }

        //添加新的客户
        int x = userMapper.insertUser(userDto);
        if (x == 0){
            return new Result(-1,"添加客户失败");
        }
        return new Result();
    }

    @Override
    public Result findUsersBypage(Integer pageNum, Integer pageSize) {
        //开启分页
        PageHelper.startPage(pageNum,pageSize);
        List<User> userList = userMapper.selectUsersBypage();
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        Result result = new Result();
        result.setTotal(pageInfo.getTotal());
        result.setData(pageInfo.getList());
        return result;
    }
}
