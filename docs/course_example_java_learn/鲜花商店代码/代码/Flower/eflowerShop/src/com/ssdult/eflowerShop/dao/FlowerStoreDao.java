package com.ssdult.eflowerShop.dao;

import java.util.List;

import com.ssdult.eflowerShop.entity.FlowerStore;



public interface FlowerStoreDao {

	/**
	 * 查询出所有鲜花商店
	 */
	public  List<FlowerStore> getAllStore();

	/**
	 * 根据查询条件查询出鲜花商店
	 */
	public  FlowerStore getFlowerStore(String sql, String[] param);

	/**
	 * 更新鲜花商店信息
	 */
	public  int updateStore(String sql, Object[] param);

}
