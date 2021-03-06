package com.qingchi.base.model.system;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author qinkaiyuan
 * @date 2019-12-22 18:46
 */
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
/**
 * 违规词
 */
@Table(name = "illegal_word", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"word"})
})
public class IllegalWordDO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String word;
    private String cause;
    @Column(columnDefinition = "varchar(255) default '正常'")
    private String status;
    //触发次数
    @Column(columnDefinition = "int default 0")
    private Integer triggerCount;
    private Date createTime;
    private Date updateTime;
}
