package com.ssdut.entity.ex;

public abstract class EX {
	private double price;//价格
	private String ex_name;//附赠品名

	/**
	 * 附赠品价格计算
	 * @return
	 */
	public double cost() {
		System.out.print("附赠品：");
		System.out.println(ex_name + ":" + price);
		System.out.println("--------------");
		return price;
	}

	public String getEx_name() {
		return ex_name;
	}

	public void setEx_name(String exName) {
		ex_name = exName;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}
