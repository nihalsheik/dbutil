package com.nihalsoft.java.dbutil.test;

import java.util.Date;

import com.nihalsoft.java.dbutil.annotation.Column;
import com.nihalsoft.java.dbutil.annotation.Id;
import com.nihalsoft.java.dbutil.annotation.Table;
import com.nihalsoft.java.dbutil.common.Entity;

@Table(name = "tbl_person")
public class Person implements Entity {

    private Long id;
    private String name;
    private Integer age;
    private Date createTime;

    @Id
    @Column
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Column(name = "create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
