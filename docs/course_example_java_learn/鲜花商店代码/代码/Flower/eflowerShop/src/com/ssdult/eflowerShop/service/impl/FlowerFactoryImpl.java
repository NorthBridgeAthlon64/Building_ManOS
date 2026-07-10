package com.ssdult.eflowerShop.service.impl;

import com.ssdult.eflowerShop.entity.Flower;



/**
 * 鲜花工厂实现类
 */
public class FlowerFactoryImpl implements com.ssdult.eflowerShop.service.FlowerFactory {

	/**
	 * 培育新品种鲜花
	 */
	@Override
	public Flower breadNewFlower(String[] flowerParam) {
		Flower flower = new Flower();
		flower.setName(flowerParam[0]);
		flower.setTypeName(flowerParam[1]);
		flower.setStoreId(Integer.parseInt(flowerParam[2]));
		flower.setPrice(Double.parseDouble(flowerParam[3]));
		return flower;
	}

}
