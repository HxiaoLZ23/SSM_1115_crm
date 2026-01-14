package com.from1115.crm.controller;
import  com.from1115.crm.common.Result;
import com.from1115.crm.dto.AccountDto;
import com.from1115.crm.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/getLogin.do")
    public Result getLogin(@Valid AccountDto accountDto, BindingResult br) {
        if (br.hasErrors()) {
            return Result.DATE_FORMAT_ERROR;
        }
        return accountService.findLoing(accountDto);
    }

    @GetMapping("/getAccountsByPage.do")
    public Result getAccountsByPage(
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return accountService.findAccountsByPage(pageNum, pageSize);
    }

    @DeleteMapping("/cutoneAccount.do")
    public Result cutoneAccount(Long id) {
        return accountService.removeAccountById(id);
    }
    @PutMapping("/editResetAccountPwd.do")
    public Result editResetAccountPwd(Long id) {
        return accountService.modifyResetAccountPwd(id);
    }


    @PutMapping("/addAccount.do")
    public Result addAccount(String userName) {
        return accountService.SaveAccount(userName);
    }


}