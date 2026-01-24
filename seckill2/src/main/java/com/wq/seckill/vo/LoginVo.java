package com.wq.seckill.vo;


import com.wq.seckill.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
//接收用户登录时 发送的信息 id 和 password
public class LoginVo {

    //增加约束
    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
    @Length(min = 32)
    private String password;
}
