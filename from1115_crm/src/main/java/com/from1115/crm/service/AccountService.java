package com.from1115.crm.service;

import  com.from1115.crm.common.Result;
import com.from1115.crm.dto.AccountDto;
public interface AccountService {

    Result findLoing(AccountDto accountDto);

Result findAccountsByPage(Integer pageNum,Integer pageSize);

Result removeAccountById(long id);

Result modifyResetAccountPwd(long id);
Result SaveAccount(String userName);
    }



















