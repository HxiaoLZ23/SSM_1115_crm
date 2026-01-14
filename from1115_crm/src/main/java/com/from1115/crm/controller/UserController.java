package com.from1115.crm.controller;

import com.from1115.crm.common.Result;
import com.from1115.crm.dto.UserDto;
import com.from1115.crm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/addUser.do")
    public Result addUser(@Valid UserDto userDto, BindingResult br){
        Result result = new Result();
        if (br.hasErrors()){
            result.setCode(-1);
            result.setMsg(br.getFieldError().getDefaultMessage());
            return result;
        }

        return userService.saveUser(userDto);
    }
    @GetMapping("/getUsersByPage.do")
    public Result getUsersBypage(@RequestParam(required = false,defaultValue = "1") Integer pageNum,@RequestParam(required = false,defaultValue = "10") Integer pageSize){

        return userService.findUsersBypage(pageNum,pageSize);
    }
}
