package com.ssdult.eflowerShop.dao;

import java.util.List;

import com.ssdult.eflowerShop.entity.Flower;


public interface FlowerDao {
	/**
	 * 查询所有鲜花信息
	 */
	public  List<Flower> getAllFlower();

	/**
	 * 根据已知鲜花的信息查询鲜花信息
	 */
	public  List<Flower> selectFlower(String sql, String[] param);

	/**
	 * 更新鲜花信息
	 */
	public  int updateFlower(String sql, Object[] param);
}
