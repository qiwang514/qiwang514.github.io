package com.wq.seckill.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName("seckill_user")
@Data
public class User implements Serializable {


    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

    private String nickname;
    private String password;
    private String salt;
    private String head;
    private Date registerDate;

    private Date lastLoginDate;
    private Integer loginCount;
}
