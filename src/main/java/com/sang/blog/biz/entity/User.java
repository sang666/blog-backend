package com.sang.blog.biz.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 *
 * </p>
 *
 * @author sang666
 * @since 2020-07-03
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_user")
@ApiModel(value = "User对象", description = "")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    public User(String id,
                String userName,
                String roles,
                String avatar,
                String email,
                String sign,
                Integer state,
                String regIp,
                String loginIp,
                Date createTime,
                Date updateTime) {
        this.id = id;
        this.userName = userName;
        this.roles = roles;
        this.avatar = avatar;
        this.email = email;
        this.sign = sign;
        this.state = state;
        this.regIp = regIp;
        this.loginIp = loginIp;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "角色")
    private String roles;

    @ApiModelProperty(value = "头像地址")
    private String avatar;

    @ApiModelProperty(value = "邮箱地址")
    private String email;

    @ApiModelProperty(value = "签名")
    private String sign;

    @ApiModelProperty(value = "状态：1表示不使用，0表示正常")
    @TableLogic
    private Integer state;

    @ApiModelProperty(value = "注册ip")
    private String regIp;

    @ApiModelProperty(value = "登录Ip")
    private String loginIp;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
