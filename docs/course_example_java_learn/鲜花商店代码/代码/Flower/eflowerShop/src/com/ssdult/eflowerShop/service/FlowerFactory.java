package com.ssdult.eflowerShop.service;

import com.ssdult.eflowerShop.entity.Flower;

/**
 *  鲜花工厂接口
 */
public interface FlowerFactory {
	/**
	 * 培育新品种宠物
	 */
	public Flower breadNewFlower(String[] petParam);
}
