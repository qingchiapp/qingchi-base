package com.qingchi.base.model.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.qingchi.base.constant.CommonStatus;
import com.qingchi.base.model.user.UserDO;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author qinkaiyuan
 * @date 2019-02-14 22:03
 */
@Entity
@Table(name = "authentication")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AuthenticationDO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String phoneNum;
    private String authCode;
    private Date createTime;
    private String ip;
    private Integer userId;
    private String status;

    public AuthenticationDO() {
    }

    public AuthenticationDO(UserDO user, String phoneNum, String authCode, String ip) {
        this.userId = user != null ? user.getId() : null;
        this.phoneNum = phoneNum;
        this.authCode = authCode;
        this.ip = ip;
        this.createTime = new Date();
        this.status = CommonStatus.init;
    }
}
