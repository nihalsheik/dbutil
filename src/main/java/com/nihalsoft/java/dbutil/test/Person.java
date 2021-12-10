package com.nihalsoft.java.dbutil.test;

import java.util.Date;

import com.nihalsoft.java.dbutil.common.Column;
import com.nihalsoft.java.dbutil.common.ColumnType;
import com.nihalsoft.java.dbutil.common.Table;

@Table(name = "tbl_person")
public class Person {

    private long id;
    private String name;
    private int age;
    private Date createTime;

    @Column(type = ColumnType.ID)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
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
