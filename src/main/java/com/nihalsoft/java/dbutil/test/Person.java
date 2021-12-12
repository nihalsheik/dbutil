package com.nihalsoft.java.dbutil.test;

import java.util.Date;

import com.nihalsoft.java.dbutil.annotation.Column;
import com.nihalsoft.java.dbutil.annotation.Id;
import com.nihalsoft.java.dbutil.annotation.Table;
import com.nihalsoft.java.dbutil.common.Entity;

@Table(name = "tbl_person")
public class Person extends Entity {

    @Id
    @Column
    public long getId() {
        return get("id");
    }

    public void setId(long id) {
        set("id", id);
    }

    @Column
    public String getName() {
        return get("name");
    }

    public void setName(String name) {
        set("name", name);
    }

    @Column
    public int getAge() {
        return get("age");
    }

    public void setAge(int age) {
        set("age", age);
    }

    @Column(name = "create_time")
    public Date getCreateTime() {
        return get("create_time");
    }

    public void setCreateTime(Date createTime) {
        set("create_time", createTime);
    }

}