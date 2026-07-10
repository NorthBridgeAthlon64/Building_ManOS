package com.ssdult.eflowerShop.service;

import com.ssdult.eflowerShop.entity.Flower;
import com.ssdult.eflowerShop.entity.FlowerStore;

/**
 * @author 鲜花培育接口
 * 
 */
public interface Breadable {
	/**
	 * 鲜花繁殖
	 */
	public Flower bread(String flower,FlowerStore store);
}
