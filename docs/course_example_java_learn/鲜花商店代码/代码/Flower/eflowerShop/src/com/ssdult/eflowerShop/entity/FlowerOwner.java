package com.ssdult.eflowerShop.entity;

/**
 * 鲜花顾客实体类
 * 
 */
public class FlowerOwner {
	/**
	 * 顾客标识符
	 */
	private int id;

	/**
	 * 顾客名称
	 */
	private String name;

	/**
	 * 顾客密码
	 */
	private String password;

	/**
	 * 顾客资金
	 */
	private double money;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

}
