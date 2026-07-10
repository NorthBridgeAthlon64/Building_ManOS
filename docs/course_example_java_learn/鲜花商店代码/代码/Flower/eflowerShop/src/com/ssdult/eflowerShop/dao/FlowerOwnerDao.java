package com.ssdult.eflowerShop.dao;

import java.util.List;

import com.ssdult.eflowerShop.entity.FlowerOwner;

public interface FlowerOwnerDao {

	/**
	 * 查询所有顾客信息
	 */
	public  List<FlowerOwner> getAllOwner();

	/**
	 * 更新顾客信息
	 */
	public  int updateOwner(String sql, String[] param);

	/**
	 * 根据查询条件查询顾客信息
	 */
	public  FlowerOwner selectOwner(String sql, String[] param);

}
