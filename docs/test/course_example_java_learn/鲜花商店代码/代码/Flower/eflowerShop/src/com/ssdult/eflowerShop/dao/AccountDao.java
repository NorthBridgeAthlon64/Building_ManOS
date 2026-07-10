package com.ssdult.eflowerShop.dao;

import java.util.List;

import com.ssdult.eflowerShop.entity.Account;



public interface AccountDao {

	/**
	 * 更新鲜花商店与顾客的交易信息
	 */
	public  int updateAccount(String sql, Object[] param);

	/**
	 * 根据查询条件查询出鲜花商店帐单
	 */
	public  List<Account> getFlowerStoreAccount(String sql, String[] param);

}
