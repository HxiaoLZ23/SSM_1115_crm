package com.from1115.crm.mapper;

import com.from1115.crm.domain.Account;
import com.from1115.crm.dto.AccountDto;

import java.util.List;

/**
* @author Administrator
* @description 针对表【account】的数据库操作Mapper
* @createDate 2026-01-15 12:35:20
* @Entity com.from1115.crm.domain.Account
*/
public interface AccountMapper {

    int deleteByPrimaryKey(Long id);

    int insert(String userName);

    int insertSelective(Account record);

    Account selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Account record);

    int updateByPrimaryKey(Account record);


    Account Login(AccountDto accountDto);

List<Account>selectAccountsList();
 int updateResetAccountPwd(long id);
 Account selectAccountByUserName(String userName);




}
