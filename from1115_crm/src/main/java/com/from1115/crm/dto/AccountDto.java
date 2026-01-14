package com.from1115.crm.dto;
import  lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Pattern;

@Data
public class AccountDto {
    @NonNull
    @Pattern(regexp = "\\w{4,20}")
    private  String username ;
    @NonNull
    @Pattern(regexp = "\\d{6}")
    private  String pwd ;
    @NonNull
    @Pattern(regexp = "[0-9a-zA-Z]{4}")
    private  String captcha;


}
