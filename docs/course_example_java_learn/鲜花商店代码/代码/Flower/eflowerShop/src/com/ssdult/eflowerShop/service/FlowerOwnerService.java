package com.ssdult.eflowerShop.service;

import java.util.List;

import com.ssdult.eflowerShop.entity.Flower;
import com.ssdult.eflowerShop.entity.FlowerOwner;



public interface FlowerOwnerService extends Sellable{
	/**
	 * 鲜花主人登录
	 */
	public FlowerOwner login();

	/**
	 * 根据顾客标识符（id）获得到该顾客所有鲜花信息
	 */
	public List<Flower> getMyFlower(int ownerId);
	
	public void FlowerOwnerbuy(Flower flower);
}
