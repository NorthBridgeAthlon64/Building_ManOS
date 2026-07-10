package com.ssdult.eflowerShop.service;

import com.ssdult.eflowerShop.entity.Flower;

/**
 *卖鲜花接口
 */
public interface Sellable {
	/**
	 * 卖鲜花
	 */
	public void sell(Flower flower);
}
