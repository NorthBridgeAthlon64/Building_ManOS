package com.ssdult.eflowerShop.service;

import com.ssdult.eflowerShop.entity.Flower;
import com.ssdult.eflowerShop.entity.FlowerStore;

/**
 * @author  买鲜花接口
 */
public interface Buyable {
	/**
	 * 顾客购买库存鲜花
	 */
	public void buy(Flower flower,FlowerStore store);
	
}
