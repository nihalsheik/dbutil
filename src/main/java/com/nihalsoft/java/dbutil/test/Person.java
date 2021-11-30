package com.nihalsoft.java.dbutil.test;

import java.time.LocalDateTime;

import com.nihalsoft.java.dbutil.common.Table;

@Table(name = "tbl_person")
public class Person {

	private long id;
	private String name;
	private int age;
	private LocalDateTime createTime;

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

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

}
