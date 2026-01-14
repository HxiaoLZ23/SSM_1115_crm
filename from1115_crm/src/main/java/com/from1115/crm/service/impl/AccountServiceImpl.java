package com.from1115.crm.service.impl;

import com.alibaba.druid.util.FnvHash;
import com.from1115.crm.common.Constants;
import com.from1115.crm.common.Result;
import com.from1115.crm.domain.Account;
import com.from1115.crm.dto.AccountDto;
import com.from1115.crm.mapper.AccountMapper;
import com.from1115.crm.service.AccountService;
import com.from1115.crm.utils.PwdUtil;
import com.from1115.crm.utils.WebScopeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import com.from1115.crm.utils.PwdUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {


    @Autowired
    private AccountMapper accountMapper;


    @Override
    public Result findLoing(AccountDto accountDto) {

        HttpSession session = WebScopeUtil.getSession();
        String code = (String) session.getAttribute(Constants.SESSION_USER_NAME);
        if (!accountDto.getCaptcha().equals(code)) {
            return new Result( -1,  "验证码有误");
        }
        String newPwd= PwdUtil.encyPwd(accountDto.getPwd());
        accountDto.setPwd(newPwd);
        Account account = accountMapper.Login(accountDto);
        if (account == null) {
            return new Result( -1,  "用户名或密码错误");
        }
        session.setAttribute(Constants.SESSION_USER_NAME,account.getUsername());
        return new Result(200,  "登录成功");
    }

    @Override
    public Result findAccountsByPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
       List<Account> accountList=          accountMapper.selectAccountsList();

        PageInfo<Account> pageInfo = new PageInfo<>(accountList);
        Result result = new Result();
        result.setTotal(pageInfo.getTotal());
        result.setData(pageInfo.getList());
        return result;

    }

    @Override
    public Result removeAccountById(long id) {

        if (id <= 0) {
            return Result.DATE_FORMAT_ERROR;
        }
        int row = accountMapper.deleteByPrimaryKey(id);
        if (row == 0) {
            return new Result( -1,  "删除失败");
        }


        return new Result();
    }

    @Override
    public Result modifyResetAccountPwd(long id) {
        if (id <= 0) {
            return Result.DATE_FORMAT_ERROR;
        }
        int row=accountMapper.updateResetAccountPwd(id);
        if (row == 0) {
            return new Result( -1,  "删除失败");
        }

        return null;
    }

    @Override
    public Result SaveAccount(String userName) {

        if (userName == null || !userName.matches("\\w{4,20}")) {
            return Result.DATE_FORMAT_ERROR;
        }
        Account account=            accountMapper.selectAccountByUserName(userName);
        if (account !=null){

            return new Result(-1,"对不起，已存在");

        }
        return new Result();



    }
}
